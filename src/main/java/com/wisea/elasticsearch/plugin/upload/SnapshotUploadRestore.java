package com.wisea.elasticsearch.plugin.upload;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.repositories.IndexId;
import org.elasticsearch.repositories.RepositoryData;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.repositories.fs.FsRepository;
import org.elasticsearch.snapshots.Snapshot;
import org.elasticsearch.snapshots.SnapshotId;
import org.elasticsearch.snapshots.SnapshotInfo;

import com.wisea.elasticsearch.plugin.entity.BackInfo;
import com.wisea.elasticsearch.plugin.entity.BaseBack;
import com.wisea.elasticsearch.plugin.util.ZipUtils;

/**
 * 上传的zip文件解压并生成index-*等恢复用信息
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月30日 下午1:51:25
 */
public class SnapshotUploadRestore extends FsRepository {

    private Environment evn;

    protected SnapshotUploadRestore(RepositoryMetaData metadata, Environment evn, NamedXContentRegistry namedXContentRegistry) {
        super(metadata, evn, namedXContentRegistry);
        this.evn = evn;
    }

    /**
     * 上传并生成index-*
     * 
     * @param path
     * @param filename
     * @throws IOException
     */
    public void uploadRestore(Path path, String filename) throws IOException {
        try {
            // 解压 zip
            ZipUtils.releaseZip(path.toString());
            // 解析文件名
            BackInfo backInfo = new BackInfo(filename);
            BaseBack repositoryInfo = new BaseBack(backInfo.getR());
            BaseBack snapshotInfo = new BaseBack(backInfo.getS());
            List<IndexId> indices = new ArrayList<>();
            indices.add(new IndexId(repositoryInfo.getName(), repositoryInfo.getId()));
            // 创建一个假的snapshot
            Snapshot snapshot = new Snapshot(metadata.name(), new SnapshotId(snapshotInfo.getName(), snapshotInfo.getId()));
            // 创建一个家的snapshot
            SnapshotInfo blobStoreSnapshot = new SnapshotInfo(snapshot.getSnapshotId(), indices.stream().map(IndexId::getName).collect(Collectors.toList()), System.currentTimeMillis(), true);
            // 获取data对象
            final RepositoryData repositoryData = getRepositoryData();
            // 写入index-*文件
            writeIndexGen(repositoryData.addSnapshot(snapshot.getSnapshotId(), blobStoreSnapshot.state(), indices), -2);
        } catch (FileAlreadyExistsException ex) {
            // if another master was elected and took over finalizing the snapshot, it is possible
            // that both nodes try to finalize the snapshot and write to the same blobs, so we just
            // log a warning here and carry on
            throw new RepositoryException(metadata.name(), "Blob already exists while " + "finalizing snapshot, assume the snapshot has already been saved", ex);
        } catch (IOException ex) {
            throw new RepositoryException(metadata.name(), "failed to update snapshot in repository", ex);
        } finally {
            // 如果出错或完成就删除zip
            Files.delete(path);
        }
    }

    /**
     * 获取存储目录
     * 
     * @return
     * @throws Exception
     */
    public Path getBlobStorePath() throws Exception {
        final String location = REPOSITORIES_LOCATION_SETTING.get(metadata.settings());
        final Path locationFile = evn.resolveRepoFile(location);
        return Files.createDirectories(locationFile);
    }
}
