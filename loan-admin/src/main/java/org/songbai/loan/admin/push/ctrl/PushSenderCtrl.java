package org.songbai.loan.admin.push.ctrl;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.push.service.PushService;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.model.sms.PushSenderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Author: qmw
 * Date: 2019/1/10 3:57 PM
 */
@RestController
@RequestMapping("/pushSender")
public class PushSenderCtrl {
	@Autowired
	private AdminUserHelper adminUserHelper;
	@Autowired
	private PushService pushService;


	@PostMapping("add")
	public Response add(PushSenderModel model, HttpServletRequest request) {
		Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
		Assert.notNull(model.getType(), LocaleKit.get("common.param.notnull", "type"));
		Assert.hasLength(model.getAppId(), LocaleKit.get("common.param.notnull", "appId"));
		Assert.hasLength(model.getAppKey(), LocaleKit.get("common.param.notnull", "appKey"));
		Assert.hasLength(model.getMaster(), LocaleKit.get("common.param.notnull", "master"));
		Assert.hasLength(model.getUrl(), LocaleKit.get("common.param.notnull", "url"));

		Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));

		List<Integer> statuss = Arrays.asList(0, 1);
		if (!statuss.contains(model.getStatus())) {
			return Response.success();
		}

		Integer agencyId = adminUserHelper.getAgencyId(request);

		if (agencyId != 0) {
			model.setAgencyId(agencyId);
		}

		pushService.insertPushSender(model);
		return Response.success();
	}

	/**
	 */
	@GetMapping("list")
	public Response list(PageRow page, Integer agencyId, HttpServletRequest request) {
		Integer currentAgencyId = adminUserHelper.getAgencyId(request);

		if (currentAgencyId == 0) {
			Assert.notNull(agencyId, LocaleKit.get("common.param.notnull", "agencyId"));
			currentAgencyId = agencyId;
		}
		page.initLimit();

		return Response.success(pushService.findPushSenderList(page, currentAgencyId));
	}

	/**
	 */
	@Accessible(onlyAgency = true)
	@PostMapping("delete")
	public Response delete(Integer id, HttpServletRequest request) {
		Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
		Integer agencyId = adminUserHelper.getAgencyId(request);
		pushService.deletePushSender(id, agencyId);
		return Response.success();
	}


	@PostMapping("update")
	public Response updateDefault(PushSenderModel model, HttpServletRequest request) {
		Assert.notNull(model.getId(), LocaleKit.get("common.param.notnull", "id"));
		Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
		Assert.notNull(model.getType(), LocaleKit.get("common.param.notnull", "type"));
		Assert.hasLength(model.getAppId(), LocaleKit.get("common.param.notnull", "appId"));
		Assert.hasLength(model.getAppKey(), LocaleKit.get("common.param.notnull", "appKey"));
		Assert.hasLength(model.getMaster(), LocaleKit.get("common.param.notnull", "master"));
		Assert.hasLength(model.getUrl(), LocaleKit.get("common.param.notnull", "url"));

		Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));

		List<Integer> statuss = Arrays.asList(0, 1);
		if (!statuss.contains(model.getStatus())) {
			return Response.success();
		}

		Integer agencyId = adminUserHelper.getAgencyId(request);

		if (agencyId != 0) {
			model.setAgencyId(agencyId);
		}

		pushService.updatePushSender(model);

		return Response.success();
	}

	/**
	 */
	@GetMapping("selected")
	public Response list(HttpServletRequest request, Integer agencyId) {
		if (agencyId == null) {
			agencyId = adminUserHelper.getAgencyId(request);
		}
		return Response.success(pushService.findPushSenderSelected(agencyId));
	}
}
