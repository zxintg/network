package com.zxin.network.http;

import android.content.Context;
import com.zxin.network.api.ZXinBaseApi;
import com.zxin.network.interceptor.ZxinBaseInterceptor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class RetrofitHelper {
    private int DEFAULT_TIME_OUT = 10;//超时时间5s
    private int DEFAULT_READ_TIME_OUT = 10;//读取时间
    private int DEFAULT_WRITE_TIME_OUT = 10;//读取时间
    private volatile OkHttpClient mOkHttpClient;
    private Context mContext;

    private ZxinBaseInterceptor[] mInterceptors;

    private Converter.Factory[] mFactorys;

    private Map<String, Object> serviceMap;

    private RetrofitHelper(Context mContext) {
        this.mContext = mContext;
    }

    /*****
     * 添加拦截器
     * @param mInterceptors
     * @param <In>
     */
    private synchronized <In extends ZxinBaseInterceptor> void setIntercepts(In... mInterceptors) {
        this.mInterceptors = mInterceptors;
    }

    /******
     * 创建工厂
     * @param mFactorys
     * @param <Fac>
     */
    private synchronized <Fac extends Converter.Factory> void setFactory(Fac... mFactorys) {
        this.mFactorys = mFactorys;
    }

    private void setDefaultTimeOut(int defaultTimeOut) {
        DEFAULT_TIME_OUT = defaultTimeOut;
    }

    private void setDefaultReadTimeOut(int defaultReadTimeOut) {
        DEFAULT_READ_TIME_OUT = defaultReadTimeOut;
    }

    private void setDefaultWriteTimeOut(int defaultWriteTimeOut) {
        DEFAULT_WRITE_TIME_OUT = defaultWriteTimeOut;
    }

    /*****
     * 创建
     */
    public synchronized void create() {
        if (mOkHttpClient == null) {
            //设置Http缓存
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
            builder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);
            //添加拦截器
            if (mInterceptors != null && mInterceptors.length > 0) {
                for (ZxinBaseInterceptor mInterceptor : mInterceptors) {
                    builder.addInterceptor(mInterceptor);
                }
            }
            mOkHttpClient = builder.build();
        }
    }

    /******
     * 添加
     * @param mBaseUrls 与 mServices 一对一
     * @param <API>
     */
    private <API extends ZXinBaseApi> void addZxinAPIs(String[] mBaseUrls, Class<API>[] mServices) {
        if (mBaseUrls == null || mServices == null || mBaseUrls.length == 0 || mServices.length == 0 || mBaseUrls.length != mServices.length) {
            return;
        }
        int len = mBaseUrls.length;
        if (serviceMap != null) {
            serviceMap = new HashMap<>();
        }
        for (int i = 0; i < len; i++) {
            String key = mBaseUrls[i];
            Class value = mServices[i];
            API api = (API) getZxinAPI(key, value);
            if (serviceMap.containsKey(key) || serviceMap.containsValue(api)) {
                continue;
            }
            serviceMap.put(key, api);
        }
    }

    /******
     * 提供model 获取相应的API
     * @param mBaseUrl
     * @param <API>
     * @return
     */
    public <API extends ZXinBaseApi> API getZxinAPI(String mBaseUrl) {
        if (serviceMap == null || serviceMap.isEmpty() || !serviceMap.containsKey(mBaseUrl)) {
            return null;
        }
        return (API) serviceMap.get(mBaseUrl);
    }

    /****
     * 清除相应的mBaseUrl 的 service
     * @param mBaseUrl
     */
    public void removeZxinAPI(String mBaseUrl) {
        if (serviceMap == null || serviceMap.isEmpty() || !serviceMap.containsKey(mBaseUrl)) {
            return;
        }
        serviceMap.remove(mBaseUrl);
    }

    public void clear() {
        if (serviceMap != null && !serviceMap.isEmpty())
            serviceMap.clear();
        mFactorys = null;
        mInterceptors = null;
        mOkHttpClient =null;
    }

    /****
     * 初始化API
     * @param mBaseUrl
     * @param service
     * @param <API>
     * @return
     */
    private <API extends ZXinBaseApi> API getZxinAPI(String mBaseUrl, Class<API> service) {
        return addFactorys(mBaseUrl).build().create(service);
    }

    private Retrofit.Builder addFactorys(String mBaseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(mBaseUrl);
        if (mFactorys != null && mFactorys.length > 0) {
            for (Converter.Factory factory : mFactorys) {
                builder.addConverterFactory(factory);
            }
        }
        return builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public static class Builder {
        private Context mContext;
        private int timeOut;
        private int readTimeOut;
        private int writeTimeOut;
        private ZxinBaseInterceptor[] mInterceptors;
        private Converter.Factory[] mFactorys;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder addTimeOut(int timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder addReadTimeOut(int readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder addWriteTimeOut(int writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public Builder addInterceptors(ZxinBaseInterceptor... mInterceptors) {
            this.mInterceptors = mInterceptors;
            return this;
        }

        public Builder addFactorys(Converter.Factory... mFactorys) {
            this.mFactorys = mFactorys;
            return this;
        }

        public RetrofitHelper build() {
            RetrofitHelper helper = new RetrofitHelper(mContext);
            if (timeOut != 0)
                helper.setDefaultTimeOut(timeOut);
            if (readTimeOut != 0)
                helper.setDefaultReadTimeOut(readTimeOut);
            if (writeTimeOut != 0)
                helper.setDefaultWriteTimeOut(writeTimeOut);
            if (mInterceptors != null && mInterceptors.length != 0)
                helper.setFactory(mFactorys);
            if (mInterceptors != null && mInterceptors.length != 0)
                helper.setIntercepts(mInterceptors);
            return helper;
        }
    }

}
