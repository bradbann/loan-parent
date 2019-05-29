package org.songbai.loan.admin.activity.ctrl;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.activity.service.ActivityService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.activity.ActivityModel;
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
 * Date: 2018/12/17 1:41 PM
 */
@RestController
@RequestMapping("/activity")
public class ActivityCtrl {
	@Autowired
	private AdminUserHelper adminUserHelper;
	@Autowired
	private ActivityService activityService;

	public static void main(String[] args) {
		System.out.println(Arrays.asList(StringUtil.split2Int("1,2,2,3,4")));
		System.out.println(Arrays.asList(StringUtil.split2Int(",1,2,2,3,4")));
		System.out.println(Arrays.asList(StringUtil.split2Int(",1,2,2,3,4,")));
		System.out.println(Arrays.asList(StringUtil.split2Int(",1,2,2,3,4,")));

		System.out.println(String.join(",", StringUtil.tokenizeToStringArray(",1,2,2,3,4,")));
	}

	/**
	 * 插入
	 */
//    @Accessible(onlyAgency = true)
	@PostMapping("add")
	public Response add(ActivityModel model, HttpServletRequest request) {
		Assert.hasLength(model.getCode(), LocaleKit.get("common.param.notnull", "code"));
		Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
		Assert.hasLength(model.getPicture(), LocaleKit.get("common.param.notnull", "picture"));
		Assert.hasLength(model.getUrl(), LocaleKit.get("common.param.notnull", "url"));
		Assert.hasLength(model.getRemark(), LocaleKit.get("common.param.notnull", "remark"));
		Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));
		Assert.notNull(model.getVestlist(), LocaleKit.get("common.param.notnull", "vestlist"));
		Assert.notNull(model.getScopes(), LocaleKit.get("common.param.notnull", "scopes"));
		List<Integer> statuss = Arrays.asList(0, 1);
		if (!statuss.contains(model.getStatus())) {
			return Response.success();
		}
		Integer agencyId = adminUserHelper.getAgencyId(request);
		if (agencyId != 0) {
			model.setAgencyId(agencyId);
		}
		if (StringUtil.isNotEmpty(model.getVestlist())) {
			String a = String.join(",", StringUtil.tokenizeToStringArray(model.getVestlist()));
			model.setVestlist("," + a + ",");
		}

		if (StringUtil.isNotEmpty(model.getScopes())) {
			String a = String.join(",", StringUtil.tokenizeToStringArray(model.getScopes()));
			model.setScopes("," + a + ",");
		}

		activityService.addActivity(model);
		return Response.success();
	}

	/**
	 */
	@GetMapping("list")
	public Response list(Integer agencyId, Integer status, PageRow pageRow, HttpServletRequest request) {
		Integer currentAgencyId = adminUserHelper.getAgencyId(request);
		if (currentAgencyId != 0) {
			agencyId = currentAgencyId;
		}
		pageRow.initLimit();
		return Response.success(activityService.activityListByAgencyId(status, agencyId, pageRow));
	}

	/**
	 */
	@GetMapping("detail")
	public Response detail(Integer id, HttpServletRequest request) {
		Integer agencyId = adminUserHelper.getAgencyId(request);
		return Response.success(activityService.activityDetailByAgencyId(id, agencyId));
	}


	/**
	 */
//    @Accessible(onlyAgency = true)
	@PostMapping("delete")
	public Response delete(Integer id, HttpServletRequest request) {
		Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
		Integer agencyId = adminUserHelper.getAgencyId(request);
		activityService.deleteActivity(id, agencyId);
		return Response.success();
	}

	/**
	 */
//    @Accessible(onlyAgency = true)
	@PostMapping("update")
	public Response delete(ActivityModel model, HttpServletRequest request) {
		Assert.notNull(model.getId(), LocaleKit.get("common.param.notnull", "id"));
		Assert.hasLength(model.getCode(), LocaleKit.get("common.param.notnull", "code"));
		Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
		Assert.hasLength(model.getPicture(), LocaleKit.get("common.param.notnull", "picture"));
		Assert.hasLength(model.getUrl(), LocaleKit.get("common.param.notnull", "url"));
		Assert.hasLength(model.getRemark(), LocaleKit.get("common.param.notnull", "remark"));
		Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));
		Assert.notNull(model.getVestlist(), LocaleKit.get("common.param.notnull", "vestlist"));
		Assert.notNull(model.getScopes(), LocaleKit.get("common.param.notnull", "scopes"));

		List<Integer> statuss = Arrays.asList(0, 1);
		if (!statuss.contains(model.getStatus())) {
			return Response.success();
		}

		Integer agencyId = adminUserHelper.getAgencyId(request);
		if (agencyId != 0) {
			model.setAgencyId(agencyId);
		}

		if (StringUtil.isNotEmpty(model.getVestlist())) {
			String a = String.join(",", StringUtil.tokenizeToStringArray(model.getVestlist()));
			model.setVestlist("," + a + ",");
		}

		if (StringUtil.isNotEmpty(model.getScopes())) {
			String a = String.join(",", StringUtil.tokenizeToStringArray(model.getScopes()));
			model.setScopes("," + a + ",");
		}

		activityService.updateActivity(model);
		return Response.success();
	}

	/**
	 */
//    @Accessible(onlyAgency = true)
	@PostMapping("updateStatus")
	public Response delete(Integer id, Integer status, HttpServletRequest request) {
		Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
		Assert.notNull(status, LocaleKit.get("common.param.notnull", "status"));
		List<Integer> statuss = Arrays.asList(0, 1);
		if (!statuss.contains(status)) {
			return Response.success();
		}
		Integer agencyId = adminUserHelper.getAgencyId(request);
		activityService.updateActivityModelStatus(id, status, agencyId);
		return Response.success();
	}

	//    @Accessible(onlyAgency = true)
	@PostMapping(value = {"/pushMsg"})
	public Response pushMsg(Integer id, HttpServletRequest request) {
		Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
		Integer agencyId = adminUserHelper.getAgencyId(request);
		activityService.pushMsg(id, agencyId);
		return Response.success();
	}
}
