package com.ubt.alpha1e.edu.userinfo.setting;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.base.AppManager;
import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.edu.base.RequstMode.CheckIsBindRequest;
import com.ubt.alpha1e.edu.base.RequstMode.SetNoticeStatusRequest;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.blockly.BlocklyProjectMode;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.data.model.BaseResponseModel;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.login.LoginManger;
import com.ubt.alpha1e.edu.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.edu.mvp.BasePresenterImpl;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.userinfo.model.MyRobotModel;
import com.ubt.alpha1e.edu.utils.GsonImpl;
import com.ubt.alpha1e.edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.weigan.loopview.LoopView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SettingPresenter extends BasePresenterImpl<SettingContract.View> implements SettingContract.Presenter,ISharedPreferensListenet {
    private static final String TAG = SettingPresenter.class.getSimpleName();
    private static int do_set_message_note = 10002;
    private static final int CHECK_ROBOT_INFO = 1;
    private static final int GET_NOTICE_STATUS = 2;
    private static final int SET_NOTICE_STATUS = 3;

    @Override
    public boolean isOnlyWifiDownload(Context context) {
        String value = BasicSharedPreferencesOperator.getInstance(context,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY);
        if (value.equals(BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doSetOnlyWifiDownload(Context context, boolean isOnly) {
        if (isOnly) {
            BasicSharedPreferencesOperator
                    .getInstance(context, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY,
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_TRUE,
                            null, -1);
        } else {
            BasicSharedPreferencesOperator
                    .getInstance(context, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                    .doWrite(
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_KEY,
                            BasicSharedPreferencesOperator.IS_ONLY_WIFI_DOWNLOAD_VALUE_FALSE,
                            null, -1);
        }
    }

    @Override
    public void doGetMessageNote() {

        BaseRequest baseRequest = new BaseRequest();

        doRequest(HttpEntity.GET_NOTICE_STATUS, baseRequest, GET_NOTICE_STATUS);

    }

    @Override
    public void doSetMessageNote(boolean isNote) {
        SetNoticeStatusRequest setNoticeStatusRequest = new SetNoticeStatusRequest();
        setNoticeStatusRequest.setStatus(isNote ? "1" : "0");
        doRequest(HttpEntity.SET_NOTICE_STATUS, setNoticeStatusRequest,SET_NOTICE_STATUS);
    }

    @Override
    public void doSetAutoUpgrade(boolean isAutoUpgrade) {
        SPUtils.getInstance().put(Constant.SP_AUTO_UPGRADE, isAutoUpgrade);
    }

    @Override
    public boolean isAutoUpgrade() {
        return SPUtils.getInstance().getBoolean(Constant.SP_AUTO_UPGRADE,true);
    }

    @Override
    public void showLanguageDialog(Context context, int currentPosition, final List<String> languageList) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_useredit_wheel, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.loopView);
        TextView textView = contentView.findViewById(R.id.tv_wheel_name);
        textView.setVisibility(View.GONE);
        DialogPlus.newDialog(context)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_sure) {
                            if (isAttachView()) {
                                mView.onLanguageSelectItem(loopView.getSelectedItem(), languageList.get(loopView.getSelectedItem()));
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
        // 设置原始数据
        loopView.setItems(languageList);
        loopView.setInitPosition(0);
        UbtLog.d("currentPosition","currentPosition => " + currentPosition);
        if(currentPosition < 0){
            loopView.setCurrentPosition(0);
        }else {
            loopView.setCurrentPosition(currentPosition);
        }
    }

    @Override
    public void doReadCurrentLanguage() {
        String init_info = BasicSharedPreferencesOperator.getInstance(mView.getContext(),
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY);
        int index = getLanguageIndex(init_info);
        String language = null;
        if (index == -1) {
            language = ((MVPBaseActivity)(mView.getContext())).getStringResources("ui_settings_language_not_set");
        } else {
            language = ((MVPBaseActivity)(mView.getContext())).getStringArray("ui_lanuages")[index].toString();
        }
        mView.onReadCurrentLanguage(index,language);
    }

    @Override
    public void doChangeLanguage(final Context context, String language) {
        BasicSharedPreferencesOperator.getInstance(context,
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY, language,
                new ISharedPreferensListenet() {
                    @Override
                    public void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code, String value) {
                        //清除短缓存数据
                        ((AlphaApplication) context.getApplicationContext()).clearCacheData();
                        mView.onChangeLanguage();
                    }
                }, -1);
    }


    @Override
    public void doLogout() {
        SPUtils.getInstance().remove(Constant.SP_USER_INFO);
        SPUtils.getInstance().remove(Constant.SP_USER_ID);
        SPUtils.getInstance().remove(Constant.SP_USER_IMAGE);
        SPUtils.getInstance().remove(Constant.SP_USER_NICKNAME);
        SPUtils.getInstance().remove(Constant.SP_LOGIN_TOKEN);
        DataSupport.deleteAll(LocalActionRecord.class);
        DataSupport.deleteAll(BlocklyProjectMode.class);
        LoginManger.getInstance().loginOut();
        AppManager.getInstance().finishAllActivity();

    }

    @Override
    public void checkMyRobotState() {
        CheckIsBindRequest checkRobotInfo = new CheckIsBindRequest();
        checkRobotInfo.setSystemType("3");
        String url = HttpEntity.CHECK_ROBOT_INFO;
        doRequest(url,checkRobotInfo,CHECK_ROBOT_INFO);
    }

    public int getLanguageCurrentIndex() {
        return getLanguageIndex(BasicSharedPreferencesOperator.getInstance(mView.getContext(),
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(
                BasicSharedPreferencesOperator.LANGUAGE_SET_KEY));
    }

    public int getLanguageIndex(String language) {
        CharSequence[] languages =  ((MVPBaseActivity)(mView.getContext())).getStringArray("ui_lanuages_up");
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].toString().toUpperCase().contains(language.toUpperCase())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code, String value) {
        if (request_code == do_set_message_note) {

        }
    }


    /**
     * 请求网络操作
     */
    public void doRequest(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(mView == null){
                    return;
                }

                UbtLog.d(TAG, "doRequest onError:" + e.getMessage());
                switch (id){
                    case CHECK_ROBOT_INFO:
                        mView.onGetRobotInfo(0,null);
                        break;
                    case GET_NOTICE_STATUS:
                        mView.onGetMessageNote(false,"");
                        break;
                    case SET_NOTICE_STATUS:
                        mView.onGetMessageNote(false,"");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG,"response = " + response);
                if(mView == null){
                    return;
                }

                switch (id) {
                    case CHECK_ROBOT_INFO:
                        BaseResponseModel<ArrayList<MyRobotModel>> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<MyRobotModel>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if(!baseResponseModel.status || baseResponseModel.models == null){
                            mView.onGetRobotInfo(0,null);
                        }else {
                            if(baseResponseModel.models.size() == 0){
                                mView.onGetRobotInfo(2,null);
                                return;
                            }else {
                                UbtLog.d(TAG, "size = "+baseResponseModel.models.size());
                                UbtLog.d(TAG, "AutoUpdate = " + baseResponseModel.models.get(0).getAutoUpdate());
                                UbtLog.d(TAG, "equipmentSeq = " + baseResponseModel.models.get(0).getEquipmentSeq());
                                UbtLog.d(TAG, "equipmentVersion = " + baseResponseModel.models.get(0).getEquipmentVersion());
                                mView.onGetRobotInfo(1,baseResponseModel.models.get(0));
                            }
                        }
                        break;
                    case GET_NOTICE_STATUS:
                        {
                            BaseResponseModel mbaseResponseModel = GsonImpl.get().toObject(response,
                                    new TypeToken<BaseResponseModel>() {
                                    }.getType());//加上type转换，避免泛型擦除
                            if(mbaseResponseModel.status){
                                mView.onGetMessageNote(true,(String) mbaseResponseModel.models);
                            }else {
                                mView.onGetMessageNote(false,"");
                            }
                            break;
                        }
                    case SET_NOTICE_STATUS:
                        {
                            BaseResponseModel mbaseResponseModel = GsonImpl.get().toObject(response,
                                    new TypeToken<BaseResponseModel>() {
                                    }.getType());//加上type转换，避免泛型擦除
                            if(mbaseResponseModel.status){
                                mView.onSetMessageNote(true,(String) mbaseResponseModel.models);
                            }else {
                                mView.onSetMessageNote(false,"");
                            }
                            break;
                        }
                    default:
                        break;
                }
            }
        });

    }
}
