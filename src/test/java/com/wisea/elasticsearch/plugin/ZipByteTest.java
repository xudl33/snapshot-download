package com.wisea.elasticsearch.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.elasticsearch.common.io.PathUtils;

public class ZipByteTest {
    public static void main(String[] args) throws IOException {

        byte[] b = Files
                .readAllBytes(PathUtils.get("C:\\Users\\diy\\Desktop\\backup\\es_back\\eyJyIjoiV1AxaUZuTXpRd0NCNFpQSHlUZXhnUSNiYW5rIiwicyI6IndxTzlHdG1MVHdldjdfOEhWUk9jNEEjc25hcHNob3QxIn0=.zip"));
        System.out.println(b.length);
    }
}
