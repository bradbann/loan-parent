package org.songbai.loan.admin.product.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.product.model.ProductGroupVo;
import org.songbai.loan.model.loan.ProductGroupModel;

import java.util.List;

public interface ProductGroupDao extends BaseMapper<ProductGroupModel> {


    List<ProductGroupVo> findProductGroupList(@Param("agencyId") Integer agencyId);

    ProductGroupModel findGroupById(@Param("groupId") Integer groupId, @Param("agencyId") Integer agencyId);

    List<ProductGroupModel> findProductGroupSelected(@Param("agencyId") Integer agencyId);

}
