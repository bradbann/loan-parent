package org.songbai.loan.admin.admin.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminDictionaryModel;

import java.util.List;

public interface AdminDictionaryDao {
    public void save(AdminDictionaryModel dictionaryModel);

    public void update(AdminDictionaryModel dictionaryModel);

    public List<AdminDictionaryModel> findByPage(@Param("model") AdminDictionaryModel dictionaryModel,
                                                 @Param("offset") Integer offset, @Param("size") Integer size);

    public Integer findRows(@Param("model") AdminDictionaryModel dictionaryModel);

    /**
     * 查询数据字典供下拉选使用(不分页)
     */
    public List<AdminDictionaryModel> queryDicToDropDown(@Param("model") AdminDictionaryModel dictionaryModel);

    void deleteDictionaryById(@Param("id") String id);

    List<AdminDictionaryModel> findDictionaryByCode(@Param("type") String type, @Param("code") String code);

}
