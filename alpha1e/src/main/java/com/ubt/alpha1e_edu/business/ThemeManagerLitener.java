package com.ubt.alpha1e_edu.business;

import com.ubt.alpha1e_edu.data.model.ThemeInfo;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener;

import java.util.List;

/**
 * Created by Administrator on 2016/3/14.
 */
public interface ThemeManagerLitener {

    void onGetThemesOnLine(List<ThemeInfo> infos, boolean isSuccess, String errorInfo);

    void onGetThemesLocal(List<ThemeInfo> infos);

    void onGetFileLenth(ThemeInfo info, double file_lenth);

    void onStopDownloadFile(ThemeInfo info, FileDownloadListener.State state);

    void onReportProgress(ThemeInfo info, double progess);

    void onDownLoadFileFinish(ThemeInfo info, FileDownloadListener.State state);

    void onGetUsingThemeInfo(ThemeInfo info);

    void onApplyThemeFinish(ThemeInfo info, boolean isSuccess);

    void onGetUsingFestivalThemeInfo(ThemeInfo info);

    void noteThemeNeedupdate(String string);
}
