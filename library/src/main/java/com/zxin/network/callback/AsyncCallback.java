package com.zxin.network.callback;



/**
 * <pre>
 *     @author zxin
 *     time  : 2019/02/02
 *     desc  : 异步callback回调接口
 *     revise:
 * </pre>
 */
public interface AsyncCallback<T> {

    /**
     * 成功时调用
     * @param t         泛型
     */
    void onSuccess(T t);

    /**
     * 异常时调用
     * @param t         异常
     */
    void onFailed(Throwable t);


    /**
     * 通知用户任务开始运行
     * @param threadName            正在运行线程的名字
     */
    void onStart(String threadName);


}
