package org.songbai.loan.admin.product.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.admin.agency.dao.AgencyDao;
import org.songbai.loan.admin.product.dao.ProductDao;
import org.songbai.loan.admin.product.dao.ProductGroupDao;
import org.songbai.loan.admin.product.model.ProductModelVO;
import org.songbai.loan.admin.product.service.ProductService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.ActivityRespCode;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.loan.ProductGroupModel;
import org.songbai.loan.model.loan.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 4:04 PM
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    SpringProperties properties;
    @Autowired
    private AgencyDao agencyDao;
    @Autowired
    private ProductGroupDao groupDao;

    @Override
    public void insertProduct(ProductModel model) {

        AgencyModel agencyModel = agencyDao.selectById(model.getAgencyId());
        if (agencyModel == null) {
            return;
        }
        if (model.getExceedDays() > agencyModel.getBadDebt()) {
            throw new BusinessException(AdminRespCode.EXCEED_DAYS_LESS_BAD_DEBT);
        }

        ProductGroupModel groupModel = groupDao.findGroupById(model.getGroupId(), model.getAgencyId());
        if (groupModel == null) {
            throw new BusinessException(AdminRespCode.PRODUCT_GROUP_NOT_EXIST);
        }

        if (model.getPay() + model.getStamp() != model.getLoan()) {
            throw new BusinessException(AdminRespCode.PRODUCT_MONEY_ERROR);
        }
        EntityWrapper<ProductModel> money = new EntityWrapper<>();
        money.eq("agency_id", model.getAgencyId());
        money.eq("deleted", CommonConst.DELETED_NO);
        money.eq("loan", model.getLoan());
        money.eq("status", CommonConst.YES);
        money.eq("group_id", model.getGroupId());
        Integer moneyCount = productDao.selectCount(money);
        if (moneyCount > 0) {
            throw new BusinessException(AdminRespCode.PRODUCT_LOAN_EXIST);
        }

        if (model.getIsDefault() == CommonConst.YES) {
            EntityWrapper<ProductModel> defaultEw = new EntityWrapper<>();
            defaultEw.eq("agency_id", model.getAgencyId());
            defaultEw.eq("deleted", CommonConst.DELETED_NO);
            defaultEw.eq("is_default", CommonConst.YES);
            defaultEw.eq("status", CommonConst.YES);
            defaultEw.eq("group_id", model.getGroupId());
            Integer existCount = productDao.selectCount(defaultEw);
            if (existCount > 0) {
                throw new BusinessException(AdminRespCode.PRODUCT_DEFAULT_EXIST);
            }
        }
        EntityWrapper<ProductModel> pew = new EntityWrapper<>();
        pew.eq("agency_id", model.getAgencyId());
        pew.eq("deleted", CommonConst.DELETED_NO);
        pew.eq("status", CommonConst.YES);
        Integer existCount = productDao.selectCount(pew);
        if (existCount <= 0) {
            if (model.getIsDefault() == CommonConst.NO) {
                throw new BusinessException(AdminRespCode.PRODUCT_DEFAULT_NOT_EXIST);

            }
        }
        EntityWrapper<ProductModel> ew = new EntityWrapper<>();
        ew.eq("agency_id", model.getAgencyId());
        ew.eq("deleted", CommonConst.DELETED_NO);
        Integer count = productDao.selectCount(ew);
        Integer limit = properties.getInteger("admin.product.limit", 10);

        if (count > limit) {
            throw new BusinessException(AdminRespCode.PRODUCT_OVER_LIMIT, "平台最大支持配置" + limit + "个标的");
        }

        productDao.insert(model);
    }

    @Override
    public void deleteProduct(Integer id, Integer agencyId) {
        ProductModel select = new ProductModel();
        if (agencyId != 0){
            select.setAgencyId(agencyId);
        }
        select.setDeleted(CommonConst.DELETED_NO);
        select.setId(id);
        ProductModel dbProduct = productDao.selectOne(select);
        if (dbProduct == null) {
            return;
        }
        if (CommonConst.YES == dbProduct.getStatus()) {
            throw new BusinessException(ActivityRespCode.ACTIVITY_CAN_NOT_OPT);
        }
        ProductModel update = new ProductModel();
        if (agencyId != 0){
            update.setAgencyId(agencyId);
        }
        update.setId(id);
        update.setDeleted(CommonConst.DELETED_YES);
        productDao.updateById(update);
    }

    @Override
    public void updateProductModelStatus(Integer id, Integer status, Integer agencyId) {
        ProductModel select = new ProductModel();
        if (agencyId != 0){
            select.setAgencyId(agencyId);
        }
        select.setId(id);
        ProductModel dbModel = productDao.selectOne(select);

        if (dbModel == null) {
            return;
        }
        if (status == CommonConst.NO) {
            if (dbModel.getIsDefault() == CommonConst.YES) {
                throw new BusinessException(AdminRespCode.PRODUCT_DEFAULT_NOT_STOP);
            }
        } else {
            EntityWrapper<ProductModel> money = new EntityWrapper<>();
            if (agencyId != 0){
                money.eq("agency_id", agencyId);
            }
            money.eq("deleted", CommonConst.DELETED_NO);
            money.eq("loan", dbModel.getLoan());
            money.eq("status", CommonConst.YES);
            money.ne("id", dbModel.getId());
            money.eq("group_id", dbModel.getGroupId());
            Integer moneyCount = productDao.selectCount(money);
            if (moneyCount > 0) {
                throw new BusinessException(AdminRespCode.PRODUCT_LOAN_EXIST);
            }
        }


        ProductModel update = new ProductModel();
        update.setId(id);
        update.setStatus(status);
        productDao.updateById(update);
    }

    @Override
    public List<ProductModelVO> findProductList(Integer agencyId, Integer status) {

        return productDao.findProductList(agencyId, status);
    }

    @Override
    public void updateProductModelDefault(Integer id, Integer isDefault, Integer agencyId) {

        ProductModel select = new ProductModel();
        if (agencyId != 0){
            select.setAgencyId(agencyId);
        }
        select.setDeleted(CommonConst.DELETED_NO);
        select.setId(id);
        ProductModel dbProduct = productDao.selectOne(select);
        if (dbProduct == null) {
            return;
        }
        if (dbProduct.getIsDefault().equals(isDefault)) {
            return;
        }
        ProductModel update = new ProductModel();

        if (agencyId != 0){
            update.setAgencyId(agencyId);
        }
        update.setId(id);
        update.setIsDefault(isDefault);
        update.setStatus(CommonConst.YES);
        productDao.updateById(update);

        if (isDefault == CommonConst.YES) {
            EntityWrapper<ProductModel> ew = new EntityWrapper<>();
            ew.ne("id", id);
            ProductModel updateOther = new ProductModel();
            updateOther.setIsDefault(CommonConst.NO);
            productDao.update(updateOther, ew);
        }
    }

    @Override
    public List<ProductModelVO> findProductSelected(Integer agencyId) {
        return productDao.findProductSelected(agencyId);
    }
}
