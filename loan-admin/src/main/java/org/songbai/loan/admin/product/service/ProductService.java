package org.songbai.loan.admin.product.service;

import org.songbai.loan.admin.product.model.ProductModelVO;
import org.songbai.loan.model.loan.ProductModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 4:04 PM
 */
public interface ProductService {
    void insertProduct(ProductModel model);

    void deleteProduct(Integer id, Integer agencyId);


    void updateProductModelStatus(Integer id, Integer status, Integer agencyId);

    List<ProductModelVO> findProductList(Integer agencyId, Integer status);

    void updateProductModelDefault(Integer id, Integer isDefault, Integer agencyId);

    List<ProductModelVO> findProductSelected(Integer agencyId);

}
