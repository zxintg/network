package com.zxin.network.mvp.model;

import android.content.Context;

import com.zxin.network.MvpCallback;
import com.zxin.network.http.RetrofitHelper;
import com.zxin.network.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zxin on 2017/11/27.
 */

public abstract class BaseModel {

    private Context context;
    //回调
    private Map<String, MvpCallback> listenerMap;
    //标志位 对应SimpleName
    private Map<Integer, String> tagMap;
    //标志位 网络失败时使用
    private List<Integer> tagList;
    private RetrofitHelper retrofitHelper = null;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MvpCallback getListener(int tag) {
        if (tagMap != null) {
            return listenerMap.get(tagMap.get(tag));
        }
        return null;
    }

    public <P extends BasePresenter> void setListener(P p, MvpCallback listeners) {
        if (listenerMap == null) {
            listenerMap = new HashMap<>();
        }
        if (listenerMap != null && !listenerMap.containsKey(p.getClass().getSimpleName())) {
            listenerMap.put(p.getClass().getSimpleName(), listeners);
        }
    }

    public void clearListener() {
        if (listenerMap != null) {
            listenerMap.clear();
        }
    }

    public <P extends BasePresenter> void removeListener(P p) {
        if (listenerMap != null) {
            listenerMap.remove(p.getClass().getSimpleName());
        }
    }

    public <P extends BasePresenter> void addTag(P p, int tag) {
        if (tagList == null) {
            tagList = new ArrayList<>();
        }
        if (tagList != null && !tagList.contains(tag)) {
            this.tagList.add(tag);
        }
        if (tagMap == null) {
            tagMap = new HashMap<>();
        }
        if (!tagMap.containsKey(tag)) {
            tagMap.put(tag, p.getClass().getSimpleName());
        }
    }

    public void clearTags() {
        if (tagList != null && !tagList.isEmpty()) {
            tagList.clear();
        }
    }

    public void removeTag(int tag) {
        if (tagList != null && !tagList.isEmpty()) {
            tagList.remove(tag);
        }
        if (tagMap != null && !tagMap.isEmpty()) {
            tagMap.remove(tag);
        }
    }
    
    public int getTag(){
       if (tagList != null && !tagList.isEmpty()) {
           return tagList.get(tagList.size()-1);
       }
        return -1;
    }

    public abstract RetrofitHelper initHelper();

    public RetrofitHelper getZxinWebApi() {
        if (retrofitHelper == null) {
            retrofitHelper = initHelper();
        }
        return retrofitHelper;
    }

}
