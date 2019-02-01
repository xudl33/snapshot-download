package com.wisea.elasticsearch.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import com.wisea.elasticsearch.plugin.download.RestDownloadAction;
import com.wisea.elasticsearch.plugin.download.SnapshotDownloadAction;
import com.wisea.elasticsearch.plugin.download.TransportSnapshotDownloadAction;
import com.wisea.elasticsearch.plugin.upload.RestUploadAction;
import com.wisea.elasticsearch.plugin.upload.SnapshotUploadAction;
import com.wisea.elasticsearch.plugin.upload.TransportSnapshotUploadAction;

public class SnapshotDownloadPlugin extends Plugin implements ActionPlugin {
    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> list = new ArrayList<>();
        list.add(new ActionHandler<>(SnapshotDownloadAction.INSTANCE, TransportSnapshotDownloadAction.class));
        list.add(new ActionHandler<>(SnapshotUploadAction.INSTANCE, TransportSnapshotUploadAction.class));
        return list;
    }

    @Override
    public List<RestHandler> getRestHandlers(final Settings settings, final RestController restController, final ClusterSettings clusterSettings, final IndexScopedSettings indexScopedSettings,
            final SettingsFilter settingsFilter, final IndexNameExpressionResolver indexNameExpressionResolver, final Supplier<DiscoveryNodes> nodesInCluster) {
        List<RestHandler> list = new ArrayList<>();
        list.add(new RestDownloadAction(settings, restController));
        list.add(new RestUploadAction(settings, restController));
        return list;
    }
}
