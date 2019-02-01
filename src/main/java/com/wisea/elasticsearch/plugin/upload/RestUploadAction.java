package com.wisea.elasticsearch.plugin.upload;

import static org.elasticsearch.rest.RestRequest.Method.POST;

import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestToXContentListener;

public class RestUploadAction extends BaseRestHandler {

    public RestUploadAction(final Settings settings, final RestController restController) {
        super(settings);
        restController.registerHandler(POST, "/_snapshot/{repository}/_upload/{filename}", this);
    }

    @Override
    public String getName() {
        return "snapshot_upload_action";
    }

    /**
     * 由于RestCotroller.hasContentType中校验 此处要必须返回true
     * <p/>
     * 且由于写死了只能是application/json或application/smile的Content-type才能上传
     */
    @Override
    public boolean supportsContentStream() {
        return true;
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        SnapshotUploadRequest snapshotuploadRequest = new SnapshotUploadRequest(request.param("repository"), request.param("filename"), request.param("charset"), request.requiredContent(),
                request.getXContentType());
        // SnapshotDownloadRequest snapshotDownloadRequest = new SnapshotDownloadRequest(request.param("repository"), request.param("snapshot"));

        // return channel -> client.admin().cluster().execute(restoreSnapshotRequest, new SnapshotDownloadListener<>(channel));
        // return channel -> client.admin().cluster().restoreSnapshot(restoreSnapshotRequest, new RestToXContentListener<>(channel));
        return channel -> client.admin().cluster().execute(SnapshotUploadAction.INSTANCE, snapshotuploadRequest, new RestToXContentListener<>(channel));
        // return channel -> client.admin().cluster().execute(SnapshotDownloadAction.INSTANCE, snapshotDownloadRequest, new SnapshotUploadListener(channel));
    }
}
