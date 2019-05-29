package org.songbai.loan.admin.chase.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.chase.po.ChaseFeedPo;
import org.songbai.loan.admin.chase.vo.ChaseFeedVo;
import org.songbai.loan.model.chase.ChaseFeedModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ChaseFeedService {
    /**
     * 新增催收反馈
     */
    void addChaseFeedBack(ChaseFeedModel chaseFeedModel);

    Page<ChaseFeedVo> getChaseFeedPage(ChaseFeedPo po, List<Integer> deptIds);

    List<ChaseFeedVo> getChaseListByChaseId(String chaseId, Integer agencyId);
}
