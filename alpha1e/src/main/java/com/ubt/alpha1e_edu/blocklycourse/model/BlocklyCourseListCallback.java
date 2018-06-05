package com.ubt.alpha1e_edu.blocklycourse.model;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public abstract class BlocklyCourseListCallback extends Callback<List<BlocklyCourseModel>> {

    @Override
    public List<BlocklyCourseModel> parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        BaseResponseModel<List<BlocklyCourseModel>> baseResponseModel = GsonImpl.get().toObject(string,
                new TypeToken<BaseResponseModel<List<BlocklyCourseModel>>>(){}.getType());//加上type转换，避免泛型擦除
        return baseResponseModel.models;
    }

}
