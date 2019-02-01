package com.wisea.elasticsearch.plugin.upload;

import static org.elasticsearch.action.ValidateActions.addValidationError;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.master.MasterNodeRequest;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Snapshot下载Request
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午5:07:27
 */
public class SnapshotUploadRequest extends MasterNodeRequest<SnapshotUploadRequest> {
    private String repository;
    private String filename;
    private String charset = "utf8";
    private BytesReference bytesReference;
    private XContentType xContentType;

    public SnapshotUploadRequest() {
    }

    public SnapshotUploadRequest(String repository, String filename, String charset, BytesReference bytesReference, XContentType xContentType) {
        this.repository = repository;
        this.filename = filename;
        this.charset = charset;
        this.bytesReference = bytesReference;
        this.xContentType = xContentType;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public BytesReference getBytesReference() {
        return bytesReference;
    }

    public void setBytesReference(BytesReference bytesReference) {
        this.bytesReference = bytesReference;
    }

    public XContentType getxContentType() {
        return xContentType;
    }

    public void setxContentType(XContentType xContentType) {
        this.xContentType = xContentType;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (repository == null) {
            validationException = addValidationError("repository is missing, url formate is /_snapshot/{repository}/{filename}/_upload", validationException);
        }
        if (filename == null) {
            validationException = addValidationError("filename is missing, url formate is /_snapshot/{repository}/{filename}/_upload", validationException);
        }
        if (bytesReference == null) {
            validationException = addValidationError("--data-binary are missing", validationException);
        }
        if (xContentType == null) {
            validationException = addValidationError("contentType is missing", validationException);
        } else {
            String lowContentType = xContentType.toString().toLowerCase();
            if (!(lowContentType.endsWith(XContentType.JSON.shortName()) || lowContentType.endsWith(XContentType.JSON.shortName()))) {
                validationException = addValidationError("contentType must be application/json or application/smile", validationException);
            }
        }
        return validationException;
    }

}
