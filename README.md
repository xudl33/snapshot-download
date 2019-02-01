# snapshot-download
本项目可用于`elasticsearch`服务器的快照`snapshot`下载和上传。

## 安装
`maven:install`编译本工程后，将会生成安装包(`target/releases/snapshot-download-x.X.X-Y.zip`)

进入`elasticsearch/bin`目录
- linux
```bash
./elasticsearch-plugin install file:////usr/local/es_plugin/snapshot-download-0.0.1-SNAPSHOT.zip
```
- windows
```bash
./elasticsearch-plugin.bat install file:///C:/es_plugin/snapshot-download-0.0.1-SNAPSHOT.zip
```

## 卸载
进入`elasticsearch/bin`目录
- linux
```bash
./elasticsearch-plugin remove snapshot-download
```

- windows
```bash
./elasticsearch-plugin.bat remove snapshot-download
```

## API
### 下载
`POST /_snapshot/{repository}/{snapshot}/_download`

_**respository和snapshot必须存在才能下载,具体备份命令请参考`elasticsearch`的官方说明文档**_

_**由于下载使用了response写入流的形式，所以curl的方式虽然能获取到文件内容，但由于无法解析文件名，所以不推荐使用curl的方式**_

文件名是经过Base64转码的仓库和快照信息的字符串拼接，如果想进行上传操作，则必须使用改名称，否则无法正确的还原备份信息，当然也可以手动制作该文件名

文件名格式：Base64(repository.id#repository.name;snapshot.uuid#snapshot.name).zip

文件名被放入`Response Header.Content-Disposition=attachment; filename=xxxxx.zip`，可以采用HttpClient或JS等方式进行自定义解析下载
```java
/**
 * 调用Elasticsearch接口Snapshot下载
 * <p/>
 * 需要依赖snapshot-download插件
 * 
 * @param esUrl
 * @param repository
 * @param snapshot
 * @param backPath
 * @return
 */
public static File snapshotDownload(String esUrl, String repository, String snapshot, String backPath) {
    String url = esUrl + MessageFormat.format(POST_DOWNLOAD_URL, repository, snapshot);
    File target = null;
    try {
        // 使用httpclient发送请求
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        // 文件名
        String filename = URLDecoder.decode(response.getFirstHeader("ajax-filename").getValue(), "utf8");
        target = new File(backPath + "/" + filename);
        File targetParent = target.getParentFile();
        // 不存在就创建新目录
        if (!targetParent.exists()) {
            targetParent.mkdirs();
        }
        // 将文件下载到本地
        FileOutputStream out = new FileOutputStream(target.getAbsolutePath());
        FileCopyUtils.copy(entity.getContent(), out);
        out.close();
    } catch (Exception e) {
        LoggerUtil.error("exception in snapshopDownload", e);
        // 如果出错就删除
        if (null != target) {
            target.delete();
        }
    }
    return target;
}
```

也可利用`wbfc.ajaxFileDownloader`进行下载
```javascript
$.ajaxFileDownloaderPost({
    url: "http://192.168.20.127:9200/_snapshot/my_backup/snapshot1/_download",
});
```
### 上传
`POST /_snapshot/{repository}/_upload/{filename}`

上传的文件名必须为指定格式，且备份的snapshot.id和snapshot.name在库中不能存在，否则无法正确还原备份信息

文件名格式：Base64(repository.id#repository.name;snapshot.uuid#snapshot.name).zip


```bash
curl -H 'Content-Type: application/x-ndjson' -XPOST http://192.168.20.127:9200/_snapshot/my_backup/_upload/V1AxaUZuTXpRd0NCNFpQSHlUZXhnUSNiYW5rO3dxTzlHdG1MVHdldjdfOEhWUk9jNEEjc25hcHNob3Qx.zip --data-binary '@/usr/local/es_back/V1AxaUZuTXpRd0NCNFpQSHlUZXhnUSNiYW5rO3dxTzlHdG1MVHdldjdfOEhWUk9jNEEjc25hcHNob3Qx.zip'
```

同样也可以使用HttpClient等工具进行上传
```java
/**
 * 调用Elasticsearch接口Snapshot上传
 * <p/>
 * 需要依赖snapshot-download插件
 * 
 * @param esUrl
 * @param repository
 * @param backPath
 * @param uploadFileName
 */
public static void snapshotUpload(String esUrl, String repository, String backPath, String uploadFileName) {
    String url = esUrl + MessageFormat.format(POST_UPLOAD_URL, repository, uploadFileName);
    File target = new File(backPath + "/" + uploadFileName);
    try {
        // 使用httpclient发送请求
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        // 将文件写入RequestEntity
        FileBody tarFileBody = new FileBody(target);
        HttpEntity reqEntity = MultipartEntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).addPart("file", tarFileBody).build();
        httpPost.setEntity(reqEntity);
        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        // 显示结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
        String line = null;
        StringBuffer sf = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            sf.append(line);
        }
        httpclient.close();
        String resultString = sf.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) JsonMapper.fromJsonString(resultString, Map.class);
        if (null == result || false == ConverterUtil.toBoolean(result.get("accepted"))) {
            throw new RuntimeException(resultString);
        }
    } catch (Exception e) {
        LoggerUtil.error("exception in snapshopUpload", e);
    }
}
```