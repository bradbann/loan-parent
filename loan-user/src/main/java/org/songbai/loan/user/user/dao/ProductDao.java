package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.ProductModel;

import java.util.List;

public interface ProductDao extends BaseMapper<ProductModel> {

	ProductModel findProductByAgencyId(Integer agencyId);

	List<Double> findUserUseProduct(@Param("groupId") Integer groupId, @Param("repayCount") Integer repayCount, @Param("overdueDay") Integer overdueDay);

	ProductModel findProductByAgencyIdAndLoan(@Param("agencyId") Integer agencyId, @Param("loan") Double loan);


}
