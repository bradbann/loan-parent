package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.model.statistic.UserStatisticModel;

@Data
public class UserStatisVo extends UserStatisticModel {
    String statisDate;
    String agencyName;
    String vestName;
    String orderRate = "0.00";//提单率
    String payRate = "0.00";//注册下款率
    String calcDate;

    public void calcRate(UserStatisVo vo) {
        if (vo.getRegisterCount() > 0) {
            Double registerCount = vo.getRegisterCount() / 100D;

            vo.setOrderRate(FormatUtil.formatDouble2(vo.getOrderCount() / registerCount));
            vo.setPayRate(FormatUtil.formatDouble2(vo.getPayCount() / registerCount));
        }

    }

}
