package org.songbai.loan.risk.moxie.magic.service;

/**
 * Created by mr.czh on 2018/11/8.
 */
public interface RiskMoxieMagicService {
    void getMagicReport2(String userName, String mobile, String idcard);



    void getMagicReport(String userName, String mobile, String idcard, String userId);

}
