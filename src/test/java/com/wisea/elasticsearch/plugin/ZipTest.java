package com.wisea.elasticsearch.plugin;

import java.io.File;

import com.wisea.elasticsearch.plugin.util.ZipUtils;

public class ZipTest {
    public static void main(String[] args) throws Exception {
        File target = new File("C:\\Users\\diy\\Desktop\\backup\\es_back\\my_backup\\1.zip");
        String[] paths = {"", "", "indices"};
        File[] sources = {
            new File("C:\\Users\\diy\\Desktop\\backup\\es_back\\my_backup\\meta-wqO9GtmLTwev7_8HVROc4A.dat"),
            new File("C:\\Users\\diy\\Desktop\\backup\\es_back\\my_backup\\snap-wqO9GtmLTwev7_8HVROc4A.dat"),
            new File("C:\\Users\\diy\\Desktop\\backup\\es_back\\my_backup\\indices\\WP1iFnMzQwCB4ZPHyTexgQ")
        };
        ZipUtils.createZip(target, paths, sources);
    }
}
