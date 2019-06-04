package com.zxin.network;

import rx.Subscriber;

/**
 * Created by zxin on 2017/10/19.
 */
public abstract class ZxinAPICallback<T> extends Subscriber<T> {
    private int tag;

    protected ZxinAPICallback(int tag) {
        this.tag = tag;
        init(this.tag);
    }

    @Override
    public void onError(Throwable e) {
        error(this.tag,e);
    }

    @Override
    public void onCompleted() {
        completed(this.tag);
    }

    @Override
    public void onNext(T tBaseResponse) {
        success(this.tag,tBaseResponse);
    }

    /*****
     * 初始化
     */
    public abstract void init(int tag);

    /****
     * 错误处理
     * @param e
     */
    public abstract void error(int tag,Throwable e);

    /*****
     * 请求成功
     * @param tBaseResponse
     */
    public abstract void success(int tag,T tBaseResponse);

    /*****
     * 完成请求
     */
    public abstract void completed(int tag);

}