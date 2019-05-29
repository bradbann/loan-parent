//package org.songbai.loan.config.filter;
//
//import ch.qos.logback.classic.Logger;
//import com.alibaba.fastjson.JSON;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.mvc.Response;
//import org.songbai.loan.admin.admin.model.AdminUserModel;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//
///**
// * 后台URL访问拦截器 1：不是后台所有的访问方法都要拦截，有一些后台的方法是没有权限数据的，过滤器不应该拦截此类方法
// *
// * @author wangd
// */
//public class UrlAccessResourceFilter implements Filter {
//
//    Logger logger = (Logger) LoggerFactory.getLogger(UrlAccessResourceFilter.class);
//
//    @Autowired
//    private FilterConfig filterConfig;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        this.filterConfig = filterConfig;
//
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest servletRequest = (HttpServletRequest) request;
//        HttpServletResponse servletResponse = (HttpServletResponse) response;
//        HttpSession session = servletRequest.getSession();
//        String path = servletRequest.getRequestURI();// ||path.indexOf("loginTrading")>-1||path.indexOf("loginAgent")>-1
//        if (path.contains(filterConfig.getInitParameter("loginUrl"))
//                || path.contains("loadForPush")
//                || path.contains("responseRedirect")
//                || path.contains("redirect")
//                || path.contains("mongoIndex")) {// web.xml
//            // 中配置只针对.do进行拦截，如果是登录请求，直接放过。
//            chain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//        AdminUserModel user = (AdminUserModel) session.getAttribute("user");
//        if (user != null) {
//            List<String> urlAccess_all = (List<String>) session.getAttribute("allUrlAccess");
////			List<String> urlAccess = (List<String>) session.getAttribute("urlAccess");
//            String userName = user.getName();
//            if (checkPermission(urlAccess_all, path)) {
//                logger.info("用户{} 正在访问  {}", userName, path);
//                chain.doFilter(servletRequest, servletResponse);
//            } else {
//                logger.info("用户{} 正在试图访问未具权限的接口  {}", userName, path);
//                servletResponse.setStatus(600);
//            }
//        } else {
//            servletResponse.setStatus(503);
//            response.setContentType("text/json;charset=UTF-8");
//            response.getWriter().println(JSON.toJSONString(Response.response(503, "重新登录")));// sendRedirect怎么可以和println同时用，
//        }
//
//    }
//
//    /**
//     * 检查权限 过滤逻辑 1：不是所有都要过滤，只有配置了URL权限的过滤，这样就可以不是所有的后台方法都要配置权限，
//     * 问题就是万一过滤器没有拦截需要拦截的方法可能性会比较高一点 2：选哦过滤的方法中会验证登录的用户是否有盖方法的访问权限，如果有放行，没有返回404
//     *
//     * @param all 所有需要检查的权限
//     */
//    private boolean checkPermission(List<String> all, String path) {
//        if (path.contains("safe_")) {
//            return true;
//        }
//        String path_all = path.substring(0, path.lastIndexOf("/") + 1) + "**";
//        if (all != null && all.size() > 0) {
//            for (String resourceModel : all) {
//                if (path.equals(resourceModel) || path_all.equals(resourceModel) || resourceModel.contains(path)
//                        || resourceModel.contains(path_all)) {
//                    return true;
//                }
//            }
//        }
//
////		if (grant != null && grant.size() > 0) {// 逻辑还需要进化，刚在session中获取不到授权的权限数据时目前是返回404
////			for (String urlAccessResourceModel : grant) {
////				if (path.equals(urlAccessResourceModel) || path_all.equals(urlAccessResourceModel)
////						|| urlAccessResourceModel.contains(path) || urlAccessResourceModel.contains(path_all)) {
////					// 通过
////					return true;
////				}
////			}
////		}
//        // 测试 默认返回true
//        return true;
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
