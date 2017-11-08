package com.ubt.alpha1e.utils.connect;

import com.ubt.alpha1e.base.BaseRequest;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
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
     *
     * @param url
     * @param params
     * @return
     */
    public static RequestCall getJsonByPostRequest(String url, String params) {
        return getJsonByPostRequest(url, params, -1);
    }

    /**
     * 获取json数据
     *
     * @param url
     * @param params
     * @param id
     * @return
     */
    public static RequestCall getJsonByPostRequest(String url, String params, int id) {
        return OkHttpUtils.postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(params)
                .id(id)
                .build();

    }

    public static RequestCall getJsonByPostRequest(String url, BaseRequest request, int id) {
        request.setUserId(SPUtils.getInstance().getString(Constant.SP_USER_ID));
        request.setToken(SPUtils.getInstance().getString(Constant.SP_LOGIN_TOKEN));
        String params = GsonImpl.get().toJson(request);
        UbtLog.d("Request", "url===" + url + "___params==" + params);
        return OkHttpUtils.postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(params)
                .id(id)
                .build();

    }

    /**
     * 获取json数据
     *
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
     *
     * @param url
     * @param id
     * @return
     */
    public static RequestCall getJsonByGetRequest(String url, String token, int id) {
        return OkHttpUtils.get()
                .url(url)
                .id(id)
                .addHeader("authorization", token)
                .build();

    }

    /**
     * 获取json数据
     *
     * @param url
     * @param params
     * @param id
     * @return
     */
    public static RequestCall getJsonByPostRequest(String url, String params, String token, int id) {
        return OkHttpUtils.postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(params)
                .addHeader("authorization", token)
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
                .requestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params))
                .id(id)
                .build();
    }

}
