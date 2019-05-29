package org.songbai.loan.config.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.RetryException;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.handler.CustomExceptionHandler;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;


@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class AdminExceptionHandler {

    protected final Log logger = LogFactory.getLog(CustomExceptionHandler.class);

    /**
     * 操作数据库出现异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SQLException.class)
    public Response handleException(SQLException e) {
        logger.error("操作数据库出现异常:", e);
        return Response.response(HttpStatus.INTERNAL_SERVER_ERROR.value(), LocaleKit.get("common.server.db.error"));
    }

    @ExceptionHandler(value = {BusinessException.class})
    @ResponseStatus(HttpStatus.OK)

    public final Response handleBusinessException(BusinessException ex) {
        logger.error("业务异常", ex);

        return Response.response(ex.getCode(), ex.getMessage());
    }


    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.OK)
    public final Response handleArgumenteException(IllegalArgumentException ex) {
        logger.error("请求参数异常", ex);
        return Response.response(HttpStatus.INTERNAL_SERVER_ERROR.value(), LocaleKit.resolverOrGet(ex.getMessage()));
    }


    @ExceptionHandler(value = {RetryException.class})
    @ResponseStatus(HttpStatus.OK)
    public final Response handleRetryException(RetryException ex) {
        logger.error("并发更新异常", ex);
        return Response.response(HttpStatus.INTERNAL_SERVER_ERROR.value(), LocaleKit.get("common.req.retry"));
    }


    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public final Response handleGeneralException(Exception ex) {
        logger.error("其他异常", ex);

        return Response.response(HttpStatus.INTERNAL_SERVER_ERROR.value(), LocaleKit.get("common.server.error"));
    }


}
