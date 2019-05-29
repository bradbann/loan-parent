/**
 *
 */
package org.songbai.loan.model.news;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZY
 */
@Data
@TableName("loan_u_user_feedback")
public class UserFeedbackModel implements Serializable {

    private static final long serialVersionUID = 3105658416182465199L;
    /**
     * 主键
     */
    Integer id;
    private Integer agencyId;//代理code
    /**
     * 反馈内容
     */
    String content;
    /**
     * 用户id
     */
    Integer userId;
    Date feedbackTime;
    /**
     * 反馈图片
     */
    private String feedbackPic;

    private String name;//用户姓名
    private String phone;//手机号
    Integer vestId;//马甲id

}
