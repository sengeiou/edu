package com.ubt.alpha1e.userinfo.setting;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.blockly.BlocklyProjectMode;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.ISharedPreferensListenet;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.weigan.loopview.LoopView;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class SettingPresenter extends BasePresenterImpl<SettingContract.View> implements SettingContract.Presenter,ISharedPreferensListenet {

    private static int do_set_message_note = 10002;

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
    public boolean isMessageNote(Context context) {
        String value = BasicSharedPreferencesOperator.getInstance(context,
                BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doReadSync(
                BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_KEY);
        if (BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_VALUE_FALSE.equalsIgnoreCase(value)){
            return false;
        } else{
            return true;
        }
    }

    @Override
    public void doSetMessageNote(Context context, boolean isNote) {
        if (isNote) {
            BasicSharedPreferencesOperator.getInstance(context,
                    BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_KEY,
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_VALUE_TRUE,
                    null, do_set_message_note);

        } else {
            BasicSharedPreferencesOperator.getInstance(context,
                    BasicSharedPreferencesOperator.DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_KEY,
                    BasicSharedPreferencesOperator.IS_MESSAGE_NOTE_VALUE_FALSE,
                    null, do_set_message_note);

        }
    }

    @Override
    public void doSetAutoUpgrade(boolean isAutoUpgrade) {
        SPUtils.getInstance().put(Constant.SP_AUTO_UPGRADE,isAutoUpgrade);
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


    private String appidWx = "wxfa7003941d57a391";
    private String appidQQOpen = "1106515940";
    private LoginProxy proxy;
    @Override
    public void doLogout() {
        SPUtils.getInstance().remove(Constant.SP_USER_INFO);
        SPUtils.getInstance().remove(Constant.SP_USER_ID);
        SPUtils.getInstance().remove(Constant.SP_USER_IMAGE);
        SPUtils.getInstance().remove(Constant.SP_USER_NICKNAME);
        SPUtils.getInstance().remove(Constant.SP_LOGIN_TOKEN);
        DataSupport.deleteAll(LocalActionRecord.class);
        DataSupport.deleteAll(BlocklyProjectMode.class);
        proxy =  LoginProxy.getInstance(appidWx, appidQQOpen);
        proxy.clearToken(ELoginPlatform.QQOpen, mView.getContext());
        proxy.clearToken(ELoginPlatform.WX, mView.getContext());

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
}
