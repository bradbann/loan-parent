package org.songbai.loan.risk.moxie.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import retrofit2.Retrofit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class MoxieReportClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoxieReportClient.class);
    protected Retrofit retrofit;


    public MoxieReportClient(String apiBaseUrl, String apiToken) {

        retrofit = createRetrofit(apiToken, apiBaseUrl);
    }


    private Retrofit createRetrofit(final String apiToken, String apiBaseUrl) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{new OkHttpClientFactory.DisableValidationTrustManager()}, new SecureRandom());

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("create retrofit ", e);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(chain -> {
            Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Token " + apiToken).build();
            return chain.proceed(newRequest);

        });

        assert sslContext != null;
        OkHttpClient client = builder
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier((hostname, session) -> true)
                .build();

        return new Retrofit
                .Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(StringConverterFactory.create())
                .client(client)
                .build();
    }


}
