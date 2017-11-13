package com.ubt.alpha1e.userinfo.aboutus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;

import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.update.UpdateTools;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class AboutUsPresenter extends BasePresenterImpl<AboutUsContract.View> implements AboutUsContract.Presenter{

    private static final String TAG = AboutUsPresenter.class.getSimpleName();

    private final int do_check_update = 1001;

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
    public boolean isWifiCoon(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doUpdateApk() {
        if (AlphaApplicationValues.getCurrentEdit() == AlphaApplicationValues.EdtionCode.for_factory_edit) {
            mView.noteNewestVersion();
            return;
        }

        String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.check_update);
        String params = HttpAddress.getParamsForPost(new String[]{"1", "2", "1"},
                        HttpAddress.Request_type.check_update, mView.getContext());
        requestDataFromWebByGet(url,params,do_check_update);

    }

    private void requestDataFromWebByGet(String url,String params,int id){

        OkHttpClientUtils.getJsonByPostRequest(url,params,id)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        switch (id){
                            case do_check_update:
                                mView.noteApkUpsateFail(((MVPBaseActivity)mView.getContext()).getStringResources("ui_common_network_request_failed"));
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (do_check_update == id) {
                            if (JsonTools.getJsonStatus(json)) {
                                UbtLog.d(TAG,"json = " + json );
                                JSONObject obj = JsonTools.getJsonModel(json);
                                String versionName = "";
                                String versionPath = "";
                                String versionSize = "";
                                String versionInfo = "";

                                try {
                                    versionName = obj.getString("versionName");
                                    versionPath = obj.getString("versionPath");
                                } catch (JSONException e1) {
                                    versionName = "";
                                    versionPath = "";
                                }

                                try {
                                    versionSize = obj.getString("versionSize");
                                } catch (JSONException e1) {
                                    versionSize = "";
                                }

                                try {
                                    versionInfo = obj.getString("versionResume");
                                } catch (JSONException e1) {
                                    versionInfo = "";
                                }

                                if (UpdateTools.compareVersion(versionName, mView.getContext())) {

                                    final String _versionPath = versionPath;
                                    final String _versionNameSizeInfo = versionName + "#"+ versionSize + "#" + versionInfo;
                                    mView.noteApkUpdate(_versionPath, _versionNameSizeInfo);

                                } else {
                                    mView.noteNewestVersion();
                                }
                            } else {
                                mView.noteApkUpsateFail(((MVPBaseActivity)mView.getContext()).getStringResources("ui_common_network_request_failed"));
                            }
                        }
                    }
                });
    }

}
