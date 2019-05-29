package org.songbai.loan.admin.admin.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.dao.AdminDictionaryDao;
import org.songbai.loan.admin.admin.model.AdminDictionaryModel;
import org.songbai.loan.admin.admin.service.AdminDictionaryService;
import org.songbai.cloud.basics.mvc.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDictionaryServiceImpl implements AdminDictionaryService {
    @Autowired
    private AdminDictionaryDao dictionaryDao;

    @Override
    public void saveDictionary(AdminDictionaryModel dictionaryModel) {
        dictionaryDao.save(dictionaryModel);
    }

    @Override
    public void updateDictionary(AdminDictionaryModel dictionaryModel) {
        dictionaryDao.update(dictionaryModel);
    }

    @Override
    public Page<AdminDictionaryModel> findDictionaryByPage(AdminDictionaryModel dictionaryModel, Integer page, Integer pageSize) {
        Integer offset = page > 0 ? page * pageSize : 0;
        List<AdminDictionaryModel> list = dictionaryDao.findByPage(dictionaryModel, offset, pageSize);
        Integer rows = dictionaryDao.findRows(dictionaryModel);
        Page<AdminDictionaryModel> dictionaryPage = new Page<>(page, pageSize, rows);
        dictionaryPage.setData(list);
        return dictionaryPage;
    }

    @Override
    public List<AdminDictionaryModel> queryDicToDropDown(AdminDictionaryModel dictionaryModel) {
        return dictionaryDao.queryDicToDropDown(dictionaryModel);
    }

    @Override
    public void deleteDictionaryByIds(String ids) {
        dictionaryDao.deleteDictionaryById(ids);
    }

    @Override
    public List<AdminDictionaryModel> findDictionaryByCode(String type, String code) {
        return dictionaryDao.findDictionaryByCode(type,code);
    }

}
