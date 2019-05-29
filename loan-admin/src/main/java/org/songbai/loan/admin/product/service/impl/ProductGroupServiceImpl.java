package org.songbai.loan.admin.product.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.admin.product.dao.ProductGroupDao;
import org.songbai.loan.admin.product.model.ProductGroupVo;
import org.songbai.loan.admin.product.service.ProductGroupService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.ActivityRespCode;
import org.songbai.loan.model.loan.ProductGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 4:04 PM
 */
@Service
public class ProductGroupServiceImpl implements ProductGroupService {
    @Autowired
    private ProductGroupDao groupDao;
    //@Autowired
    //private ProductDao productDao;


    @Override
    public void insertProductGroup(ProductGroupModel model) {
        groupDao.insert(model);
    }

    @Override
    public List<ProductGroupVo> findProductGroupList(Integer agencyId) {
        return groupDao.findProductGroupList(agencyId);
    }

    @Override
    public void deleteProductGroup(Integer id, Integer agencyId) {
        ProductGroupModel select = new ProductGroupModel();
        if (agencyId != 0){
            select.setAgencyId(agencyId);
        }
        select.setDeleted(CommonConst.DELETED_NO);
        select.setId(id);
        ProductGroupModel dbGroup = groupDao.selectOne(select);
        if (dbGroup == null) {
            return;
        }
        if (CommonConst.YES == dbGroup.getStatus()) {
            throw new BusinessException(ActivityRespCode.ACTIVITY_CAN_NOT_OPT);
        }
        //int count = productDao.findStartProductBy(id);
        //if (count > 0) {
        //    throw new BusinessException(AdminRespCode.PRODUCT_HAS_START);
        //}

        ProductGroupModel update = new ProductGroupModel();
        if (agencyId != 0){
            update.setAgencyId(agencyId);
        }
        update.setId(id);
        update.setDeleted(CommonConst.DELETED_YES);
        groupDao.updateById(update);

    }

    @Override
    public void update(ProductGroupModel model) {
        ProductGroupModel select = new ProductGroupModel();
        if (model.getAgencyId() != null){
            select.setAgencyId(model.getAgencyId());
        }
        select.setDeleted(CommonConst.DELETED_NO);
        select.setId(model.getId());
        ProductGroupModel dbGroup = groupDao.selectOne(select);
        if (dbGroup == null) {
            return;
        }
        //if (model.getStatus() == CommonConst.NO) {
        //    int count = productDao.findStartProductBy(model.getId());
        //    if (count > 0) {
        //        throw new BusinessException(AdminRespCode.PRODUCT_HAS_START);
        //    }
        //}
        groupDao.updateById(model);
    }

    @Override
    public  List<ProductGroupModel> findProductGroupSelected(Integer agencyId) {
        return groupDao.findProductGroupSelected(agencyId);
    }
}
