package org.songbai.loan.admin.user.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserReportDao {


    Map<String, Object> selectUserDataTask(@Param("userThirdId") String userThirdId, @Param("sources") String sources);

}
