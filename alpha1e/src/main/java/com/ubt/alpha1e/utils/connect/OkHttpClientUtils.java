package com.ubt.alpha1e.utils.connect;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.RequestCall;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/3/21.
 */
public class OkHttpClientUtils {

    /**
     * 获取json数据
     * @param url
     * @param params
     * @return
     */
    public static RequestCall getJsonByPostRequest(String url, String params) {
        return getJsonByPostRequest(url,params,-1);
    }

    /**
     * 获取json数据
     * @param url
     * @param params
     * @param id
     * @return
     */
    public static RequestCall getJsonByPostRequest(String url, String params,int id) {
        return OkHttpUtils.postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(params)
                .id(id)
                .build();

    }


    /**
     * 获取json数据
     * @param url
     * @param id
     * @return
     */
    public static RequestCall getJsonByGetRequest(String url, int id) {
        return OkHttpUtils.get()
                .url(url)
                .id(id)
                .build();

    }

    /**
     * 获取json数据
     * @param url
     * @param id
     * @return
     */
    public static RequestCall getJsonByGetRequest(String url, String token, int id) {
        return OkHttpUtils.get()
                .url(url)
                .id(id)
                .addHeader("authorization",token)
                .build();

    }

    /**
     * 获取json数据
     * @param url
     * @param params
     * @param id
     * @return
     */
    public static RequestCall getJsonByPostRequest(String url, String params, String token,int id) {
        return OkHttpUtils.postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(params)
                .addHeader("authorization",token)
                .id(id)
                .build();

    }

    public static RequestCall getJsonByPatchRequest(String url, String params, String token, int id) {
        return OkHttpUtils.patch()
                .url(url)
                .requestBody(params)
                .addHeader("authorization", token)
                .id(id)
                .build();

    }


    public static RequestCall getJsonByPutRequest(String url, String params, int id) {
        return OkHttpUtils.put()
                .url(url)
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),params))
                .id(id)
                .build();
    }

}
