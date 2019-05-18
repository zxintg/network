package com.zxin.network.interceptor;

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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (request.method().equals("GET")) {
            request = request.newBuilder().url(interceptRequestByGet(request.url().newBuilder()).build()).build();
        } else if (request.method().equals("POST")) {
            if (request.body() instanceof FormBody) {
                request = request.newBuilder().post(interceptRequestByPost(request.body()).build()).build();
            }
        }
        return chain.proceed(request);
    }

    public abstract HttpUrl.Builder interceptRequestByGet(HttpUrl.Builder builder);

    public abstract FormBody.Builder interceptRequestByPost(RequestBody requestBody);

    public abstract Response interceptResponse(Chain chain,Request request);
}
