package com.ubt.alpha1e.edu.utils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/12.
 */
public abstract  class Json {
    private static Json json;

    /**
     * set new json instance
     *
     * @param json new instance
     * @return new instance
     */
    public static Json set(Json json) {
        Json.json = json;
        return Json.json;
    }

    /**
     * set default json handler: Google Gson
     */
    public static Json setDefault() {
        Json.json = new GsonImpl();
        return Json.json;
    }

    /**
     * get default json handler
     *
     * @return Json
     */
    public static Json get() {
        if (json == null) {
            //json = new FastJson();
            json = new GsonImpl();
        }
        return json;
    }

    public abstract String toJson(Object src);

    public abstract <T> T toObject(String json, Class<T> claxx);

    public abstract <T> T toObject(String json, Type claxx);

    public abstract <T> T toObject(byte[] bytes, Class<T> claxx);

    public abstract Map<String,String> getMap(final Object object);


}
