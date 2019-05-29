package org.songbai.loan.admin.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.cloud.basics.helper.upload.ImageHelper;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/upload")
@LimitLess
public class UploadController {
    final String getAliyunStream = "/admin/upload/innerGetFileStream.do?objectKey=";
    private Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    ImageHelper imageHelper;
    @Autowired
    AliyunOssHelper aliyunOssHelper;

    @RequestMapping(value = "/image", method = {RequestMethod.POST})
    public Response uploadImage(String picture, String suffix) {
        logger.info("调用image");
        Assert.notNull(picture, "图片不能为空");

        suffix = StringUtils.trimToNull(suffix);
        try {
            return Response.success(internalUploadImage(picture, suffix));
        } catch (Exception ex) {
            logger.error("uploadImage>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }
//        return Response.error();
    }

    @RequestMapping(value = "/images", method = {RequestMethod.POST})
    public Response uploadImages(String picture, String suffix) {
        Assert.notNull(picture, "参数异常");
        List<String> resultList = new ArrayList<>();
        suffix = StringUtils.trimToNull(suffix);
        try {
            String[] strings = StringUtils.split(picture, ";,");
            if (strings == null || strings.length == 0) {
                throw new BusinessException(UserRespCode.UPLOAD_DATA_NULL);
            }
            for (String string : strings) {
                resultList.add(internalUploadImage(string, suffix));
            }
        } catch (BusinessException ex) {
            logger.error("uploadImages>>>> ", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("uploadImages>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }
        return Response.success(resultList);
    }

    @RequestMapping(value = "/fileEncode", method = {RequestMethod.POST})
    public Response uploadFileBase64(String context, String fileName) {
        try {
            String timeKey = generateDateKey();
            String objectKey = timeKey + "/" + getUserSign() + "/" + fileName;
            return Response.success(aliyunOssHelper.saveInputStreamToAli(objectKey, context));
        } catch (Exception ex) {
            logger.error("uploadFileBase64>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }

    }

    @PostMapping("/file")
    public Response uploadFile(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            return Response.success(internalUpload(file));
        } catch (Exception ex) {
            logger.error("uploadFile>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }

    }

    @RequestMapping(value = "/files")
    public Response uploadFiles(HttpServletRequest request) {
        try {
            if (request instanceof MultipartRequest) {
                MultipartRequest realReq = (MultipartRequest) request;

                if (realReq.getFileMap() == null || realReq.getFileMap().size() == 0) {
                    throw new BusinessException(UserRespCode.UPLOAD_DATA_NULL);
                }

                Map<String, String> param = new HashMap<>();

                realReq.getFileMap().forEach((k, v) -> {
                    String url = internalUpload(v);
                    param.putIfAbsent(k, url);
                });
            }
        } catch (BusinessException ex) {
            logger.error("uploadImages>>>> ", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("uploadFiles>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }

        return Response.error();
    }

    @PostMapping("/innerUploadFile")
    public String innerUploadFile(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String dateKey = generateDateKey();
            String objectKey = dateKey + suffix;
            String result = aliyunOssHelper.innerSaveInputStream(objectKey, file);
            logger.info("result:{}", result);
            return getAliyunStream + result;
        } catch (Exception ex) {
            logger.error("uploadFile>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }

    }

    @GetMapping("/innerGetFileStream")
    @LimitLess
    public void innerGetFileStream(String objectKey, HttpServletResponse response) {
        try {
            aliyunOssHelper.innerGetFileStream(response, objectKey);
        } catch (Exception ex) {
            logger.error("uploadFile>>>> {}", ex);
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }

    }


    private String internalUpload(MultipartFile file) {
        Assert.notNull(file, "file对象不能为空");
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String dateKey = generateDateKey();
        String objectKey = dateKey + suffix;
        logger.info("objectKey:{}", objectKey);
        String s = aliyunOssHelper.saveInputStreamToAli(objectKey, file);
        logger.info("result:{}", s);
        return aliyunOssHelper.saveInputStreamToAli(objectKey, file);
    }

    private String generateDateKey() {
        Date date = new Date();
        String month = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("yyyyMM"));
        String days = SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1);
        String time = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("HHmmssSSS"));
        return month + "/" + days + "/" + time;
    }

    public static void main(String[] args) {
//    	201811/20181106/195509382
//    	201811/20181106/200330558
//    	201811/20181106/200339510
//    	201811/20181106/200349392
		System.out.println(new UploadController().generateDateKey());
	}
    private String getUserSign() {

        Integer userId = UserUtil.getUserId();

        return userId == null ? "user" : userId + "";
    }


    private String internalUploadImage(String picture, String suffix) {
        String imagePrefix = getUserSign() + "i";
        String prefix = generateDateKey() + "/" + imagePrefix;

        return imageHelper.dataPic(picture, prefix, suffix);
    }

}


