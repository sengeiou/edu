package com.ubt.alpha1e.ui.helper;

import android.graphics.Bitmap;

import com.ubt.alpha1e.data.model.ThemeInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/3/14.
 */
public interface IThemeUI {

    void onGetThemes(List<ThemeInfo> infos);

    void onGetImages(Bitmap img, long id);

    void onReportProgress(long id, int progress);

    void onReportDownloadFinish(long id, boolean isSuccess);

    void onStopDownload(long id);

    void onGetUsingTheme(ThemeInfo info);

    void onApplyThemeFinish(ThemeInfo info, boolean is_success);

}
