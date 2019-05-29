package org.songbai.loan.service.user.service;

import org.songbai.loan.model.loan.ProductGroupModel;
import org.songbai.loan.model.loan.ProductModel;
import org.springframework.stereotype.Component;

@Component
public interface ComProductService {
    ProductModel getProductInfoById(Integer productId);

    ProductGroupModel getProductGroupByGroupId(Integer groupId);
}
