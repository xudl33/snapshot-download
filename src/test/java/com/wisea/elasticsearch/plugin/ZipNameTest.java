package com.wisea.elasticsearch.plugin;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ZipNameTest {
    public static void main(String[] args) {
        String aa = "WP1iFnMzQwCB4ZPHyTexgQ#bank;wqO9GtmLTwev7_8HVROc4A#snapshot1";
        String vv = Base64Coder.encodeString(aa);
        System.out.println(vv);
        System.out.println(Base64Coder.decodeString(vv));
    }
}
