package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.version.MarketModel;

/**
 * Author: qmw
 * Date: 2018/11/9 4:19 PM
 */
public interface MarketDao extends BaseMapper<MarketModel> {
	MarketModel selectMarketIdByKey(String marketKey);
}
