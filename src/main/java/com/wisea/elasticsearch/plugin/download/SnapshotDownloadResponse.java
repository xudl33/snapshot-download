package com.wisea.elasticsearch.plugin.download;

import java.io.File;

import org.elasticsearch.action.ActionResponse;

/**
 * 下载Service返回值
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午4:28:55
 */
public class SnapshotDownloadResponse extends ActionResponse {
    private File file = null;

    public SnapshotDownloadResponse() {
    }

    public SnapshotDownloadResponse(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
