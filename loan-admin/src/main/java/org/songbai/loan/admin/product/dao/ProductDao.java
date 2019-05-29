package org.songbai.loan.admin.product.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.product.model.ProductModelVO;
import org.songbai.loan.model.loan.ProductModel;

import java.util.List;

public interface ProductDao extends BaseMapper<ProductModel> {


    List<ProductModelVO> findProductList(@Param("agencyId") Integer agencyId, @Param("status") Integer status);

    int findStartProductBy(@Param("groupId") Integer groupId);

    List<ProductModelVO> findProductSelected(@Param("agencyId") Integer agencyId);

}
