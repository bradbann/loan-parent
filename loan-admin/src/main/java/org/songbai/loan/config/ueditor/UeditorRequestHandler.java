package org.songbai.loan.config.ueditor;

import org.springframework.web.HttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UeditorRequestHandler implements HttpRequestHandler {


//    @RequestMapping({"safe_controller", "controller"})
//    public void controller(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        request.setCharacterEncoding("utf-8");
//        response.setHeader("Content-Type", "text/html");
//
//        String rootPath = request.getServletContext().getRealPath("/");
//
//        response.getWriter().write(new ActionEnter(request, rootPath).exec());
//
//    }
//
//
//    @RequestMapping({"safe_config", "config"})
//    public void config(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        request.setCharacterEncoding("utf-8");
//        response.setHeader("Content-Type", "text/html");
//
//        String rootPath = request.getServletContext().getRealPath("/");
//
//        response.getWriter().write(new ActionEnter(request, rootPath).exec());
//    }


    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "text/html");

        String rootPath = request.getServletContext().getRealPath("/");

        response.getWriter().write(new ActionEnter(request, rootPath).exec());
    }
}
