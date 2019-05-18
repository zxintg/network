package com.zxin.network.interceptor;

import android.content.Context;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*****
 * 基础拦截器
 *
 * by zxin
 *
 */
public abstract class ZxinBaseInterceptor implements Interceptor {

    private Context mContext;

    protected ZxinBaseInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public Response intercept(Chain chain) {
        try {
            Request request = chain.request();
            if (request.method().equals("GET")) {
                HttpUrl.Builder builder  = interceptRequestByGet(request.url().newBuilder());
                if (builder != null) {
                    request = request.newBuilder().url(builder.build()).build();
                }
            } else if (request.method().equals("POST")) {
                if (request.body() instanceof FormBody) {
                    FormBody.Builder builder = interceptRequestByPost(request.body());
                    if (builder != null) {
                        request = request.newBuilder().post(builder.build()).build();
                    }
                }
            }
            Response response = interceptResponse(chain, request);
            if (response == null) {
                response = chain.proceed(request);
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract HttpUrl.Builder interceptRequestByGet(HttpUrl.Builder builder) throws IOException;

    public abstract FormBody.Builder interceptRequestByPost(RequestBody requestBody) throws IOException;

    public abstract Response interceptResponse(Chain chain, Request request) throws IOException;
}
