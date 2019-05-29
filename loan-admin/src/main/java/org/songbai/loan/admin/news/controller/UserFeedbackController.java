package org.songbai.loan.admin.news.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.news.model.po.UserFeedPo;
import org.songbai.loan.admin.news.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 意见反馈
 *
 * @date 2018年10月31日 15:04:14
 * @description
 */
@RequestMapping("/userFeedback")
@Controller
public class UserFeedbackController {

    @Autowired
    UserFeedbackService userFeedbackService;
    @Autowired
    AdminUserHelper adminUserHelper;

    @RequestMapping(value = "/update")
    @ResponseBody
    public Response updateLiveNotice(Integer id) {
        Assert.notNull(id, "id不能为空");
        userFeedbackService.updateUserFeedback(id);
        return Response.success();
    }

    /**
     * 分页获取
     */
    @RequestMapping(value = "/querypaging")
    @ResponseBody
    public Response queryPagingLiveNotice(UserFeedPo po, HttpServletRequest request) {
        Integer currAgencyId = adminUserHelper.getAgencyId(request);
        if (currAgencyId != 0) {
            po.setAgencyId(currAgencyId);
        }
        po.initLimit();
        String startTime = po.getStartTime();
        String endTime = po.getEndTime();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && startTime.equals(endTime)){
            po.setStartTime(startTime +" 00:00:00");
            po.setEndTime(endTime + " 23:59:59");
        }
        return Response.success(userFeedbackService.qureyPage(po));
    }

}
