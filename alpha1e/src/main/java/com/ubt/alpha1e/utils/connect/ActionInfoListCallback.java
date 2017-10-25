package com.ubt.alpha1e.utils.connect;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.utils.GsonImpl;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/21.
 */
public abstract class ActionInfoListCallback extends Callback<List<ActionInfo>> {

    @Override
    public List<ActionInfo> parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        BaseResponseModel<List<ActionInfo>> baseResponseModel = GsonImpl.get().toObject(string,
                new TypeToken<BaseResponseModel<List<ActionInfo>>>(){}.getType());//加上type转换，避免泛型擦除
        return baseResponseModel.models;
    }

}
