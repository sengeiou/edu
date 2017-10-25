package com.ubt.alpha1e.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.IFileListener;
import com.ubt.alpha1e.data.ISharedPreferensListenet;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.data.Md5;
import com.ubt.alpha1e.data.ZipTools;
import com.ubt.alpha1e.data.model.ThemeInfo;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.IJsonListener;
import com.zhy.changeskin.SkinManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/14.
 */
public class ThemeManager implements FileDownloadListener, IJsonListener, IFileListener, ISharedPreferensListenet {

    private static final String TAG = "ThemeManager";

    private static Date lastTime = null;
    //请求码------------------------------start
    private static final long getThremesOnLine = 1001;
    private static final long getThemeRecordLocal = 1002;
    private static final long getUsingThemeInfo = 1003;
    private static final long getFesyivalThemeInfo = 1004;
    private static final long checkThemeInfo = 1005;
    private static final long checkLanguageInfo = 1006;
    private static final long getLanguageOnline = 1007;
    //请求码------------------------------end

    //下载状态相关-----------------------------start
    private List<ThemeInfo> mDownList; // 所有正在下载的任务
    private List<ThemeManagerLitener> mThemeListenerLists; // 所有监听者
    private Map<Long, ThemeInfo> mDownRequestCodeMap;  // 下载请求码与下载任务对应关系
    //下载状态相关-----------------------------end
    private ThemeInfo mCurrentUsingTheme;
    private ThemeInfo mCurrentFestivalTheme;
    private ThemeInfo mThemeOnLine = null;
    private ThemeInfo mThemeFestivalOnLine = null;

    private String mLatestUrl = "";

    private Context mContext;


    private ThemeManager() {
    }


    private static ThemeManager thiz;

    public static ThemeManager getInstance(Context _context) {
        if (thiz == null) {
            thiz = new ThemeManager();
            thiz.mDownList = new ArrayList<ThemeInfo>();
            thiz.mThemeListenerLists = new ArrayList<ThemeManagerLitener>();
            thiz.mDownRequestCodeMap = new HashMap<Long, ThemeInfo>();
        }
        thiz.mContext = _context.getApplicationContext();
        return thiz;
    }

    // 移除监听者
    public void removeListener(ThemeManagerLitener listener) {
        if (mThemeListenerLists.contains(listener))
            mThemeListenerLists.remove(listener);
    }

    // 添加监听者
    public void addListener(ThemeManagerLitener listener) {
        if (!mThemeListenerLists.contains(listener))
            mThemeListenerLists.add(listener);
    }


    public boolean isDownloading(long theme_id) {
        boolean result = false;
        for (int i = 0; i < mDownList.size(); i++) {
            if (Long.parseLong(mDownList.get(i).themeSeq) == theme_id)
                result = true;
        }
        return result;
    }

    public void downloadTheme(ThemeInfo _info) {

        //复制信息
        ThemeInfo info = ThemeInfo.doClone(_info);

        if (isDownloading(Long.parseLong(info.themeSeq))) {
            return;
        }

        ThemeInfo c_info = getThemeById(Long.parseLong(info.themeSeq));
        if (c_info == null) {
            c_info = info;
            thiz.mDownList.add(c_info);
        }
        long req_code = new Date().getTime();


        c_info.downloadState = 1;
        MyLog.writeLog("主题下载", "下载基础包" + c_info.themeUrl + "-->" + FileTools.theme_cache + "/"
                + Md5.getMD5(c_info.themeUrl));
        GetDataFromWeb.getFileFromHttp(
                req_code,
                c_info.themeUrl,
                FileTools.theme_cache + "/"
                        + Md5.getMD5(c_info.themeUrl), thiz);
        mDownRequestCodeMap.put(req_code, c_info);
    }

    public void cancelDownloadTheme(long info_id) {
        Iterator iter = mDownRequestCodeMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Long key = (Long) entry.getKey();
            ThemeInfo info = (ThemeInfo) entry.getValue();
            if (Long.parseLong(info.themeSeq) == info_id) {
                GetDataFromWeb.doStopFileDownLoad(key);
                break;
            }
        }
    }

    private ThemeInfo getThemeById(long id) {
        ThemeInfo info = null;
        for (int i = 0; i < thiz.mDownList.size(); i++) {
            if (Long.parseLong(thiz.mDownList.get(i).themeSeq) == id)
                info = thiz.mDownList.get(i);
        }
        return info;
    }

    private ThemeInfo getThemeByReqCode(long req_code) {
        return mDownRequestCodeMap.get(req_code);
    }

    public void doGetThemesOnLine() {
        //联网获取数据
        JSONObject jobj = null;
        try {
            jobj = new JSONObject();
            jobj.put("themeObjectType", 1);//主题类型，1表示android
            jobj.put("appVersion", mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
        } catch (Exception e) {
            jobj = null;
            e.printStackTrace();
        }
        GetDataFromWeb.getJsonByPost(getThremesOnLine, HttpAddress
                .getRequestUrl(HttpAddress.Request_type.get_themes), HttpAddress
                .getParamsForPost(jobj.toString(), mContext), this);
    }

    public void doGetThemeDownloadRecord() {
        FileTools.readFileString(FileTools.theme_cache, FileTools.theme_log_name, getThemeRecordLocal, this);

    }


    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        if (request_code == getThremesOnLine) {
            //添加本机主题--------------------------start
            List<ThemeInfo> infos = new ArrayList<ThemeInfo>();
            ThemeInfo local_info = new ThemeInfo();
            local_info.themeSeq = -9999 + "";
            local_info.downloadState = 0;
            local_info.themeContext = mContext.getResources().getString(R.string.ui_theme_default);
            local_info.themeDetailImage = "0;0;0;0";
            infos.add(local_info);
            //添加本机主题--------------------------end

            String info_str = "";
            try {
                info_str = JsonTools.getJsonModels(json).toString();
            } catch (Exception e) {
                info_str = "";
            }
            infos.addAll(ThemeInfo.getModelList(info_str));

            for (int i = 0; i < mThemeListenerLists.size(); i++) {
                mThemeListenerLists.get(i).onGetThemesOnLine(infos, true, "");
            }
        }
        if (request_code == checkThemeInfo) {
            if (JsonTools.getJsonStatus(json)) {
                JSONArray j_list = JsonTools.getJsonModels(json);
                for (int i = 0; i < j_list.length(); i++) {
                    try {
                        ThemeInfo info = GsonImpl.get().toObject(j_list.get(i).toString(),ThemeInfo.class);
                        if (info.themeType.trim().equals("2")) {
                            //只做节日主题
                            //mThemeOnLine = info;
                        } else if (info.themeType.trim().equals("1")) {
                            mThemeFestivalOnLine = info;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                MyLog.writeLog("主题检查程序", "收到网络回复:mThemeOnLine-->" + ThemeInfo.getModelStr(mThemeOnLine) + "\nmThemeFestivalOnLine-->" + ThemeInfo.getModelStr(mThemeFestivalOnLine));

                if (!mCurrentUsingTheme.themeSeq.equals("-9999") && mThemeOnLine != null && !mCurrentUsingTheme.themeExtendUrl.equals(FileTools.theme_cache + "/" + Md5.getMD5(mThemeOnLine.themeExtendUrl))) {
                    //普通皮肤包需要升级
                    for (int i = 0; i < mThemeListenerLists.size(); i++) {
                        mThemeListenerLists.get(i).noteThemeNeedupdate("theme");
                    }
                    MyLog.writeLog("主题检查程序", "普通皮肤包需要升级");
                    return;
                }

                String festival_local_url = mCurrentFestivalTheme == null ? "" : mCurrentFestivalTheme.themeUrl;

                if (mThemeFestivalOnLine != null && !festival_local_url.equals(FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl))) {
                    //节日皮肤包需要升级
                    for (int i = 0; i < mThemeListenerLists.size(); i++) {
                        mThemeListenerLists.get(i).noteThemeNeedupdate("theme");
                    }
                    MyLog.writeLog("主题检查程序", "节日皮肤包需要升级");
                    return;
                }else{

                    //改变已经下载的主题时间，如已经下载，在服务器直接把他设置时间，如设过期
                    if(mCurrentFestivalTheme != null && mThemeFestivalOnLine != null
                            && festival_local_url.equals(FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl))){
                        if(!mCurrentFestivalTheme.themeEffTime.equals(mThemeFestivalOnLine.themeEffTime)
                                || !mCurrentFestivalTheme.themeExpTime.equals(mThemeFestivalOnLine.themeExpTime) ){
                            mThemeFestivalOnLine.themeUrl = FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl);
                            //变更本地下载记录
                            updateFestivalDownloadHistory(mThemeFestivalOnLine);
                        }
                    }

                    //已经下载有主题，后台日期过期，无返回，则删除本地主题
                    if(mCurrentFestivalTheme != null && mThemeFestivalOnLine == null){
                        File file = new File(FileTools.theme_cache + "/" + FileTools.theme_festival_log_name);
                        UbtLog.d(TAG,"file.exists() = " + file.exists());
                        if(file.exists()){
                            file.delete();
                        }

                        File themeFile = new File(mCurrentFestivalTheme.themeUrl);
                        if(themeFile.exists()){
                            themeFile.delete();
                        }
                    }
                }
                MyLog.writeLog("主题检查程序", "不需升级");
            }
        }else if(request_code == checkLanguageInfo)//检查语言
        {
            if(!isSuccess)
            {
//                for (int i = 0; i < mThemeListenerLists.size(); i++) {
//                    mThemeListenerLists.get(i).noteThemeNeedupdate("lang");
//                }
                return;
            }
            if (JsonTools.getJsonStatus(json)) {
                JSONArray j_list = JsonTools.getJsonModels(json);
                for (int i = 0; i < j_list.length(); i++) {
                    try {
                        ThemeInfo info = GsonImpl.get().toObject(j_list.get(i).toString(),ThemeInfo.class);
                        if(info.themeType.equals("3") )
                        {
                            String themeUrl = info.themeUrl;
                            if(!TextUtils.isEmpty(themeUrl))
                                mLatestUrl = themeUrl;

                            if(TextUtils.isEmpty(themeUrl)||themeUrl.equals(getCurrentUsingUrl()))
                            {
                                return;
                            }else
                            {
                                notifyListeners("lang");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result, boolean result_state, long request_code) {
        if (request_code == getThemeRecordLocal) {
            List<ThemeInfo> infos = ThemeInfo.getModelList(result);
            for (int i = 0; i < mThemeListenerLists.size(); i++) {
                mThemeListenerLists.get(i).onGetThemesLocal(infos);
            }
        }
        if (request_code == getFesyivalThemeInfo) {
            mCurrentFestivalTheme = new ThemeInfo().getThiz(result);
            for (int i = 0; i < mThemeListenerLists.size(); i++) {
                mThemeListenerLists.get(i).onGetUsingFestivalThemeInfo(mCurrentFestivalTheme);
            }
        }

    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result, long request_code) {

    }

    @Override
    public void onWriteDataFinish(long requestCode, FileTools.State state) {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClaerCache() {

    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {

        if (state == State.success) {
            ThemeInfo info = getThemeByReqCode(request_code);
            mDownList.remove(info);
            if (mThemeListenerLists != null) {
                for (int i = 0; i < mThemeListenerLists.size(); i++) {
                    mThemeListenerLists.get(i).onStopDownloadFile(info, state);
                }
            }
        } else {

        }

    }

    @Override
    public void onReportProgress(long request_code, double progess) {
        Date curDate = new Date(System.currentTimeMillis());
        float time_difference = 500;
        if (lastTime != null) {
            time_difference = curDate.getTime() - lastTime.getTime();
        }

        if (mThemeListenerLists != null && time_difference >= 500) {
            for (int i = 0; i < mThemeListenerLists.size(); i++) {
                mThemeListenerLists.get(i).onReportProgress(
                        getThemeByReqCode(request_code), progess);
            }
            lastTime = curDate;
        }
    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {

        if(request_code == getLanguageOnline)//语言包下载完成
        {

            if(state!=State.success)
            {

                for (int i = 0; i < mThemeListenerLists.size(); i++) {
                    mThemeListenerLists.get(i).onApplyThemeFinish(null,false);//暂用主题接口,下载失败
                }
            }else//下载成功
            {
                BasicSharedPreferencesOperator.getInstance(mContext,
                        BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(BasicSharedPreferencesOperator.LANGUAGE_USING_INFO,mLatestUrl,this,(int)getLanguageOnline);
            }


        }else
        {
        final ThemeInfo info = getThemeByReqCode(request_code);

        if (info != null) {

            if (state != State.success) {
                mDownList.remove(info);
                for (int i = 0; i < mThemeListenerLists.size(); i++) {
                    mThemeListenerLists.get(i).onDownLoadFileFinish(info, state);
                }
                return;
            }

            if (info.downloadState == 1) {
                //正在下载基础包
                MyLog.writeLog("主题下载", "基础包下载完毕，下载增量包");
                info.themeUrl = FileTools.theme_cache + "/" + Md5.getMD5(info.themeUrl);
                info.downloadState = 2;
                if (info.themeExtendUrl != null && !info.themeExtendUrl.equals("")) {
                    //如果有拓展包
                    GetDataFromWeb.getFileFromHttp(
                            request_code,
                            info.themeExtendUrl,
                            FileTools.theme_cache + "/"
                                    + Md5.getMD5(info.themeExtendUrl), thiz);
                } else {
                    onDownLoadFileFinish(request_code, State.success);
                }

            } else if (info.downloadState == 2) {
                //正在下载增量包
                MyLog.writeLog("主题下载", "主题下载完毕");
                mDownList.remove(info);
                if (info.themeExtendUrl != null && !info.themeExtendUrl.equals("")) {
                    //如果有拓展包
                    info.themeExtendUrl = FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl);
                } else {
                    info.themeExtendUrl = "";
                }
                info.downloadState = 0;

                for (int i = 0; i < mThemeListenerLists.size(); i++) {
                    mThemeListenerLists.get(i).onDownLoadFileFinish(info, state);
                }
                updateDownloadHistory(info);
            }
        }
       }
    }

    private static void updateFestivalDownloadHistory(ThemeInfo info) {
        FileTools.writeFileString(
                FileTools.theme_cache,
                FileTools.theme_festival_log_name, -1,
                ThemeInfo.getModelStr(info), null);
    }

    private static void updateDownloadHistory(ThemeInfo info) {
        String history_info = FileTools.readFileStringSync(FileTools.theme_cache,
                FileTools.theme_log_name);
        ArrayList<ThemeInfo> history = ThemeInfo
                .getModelList(history_info);
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).themeSeq.equalsIgnoreCase(info.themeSeq))
                history.remove(i);
        }
        history.add(info);
        FileTools.writeFileString(
                FileTools.theme_cache,
                FileTools.theme_log_name, -1,
                ThemeInfo.getModeslStr(history), null);
    }

    public static void doCheckDownloadState(List<ThemeInfo> infos, List<ThemeInfo> records) {
        if (records != null && infos != null) {
            for (int i = 0; i < infos.size(); i++) {
                for (int j = 0; j < records.size(); j++) {
                    if (infos.get(i).themeSeq.equalsIgnoreCase(records.get(j).themeSeq) || infos.get(i).themeSeq.equalsIgnoreCase("-9999")) {
                        infos.get(i).downloadState = 0;
                        break;
                    }
                }
            }
        }
    }


    public void doApplyThemeAsync(final ThemeInfo info) {
        FileTools.pool.execute(new Runnable() {
            @Override
            public void run() {
                if (mThemeListenerLists != null) {
                    for (int i = 0; i < mThemeListenerLists.size(); i++) {
                        mThemeListenerLists.get(i).onApplyThemeFinish(info == null ? mThemeOnLine : info, doApplyThemeSync(info == null ? mThemeOnLine : info));
                    }
                }
            }
        });
    }

    private static void changeApplyCommomState(Context _context, String value) {
        String key = BasicSharedPreferencesOperator.THREME_USING_INFO_COMMON;
        BasicSharedPreferencesOperator.getInstance(_context,
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(key, value, null,
                -1);
    }

    private static void changeApplyFestivalState(Context _context, String value) {
        String key = BasicSharedPreferencesOperator.THREME_USING_INFO_FESTIVAL;
        BasicSharedPreferencesOperator.getInstance(_context,
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(key, value, null,
                -1);
    }

    public boolean doApplyThemeSync(ThemeInfo info) {
        boolean f = true;
        //lihai update
        if(f){
            boolean result = doApplyThemeSync1(info);
            return result;
        }

        File themePkg = new File(FileTools.theme_pkg_file);
        //如果是默认皮肤
        if (info != null && info.themeSeq.equals("-9999")) {
            // 修改皮肤配置
            if (1 == 0) {
                if (themePkg.exists()) {
                    FileTools.DeleteFile(themePkg);
                }
            } else {
                if (themePkg.exists()) {
                    FileTools.DeleteFile(themePkg);
                }

                File log = new File(FileTools.theme_cache, FileTools.theme_log_name);
                if (log.exists()) {
                    FileTools.DeleteFile(log);
                }
            }

            String key = BasicSharedPreferencesOperator.THREME_USING_INFO_FESTIVAL;
            BasicSharedPreferencesOperator.getInstance(mContext,
                    BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(key);
            ThemeInfo festival_info = new ThemeInfo().getThiz(key);
            if (festival_info != null) {
                File festival_pkg = new File(festival_info.themeUrl);
                if (festival_pkg.exists()) {
                    SkinManager.getInstance().init(mContext);
                    SkinManager.getInstance().changeSkin(festival_info.themeUrl, mContext.getPackageName(), null);
                } else {
                    SkinManager.getInstance().removeAnySkin();
                }
            } else {
                SkinManager.getInstance().removeAnySkin();
            }

            changeApplyCommomState(mContext, "");

            return true;
        }
        //获取本地下载记录
        String downloadStr = FileTools.readFileStringSync(FileTools.theme_cache, FileTools.theme_log_name);
        List<ThemeInfo> infos = ThemeInfo.getModelList(downloadStr);
        ThemeInfo local_info = null;
        for (int i = 0; i < infos.size(); i++) {
            if (info.themeSeq.equals(infos.get(i).themeSeq)) {
                local_info = infos.get(i);
                break;
            }
        }
        //如果远程增量包与本地不一致，升级增量包
        if (local_info != null) {
            MyLog.writeLog("主题下载", "local_info.themeExtendUrl-->" + local_info.themeExtendUrl + "\n服务器" + FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl));
            if (!local_info.themeExtendUrl.equals(FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl))
                    && info.themeExtendUrl != null) {
                //升级本地增量包
                MyLog.writeLog("主题下载", "升级增量包");
                if (!GetDataFromWeb.getFileFromHttpSync(info.themeExtendUrl, FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl))) {
                    return false;
                } else {
                    local_info.themeExtendUrl = FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl);
                    //变更本地下载记录
                    updateDownloadHistory(local_info);
                }
            }
        }

        //如果本地节日皮肤与远程不一致，下载远程节日皮肤
        String festival_local_url = mCurrentFestivalTheme == null ? "" : mCurrentFestivalTheme.themeUrl;

        //如果远程皮肤有效
        if (mThemeFestivalOnLine != null) {
            if (!festival_local_url.equals(FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl))) {
                if (!GetDataFromWeb.getFileFromHttpSync(mThemeFestivalOnLine.themeUrl, FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl))) {
                    return false;
                } else {
                    mThemeFestivalOnLine.themeUrl = FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl);
                    //变更本地下载记录
                    updateFestivalDownloadHistory(mThemeFestivalOnLine);
                }
            }
        }
        //如果远程皮肤无效
        else {
            if (festival_local_url != null) {
                FileTools.DeleteFile(new File(festival_local_url));
            }
        }

        File basePkg = new File(local_info == null ? "NO_FILE" : local_info.themeUrl);
        File extendPkg = null;
        File FestivalPkg = null;

        MyLog.writeLog("节日主题", "检查拓展包");
        if (local_info != null && local_info.themeExtendUrl != null && !local_info.themeExtendUrl.equals("")) {
            //如果有拓展包
            extendPkg = new File(local_info.themeExtendUrl);
            MyLog.writeLog("节日主题", "有拓展包");
        }

        ThemeInfo festival = new ThemeInfo().getThiz(FileTools.readFileStringSync(FileTools.theme_cache, FileTools.theme_festival_log_name));

        if (festival == null) {
            FestivalPkg = null;
        } else {
            MyLog.writeLog("节日主题", "节日主题路径：" + festival.themeUrl);
            FestivalPkg = new File(festival.themeUrl);
            if (!FestivalPkg.exists()) {
                FestivalPkg = null;
            }
        }
        MyLog.writeLog("节日主题", "合并皮肤包并产生新皮肤包");
        // 合并皮肤包并产生新皮肤包--------------start

        if (1 == 1) {
            File themePkg_old = new File(FileTools.theme_pkg_file + ".old");

            //先保存原来的皮肤
            if (themePkg.exists()) {
                if (themePkg_old.exists()) {
                    FileTools.DeleteFile(themePkg_old);
                }
                themePkg.renameTo(themePkg_old);
            }

            UbtLog.d(TAG,"basePkg:" + basePkg.getPath());
            if (basePkg.exists()) {
                ZipTools.unZip(basePkg.getAbsolutePath(), FileTools.theme_cache
                        + "/tmp");
                MyLog.writeLog("节日主题", "基础包存在");
            }
            UbtLog.d(TAG,"extendPkg:" + extendPkg);
            if (extendPkg != null) {
                ZipTools.unZip(extendPkg.getAbsolutePath(), FileTools.theme_cache
                        + "/tmp");
                MyLog.writeLog("节日主题", "增量包存在");
            }

            UbtLog.d(TAG,"FestivalPkg:" + FestivalPkg);
            if (FestivalPkg != null) {
                UbtLog.d(TAG,"FestivalPkg:" + FestivalPkg.getAbsolutePath());
                ZipTools.unZip(FestivalPkg.getAbsolutePath(), FileTools.theme_cache
                        + "/tmp");
                MyLog.writeLog("节日主题", "节日包存在");
            }

            if (themePkg_old.exists()) {
                UbtLog.d(TAG,"themePkg_old:" + themePkg_old.getAbsolutePath());
                ZipTools.unZip(themePkg_old.getAbsolutePath(), FileTools.theme_cache
                        + "/tmp");
                MyLog.writeLog("节日主题", "原来包存在");
            }

            try {
                ZipTools.doZip(FileTools.theme_cache + "/tmp",
                        FileTools.theme_pkg_file);
                //换肤成功，删除原来的皮肤
                if (themePkg_old.exists()) {
                    FileTools.DeleteFile(themePkg_old);
                }
                File tmp_path = new File(FileTools.theme_cache
                        + "/tmp");
                if (tmp_path.exists()) {
                    FileTools.DeleteFile(tmp_path);
                }
                // 修改皮肤配置
                changeApplyCommomState(mContext, ThemeInfo.getModelStr(local_info));
                changeApplyFestivalState(mContext, ThemeInfo.getModelStr(festival));
                //执行换肤
                SkinManager.getInstance().init(mContext);
                SkinManager.getInstance().changeSkin(FileTools.theme_pkg_file, mContext.getPackageName(), null);
                return true;
            } catch (IOException e) {
                //换肤失败。还原原来的皮肤
                if (themePkg_old.exists()) {
                    themePkg_old.renameTo(themePkg);
                }
                return false;
            }
        } else {
            File themePkg_old = new File(FileTools.theme_pkg_file + ".old");

            //先保存原来的皮肤
            if (themePkg.exists()) {
                if (themePkg_old.exists()) {
                    FileTools.DeleteFile(themePkg_old);
                }
                themePkg.renameTo(themePkg_old);
            }

            if (basePkg.exists()) {
                basePkg.renameTo(themePkg);
                // 修改皮肤配置
                changeApplyCommomState(mContext, ThemeInfo.getModelStr(local_info));
                //执行换肤
                SkinManager.getInstance().init(mContext);
                SkinManager.getInstance().changeSkin(FileTools.theme_pkg_file, mContext.getPackageName(), null);
                return true;
            } else {
                themePkg_old.renameTo(themePkg);
                return false;
            }

        }
        // 合并皮肤包并产生新皮肤包--------------end

    }

    private boolean doApplyThemeSync1(ThemeInfo info){
        File themePkg = new File(FileTools.theme_pkg_file);

        //获取本地下载记录
        String downloadStr = FileTools.readFileStringSync(FileTools.theme_cache, FileTools.theme_log_name);
        List<ThemeInfo> infos = ThemeInfo.getModelList(downloadStr);
        ThemeInfo local_info = null;
        for (int i = 0; i < infos.size(); i++) {
            if (info.themeSeq.equals(infos.get(i).themeSeq)) {
                local_info = infos.get(i);
                break;
            }
        }
        //如果远程增量包与本地不一致，升级增量包
        if (local_info != null) {
            MyLog.writeLog("主题下载", "local_info.themeExtendUrl-->" + local_info.themeExtendUrl + "\n服务器" + FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl));
            if (!local_info.themeExtendUrl.equals(FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl))
                    && info.themeExtendUrl != null) {
                //升级本地增量包
                MyLog.writeLog("主题下载", "升级增量包");
                if (!GetDataFromWeb.getFileFromHttpSync(info.themeExtendUrl, FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl))) {
                    return false;
                } else {
                    local_info.themeExtendUrl = FileTools.theme_cache + "/" + Md5.getMD5(info.themeExtendUrl);
                    //变更本地下载记录
                    updateDownloadHistory(local_info);
                }
            }
        }

        //如果本地节日皮肤与远程不一致，下载远程节日皮肤
        String festival_local_url = mCurrentFestivalTheme == null ? "" : mCurrentFestivalTheme.themeUrl;

        //如果远程皮肤有效
        if (mThemeFestivalOnLine != null) {
            if (!festival_local_url.equals(FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl))) {
                if (!GetDataFromWeb.getFileFromHttpSync(mThemeFestivalOnLine.themeUrl, FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl))) {
                    return false;
                } else {
                    mThemeFestivalOnLine.themeUrl = FileTools.theme_cache + "/" + Md5.getMD5(mThemeFestivalOnLine.themeUrl);

                    FileTools.copyFile(mThemeFestivalOnLine.themeUrl,FileTools.theme_pkg_festival_file,true);

                    //变更本地下载记录
                    updateFestivalDownloadHistory(mThemeFestivalOnLine);
                }
            }
        }
        //如果远程皮肤无效
        else {
            if (festival_local_url != null) {
                FileTools.DeleteFile(new File(festival_local_url));
            }
        }

        ThemeInfo festival = new ThemeInfo().getThiz(FileTools.readFileStringSync(FileTools.theme_cache, FileTools.theme_festival_log_name));

        // 修改皮肤配置
        changeApplyCommomState(mContext, ThemeInfo.getModelStr(local_info));
        changeApplyFestivalState(mContext, ThemeInfo.getModelStr(festival));

        return true;

    }

    public void getUsingTheme() {
        BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadAsync(BasicSharedPreferencesOperator.THREME_USING_INFO_COMMON, this, (int) getUsingThemeInfo);
    }

    public void getUsingFestivalTheme() {
        FileTools.readFileString(FileTools.theme_cache, FileTools.theme_festival_log_name, getFesyivalThemeInfo, this);
    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code, String value) {
        if (request_code == getUsingThemeInfo) {
            mCurrentUsingTheme = new ThemeInfo().getThiz(value);
            if (mCurrentUsingTheme == null) {
                mCurrentUsingTheme = new ThemeInfo();
                mCurrentUsingTheme.themeSeq = "-9999";
                mCurrentUsingTheme.downloadState = 0;
                mCurrentUsingTheme.themeContext = mContext.getResources().getString(R.string.ui_theme_default);
            }
            for (int i = 0; i < mThemeListenerLists.size(); i++) {
                mThemeListenerLists.get(i).onGetUsingThemeInfo(mCurrentUsingTheme);
            }
        }else if(request_code == getLanguageOnline)//语言包
        {
            for (int i = 0; i < mThemeListenerLists.size(); i++) {
                mThemeListenerLists.get(i).onApplyThemeFinish(null,true);//暂用主题接口
            }

        }


    }


    public void doCheckThemeState() {
        //异步获取当前节日皮肤信息
        getUsingFestivalTheme();
        //同步获取当前普通皮肤信息
        String mCurrentUsingThemeInfo = BasicSharedPreferencesOperator.getInstance(mContext, BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(BasicSharedPreferencesOperator.THREME_USING_INFO_COMMON);
        mCurrentUsingTheme = new ThemeInfo().getThiz(mCurrentUsingThemeInfo);
        //如果当前普通皮肤为空则为默认皮肤
        if (mCurrentUsingTheme == null) {
            mCurrentUsingTheme = new ThemeInfo();
            mCurrentUsingTheme.themeSeq = "-9999";
        }

        MyLog.writeLog("主题检查程序", "doCheckThemeState" + ":mCurrentUsingTheme.themeSeq" + "-->" + mCurrentUsingTheme.themeSeq);

        JSONObject jobj = null;
        try {
            jobj = new JSONObject();
            jobj.put("themeObjectType", 1);//主题类型，1表示android
            jobj.put("appVersion", mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
            jobj.put("themeSeq", mCurrentUsingTheme.themeSeq);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //发起网络请求
        GetDataFromWeb.getJsonByPost(checkThemeInfo, HttpAddress
                .getRequestUrl(HttpAddress.Request_type.check_threme), HttpAddress
                .getParamsForPost(jobj.toString(),
                        mContext), this);
    }



    public void  doCheckLanguageState()//检查语言包状态
    {

        JSONObject jobj = null;
        try {
            jobj = new JSONObject();
            jobj.put("themeObjectType", 1);//主题类型，1表示android
            jobj.put("appVersion",  mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
            jobj.put("themeSeq", "-9999");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //发起网络请求
        GetDataFromWeb.getJsonByPost(checkLanguageInfo, HttpAddress
                .getRequestUrl(HttpAddress.Request_type.check_threme), HttpAddress
                .getParamsForPost(jobj.toString(),
                        mContext), this);
    }

    public void doUpdateLanguages()//获取语言包
    {
          if(!mLatestUrl.equalsIgnoreCase(getCurrentUsingUrl()))
          {
              GetDataFromWeb.getFileFromHttp(
                      getLanguageOnline,
                      mLatestUrl,
                      FileTools.theme_pkg_file,
                      this);
          }

    }

    //获取当前存储的语言包地址
    private String getCurrentUsingUrl()
    {

        return BasicSharedPreferencesOperator.getInstance(mContext,
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(BasicSharedPreferencesOperator.LANGUAGE_USING_INFO);

    }

    private void notifyListeners(String string)
    {
        for (int i = 0; i < mThemeListenerLists.size(); i++) {
            mThemeListenerLists.get(i).noteThemeNeedupdate(string);
        }
    }
}
