package com.wisea.elasticsearch.plugin.upload;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

public class SnapshotUploadAction extends Action<SnapshotUploadRequest, SnapshotUploadResponse, SnapshotUploadRequestBuilder> {

    public static final SnapshotUploadAction INSTANCE = new SnapshotUploadAction();
    public static final String NAME = "cluster:admin/snapshot/upload";

    private SnapshotUploadAction() {
        super(NAME);
    }

    @Override
    public SnapshotUploadRequestBuilder newRequestBuilder(ElasticsearchClient client) {
        return new SnapshotUploadRequestBuilder(client, this);
    }

    @Override
    public SnapshotUploadResponse newResponse() {
        return new SnapshotUploadResponse();
    }

}
