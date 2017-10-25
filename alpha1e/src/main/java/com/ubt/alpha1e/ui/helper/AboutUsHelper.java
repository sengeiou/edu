package com.ubt.alpha1e.ui.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.AlphaApplicationValues.EdtionCode;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.update.UpdateTools;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutUsHelper extends BaseHelper implements IJsonListener {

    private IAboutUsUI mUI;

    private long do_check_update = 11001;

    public AboutUsHelper(IAboutUsUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;

    }

    public void doUpdateApk() {
        if (AlphaApplicationValues.getCurrentEdit() == EdtionCode.for_factory_edit) {
            mUI.noteNewestVersion();
            return;
        }

        GetDataFromWeb.getJsonByPost(do_check_update, HttpAddress
                .getRequestUrl(Request_type.check_update), HttpAddress
                .getParamsForPost(new String[]{"1", "2", "1"},
                        Request_type.check_update, mBaseActivity), this);
    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {

        if (do_check_update == request_code) {
            if (JsonTools.getJsonStatus(json)) {

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

                if (UpdateTools.compareVersion(versionName, mBaseActivity)) {
                    // �Ӹ���ʾ��UI
                    final String _versionPath = versionPath;
                    final String _versionNameSizeInfo = versionName + "#"
                            + versionSize + "#" + versionInfo;
                    mUI.noteApkUpdate(_versionPath, _versionNameSizeInfo);

                } else {
                    mUI.noteNewestVersion();
                }
            } else {

                mUI.noteApkUpsateFail(mBaseActivity.getResources().getString(
                        R.string.ui_common_network_request_failed));
            }
        }
    }

    public static boolean isWifiCoon(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.getState() == State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {
        // TODO Auto-generated method stub

    }

    @Override
    public void DistoryHelper() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {
        // TODO Auto-generated method stub

    }

}
