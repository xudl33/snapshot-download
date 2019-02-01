package com.wisea.elasticsearch.plugin;

import java.io.IOException;

import org.codelibs.curl.CurlResponse;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.EcrCurl;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

//@RunWith(BlockJUnit4ClassRunner.class)
public class SnapshotDownloadPluginTest {
    private static String clusterName;
    private static ElasticsearchClusterRunner runner;
    private static Node node;

//    @BeforeClass
    public static void setUp() {
        clusterName = "es-snapshot-download" + System.currentTimeMillis();
        // create runner instance
        runner = new ElasticsearchClusterRunner();
        // create ES nodes
        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(final int number, final Builder settingsBuilder) {
                settingsBuilder.put("network.host", "192.168.20.127");
                settingsBuilder.put("path.repo", "C:\\Users\\diy\\Desktop\\backup\\es_back_test");
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.put("http.cors.allow-origin", "*");
                // settingsBuilder.putList("discovery.zen.ping.unicast.hosts", "localhost:9301-9310");
            }
        }).build(ElasticsearchClusterRunner.newConfigs().basePath("E:\\es-cluster4112237461987566389").clusterName(clusterName).numOfNode(1)
                .pluginTypes("com.wisea.elasticsearch.plugin.SnapshotDownloadPlugin"));

        // wait for yellow status
        runner.ensureYellow();

        node = runner.node();
        System.out.println("setUp");
    }

//    @AfterClass
    public static void tearDown() throws IOException {
        // // delete all files
        // runner.clean();
        // close runner
        // runner.close();
        System.out.println("tearDown");
    }

//    @Test
    public void download() {
        System.out.println("download");
        // CurlResponse response = EcrCurl.post(node, "_snapshot/my_backup/snapshot1/_download").execute();
        // final String content = response.getContentAsString();
        // System.out.println(response.getHeaders());
        // System.out.println(content);
    }
}
