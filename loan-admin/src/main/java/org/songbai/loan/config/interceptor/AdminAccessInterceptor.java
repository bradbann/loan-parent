package org.songbai.loan.config.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.cloud.basics.utils.http.IpUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.admin.support.AgencySecurityHelper;
import org.songbai.loan.config.Accessible;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by YHJ on 2018/03/26.
 */
public class AdminAccessInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger("ACCESS");

    private ApplicationContext applicationContext;

    public AdminAccessInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    AgencySecurityHelper securityHelper;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            Accessible accessible = method.getMethodAnnotation(Accessible.class);

            if (accessible == null) {
                accessible = method.getBeanType().getAnnotation(Accessible.class);
            }

            return internalCheck(request, response, accessible);
        }

        return true;
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        record(request, false);
    }


    private void record(HttpServletRequest request, boolean start) {
        String uri = request.getRequestURI();
        String ip = IpUtil.getIp(request);
        String ua = HeaderKit.getUserAgent(request);

        String name = "";
        String param = paramWrapper(request);


        AdminUserModel userModel = (AdminUserModel) request.getAttribute(AdminUserHelper.SESSION_USER);

        if (userModel != null) {
            name = userModel.getName() + ":" + userModel.getId() + "";
        }

        if (start) {
            logger.info("[{}] > user[{}] access:[{}],params:{}", ip, name, uri, param);
        } else {
            logger.info("[{}] < user[{}] access:[{}],params:{}", ip, name, uri, param);
        }


    }


    private String paramWrapper(HttpServletRequest request) {

        HashMap<String, String> result = new HashMap<>();

        Enumeration<String> enumeration = request.getParameterNames();

        while (enumeration.hasMoreElements()) {
            String k = enumeration.nextElement();
            String v = request.getParameter(k);

            result.put(k, v);
        }
        return JSONObject.toJSONString(result);
    }

    private void writeErr(HttpServletResponse response, Response resp) throws IOException {

        response.setContentType("text/json;charset=UTF-8");

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(JSON.toJSONString(resp));
    }


    private boolean internalCheck(HttpServletRequest request, HttpServletResponse response, Accessible accessible) throws IOException {
        intSecurityHelper();
        if (accessible != null && securityHelper != null) {
            try {
                if (accessible.platform()) {
                    securityHelper.onlyPingtai(request);
                }
                if (accessible.admin()) {
                    securityHelper.onlyAgencyAdmin(request);
                }
                if (accessible.superUser()) {
                    securityHelper.onlySuperUser(request);
                }
                if (accessible.onlyAgency()) {
                    securityHelper.onlyAgency(request);
                }
                if (accessible.onlyAgencyCommon()) {
                    securityHelper.onlyAgencyCommon(request);
                }
            } catch (BusinessException e) {
                writeErr(response, Response.response(e.getCode(), e.getMessage()));

                return false;
            }
        }
        return true;
    }

    private void intSecurityHelper() {
        if (securityHelper == null) {
            try {
                if (applicationContext != null) {
                    securityHelper = applicationContext.getBean(AgencySecurityHelper.class);
                }
            } catch (BeansException e) {
                //Ignore
            }
        }

    }

}
