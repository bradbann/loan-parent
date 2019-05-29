package org.songbai.loan.admin.finance.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.order.dao.FinanceBankDao;
import org.songbai.loan.admin.order.dao.FinancePlatformConfigDao;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.finance.FinancePlatformConfigModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wjl
 * @date: 2018/12/19 16:03
 * Description: 支付平台配置表
 */
@RestController
@RequestMapping("/payPlatformConfig")
public class PayPlatformConfigController {

	@Autowired
	private AdminUserHelper adminUserHelper;
	@Autowired
	private FinanceBankDao financeBankDao;
	@Autowired
	private FinancePlatformConfigDao configDao;
    @Autowired
    private ComAgencyService comAgencyService;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@PostConstruct
	public void initSellId() {
		//查找配置了易宝支付的代理
		List<PlatformConfig> configs = configDao.selectPlatformConfigListByAgencyId(null, null, FinanceConstant.PayPlatform.YIBAO.code);
		configs.forEach(config -> {
			Integer agencyId = config.getAgencyId();
			if (agencyId != null){
				PlatformConfig platformConfig = JSON.parseObject(config.getParam(), PlatformConfig.class);
				String sellId = platformConfig.getSellId();
				redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_SELLID, agencyId, sellId);
			}
		});
	}

	/**
	 * 平台支持的支付列表
	 */
	@GetMapping("/platformList")
	public Response platformList() {
		return Response.success(configDao.selectPlatformList());
	}

	/**
	 * 代理的支付配置列表
	 */
	@GetMapping("/platformConfigList")
	public Response platformConfigList(HttpServletRequest request, Integer agencyId) {//0 平台列表  1 查询
		Integer id = adminUserHelper.getAgencyId(request);
		List<PlatformConfig> list;
		if (id == 0) {
			list = configDao.selectPlatformConfigListByAgencyId(agencyId, null, null);
		} else {
			list = configDao.selectPlatformConfigListByAgencyId(id, null, null);
		}
		if (CollectionUtils.isEmpty(list)) {
			return Response.success(new ArrayList<>());
		}
		for (PlatformConfig config : list) {
			if (StringUtils.isNotBlank(config.getParam())) {
				PlatformConfig platformConfig = JSON.parseObject(config.getParam(), PlatformConfig.class);
				BeanUtil.copyNotNullProperties(platformConfig, config);
			}
			config.setParam(null);
            AgencyModel agency = comAgencyService.findAgencyById(config.getAgencyId());
            if (agency != null) {
                config.setAgencyName(agency.getAgencyName());
            }
        }
		return Response.success(list);
	}

	/**
	 * 代理新增支付配置
	 */
	@PostMapping("/addPlatformConfig")
	public Response addPlatform(PlatformConfig config) {
		config = commonMethod(config);
		FinancePlatformConfigModel platformConfigModel = new FinancePlatformConfigModel();
		BeanUtil.copyNotNullProperties(config, platformConfigModel);
		configDao.insert(platformConfigModel);
		initSellId();
		jmsTemplate.convertAndSend(JmsDest.PAYPLATFORM_CONFIG, "");
		return Response.success();
	}

	/**
	 * 代理修改支付配置
	 */
	@PostMapping("/updatePlatformConfig")
	public Response updatePlatformConfig(PlatformConfig config) {
		config = commonMethod(config);
		FinancePlatformConfigModel platformConfigModel = new FinancePlatformConfigModel();
		BeanUtil.copyNotNullProperties(config, platformConfigModel);
		platformConfigModel.setId(config.getId());
		configDao.updateById(platformConfigModel);
		initSellId();
		jmsTemplate.convertAndSend(JmsDest.PAYPLATFORM_CONFIG, "");
		return Response.success();
	}

	@GetMapping("/selectBankList")
	public Response selectBankList() {
		return Response.success(financeBankDao.selectList(null));
	}

	private PlatformConfig commonMethod(PlatformConfig config) {
		Assert.hasText(config.getPrivateKey(), "私钥不能为空");
		Assert.hasText(config.getPublicKey(), "公钥不能为空");
		Assert.hasText(config.getSellId(), "商户id不能为空");
		Assert.hasText(config.getUrl(), "url不能为空");
		Assert.hasText(config.getStatus() + "", "状态不能为空");
		Assert.hasText(config.getPlatformId() + "", "支付平台id不能为空");

		//根据代理查询当前有没有已经开启的支付通道
		if (config.getStatus() == FinanceConstant.Status.ENABLE.key) {
			List<PlatformConfig> configModelList = configDao.selectPlatformConfigListByAgencyId(config.getAgencyId(), FinanceConstant.Status.ENABLE.key, null);
			if (CollectionUtils.isNotEmpty(configModelList)) {
				throw new BusinessException(AdminRespCode.PLATFORM_ONLY_ONE);
			}
		}
		String param = config.toString();
		config.setPrivateKey(config.getPrivateKey().trim());
		config.setPublicKey(config.getPublicKey().trim());
		config.setSellId(config.getSellId().trim());
		config.setUrl(config.getUrl().trim());
		config.setParam(param);
		config.setBind(FinanceConstant.Status.ENABLE.key);
		config.setType(3);
		return config;
	}
}
