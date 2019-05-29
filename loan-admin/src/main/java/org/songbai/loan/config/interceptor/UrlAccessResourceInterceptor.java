package org.songbai.loan.config.interceptor;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 薛松 on 2018/03/26.
 */
public class UrlAccessResourceInterceptor extends HandlerInterceptorAdapter {

    private static final String PATH_SPLIT = "/";

    private static Logger logger = (Logger) LoggerFactory.getLogger(UrlAccessResourceInterceptor.class);

    private final String loginUrl = "/login/verification.do";

    private AntPathMatcher matcher = new AntPathMatcher();


    private final List<String> NO_LOGIN_URL = Arrays.asList(
            loginUrl,
            "/login/safe_checkUserValid.do",
            "/login/imgCode.do",
            "/login/sendSmsCode.do",
            "/finance/changJieNotify.do",
            "/finance/yiBaoNotify/*.do",
            "/login/responseRedirect.do",
            "/agency/safe_getAgencyName.do"
    );

    private AdminUserHelper adminUserHelper;
    private ApplicationContext applicationContext;


    public UrlAccessResourceInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        register(request);

        String path = request.getRequestURI();
        if (isNotNeedLoin(path,request.getContextPath())) {
            return true;
        }
        AdminUserModel user = adminUserHelper.getAdminUser(request);
        if (user != null) {
            adminUserHelper.expireUserInfo(request);
            List<String> urlAccess = (List<String>) adminUserHelper.getAccess(request);
//            List<String> urlAccess = (List<String>) session.getAttribute("urlAccess");
            String userName = user.getName();
            if (checkPermission(urlAccess, path, request.getContextPath())) {
                logger.info("用户{} 正在访问  {}", userName, path);
                return true;
            } else {
                String environment = applicationContext.getEnvironment().getActiveProfiles()[0];
                logger.info("云平台环境{},用户{} 正在试图访问未具权限的接口  {}", environment, userName, path);
                if (environment.equals("dev")) return true;
                throw new BusinessException(RespCode.PERMISSION_DENY);
            }
//            return true;
        } else {
            logger.error("2-=====error :");
//            return true;
            throw new BusinessException(RespCode.AUTH_NOT_AUTH);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    /**
     * 检查权限 过滤逻辑 1：不是所有都要过滤，只有配置了URL权限的过滤，这样就可以不是所有的后台方法都要配置权限，
     * 问题就是万一过滤器没有拦截需要拦截的方法可能性会比较高一点 2：选中过滤的方法中会验证登录的用户是否有盖方法的访问权限，如果有放行，没有返回404
     *
     * @return
     */
    private boolean checkPermission(List<String> grant, String path, String contextPath) {

        String profile = applicationContext.getEnvironment().getProperty("spring.profiles.active");

        if (StringUtil.isNotEmpty(profile) && "dev".equals(profile)) {
            return true;
        }

        return path.contains("safe_") || internalCheck(grant, path, contextPath);
    }


    private boolean internalCheck(List<String> resources, String path, String contextPath) {

        String realPath = getRealPath(path, contextPath);

        if (resources != null && resources.size() > 0) {
            for (String resource : resources) {

                String realResourcePath = getRealPath(resource, contextPath);

                if (matcher.isPattern(realResourcePath)) {
                    if (matcher.match(realResourcePath, realPath)) {
                        return true;
                    }
                }

                if (realPath.equals(realResourcePath) || realPath.endsWith(realResourcePath)) {
                    return true;
                }
            }
        }
        return false;
    }


    private String getRealPath(String path, String contextPath) {

        if (contextPath == null) {
            return path;
        }

        if (!path.startsWith(PATH_SPLIT)) {
            path = PATH_SPLIT + path;
        }

        if (!contextPath.startsWith(PATH_SPLIT)) {
            contextPath = PATH_SPLIT + contextPath;
        }

        return path.startsWith(contextPath) ? path.substring(contextPath.length()) : path;
    }

    private void register(HttpServletRequest request) {
        if (adminUserHelper == null) {
            try {
                if (applicationContext != null) {
                    adminUserHelper = applicationContext.getBean(AdminUserHelper.class);
                }
            } catch (BeansException e) {
                //Ignore
            }
        }
        if (adminUserHelper != null) {
            adminUserHelper.register(request);
        }

    }


    private boolean isNotNeedLoin(String url,String contextPath) {

        url = getRealPath(url,contextPath);

        for (String path : NO_LOGIN_URL) {


            if (matcher.isPattern(path)) {
                if (matcher.match(path, url)) {
                    return true;
                }
            }

            if (path.equals(url) || url.endsWith(path)) {
                return true;
            }
        }
        return false;

    }
}
