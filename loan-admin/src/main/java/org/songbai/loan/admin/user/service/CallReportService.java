package org.songbai.loan.admin.user.service;

import java.util.List;
import java.util.Map;

public interface CallReportService {

    Map<String, Object> callReport(String userId, Integer agencyId, int page, int pageSize);

    List<Object> queryMobileSms(String userId, Integer agencyId, String sendType, int page, int pageSize);

    List<Object> queryVoiceCall(String userId, String dialType, Integer agencyId, int page, int pageSize);
}
