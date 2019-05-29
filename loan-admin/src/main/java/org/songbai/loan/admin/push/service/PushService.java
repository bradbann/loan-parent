package org.songbai.loan.admin.push.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.push.model.vo.PushSenderVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.sms.PushSenderModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/14 11:25 AM
 */
public interface PushService {
    void insertPushSender(PushSenderModel model);


    Page<PushSenderVO> findPushSenderList(PageRow page, Integer currentAgencyId);

    void deletePushSender(Integer id, Integer agencyId);

    void updatePushSender(PushSenderModel model);

    List<PushSenderModel> findPushSenderSelected(Integer agencyId);

}
