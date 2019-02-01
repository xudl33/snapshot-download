package com.wisea.elasticsearch.plugin.download;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.master.MasterNodeRequest;

/**
 * Snapshot下载Request
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午5:07:27
 */
public class SnapshotDownloadRequest extends MasterNodeRequest<SnapshotDownloadRequest> {
    private String repository;
    private String snapshot;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public SnapshotDownloadRequest() {
    }

    public SnapshotDownloadRequest(String repository, String snapshot) {
        this.repository = repository;
        this.snapshot = snapshot;
    }

    @Override
    public ActionRequestValidationException validate() {
        // TODO Auto-generated method stub
        return null;
    }

}
