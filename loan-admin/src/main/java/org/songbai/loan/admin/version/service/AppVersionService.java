package org.songbai.loan.admin.version.service;

import org.songbai.loan.admin.version.model.vo.AppVersionVO;
import org.songbai.loan.model.version.AppVersionModel;

import java.util.List;

/**
 * Created by hacfox on 09/10/2017
 */
public interface AppVersionService {

    Integer update(AppVersionModel model);

    Integer getCount(AppVersionVO model);

    Integer addVersion(AppVersionModel model);

    Integer findVersionByAgencyId(Integer agencyId, Integer platform, Integer vestId);

    AppVersionModel findInfoByAgencyIdAndPlatform(Integer agencyId, Integer platform, Integer vestId);

    List<AppVersionVO> findVersionPage(AppVersionVO model, Integer limit, Integer pageSize);
}
