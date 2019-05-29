package org.songbai.loan.admin.agency.helper;

import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.model.agency.AgencyModel;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Author: qmw
 * Date: 2018/4/25 下午4:16
 */
@Component
public class AgencyAssertCtrlHelper {


    public void checkAgencyAdd(AgencyModel agencyModel) {
        Assert.notNull(agencyModel.getAgencyName(), LocaleKit.get("common.param.notnull", "agencyName"));
//        Assert.notNull(agencyModel.getAgencyCode(), LocaleKit.get("common.param.notnull", "agencyCode"));
        Assert.notNull(agencyModel.getLinkMan(), LocaleKit.get("common.param.notnull", "linkMan"));
        Assert.notNull(agencyModel.getLinkPhone(), LocaleKit.get("common.param.notnull", "linkPhone"));
        Assert.notNull(agencyModel.getAgencyUrl(), LocaleKit.get("common.param.notnull", "agencyUrl"));
//        Assert.notNull(agencyModel.getAgencyGroup(), LocaleKit.get("common.param.notnull", "agencyGroup"));


    }
}
