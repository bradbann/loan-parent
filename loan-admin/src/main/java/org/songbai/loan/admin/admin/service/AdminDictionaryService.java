package org.songbai.loan.admin.admin.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminDictionaryModel;
import org.springframework.stereotype.Component;

import java.util.List;

public interface AdminDictionaryService {
    public void saveDictionary(AdminDictionaryModel dictionaryModel);

    public void updateDictionary(AdminDictionaryModel dictionaryModel);

    public Page<AdminDictionaryModel> findDictionaryByPage(AdminDictionaryModel dictionaryModel, Integer page, Integer pageSize);

    /**
     * 查询数据字典供下拉选使用(不分页)
     */
    public List<AdminDictionaryModel> queryDicToDropDown(AdminDictionaryModel dictionaryModel);

    void deleteDictionaryByIds(String ids);

    List<AdminDictionaryModel> findDictionaryByCode(String type, String code);

}
