package org.songbai.loan.config.ueditor.upload;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.loan.config.ueditor.define.AppInfo;
import org.songbai.loan.config.ueditor.define.BaseState;
import org.songbai.loan.config.ueditor.define.State;

import java.io.*;

public class StorageManager {

    private static Logger logger = LoggerFactory.getLogger(StorageManager.class);

    private static AliyunOssHelper aliyunOssHelper;


    public static void init(AliyunOssHelper aliyunOssHelper) {

        StorageManager.aliyunOssHelper = aliyunOssHelper;
    }


    /**
     * 添加阿里云上传图片方法
     *
     * @return
     */
    public static State saveInputStreamToAli(String savePath, InputStream is) {
        try {
            String url = aliyunOssHelper.saveInputStreamToAli(savePath, is);

            State state = new BaseState(true);

            state.putInfo("url", url);

            return state;
        } catch (Exception e) {
            logger.error("save file into aliyun ", e);
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

    public static State saveBinaryFile(byte[] data, String path) {
        File file = new File(path);

        State state = valid(file);

        if (!state.isSuccess()) {
            return state;
        }
        try {
            IOUtils.copy(new ByteArrayInputStream(data), new FileOutputStream(file));
        } catch (IOException ioe) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        state = new BaseState(true, file.getAbsolutePath());
        state.putInfo("size", data.length);
        state.putInfo("title", file.getName());
        return state;
    }

//    public static State saveFileByInputStream(InputStream is, String path,
//                                              long maxSize) {
//        State state = null;
//
//        File tmpFile = getTmpFile();
//        try {
//            IOUtils.copy(is, new FileOutputStream(tmpFile));
//
//            if (tmpFile.length() > maxSize) {
//                tmpFile.delete();
//                return new BaseState(false, AppInfo.MAX_SIZE);
//            }
//
//            state = saveTmpFile(tmpFile, path);
//
//            if (!state.isSuccess()) {
//                tmpFile.delete();
//            }
//
//            return state;
//
//        } catch (IOException e) {
//        }
//        return new BaseState(false, AppInfo.IO_ERROR);
//    }

    public static State saveFileByInputStream(InputStream is, String path) {
        State state = null;

        File tmpFile = getTmpFile();
        try {
            IOUtils.copy(is, new FileOutputStream(tmpFile));

            state = saveTmpFile(tmpFile, path);

            if (!state.isSuccess()) {
                tmpFile.delete();
            }

            return state;
        } catch (IOException e) {
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

    private static File getTmpFile() {
        File tmpDir = FileUtils.getTempDirectory();
        String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
        return new File(tmpDir, tmpFileName);
    }

    private static State saveTmpFile(File tmpFile, String path) {
        State state = null;
        File targetFile = new File(path);

        if (targetFile.canWrite()) {
            return new BaseState(false, AppInfo.PERMISSION_DENIED);
        }
        try {
            FileUtils.moveFile(tmpFile, targetFile);
        } catch (IOException e) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        state = new BaseState(true);
        state.putInfo("size", targetFile.length());
        state.putInfo("title", targetFile.getName());

        return state;
    }

    private static State valid(File file) {
        File parentPath = file.getParentFile();

        if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
            return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
        }

        if (!parentPath.canWrite()) {
            return new BaseState(false, AppInfo.PERMISSION_DENIED);
        }

        return new BaseState(true);
    }
}
