package com.ubt.alpha1e_edu.utils.connect;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e_edu.data.model.BannerInfo;
import com.ubt.alpha1e_edu.data.model.BaseResponseModel;
import com.ubt.alpha1e_edu.utils.GsonImpl;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/21.
 */
public abstract class BannerInfoListCallback extends Callback<List<BannerInfo>> {

    @Override
    public List<BannerInfo> parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        BaseResponseModel<List<BannerInfo>> baseResponseModel = GsonImpl.get().toObject(string,
                new TypeToken<BaseResponseModel<List<BannerInfo>>>(){}.getType());//加上type转换，避免泛型擦除
        return baseResponseModel.models;
    }


}
