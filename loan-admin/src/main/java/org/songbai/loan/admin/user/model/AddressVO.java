package org.songbai.loan.admin.user.model;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/12/18 7:22 PM
 */
@Data
public class AddressVO {
    private String id;
    private String name;
    private String address;
    private String fullAddress;
    private String zipCode;
    private String phoneNumber;
    private Integer isDefault;
    private String concatName;//通信录名称
    private String kinsfolkName;//直接/间接联系人名称
}
