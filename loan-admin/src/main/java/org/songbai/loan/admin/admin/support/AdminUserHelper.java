package org.songbai.loan.admin.admin.support;


import org.apache.commons.collections.CollectionUtils;
import org.songbai.cloud.basics.encrypt.PasswordEncryptUtil;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.CookieKit;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.model.AdminPageElementResourceModel;
import org.songbai.loan.admin.admin.model.AdminSecurityResourceModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.songbai.loan.admin.agency.service.AgencyService;
import org.songbai.loan.model.agency.AgencyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AdminUserHelper {
    public static final String REDIS_SESSION_KEY = "admin:session:";

    public static final String COOKIE_SESSION_KEY1 = "s1";
    public static final String COOKIE_SESSION_KEY2 = "s2";
    public static final int COOKIE_REDIS_EXPIRY = 2 * 60 * 60;
    public static final int COOKIE_SESSION_EXPIRY = 10 * 60 * 60;


    public static final String SESSION_TOKEN = "login_token";
    public static final String SESSION_USER = "user";
    public static final String SESSION_ACCESS = "allUrlAccess";
    public static final String SESSION_MENU = "menu";
    public static final String SESSION_AGENCY = "agency";

    @Autowired
    AdminUrlAccessResourceService adminUrlAccessResourceService;

    @Autowired
    AdminMenuResouceService adminMenuResouceService;

    @Autowired
    AgencyService agencyService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void login(HttpServletRequest request, HttpServletResponse response, AdminUserModel userModel) {


        String token = produceToken(request, response, userModel);

        redisTemplate.opsForHash().put(REDIS_SESSION_KEY + token, SESSION_USER, userModel);
        redisTemplate.opsForHash().put(REDIS_SESSION_KEY + token, SESSION_ACCESS, requireUserResource(userModel));
        redisTemplate.opsForHash().put(REDIS_SESSION_KEY + token, SESSION_MENU, requireUserMenu(userModel));
        redisTemplate.opsForHash().put(REDIS_SESSION_KEY + token, SESSION_AGENCY, requireAgency(userModel));
        long a = redisTemplate.getExpire(REDIS_SESSION_KEY + token);
        if (a <= 0) {
            redisTemplate.expire(REDIS_SESSION_KEY + token, COOKIE_REDIS_EXPIRY, TimeUnit.SECONDS);
        }

        request.setAttribute(SESSION_USER, userModel);
        request.setAttribute(SESSION_TOKEN, token);
    }


    public void logout(HttpServletRequest request, HttpServletResponse response) {

        redisTemplate.delete(REDIS_SESSION_KEY + request.getAttribute(SESSION_TOKEN));

        addCookie(response, COOKIE_SESSION_KEY1, "", 0);
        addCookie(response, COOKIE_SESSION_KEY2, "", 0);

    }

    public void register(HttpServletRequest request) {
        String token1 = CookieKit.getCookieAttr(COOKIE_SESSION_KEY1, request);
        String token2 = CookieKit.getCookieAttr(COOKIE_SESSION_KEY2, request);

        if (StringUtil.isEmpty(token1) || StringUtil.isEmpty(token2)) {
            return;
        }

        String realToken2 = PasswordEncryptUtil.digest(token1, HeaderKit.getClientIp(request), 2);

        if (token2.equalsIgnoreCase(realToken2)) {

            request.setAttribute(SESSION_TOKEN, token1);

            AdminUserModel userModel = (AdminUserModel) redisTemplate.opsForHash().get(REDIS_SESSION_KEY + token1, SESSION_USER);
            request.setAttribute(SESSION_USER, userModel);
        }
    }


    public AgencyModel getAgency() {
        return getAgency(getRequest());
    }

    public AgencyModel getAgency(HttpServletRequest request) {

        String token = (String) request.getAttribute(SESSION_TOKEN);

        return (AgencyModel) redisTemplate.opsForHash().get(REDIS_SESSION_KEY + token, SESSION_AGENCY);
    }

    public Integer getAgencyId() {
        return getAgencyId(getRequest());
    }

    public Integer getAgencyId(HttpServletRequest request) {
        return getAdminUser(request).getDataId();
    }

    public List<String> getAccess() {

        return getAccess(getRequest());
    }

    public List<String> getAccess(HttpServletRequest request) {
        String token = (String) request.getAttribute(SESSION_TOKEN);


        return (List<String>) redisTemplate.opsForHash().get(REDIS_SESSION_KEY + token, SESSION_ACCESS);
    }

    public AdminUserModel getAdminUser() {
        return getAdminUser(getRequest());
    }

    public AdminUserModel getAdminUser(HttpServletRequest request) {
        String token = (String) request.getAttribute(SESSION_TOKEN);
        return (AdminUserModel) redisTemplate.opsForHash().get(REDIS_SESSION_KEY + token, SESSION_USER);
    }

    public Integer getAdminUserId() {
        return getAdminUser(getRequest()).getId();
    }

    public Integer getAdminUserId(HttpServletRequest request) {

        return getAdminUser(request).getId();
    }


    public List<AdminMenuResourceModel> getMenu(HttpServletRequest request) {

        String token = (String) request.getAttribute(SESSION_TOKEN);


        return (List<AdminMenuResourceModel>) redisTemplate.opsForHash().get(REDIS_SESSION_KEY + token, SESSION_MENU);
    }

    private String produceToken(HttpServletRequest request, HttpServletResponse response, AdminUserModel userModel) {


        String token1 = userModel.getId() + userModel.getUserAccount() + System.currentTimeMillis();
        token1 = PasswordEncryptUtil.digest(token1, HeaderKit.getUserAgent(request), 1);


        String token2 = PasswordEncryptUtil.digest(token1, HeaderKit.getClientIp(request), 2);

        addCookie(response, COOKIE_SESSION_KEY1, token1, COOKIE_SESSION_EXPIRY);
        addCookie(response, COOKIE_SESSION_KEY2, token2, COOKIE_SESSION_EXPIRY);

        return token1;
    }

    private void addCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie2 = new Cookie(key, value);
        cookie2.setHttpOnly(true);
        cookie2.setMaxAge(maxAge);
        cookie2.setPath("/");
        response.addCookie(cookie2);
    }


    /**
     * 用户菜单权限
     */
    private List<String> requireUserResource(AdminUserModel userModel) {
        List<AdminSecurityResourceModel> adminSecurityResourceModels_all = null;
        List<AdminSecurityResourceModel> resourceList = new ArrayList<>();
        if (userModel.getId().equals(0)) {
            adminSecurityResourceModels_all = adminUrlAccessResourceService.getAllByMenuIdByCategoryForSuperMan(AdminPageElementResourceModel.CATEGORY);
            return handleUrlAccessList(adminSecurityResourceModels_all);
        } else if (userModel.getRoleType() == 1) {//代理管理员
            adminSecurityResourceModels_all = adminUrlAccessResourceService.getAllByMenuIdByCategoryAndDeptId(AdminPageElementResourceModel.CATEGORY, null, userModel.getDataId(), 1);
        } else if (userModel.getIsManager() == 1) {//部门管理员
            adminSecurityResourceModels_all = adminUrlAccessResourceService.getAllByMenuIdByCategoryAndDeptId(AdminPageElementResourceModel.CATEGORY, userModel.getDeptId(), userModel.getDataId(), 0);
        }
        if (CollectionUtils.isNotEmpty(adminSecurityResourceModels_all))
            resourceList.addAll(adminSecurityResourceModels_all);

        adminSecurityResourceModels_all = adminUrlAccessResourceService.getAllByMenuIdByCategory(AdminPageElementResourceModel.CATEGORY, userModel.getId());
        for (AdminSecurityResourceModel model : adminSecurityResourceModels_all) {
            if (!this.hasUrlAccessContent(resourceList, model)) {
                resourceList.add(model);
            }
        }


        return handleUrlAccessList(resourceList);
    }

    private boolean hasUrlAccessContent(List<AdminSecurityResourceModel> resourceList, AdminSecurityResourceModel adminSecurityResourceModel) {
        for (AdminSecurityResourceModel model : resourceList) {
            if (model.getId().intValue() == adminSecurityResourceModel.getId().intValue()
                    || resourceList.contains(adminSecurityResourceModel)) {
                return true;
            }
        }
        return false;
    }

    private List<String> handleUrlAccessList(List<AdminSecurityResourceModel> adminSecurityResourceModels_all) {
        List<String> urlAccessList_all = new ArrayList<>();
        for (AdminSecurityResourceModel model : adminSecurityResourceModels_all) {
            if (model.getUrl() != null) {
                String urlAccess[] = model.getUrl().split("\\|");
                urlAccessList_all.addAll(Arrays.asList(urlAccess));
            }
        }
        return urlAccessList_all;
    }

    private List<AdminMenuResourceModel> requireUserMenu(AdminUserModel userModel) {
        List<AdminMenuResourceModel> menu = adminMenuResouceService.getMenuPedigreeByActorId(userModel);

        return menu;
    }


//    private AgencyModel requireAgency(AdminUserModel userModel) {
//        AgencyModel model = agencyService.findById(userModel.getDataId());
//
//        return model;
//    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    private AgencyModel requireAgency(AdminUserModel userModel) {

        return agencyService.findById(userModel.getDataId());
    }

    public void expireUserInfo(HttpServletRequest request) {
        String token = (String) request.getAttribute(SESSION_TOKEN);
        if (redisTemplate.hasKey(REDIS_SESSION_KEY + token))
            redisTemplate.expire(REDIS_SESSION_KEY + token, COOKIE_REDIS_EXPIRY, TimeUnit.SECONDS);

    }
}
