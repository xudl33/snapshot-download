package com.wisea.elasticsearch.plugin.download;

import org.elasticsearch.action.support.master.MasterNodeOperationRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class SnapshotDownloadRequestBuilder extends MasterNodeOperationRequestBuilder<SnapshotDownloadRequest, SnapshotDownloadResponse, SnapshotDownloadRequestBuilder> {

    public SnapshotDownloadRequestBuilder(ElasticsearchClient client, SnapshotDownloadAction action) {
        super(client, action, new SnapshotDownloadRequest());
    }

}
