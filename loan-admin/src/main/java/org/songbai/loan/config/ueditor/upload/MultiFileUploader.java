package org.songbai.loan.config.ueditor.upload;

import org.songbai.loan.config.ueditor.PathFormat;
import org.songbai.loan.config.ueditor.define.AppInfo;
import org.songbai.loan.config.ueditor.define.BaseState;
import org.songbai.loan.config.ueditor.define.FileType;
import org.songbai.loan.config.ueditor.define.State;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MultiFileUploader {

    public static State save(MultipartFile content, Map<String, Object> conf) {

        try {
            if (content.getSize() == 0) {
                return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
            }
            String savePath = (String) conf.get("savePath");
            String originFileName = content.getOriginalFilename();
            String suffix = FileType.getSuffixByFilename(originFileName);

            originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
            savePath = savePath + suffix;

            if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
                return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
            }

            savePath = PathFormat.parse(savePath, originFileName);
            String title = savePath;

            InputStream is = content.getInputStream();

            /**
             * ali上传图片方法
             */
            State storageState = StorageManager.saveInputStreamToAli(savePath, is);

            is.close();

            if (storageState.isSuccess()) {
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", originFileName + suffix);
                storageState.putInfo("title", title);
                // size 的大小设定
                storageState.putInfo("size", content.getSize());
            }


            return storageState;
        } catch (IOException e) {
            //Ignore
        }

        return new BaseState(false, AppInfo.IO_ERROR);
    }


    private static boolean validType(String type, String[] allowTypes) {
        List<String> list = Arrays.asList(allowTypes);

        return list.contains(type);
    }


}
