package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.ProductGroupModel;

public interface ProductGroupDao extends BaseMapper<ProductGroupModel> {


    ProductGroupModel findProductGroupById(@Param("groupId") Integer groupId, @Param("status") Integer status);

}
