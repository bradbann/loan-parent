package org.songbai.loan.user.user.model.vo;

import lombok.Data;
import org.songbai.loan.model.user.UserBankCardModel;

/**
 * @author: wjl
 * @date: 2018/12/22 11:02
 * Description:
 */
@Data
public class UserBankCardVo extends UserBankCardModel {

	private String title;
	private String url;
	private String h5Url;
}
