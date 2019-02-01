package com.wisea.elasticsearch.plugin.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Zip工具类
 * <p/>
 * 压缩、解压文件或文件夹中的文件
 * 
 * @author XuDL(Wisea)
 * 
 */
public class ZipUtils {
    protected static Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 将文件写入ZipOutputStream
     * 
     * @param zos
     *            ZipOutputStream
     * @param path
     *            源文件路径
     * @param excludeDirectory
     *            是否保留目录结构
     * @param files
     *            文件
     * @throws IOException
     */
    public static void writeToZipOutputStream(ZipOutputStream zos, String path, boolean excludeDirectory, File... files) throws IOException {
        writeToZipOutputStream(zos, path, excludeDirectory, null, files);
    }

    /**
     * 将文件或文件夹按照自定义的目录写入ZipOutputStream
     * 
     * @param zos
     * @param zipPath
     * @param files
     * @throws IOException
     */
    public static void writeToZipOutputStreamWithCustomPath(ZipOutputStream zos, String zipPath, File files) throws IOException {
        if (null != zipPath && !"".equals(zipPath)) {
            if (!zipPath.endsWith("/") || !zipPath.endsWith("\\")) {
                zipPath += "/";
            }
        }
        if (files.isFile()) {
            ZipEntry ze = new ZipEntry(zipPath + files.getName());
            ze.setSize(files.length());
            ze.setTime(files.lastModified());
            zos.putNextEntry(ze);
            InputStream fis = new BufferedInputStream(new FileInputStream(files));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            zos.write(buffer);
            fis.close();
        } else {
            File[] loopFiles = files.listFiles();
            for (File file : loopFiles) {
                writeToZipOutputStreamWithCustomPath(zos, zipPath + files.getName(), file);
            }
        }
    }

    /**
     * 将文件写入ZipOutputStream
     * 
     * @param zos
     *            ZipOutputStream
     * @param path
     *            源文件路径
     * @param excludeDirectory
     *            是否保留目录结构
     * @param files
     *            文件
     * @throws IOException
     */
    public static void writeToZipOutputStream(ZipOutputStream zos, String path, boolean excludeDirectory, String basePath, File... files) throws IOException {
        for (File file : files) {
            if (!file.isFile()) {
                writeToZipOutputStream(zos, path, excludeDirectory, basePath, file.listFiles());
            } else {
                String zipPath = getAbsFileName(path, file);
                if (!excludeDirectory) {
                    zipPath = file.getName();
                }
                if (null != basePath) {
                    if (!basePath.endsWith("/")) {
                        basePath += "/";
                    }
                    if (excludeDirectory) {
                        zipPath = zipPath.substring(zipPath.indexOf(basePath) + basePath.length());
                    } else {
                        zipPath = basePath + zipPath;
                    }
                }
                ZipEntry ze = new ZipEntry(zipPath);
                ze.setSize(file.length());
                ze.setTime(file.lastModified());
                zos.putNextEntry(ze);
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                zos.write(buffer);
                fis.close();
            }
        }
    }

    /**
     * 将一个文件或文件夹保留目录结构打包成压缩文件
     * 
     * @param source
     *            源文件或文件夹
     * @param target
     *            目标文件
     * @throws Exception
     *             异常
     */
    public static void createZip(File source, File target) throws Exception {
        createZip(source, target, true);
    }

    /**
     * 将若干文件打包成压缩文件
     * 
     * @param target
     *            目标文件
     * @param sources
     *            源文件数组
     * @throws Exception
     *             异常
     */
    public static void createZip(File target, File... sources) throws Exception {
        // 压缩文件名
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
        // 写入压缩文件
        writeToZipOutputStream(zos, target.getAbsolutePath(), false, sources);
        // 刷新输出流
        zos.flush();
        // 关闭输出流
        zos.close();
    }

    /**
     * 将若干文件或文件夹打包成压缩文件并自定义目录
     * <p/>
     * paths=zip中的相对目录
     * 
     * @param target
     * @param paths
     * @param sources
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static void createZip(File target, String[] paths, File[] sources) throws Exception {
        // 压缩文件名
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
        if (paths.length != sources.length) {
            throw new RuntimeException("paths.length must be equals sources.length");
        }
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            File sfile = sources[i];
            // 源文件或文件夹必须存在
            if (!sfile.exists()) {
                return;
            }
            // 写入zip并指定路径
            writeToZipOutputStreamWithCustomPath(zos, path, sfile);
        }
        // 刷新输出流
        zos.flush();
        // 关闭输出流
        zos.close();
    }

    /**
     * 将一个文件或文件夹保留目录结构打包成压缩文件
     * 
     * @param source
     *            源文件或文件夹
     * @param target
     *            目标文件
     * @param excludeDirectory
     *            是否包含目录结构
     * @throws Exception
     *             异常
     */
    public static void createZip(File source, File target, boolean excludeDirectory) throws Exception {
        // 源文件或文件夹必须存在
        if (!source.exists()) {
            return;
        }
        // 压缩文件名
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
        // 写入压缩文件
        writeToZipOutputStream(zos, source.getParent(), excludeDirectory, source);
        // 刷新输出流
        zos.flush();
        // 关闭输出流
        zos.close();
    }

    /**
     * 将一个文件或文件夹保留目录结构打包成压缩文件
     * 
     * 
     * @param baseDir
     *            所要压缩的目录名（包含绝对路径）
     * @param objFileName
     *            压缩后的文件名
     * @throws Exception
     */
    public static void createZip(String sourceDir, String targetFile) throws Exception {
        createZip(new File(sourceDir), new File(targetFile));
    }

    /**
     * 解压缩源文件到相同目录
     * 
     * @param sourceZip
     * @param outFileName
     * @throws IOException
     */
    public static void releaseZip(String sourceZip) throws IOException {
        File sourceFile = new File(sourceZip);
        if (!sourceFile.exists()) {
            return;
        }
        releaseZip(sourceFile, sourceFile.getParentFile().getAbsoluteFile());
    }

    /**
     * 解压缩源文件到目标文件
     * 
     * @param sourceZip
     * @param outFileName
     * @throws IOException
     */
    public static void releaseZip(String sourceZip, String outFileName) throws IOException {
        releaseZip(new File(sourceZip), new File(outFileName));
    }

    /**
     * 解压缩源文件到目标文件
     * 
     * <p/>
     * 
     * @throws Exception
     */
    public static void releaseZip(File sourceZip, File outFileName) throws IOException {
        // 源文件必须存在
        if (!sourceZip.exists()) {
            return;
        }
        // 目标必须是一个文件夹
        if (!outFileName.isDirectory()) {
            return;
        }
        ZipFile zfile = new ZipFile(sourceZip);
        Enumeration<?> zList = zfile.entries();
        ZipEntry ze = null;
        while (zList.hasMoreElements()) {
            // 从ZipFile中得到一个ZipEntry
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                continue;
            }
            // 以ZipEntry为参数得到一个InputStream，并写到OutputStream中
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(outFileName.getAbsolutePath(), ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            os.write(buffer);
            is.close();
            os.close();
            logger.debug("Extracted: " + ze.getName());
        }
        zfile.close();
    }

    /**
     * 取得指定目录下的所有文件列表，包括子目录.
     * 
     * @param baseDir
     *            File 指定的目录
     * @return 包含java.io.File的List
     */
    public static List<File> getSubFiles(File baseDir) {
        List<File> ret = new ArrayList<File>();
        // File base=new File(baseDir);
        File[] tmp = baseDir.listFiles();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].isFile()) {
                ret.add(tmp[i]);
            }
            if (tmp[i].isDirectory()) {
                ret.addAll(getSubFiles(tmp[i]));
            }
        }
        return ret;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件
     * 
     * @param baseDir
     *            指定根目录
     * @param absFileName
     *            相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    private static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        // System.out.println(dirs.length);
        File ret = new File(baseDir);
        // System.out.println(ret);
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, dirs[dirs.length - 1]);
        return ret;
    }

    /**
     * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.
     * 
     * @param baseDir
     *            java.lang.String 根目录
     * @param realFileName
     *            java.io.File 实际的文件名
     * @return 相对文件名
     */
    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        String ret = real.getName();
        while (true) {
            real = real.getParentFile();
            if (real == null)
                break;
            if (real.equals(base))
                break;
            else {
                ret = real.getName() + "/" + ret;
            }
        }
        return ret;
    }

    public static void main(String args[]) {
        try {
            // ZipUtils.releaseZip("C:/Users/diy/Desktop/ziptest.zip", "C:/Users/diy/Desktop/");
            // ZipUtils.releaseZip("C:/Users/diy/Desktop/ziptest.zip");
            // ZipUtils.createZip("F:/技术相关文档/markdown/", "C:/Users/diy/Desktop/ziptest.zip");
            // ZipUtils.createZip("F:/技术相关文档/markdown/语法.txt", "C:/Users/diy/Desktop/ziptest.zip");
            ZipUtils.createZip(new File("C:/Users/diy/Desktop/ziptest.zip"), new File("F:/技术相关文档/markdown/语法.txt"), new File("C:/Users/diy/Desktop/04_avatar_middle.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
