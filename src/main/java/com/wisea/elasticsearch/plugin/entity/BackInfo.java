package com.wisea.elasticsearch.plugin.entity;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.wisea.elasticsearch.plugin.exception.BackInfoException;

/**
 * 备份基础信息
 * 
 * <p/>
 * 该实体会被转换为JSON字符串并做Base64转码，用结果此命名下载接口生成的zip文件
 * <p/>
 * 上传时会重新Base64解码，重新映射回该类
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月29日 下午4:22:07
 */
public class BackInfo {

    /** repository: repositoryId#repositoryName */
    private String r;

    /** snapshot: snapshotId#snapshotName */
    private String s;

    public BackInfo() {
    }

    public BackInfo(String filename) {
        this();
        if (null != filename) {
            // 删除后缀 只需要文件名
            String simName = filename.substring(0, filename.lastIndexOf("."));
            this.decode(simName);
        }
    }

    public BackInfo(String repositoryId, String repositoryName, String snapshotId, String snapshotName) {
        this.r = repositoryId + "#" + repositoryName;
        this.s = snapshotId + "#" + snapshotName;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String encode() {
        if (null == this.r || null == this.s) {
            return "";
        }
        // base64加密
        return Base64Coder.encodeString(this.r + ";" + this.s);
    }

    public void decode(String encodeStr) {
        if (null != encodeStr) {
            // NamedXContentRegistry
            String decodeStr = Base64Coder.decodeString(encodeStr);
            String[] rsArrya = decodeStr.split(";");
            if (rsArrya.length != 2) {
                throw new BackInfoException("BackInfo " + decodeStr + " formate must be [repositoryId#repositoryName;snapshotId#snapshotName]");
            }
            setR(rsArrya[0]);
            setS(rsArrya[1]);
        }
    }

}
