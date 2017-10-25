package com.ubt.alpha1e.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/6/12.
 */
public class GsonImpl extends Json {
    private Gson gson = new Gson();

    @Override
    public String toJson(Object src) {
        return gson.toJson(src);
    }

    @Override
    public <T> T toObject(String json, Class<T> claxx) {
        return gson.fromJson(json, claxx);
    }

    @Override
    public <T> T toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> claxx) {
        return gson.fromJson(new String(bytes), claxx);
    }
}
