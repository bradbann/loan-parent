package org.songbai.loan.user.finance.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayBankCardVO {



    private Integer userId ;
    private String userThridId ;

    private String name;//姓名
    private String idcardNum;//身份证号
    private String bankName;//银行名称
    private String bankCode;//平台的银行卡code
    private String bankCardNum;//银行卡号
    private Integer bankCardType;//银行卡类型，0:不能识别; 1: 借记卡; 2: 信用卡
    private String bankPhone;//银行卡预留手机号
    private String bindPlatform;//绑定的支付平台code
}
