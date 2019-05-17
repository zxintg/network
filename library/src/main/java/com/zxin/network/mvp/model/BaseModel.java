package com.zxin.network.mvp.model;

import android.content.Context;

import com.zxin.network.MvpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxin on 2017/11/27.
 */

public class BaseModel {
    private Context context;

    private List<MvpCallback> listListener;

    private int tag;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MvpCallback getListener() {
        if (listListener != null && !listListener.isEmpty()) {
            return listListener.get(listListener.size()-1);
        }
        return null;
    }

    public void setListener(MvpCallback... listeners) {
        if (listeners != null && listeners.length > 0) {
            if (listListener == null) {
                listListener = new ArrayList<>();
            }
            for (MvpCallback listener : listeners) {
                if (listListener.contains(listener)){
                    continue;
                }
                listListener.add(listener);
            }
        }
    }

    public void clearListener(){
        if (listListener != null) {
            listListener.clear();
        }
    }

    public void removeListener(MvpCallback listener){
        if (listListener != null) {
            listListener.remove(listener);
        }
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }


}
