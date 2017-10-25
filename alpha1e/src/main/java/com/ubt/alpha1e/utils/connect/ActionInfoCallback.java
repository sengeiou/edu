package com.ubt.alpha1e.utils.connect;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.utils.GsonImpl;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/21.
 */
public abstract class ActionInfoCallback extends Callback<ActionInfo> {

    @Override
    public ActionInfo parseNetworkResponse(Response response,int i) throws IOException
    {
        String string = response.body().string();
        BaseResponseModel<ActionInfo> baseResponseModel = GsonImpl.get().toObject(string,
                new TypeToken<BaseResponseModel<ActionInfo>>(){}.getType());//加上type转换，避免泛型擦除
        return baseResponseModel.models;
    }

}
