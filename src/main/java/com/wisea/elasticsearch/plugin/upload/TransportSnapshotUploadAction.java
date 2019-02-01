package com.wisea.elasticsearch.plugin.upload;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.master.TransportMasterNodeAction;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.repositories.RepositoriesService;
import org.elasticsearch.repositories.Repository;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

/**
 * 上传快照执行Service
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午4:29:12
 */
public class TransportSnapshotUploadAction extends TransportMasterNodeAction<SnapshotUploadRequest, SnapshotUploadResponse> {
    private final RepositoriesService repositoriesService;
    private SnapshotUploadRestore snapshotUploadRestore;
    private Environment env;
    private NamedXContentRegistry namedXContentRegistry;

    @Inject
    public TransportSnapshotUploadAction(Settings settings, TransportService transportService, ClusterService clusterService, ThreadPool threadPool, RepositoriesService repositoriesService,
            ActionFilters actionFilters, IndexNameExpressionResolver indexNameExpressionResolver, Environment env, NamedXContentRegistry namedXContentRegistry) {
        super(settings, SnapshotUploadAction.NAME, transportService, clusterService, threadPool, actionFilters, indexNameExpressionResolver, SnapshotUploadRequest::new);
        this.repositoriesService = repositoriesService;
        this.env = env;
        this.namedXContentRegistry = namedXContentRegistry;
    }

    @Override
    protected String executor() {
        return ThreadPool.Names.SNAPSHOT;
    }

    @Override
    protected SnapshotUploadResponse newResponse() {
        return new SnapshotUploadResponse();
    }

    public static String getCodeType(byte[] b) {
        if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
            return "utf8";
        } else if (b[0] == -1 && b[1] == -2) {
            return "unicode";
        } else {
            return "gbk";
        }
    }

    @Override
    protected void masterOperation(SnapshotUploadRequest request, ClusterState state, ActionListener<SnapshotUploadResponse> listener) throws Exception {
        // 查询对应的仓库
        Repository repository = repositoriesService.repository(request.getRepository());
        // 获取上传文件byte流
        BytesReference bytesReference = request.getBytesReference();

        // 创建SnapshotUploadRestore
        if (null == snapshotUploadRestore || !snapshotUploadRestore.getMetadata().equals(repository.getMetadata())) {
            snapshotUploadRestore = new SnapshotUploadRestore(repository.getMetadata(), this.env, namedXContentRegistry);
        }
        String filename = request.getFilename();
        // 创建上传的path
        Path path = PathUtils.get(snapshotUploadRestore.getBlobStorePath().toString() + "/" + filename);

        // 将上传的文件拷贝到仓库目(覆盖)
        Files.copy(bytesReference.streamInput(), path, StandardCopyOption.REPLACE_EXISTING);
        // 开启执行 否则会报不是started状态
        snapshotUploadRestore.start();
        // 解压zip并重写index-*等文件
        snapshotUploadRestore.uploadRestore(path, filename);

        listener.onResponse(newResponse());
    }

    @Override
    protected ClusterBlockException checkBlock(SnapshotUploadRequest request, ClusterState state) {
        // Restoring a snapshot might change the global state and create/change an index,
        // so we need to check for METADATA_WRITE and WRITE blocks
        ClusterBlockException blockException = state.blocks().globalBlockedException(ClusterBlockLevel.METADATA_WRITE);
        if (blockException != null) {
            return blockException;
        }
        return state.blocks().globalBlockedException(ClusterBlockLevel.WRITE);
    }

}
