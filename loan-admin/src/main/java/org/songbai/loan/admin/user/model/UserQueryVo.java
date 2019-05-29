package org.songbai.loan.admin.user.model;

import lombok.Data;
import org.songbai.loan.model.user.UserModel;

/**
 * user 查询扩展类
 * @author wjl
 * @date 2018年10月30日 10:45:03
 * @description
 */
@Data
public class UserQueryVo extends UserModel {

    private static final long serialVersionUID = 1L;

    private String limitStartStart;//限制时间
    private String limitStartEnd;
    private String limitEndStart;
    private String limitEndEnd;
    private String createTimeStart;//创建时间
    private String createTimeEnd;
    private String updateTimeStart;//更新时间
    private String updateTimeEnd;
    private Integer page;
    private Integer pageSize;
    private Integer limit;
    private Integer channelId;//渠道id
    Integer vestId;//马甲id
    String channelCode;
}
