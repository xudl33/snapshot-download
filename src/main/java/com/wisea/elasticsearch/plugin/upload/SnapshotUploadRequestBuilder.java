package com.wisea.elasticsearch.plugin.upload;

import org.elasticsearch.action.support.master.MasterNodeOperationRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class SnapshotUploadRequestBuilder extends MasterNodeOperationRequestBuilder<SnapshotUploadRequest, SnapshotUploadResponse, SnapshotUploadRequestBuilder> {

    public SnapshotUploadRequestBuilder(ElasticsearchClient client, SnapshotUploadAction action) {
        super(client, action, new SnapshotUploadRequest());
    }

}
