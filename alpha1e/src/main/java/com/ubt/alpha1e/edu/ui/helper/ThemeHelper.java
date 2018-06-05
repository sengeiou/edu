package com.ubt.alpha1e.edu.ui.helper;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.business.ThemeManager;
import com.ubt.alpha1e.edu.business.ThemeManagerLitener;
import com.ubt.alpha1e.edu.utils.cache.ImageCache;
import com.ubt.alpha1e.edu.data.model.ThemeInfo;
import com.ubt.alpha1e.edu.net.http.GetImagesFromWeb;
import com.ubt.alpha1e.edu.net.http.IGetImagesListener;
import com.ubt.alpha1e.edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.edu.ui.BaseActivity;

import java.util.List;


/**
 * Created by Administrator on 2016/3/14.
 */
public class ThemeHelper extends BaseHelper implements ThemeManagerLitener, IGetImagesListener {


    public static final String MAP_THEME_INFO_KEY = "MAP_THEME_INFO_KEY";
    public static final String MAP_THEME_IMG_KEY = "MAP_THEME_IMG_KEY";
    public static final String MAP_THEME_NAME_KEY = "MAP_THEME_NAME_KEY";
    public static final String MAP_THEME_SIZ_KEY = "MAP_THEME_SIZ_KEY";
    public static final String MAP_THEME_DOWNLOAD_STATE_KEY = "MAP_THEME_DOWNLOAD_STATE_KEY";
    public static final String MAP_THEME_DOWNLOAD_PROGRESS_KEY = "MAP_THEME_DOWNLOAD_PROGRESS_KEY";
    // public static final String MAP_THEME_INFO_IMGS_KEY = "MAP_THEME_INFO_IMGS_KEY";
    public static final String THEME_INFO_KEY = "THEME_INFO_KEY";
    public static final String THEME_IS_CURRENT_USING = "THEME_IS_CURRENT_USING";

    public static enum themeDownloadState {
        NOT_DOWNLOAD, DOWNLOADING, DOWNLOADED
    }

    private final static int ON_GET_THEMES = 10001;

    private IThemeUI mUI;

    private ThemeManager mThemeManager;

    private List<ThemeInfo> mThremeInfos;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ON_GET_THEMES) {
                mUI.onGetThemes(mThremeInfos);
            }
        }
    };

    public ThemeHelper(IThemeUI _ui, BaseActivity _baseActivity) {
        super(_baseActivity);
        this.mUI = _ui;
        mThemeManager = ThemeManager.getInstance(mBaseActivity);
        mThemeManager.addListener(this);
    }


    @Override
    public void RegisterHelper() {
        super.RegisterHelper();
        mThemeManager.addListener(this);
    }

    @Override
    public void UnRegisterHelper() {
        mThemeManager.removeListener(this);
        super.UnRegisterHelper();
    }

    public void doGetThremes() {
        //现获取在线的主题，然后用本地状态比对
        mThemeManager.doGetThemesOnLine();

    }

    public void doGetUsingTheme() {
        mThemeManager.getUsingTheme();

    }

    public void doApplyTheme(ThemeInfo info) {
        mThemeManager.doApplyThemeAsync(info);
    }

    public void DistoryHelper() {

        mThemeManager.removeListener(this);

        ImageCache.getInstances().clearCache();
        System.gc();

    }

    @Override
    public void onSendData(String mac, byte[] datas, int nLen) {

    }

    @Override
    public void onConnectState(boolean bsucceed, String mac) {

    }

    @Override
    public void onGetImage(boolean isSuccess, Bitmap bitmap, long request_code) {

    }

    @Override
    public void onGetThemesOnLine(List<ThemeInfo> infos, boolean isSuccess, String errorInfo) {

        if (isSuccess) {
            mThremeInfos = infos;
            for (int i = 0; i < mThremeInfos.size(); i++) {
                if (mThemeManager.isDownloading(Long.parseLong(mThremeInfos.get(i).themeSeq))) {
                    mThremeInfos.get(i).downloadState = 1;
                }
            }
            mThemeManager.doGetThemeDownloadRecord();
        } else {
            Message msg = new Message();
            msg.what = ON_GET_THEMES;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onGetThemesLocal(List<ThemeInfo> infos) {
        mThemeManager.doCheckDownloadState(mThremeInfos, infos);
        Message msg = new Message();
        msg.what = ON_GET_THEMES;
        mHandler.sendMessage(msg);
    }

    public void doGetImages(List<Long> ids, List<String> urls, float h,
                            float w, int corners) {

        GetImagesFromWeb.getInstance().getImages(ids, urls, this, h, w,
                corners);
    }


    @Override
    public void onNoteDataChaged(final Bitmap img, final long id) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mUI.onGetImages(img, id);
            }
        });
    }

    public void downloadTheme(ThemeInfo info) {
        if (mThemeManager.isDownloading(Long.parseLong(info.themeSeq))) {
            //执行取消逻辑
            mThemeManager.cancelDownloadTheme(Long.parseLong(info.themeSeq));
        } else {
            //执行下载逻辑
            mThemeManager.downloadTheme(info);
        }
    }

    @Override
    public void onGetFileLenth(ThemeInfo infos, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(final ThemeInfo info, FileDownloadListener.State state) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //测试信息-----------start
                if (1 == 0)
                    Toast.makeText(mBaseActivity, info.themeContext + "下载完毕", Toast.LENGTH_SHORT).show();
                //测试信息-----------end
                mUI.onStopDownload(Long.parseLong(info.themeSeq));
            }
        });
    }

    @Override
    public void onReportProgress(final ThemeInfo info, double progess) {
        if (info.downloadState == 1 && info.themeExtendUrl != null && !info.themeExtendUrl.equals("")) {
            progess = progess / 2;
        } else if (info.downloadState == 2) {
            progess = 50 + progess / 2;
        }

        final double _progess = progess;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //测试信息-----------start
                if (1 == 0)
                    Toast.makeText(mBaseActivity, info.themeContext + "下载：" + _progess, Toast.LENGTH_SHORT).show();
                //测试信息-----------end
                mUI.onReportProgress(Long.parseLong(info.themeSeq), (int) _progess);
            }
        });


    }

    @Override
    public void onDownLoadFileFinish(final ThemeInfo info, final FileDownloadListener.State state) {


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //测试信息-----------start
                if (1 == 0)
                    Toast.makeText(mBaseActivity, info.themeContext + "下载完毕", Toast.LENGTH_SHORT).show();
                //测试信息-----------end
                boolean isSuccess = state == FileDownloadListener.State.success ? true : false;
                mUI.onReportDownloadFinish(Long.parseLong(info.themeSeq), isSuccess);
            }
        });


    }

    @Override
    public void onGetUsingThemeInfo(final ThemeInfo infos) {
        mHandler.post(new Runnable() {
                          @Override
                          public void run() {
                              mUI.onGetUsingTheme(infos);
                          }
                      }
        );
    }

    @Override
    public void onApplyThemeFinish(final ThemeInfo info, final boolean isSuccess) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mUI.onApplyThemeFinish(info, isSuccess);
            }
        });
        //重启app
        ((AlphaApplication) mBaseActivity.getApplication()).doExitApp(false);
        ((AlphaApplication) mBaseActivity.getApplication()).doRestartApp();
    }

    @Override
    public void onGetUsingFestivalThemeInfo(ThemeInfo info) {

    }

    @Override
    public void noteThemeNeedupdate(String string) {

    }
}
