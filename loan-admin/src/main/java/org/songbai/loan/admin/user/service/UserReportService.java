package org.songbai.loan.admin.user.service;

import org.songbai.loan.admin.user.model.AddressVO;
import org.songbai.loan.admin.user.model.TradeVO;
import org.songbai.loan.admin.user.model.UserReportVO;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.risk.moxie.carrier.model.ReportDataModel;
import org.songbai.loan.risk.moxie.taobao.model.TaobaoReportModel;

import java.util.List;

public interface UserReportService {
    UserReportVO getReport(String thirdId, Integer agencyId);

    List<UserContactModel> getUserContact(String thirdId, Integer agencyId);

    ReportDataModel getCarrierReport(String thirdId, Integer agencyId);


    TaobaoReportModel getTaobaoReport(String thirdId, Integer agencyId);

    List<AddressVO> getTaobaoAddr(String thirdId, Integer agencyId);

    List<TradeVO> getTaobaoTrade(String thirdId, Integer agencyId);
}
