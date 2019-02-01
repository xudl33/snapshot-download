package com.wisea.elasticsearch.plugin.upload;

import java.io.IOException;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * 下载Service返回值
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午4:28:55
 */
public class SnapshotUploadResponse extends ActionResponse implements ToXContentObject {

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field("accepted", true);
        builder.endObject();
        return builder;
    }
    // private File file = null;
    //
    // public File getFile() {
    // return file;
    // }
    //
    // public void setFile(File file) {
    // this.file = file;
    // }

}
