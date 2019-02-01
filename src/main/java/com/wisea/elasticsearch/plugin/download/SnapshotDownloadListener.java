package com.wisea.elasticsearch.plugin.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapshotDownloadListener implements ActionListener<SnapshotDownloadResponse> {
    private final RestChannel channel;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SnapshotDownloadListener(RestChannel channel) {
        this.channel = channel;
    }

    @Override
    public void onResponse(SnapshotDownloadResponse response) {
        File outputFile = response.getFile();
        InputStream fis = null;
        try {
            fis = new FileInputStream(outputFile);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] bytes = new byte[1024];
            int len;
            while ((len = fis.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            String mimeType = "application/zip";
            String fName = toDownloadName(URLEncoder.encode(outputFile.getName(), "utf8"));
            final BytesRestResponse byteResponse = new BytesRestResponse(RestStatus.OK, "application/octet-stream; charset=utf-8", out.toByteArray());
            byteResponse.addHeader("Access-Control-Expose-Headers", "ajax-mimeType, ajax-filename");
            byteResponse.addHeader("Content-Disposition", "attachment; filename=" + fName);
            byteResponse.addHeader("ajax-mimeType", mimeType);
            byteResponse.addHeader("ajax-filename", fName);
            fis.close();
            out.flush();
            channel.sendResponse(byteResponse);
            out.close();
        } catch (IOException e) {
            onFailure(e);
        } finally {
            // 下载完成后删除zip文件
            outputFile.delete();
        }
    }

    /**
     * 转成下载专用的文件名
     * <p>
     * 下载的文件如果经过URLEncoder的转码，其中有些特殊符号就也会被跟着转成中文，但是这些符号不会被正确解码，所以要进行二次转换
     * 
     * @param str
     * @return
     */
    public String toDownloadName(String str) {
        if (null == str) {
            return str;
        }
        return str.replaceAll("%20", "\\+").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&");
    }

    @Override
    public void onFailure(Exception e) {
        try {
            channel.sendResponse(new BytesRestResponse(channel, RestStatus.INTERNAL_SERVER_ERROR, e));
        } catch (final IOException e1) {
            logger.error("Failed to send failure response", e1);
        }
    }

}
