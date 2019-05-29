package org.songbai.loan.admin.version.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.version.model.vo.AppVestVO;
import org.songbai.loan.model.version.AppVestModel;

import java.util.List;

/**
 * Created by mr.czh on 2017/11/20.
 */
public interface AdminVestService {

    /**
     * 分页查找当前渠道下的push消息(新)
     */
    Page<AppVestVO> findByPage(AppVestModel vestModel, Integer page, Integer pageSize);

    /**
     * 新增
     */
    void saveVest(AppVestModel vestModel);

    /**
     * 更新
     *
     * @param vestModel
     * @return
     */
    void updateVest(AppVestModel vestModel);

    void deleteVest(String[] idArr);

    List<AppVestModel> findVestList(AppVestModel model);
}
