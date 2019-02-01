package com.wisea.elasticsearch.plugin.download;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.master.TransportMasterNodeAction;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.repositories.IndexId;
import org.elasticsearch.repositories.RepositoriesService;
import org.elasticsearch.repositories.Repository;
import org.elasticsearch.repositories.RepositoryData;
import org.elasticsearch.repositories.fs.FsRepository;
import org.elasticsearch.snapshots.SnapshotId;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotRestoreException;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import com.wisea.elasticsearch.plugin.entity.BackInfo;
import com.wisea.elasticsearch.plugin.util.ZipUtils;

/**
 * 下载快照执行Service
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午4:29:12
 */
public class TransportSnapshotDownloadAction extends TransportMasterNodeAction<SnapshotDownloadRequest, SnapshotDownloadResponse> {
    private final RepositoriesService repositoriesService;
    private Environment env;
    public static final String SNAPSHOT_NAME_FORMAT = "snap-%s.dat";
    public static final String METADATA_NAME_FORMAT = "meta-%s.dat";

    @Inject
    public TransportSnapshotDownloadAction(Settings settings, TransportService transportService, ClusterService clusterService, ThreadPool threadPool, RepositoriesService repositoriesService,
            ActionFilters actionFilters, IndexNameExpressionResolver indexNameExpressionResolver
            // , SnapshotUploadRestore res
            , Environment env, NamedXContentRegistry namedXContentRegistry) {
        super(settings, SnapshotDownloadAction.NAME, transportService, clusterService, threadPool, actionFilters, indexNameExpressionResolver, SnapshotDownloadRequest::new);
        this.repositoriesService = repositoriesService;
        this.env = env;
    }

    @Override
    protected String executor() {
        return ThreadPool.Names.SNAPSHOT;
    }

    @Override
    protected SnapshotDownloadResponse newResponse() {
        return new SnapshotDownloadResponse();
    }

    @Override
    protected void masterOperation(SnapshotDownloadRequest request, ClusterState state, ActionListener<SnapshotDownloadResponse> listener) throws Exception {

        String repositoryName = request.getRepository();
        String snapshotName = request.getSnapshot();
        Repository repository = repositoriesService.repository(repositoryName);
        RepositoryData repositoryData = repository.getRepositoryData();
        Optional<SnapshotId> matchingSnapshotId = repositoryData.getSnapshotIds().stream().filter(s -> snapshotName.equals(s.getName())).findFirst();
        if (matchingSnapshotId.isPresent() == false) {
            throw new SnapshotRestoreException(repositoryName, snapshotName, "snapshot does not exist");
        }

        SnapshotId snapshotId = matchingSnapshotId.get();
        SnapshotInfo snapshotInfo = repository.getSnapshotInfo(snapshotId);
        IndexId repositoryIndexId = repositoryData.getIndices().get(snapshotInfo.indices().get(0));

        BackInfo backInfo = new BackInfo(repositoryIndexId.getId(), repositoryIndexId.getName(), snapshotId.getUUID(), snapshotId.getName());

        Path targetPath = getBlobStorePath(repository.getMetadata());

        String tarAbsPath = targetPath.toFile().getAbsolutePath();
        File targetFile = new File(tarAbsPath + "/" + backInfo.encode() + ".zip");
        String[] paths = { "", "", "indices" };
        File[] sources = { new File(tarAbsPath + "/" + String.format(Locale.ROOT, SNAPSHOT_NAME_FORMAT, snapshotId.getUUID())),
                new File(tarAbsPath + "/" + String.format(Locale.ROOT, METADATA_NAME_FORMAT, snapshotId.getUUID())), new File(tarAbsPath + "/indices/" + repositoryIndexId.getId()) };
        // 创建zip
        ZipUtils.createZip(targetFile, paths, sources);
        listener.onResponse(new SnapshotDownloadResponse(targetFile));
    }

    @Override
    protected ClusterBlockException checkBlock(SnapshotDownloadRequest request, ClusterState state) {
        // Restoring a snapshot might change the global state and create/change an index,
        // so we need to check for METADATA_WRITE and WRITE blocks
        ClusterBlockException blockException = state.blocks().globalBlockedException(ClusterBlockLevel.METADATA_WRITE);
        if (blockException != null) {
            return blockException;
        }
        return state.blocks().globalBlockedException(ClusterBlockLevel.WRITE);
    }

    /**
     * 获取存储目录
     * 
     * @return
     * @throws Exception
     */
    public Path getBlobStorePath(RepositoryMetaData metadata) throws Exception {
        final String location = FsRepository.REPOSITORIES_LOCATION_SETTING.get(metadata.settings());
        final Path locationFile = env.resolveRepoFile(location);
        return Files.createDirectories(locationFile);
    }
}
