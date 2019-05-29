package org.songbai.loan.user.news.service.impl;

import org.apache.commons.lang3.time.FastDateFormat;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.model.news.UserFeedbackModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.news.dao.FeedBackDao;
import org.songbai.loan.user.news.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class FeedBackServiceImpl implements FeedBackService {

    @Autowired
    private FeedBackDao feedBackDao;
    @Autowired
    private AliyunOssHelper aliyunOssHelper;
    @Autowired
    private ComUserService userService;

    @Override
    public void commitBack(String content, MultipartFile[] files, Integer vestId) {
        UserFeedbackModel model = new UserFeedbackModel();

        StringBuilder sb = new StringBuilder();
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.getSize() < 1000) {
                    continue;
                } else {
                    String string = file.getOriginalFilename().toLowerCase();
                    String name = generateDateKey() + string.substring(string.lastIndexOf("."));
                    String url;
                    try {
                        url = aliyunOssHelper.saveInputStreamToAli(name, file);
                    } catch (Exception e) {
                        throw new BusinessException("图片上传失败！");
                    }
                    sb.append(url).append(",");
                }
            }
            if (sb.length() > 0) {
                sb.substring(0, sb.length() - 1);
            }
            model.setFeedbackPic(sb.toString());
        }

        Integer userId = UserUtil.getUserId();
        UserModel userModel = userService.selectUserModelById(UserUtil.getUserId());
        if (userModel != null) {
            model.setAgencyId(userModel.getAgencyId());
            model.setName(userModel.getName());
            model.setPhone(userModel.getPhone());
        }

        if (vestId != null) model.setVestId(vestId);
        model.setUserId(userId);
        model.setContent(content);
        model.setFeedbackTime(new Date());
        feedBackDao.insert(model);
    }

    private String generateDateKey() {
        Date date = new Date();
        String month = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("yyyyMM"));
        String days = SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1);
        String time = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("HHmmssSSS"));
        return month + "/" + days + "/" + time;
    }

}
