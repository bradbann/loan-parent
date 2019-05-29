package org.songbai.loan.risk.config.retrofit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetrofitBeanFactory {
    private static Map<String, RetrofitBean> resolvableDependencies = new HashMap<>(16);
    private static final int readTimeOut = 30;
    private static final int writeTimeOut = 30;
    private static final int connTimeOut = 30;

//    /**
//     * 获得service服务实体
//     *
//     * @param requiredType
//     * @return
//     * @throws BeansException
//     */
//    public static Object getBean(Class requiredType) throws BeansException {
//        for (Map.Entry<String, RetrofitBean> entrySet : resolvableDependencies.entrySet()) {
//            RetrofitBean retrofitBean = entrySet.getValue();
//            for (Map.Entry<Class, Object> serviceSet : retrofitBean.getService().entrySet()) {
//                if (requiredType == serviceSet.getKey()) {
//                    return serviceSet.getValue();
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 创建service服务实体
     *
     * @param baseUrl
     * @param serviceClass
     */
    public static Object requireRetrofitBean(String baseUrl, Class serviceClass, Class... interceptorClass) {
        if (StringUtils.isEmpty(baseUrl)) {
            return null;
        }
        RetrofitBean retrofitBean = getRetrofitBean(baseUrl, interceptorClass);
        Retrofit retrofit = retrofitBean.getRetrofit();
        Object bean = retrofit.create(serviceClass);
        retrofitBean.getService().put(serviceClass, bean);
        return bean;
    }

    private static RetrofitBean getRetrofitBean(String baseUrl, Class[] interceptorClass) {
        RetrofitBean retrofitBean = resolvableDependencies.get(baseUrl);
        //如果为空设置一个
        if (retrofitBean == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(readTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .readTimeout(connTimeOut, TimeUnit.SECONDS)
                    .addInterceptor(new LoggingInterceptor());
            if (interceptorClass != null && interceptorClass.length > 0) {
                for (Class clazz : interceptorClass) {
                    if (Interceptor.class.isAssignableFrom(clazz)) {
                        try {
                            clientBuilder.addInterceptor((Interceptor) clazz.newInstance());
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(clientBuilder.build())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .build();

            retrofitBean = new RetrofitBean(retrofit);
            resolvableDependencies.put(baseUrl, retrofitBean);
        }
        return retrofitBean;
    }


    static class RetrofitBean {
        Retrofit retrofit;
        Map<Class, Object> service = new HashMap<>();

        RetrofitBean(Retrofit retrofit) {
            this.retrofit = retrofit;
        }

        public Retrofit getRetrofit() {
            return retrofit;
        }

        public Map<Class, Object> getService() {
            return service;
        }

        public void setService(Map<Class, Object> service) {
            this.service = service;
        }
    }


}





