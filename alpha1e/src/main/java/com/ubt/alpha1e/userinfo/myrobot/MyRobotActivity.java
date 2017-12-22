package com.ubt.alpha1e.userinfo.myrobot;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.GotoBindRequest;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * @author: dicy.cheng
 * @description:  连接状态
 * @create: 2017/12/21
 * @email: dicy.cheng@ubtrobot.com
 * @modified:
 */

public class MyRobotActivity extends MVPBaseActivity<MyRobotContract.View, MyRobotPresenter> implements MyRobotContract.View {

    private String TAG = "MyRobotActivity";

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.btn_goto_unbind)
    Button btn_goto_unbind;



    private static final int ROBOT_GOTO_UNBIND = 1;

    @OnClick({R.id.ib_return,R.id.btn_goto_unbind})
    protected void switchActivity(View view) {
        UbtLog.d(TAG, "VIEW +" + view.getTag());
        Intent mLaunch = new Intent();
        switch (view.getId()) {
            case R.id.ib_return:
                MyRobotActivity.this.finish();
                break;
            case R.id.btn_goto_unbind:
                gotoUnBind();
                break;
            default:
                break;
        }
    }


    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, MyRobotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.myrobot;
    }

    //一键解绑
    public void gotoUnBind(){

        GotoBindRequest gotoBindRequest = new GotoBindRequest();
        gotoBindRequest.setEquipmentId(AlphaApplication.currentRobotSN);
        gotoBindRequest.setSystemType("3");

        String url = HttpEntity.ROBOT_UNBIND;
        doRequest(url,gotoBindRequest,ROBOT_GOTO_UNBIND);

    }

    /**
     * 网路请求
     */
    public void doRequest(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestCheckIsBind onError:" + e.getMessage());
                switch (id){
                    case ROBOT_GOTO_UNBIND:
                        UbtLog.d(TAG, "解绑失败" );
                        ToastUtils.showShort("解绑失败");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"doRequestCheckIsBind response = " + response);
//				BaseResponseModel<BaseModel> baseResponseModel = GsonImpl.get().toObject(response,new TypeToken<BaseResponseModel<BaseModel>>() {}.getType());
                BaseResponseModel<String> baseResponseModel = GsonImpl.get().toObject(response,
                        new TypeToken<BaseResponseModel<String>>(){}.getType());
                switch (id){
                    case ROBOT_GOTO_UNBIND:
                        UbtLog.d(TAG, "status:" + baseResponseModel.status);
                        if(baseResponseModel.status){
                            UbtLog.d(TAG, "解绑成功" );
                            ToastUtils.showShort("解绑成功");
                        }else {
                            UbtLog.d(TAG, "解绑失败" );
                            ToastUtils.showShort("解绑失败");
                        }

                        break;

                    default:
                        break;
                }
            }
        });

    }
}
