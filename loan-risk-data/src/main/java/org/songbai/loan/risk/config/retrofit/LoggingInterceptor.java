package org.songbai.loan.risk.config.retrofit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;


@Slf4j
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        log.info(String.format("发送请求：%s%n请求头：%s", request.url(), request.headers()));
        Response response = chain.proceed(request);
        return response;
    }
}
