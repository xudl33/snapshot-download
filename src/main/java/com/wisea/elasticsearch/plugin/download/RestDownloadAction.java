package com.wisea.elasticsearch.plugin.download;

import static org.elasticsearch.rest.RestRequest.Method.POST;

import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;

/**
 * Snapshot下载
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午6:21:28
 */
public class RestDownloadAction extends BaseRestHandler {

    public RestDownloadAction(final Settings settings, final RestController restController) {
        super(settings);
        restController.registerHandler(POST, "/_snapshot/{repository}/{snapshot}/_download", this);
    }

    @Override
    public String getName() {
        return "snapshot_download_action";
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        SnapshotDownloadRequest snapshotDownloadRequest = new SnapshotDownloadRequest(request.param("repository"), request.param("snapshot"));
        return channel -> client.admin().cluster().execute(SnapshotDownloadAction.INSTANCE, snapshotDownloadRequest, new SnapshotDownloadListener(channel));
    }
}
