package org.songbai.loan.service.user.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.ProductGroupModel;
import org.songbai.loan.model.loan.ProductModel;

public interface ComProductDao {
    ProductModel getProductInfoById(@Param("productId") Integer productId);

    ProductGroupModel getProductGroupByGroupId(@Param("groupId") Integer groupId);
}
