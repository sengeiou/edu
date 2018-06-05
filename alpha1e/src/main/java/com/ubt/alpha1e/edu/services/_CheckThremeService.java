package com.ubt.alpha1e.edu.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;

import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.edu.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e.edu.data.JsonTools;
import com.ubt.alpha1e.edu.data.Md5;
import com.ubt.alpha1e.edu.data.ZipTools;
import com.ubt.alpha1e.edu.data.model.ThemeInfo;
import com.ubt.alpha1e.edu.utils.log.MyLog;
import com.ubt.alpha1e.edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e.edu.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.edu.net.http.basic.IJsonListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class _CheckThremeService extends Service implements IJsonListener,
        FileDownloadListener, ISharedPreferensListenet {

    private String mCurrentUsingCommonThremeInfo = "";
    private String mCurrentUsingFestivalThremeInfo = "";

    private ThemeInfo commonThemeService = null;
    private ThemeInfo festivalThemeService = null;

    private static final long on_get_common_base_pkg = 11001;
    private static final long on_get_common_extend_pkg = 11002;
    private static final long do_check_threme = 11003;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        MyLog.writeLog("_CheckThremeService", "onCreate");

        doGetUsingInfo();

        // 测试逻辑
        if (1 == 1) {
            ThemeInfo info = new ThemeInfo();
            info.themeSeq = "1001";
            try {
                info.themeVersion = _CheckThremeService.this.getPackageManager()
                        .getPackageInfo(
                                _CheckThremeService.this.getPackageName(), 0).versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            mCurrentUsingCommonThremeInfo = ThemeInfo.getModelStr(info);
            mCurrentUsingFestivalThremeInfo = ThemeInfo.getModelStr(info);
        }

        super.onCreate();
    }

    private void doGetUsingInfo() {
        mCurrentUsingCommonThremeInfo = BasicSharedPreferencesOperator
                .getInstance(_CheckThremeService.this, DataType.APP_INFO_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.THREME_USING_INFO_COMMON);
        mCurrentUsingFestivalThremeInfo = BasicSharedPreferencesOperator
                .getInstance(_CheckThremeService.this, DataType.APP_INFO_RECORD)
                .doReadSync(
                        BasicSharedPreferencesOperator.THREME_USING_INFO_FESTIVAL);

    }

    @Override
    public int onStartCommand(android.content.Intent intent, int flags,
                              int startId) {

        MyLog.writeLog("_CheckThremeService", "mCurrentUsingCommonThremeInfo-->"
                + mCurrentUsingCommonThremeInfo);

        if (mCurrentUsingCommonThremeInfo
                .equals(BasicSharedPreferencesOperator.NO_VALUE)
                || mCurrentUsingCommonThremeInfo.equals("")) {
            // 没有使用任何主题
        } else {
            JSONObject jobj = null;
            try {
                jobj = new JSONObject();
                jobj.put("themeObjectType", 1);
                jobj.put("themeSeq", getUsingCommonTheme().themeSeq);
                jobj.put("themeVersion", getUsingCommonTheme().themeVersion);
                // 测试信息
                jobj.put("themeType", 2);
            } catch (Exception e) {
                jobj = null;
                e.printStackTrace();
            }
            if (jobj != null)
                GetDataFromWeb.getJsonByPost(do_check_threme, HttpAddress
                        .getRequestUrl(Request_type.check_threme), HttpAddress
                        .getParamsForPost(jobj.toString(),
                                _CheckThremeService.this), this);
        }

        // 如果在执行完onStartCommand后，服务才被异常kill掉，则系统不会自动重启该服务。
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        if (do_check_threme == request_code) {


            if (JsonTools.getJsonStatus(json)) {
                JSONArray j_list = JsonTools.getJsonModels(json);
                for (int i = 0; i < j_list.length(); i++) {
                    try {
                        ThemeInfo info = new ThemeInfo().getThiz(j_list
                                .get(i).toString());
                        if (info.themeType.trim().equals("2")) {
                            commonThemeService = info;
                        } else if (info.themeType.trim().equals("1")) {
                            festivalThemeService = info;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            doCheckCommonTheme(commonThemeService);
            doCheckFestivalTheme(festivalThemeService);

        }
    }

    private ThemeInfo getUsingCommonTheme() {
        return new ThemeInfo().getThiz(mCurrentUsingCommonThremeInfo);
    }

    private void doCheckCommonTheme(final ThemeInfo commonTheme) {

        MyLog.writeLog("_CheckThremeService",
                "commonTheme-->" + ThemeInfo.getModelStr(commonTheme));

        if (commonTheme == null)
            return;
        if (commonTheme.themeVersion.trim().equals(
                getUsingCommonTheme().themeVersion))
            return;

        File basePkg = new File(getUsingCommonTheme().themeUrl);
        if (!basePkg.exists()) {
            GetDataFromWeb.getFileFromHttp(
                    on_get_common_extend_pkg,
                    commonTheme.themeUrl,
                    FileTools.theme_cache + "/"
                            + Md5.getMD5(commonTheme.themeUrl), this);
        }

        File extendPkg = new File(getUsingCommonTheme().themeExtendUrl);
        if (!extendPkg.exists()) {

            GetDataFromWeb.getFileFromHttp(
                    on_get_common_base_pkg,
                    commonTheme.themeExtendUrl,
                    FileTools.theme_cache + "/"
                            + Md5.getMD5(commonTheme.themeExtendUrl), this);

        }
    }

    private void doCheckFestivalTheme(ThemeInfo festivalTheme) {

    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReportProgress(long request_code, double progess) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {

        if (state != State.success) {
            return;
        }

        String key = "";
        String value = "";

        if (request_code == on_get_common_base_pkg
                || request_code == on_get_common_extend_pkg) {
            ThemeInfo info = getUsingCommonTheme();
            key = BasicSharedPreferencesOperator.THREME_USING_INFO_COMMON;
            if (request_code == on_get_common_base_pkg) {
                info.themeUrl = FileTools.theme_cache + "/"
                        + Md5.getMD5(commonThemeService.themeUrl);
                value = ThemeInfo.getModelStr(info);
            } else if (request_code == on_get_common_extend_pkg) {
                info.themeExtendUrl = FileTools.theme_cache + "/"
                        + Md5.getMD5(commonThemeService.themeExtendUrl);
                value = ThemeInfo.getModelStr(info);
            }

            BasicSharedPreferencesOperator.getInstance(_CheckThremeService.this,
                    DataType.APP_INFO_RECORD).doWrite(key, value, this,
                    (int) request_code);
        }

    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess,
                                               long request_code, String value) {

        if (!isSuccess)
            return;
        doGetUsingInfo();
        if (request_code == on_get_common_base_pkg
                || request_code == on_get_common_extend_pkg) {
            File basePkg = new File(getUsingCommonTheme().themeUrl);
            File extendPkg = new File(getUsingCommonTheme().themeExtendUrl);
            File themePkg = new File(FileTools.theme_pkg_file + ".tmp");
            if (basePkg.exists() && extendPkg.exists()) {
                // 合并皮肤包并产生新皮肤包
                if (themePkg.exists()) {
                    FileTools.DeleteFile(themePkg);
                }
                basePkg.renameTo(themePkg);

                ZipTools.unZip(themePkg.getAbsolutePath(), themePkg.getParent()
                        + "/tmp");
                FileTools.DeleteFile(themePkg);
                extendPkg.renameTo(themePkg);
                ZipTools.unZip(themePkg.getAbsolutePath(), themePkg.getParent()
                        + "/tmp");

                // 合并
                try {
                    ZipTools.doZip(themePkg.getParent() + "/tmp",
                            FileTools.theme_pkg_file);
                    FileTools.DeleteFile(themePkg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
