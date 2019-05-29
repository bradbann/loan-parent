package org.songbai.loan.config.ueditor.upload;

import org.songbai.loan.config.ueditor.define.State;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Uploader {
    private HttpServletRequest request = null;
    private Map<String, Object> conf = null;

    public Uploader(HttpServletRequest request, Map<String, Object> conf) {
        this.request = request;
        this.conf = conf;
    }

    public final State doExec() {
        String filedName = (String) this.conf.get("fieldName");
        State state = null;

        if ("true".equals(this.conf.get("isBase64"))) {
            state = Base64Uploader.save(this.request.getParameter(filedName),
                    this.conf);
        } else {

            if (request instanceof MultipartRequest) {

                MultipartRequest multipartRequest = (MultipartRequest) request;

                MultipartFile file = multipartRequest.getFile(filedName);

                state = MultiFileUploader.save(file, this.conf);

            } else {
                state = BinaryUploader.save(this.request, this.conf);
            }


        }

        return state;
    }
}
