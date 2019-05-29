package org.songbai.loan.admin.product.service;

import org.songbai.loan.admin.product.model.ProductGroupVo;
import org.songbai.loan.model.loan.ProductGroupModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 4:04 PM
 */
public interface ProductGroupService {


    void insertProductGroup(ProductGroupModel model);

    List<ProductGroupVo> findProductGroupList(Integer agencyId);

    void deleteProductGroup(Integer id, Integer agencyId);

    void update(ProductGroupModel model);

    List<ProductGroupModel> findProductGroupSelected(Integer agencyId);

}
