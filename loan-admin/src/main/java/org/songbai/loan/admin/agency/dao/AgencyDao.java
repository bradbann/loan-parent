package org.songbai.loan.admin.agency.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminDeptResourceModel;
import org.songbai.loan.admin.agency.po.AgencyPo;
import org.songbai.loan.admin.agency.vo.AgencySearchVO;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.user.UserModel;

import java.util.List;
import java.util.Set;

public interface AgencyDao extends BaseMapper<AgencyModel> {

    void addAgency(@Param("model") AgencyModel agencyModel);

    List<AgencyModel> pageAgency(@Param("agencyId") Integer agencyId, @Param("id") Integer id,
                                 @Param("agencyName") String agencyName, @Param("limit") Integer limit, @Param("size") Integer size,
                                 @Param("account") String account, @Param("linkPhone") String linkPhone, @Param("agencyGroup") Integer agencyGroup,
                                 @Param("disable") Integer disable, @Param("relationCode") String relationCode, @Param("shareStatus") Integer shareStatus);

    Integer pageAgency_count(@Param("agencyId") Integer agencyId, @Param("id") Integer id,
                             @Param("agencyName") String agencyName, @Param("linkPhone") String linkPhone, @Param("agencyGroup") Integer agencyGroup,
                             @Param("disable") Integer disable, @Param("relationCode") String relationCode, @Param("shareStatus") Integer shareStatus);

    void updateAgency(AgencyModel agencyModel);

    Integer findAgencyCount(@Param("agencyName") String agentName, @Param("agencyCode") String agentCode,
                            @Param("identification") String identification, @Param("id") Integer id);

    AgencyModel findById(@Param("id") Integer id);

    List<AgencyModel> findSubAgency(@Param("id") int id);

    List<AgencyModel> findSecAgency(@Param("id") Integer id);

    AgencyModel findHigherAgency(@Param("id") Integer id);

    List<AgencyModel> findLowAgencyBySuperAgencyId(@Param("superId") Integer superId, @Param("agencyGroup") Integer agencyGroup, @Param("relationCode") String relationCode);

    List<AgencyModel> findAllAgencyByStatus(@Param("status") int status);

    void disabledAgencyAccount(@Param("ids") List<Integer> ids, @Param("disable") Integer disable, @Param("superId") Integer superId);

    List<Integer> queryAllSubAgency(@Param("relationCode") String relationCode);

    void updateShareStatus(String[] idArr, Integer shareStatus);

    List<AgencySearchVO> findAgencyList(@Param("username") String username, @Param("relationCode") String relationCode);

    Set<Integer> findUserId(@Param("agencyId") Integer agencyId, @Param("promoterId") Integer promoterId);

    List<Integer> findAgencyLikeParam(@Param("agencyName") String agencyName, @Param("linkMan") String linkMan, @Param("linkPhone") String linkPhone);

    Integer agencyListCount(@Param("po") AgencyPo po);

    List<AgencyModel> agencyList(@Param("po") AgencyPo po,
                                 @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    List<AgencyModel> subAgencyList(@Param("agencyName") String agencyName, @Param("agencyCode") String agencyCode,
                                    @Param("superAgencyId") Integer superAgencyId, @Param("agencyId") Integer agencyId,
                                    @Param("linkMan") String linkMan,
                                    @Param("linkPhone") String linkPhone, @Param("agencyGroup") Integer agencyGroup,
                                    @Param("status") Integer status,
                                    @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer subAgencyListCount(@Param("agencyName") String agencyName, @Param("agencyCode") String agencyCode,
                               @Param("superAgencyId") Integer superAgencyId, @Param("agencyId") Integer agencyId,
                               @Param("linkMan") String linkMan,
                               @Param("linkPhone") String linkPhone, @Param("agencyGroup") Integer agencyGroup,
                               @Param("status") Integer status);

    void disabledSubAgencyAccount(@Param("id") Integer id, @Param("status") Integer status);

    AgencyModel selectBindUser(Integer userId);

    UserModel findAgencyAccount(Integer agencyId);

    void updateUserInfoType(@Param("userId") Integer userId, @Param("value") Integer value);

    List<AgencyModel> selectAllByAgencyId(@Param("id") Integer id);

    Integer countDomainNoCurrentUser(@Param("hosts") String[] hosts);

    void deleteResourceByAgencyId(@Param("agencyId") Integer agencyId);

    void createAdminDeptResource(@Param("list") List<AdminDeptResourceModel> list);

    List<Integer> selectEnableAutoPayAgency();

}
