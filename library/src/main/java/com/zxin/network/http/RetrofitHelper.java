package com.zxin.network.http;

import android.content.Context;

import com.zxin.network.api.ZXinBaseApi;
import com.zxin.network.api.ZXinWebApi;
import com.zxin.network.interceptor.BaseNetWorkInterceptor;
import com.zxin.network.interceptor.HttpCacheInterceptor;
import com.zxin.network.interceptor.HttpHeaderInterceptor;
import com.zxin.network.response.ResponseConverterFactory;
import com.zxin.network.util.NetworkUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {
    private int DEFAULT_TIME_OUT = 10;//超时时间5s
    private int DEFAULT_READ_TIME_OUT = 10;//读取时间
    private int DEFAULT_WRITE_TIME_OUT = 10;//读取时间
    private static volatile OkHttpClient mOkHttpClient;
    private static volatile RetrofitHelper retrofitHelper;
    private Context mContext;
    private String mBaseUrl = "";

    private RetrofitHelper(Context mContext) {
        this.mContext = mContext;
    }

    private BaseNetWorkInterceptor[] mInterceptors;

    /*****
     * 添加拦截器
     * @param intercept
     * @param <In>
     */
    private synchronized <In extends BaseNetWorkInterceptor> void setIntercepts(In... intercept) {
        this.mInterceptors = intercept;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    private void setmBaseUrl(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    private void setDefaultTimeOut(int defaultTimeOut){
        DEFAULT_TIME_OUT = defaultTimeOut;
    }

    private void setDefaultReadTimeOut(int defaultReadTimeOut){
        DEFAULT_READ_TIME_OUT = defaultReadTimeOut;
    }

    private void setDefaultWriteTimeOut(int defaultWriteTimeOut){
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
                for (BaseNetWorkInterceptor mInterceptor : mInterceptors) {
                    builder.addInterceptor(mInterceptor);
                }
            }
            mOkHttpClient = builder.build();
        }
    }

    /****
     * 初始化API
     * @param service
     * @param <API>
     * @return
     */
    public <API extends ZXinBaseApi> API initZxinAPI(Class<API> service) {
        return new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(getBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(ResponseConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(service);
    }

    public static class Builder {
        private Context mContext;
        private String mBaseUrl;
        private int timeOut;
        private int readTimeOut;
        private int writeTimeOut;
        private BaseNetWorkInterceptor[] mInterceptors;

        public Builder(Context mContext){
            this.mContext = mContext;
        }

        public Builder addBaseUrl(String mBaseUrl){
            this.mBaseUrl = mBaseUrl;
            return this;
        }

        public Builder addTimeOut(int timeOut){
            this.timeOut = timeOut;
            return this;
        }

        public Builder addReadTimeOut(int readTimeOut){
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder addWriteTimeOut(int writeTimeOut){
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public Builder addInterceptors(BaseNetWorkInterceptor[] mInterceptors){
            this.mInterceptors = mInterceptors;
            return this;
        }

        public RetrofitHelper build(){
            RetrofitHelper helper = new RetrofitHelper(mContext);
            helper.setmBaseUrl(mBaseUrl);
            helper.setIntercepts(mInterceptors);
            helper.setDefaultTimeOut(timeOut);
            helper.setDefaultReadTimeOut(readTimeOut);
            helper.setDefaultWriteTimeOut(writeTimeOut);
            return helper;
        }
    }

}
