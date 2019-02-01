package com.wisea.elasticsearch.plugin.download;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

public class SnapshotDownloadAction extends Action<SnapshotDownloadRequest, SnapshotDownloadResponse, SnapshotDownloadRequestBuilder> {

    public static final SnapshotDownloadAction INSTANCE = new SnapshotDownloadAction();
    public static final String NAME = "cluster:admin/snapshot/download";

    private SnapshotDownloadAction() {
        super(NAME);
    }

    @Override
    public SnapshotDownloadRequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new SnapshotDownloadRequestBuilder(client, this);
    }

    @Override
    public SnapshotDownloadResponse newResponse() {
        return new SnapshotDownloadResponse();
    }

}
