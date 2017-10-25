package com.ubt.alpha1e.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.AlphaApplicationValues.EdtionCode;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.utils.SignaturesUtil;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.lang.reflect.Method;

public class ApkUpdateManager {

    private static final String TAG = "ApkUpdateManager";

    private static ApkUpdateManager thiz;
    private static boolean isUpdate = false;
    public static boolean isNewersion = false;
    private Context mContext;
    private NotificationManager mNotificationManager;
    private String mUrl = "";
    private String mFile_name = "";
    private final int UPDATE_NOTIFICATION_ID = 1001;

    private ApkUpdateManager() {

    }

    public static ApkUpdateManager getInstance(Context cont, String url) {
        if (thiz == null) {
            thiz = new ApkUpdateManager();
            isUpdate = false;
        }
        thiz.mContext = cont;
        thiz.mNotificationManager = (NotificationManager) thiz.mContext
                .getSystemService(thiz.mContext.NOTIFICATION_SERVICE);
        thiz.mUrl = url;
        String[] strs = thiz.mUrl.split("/");
        UbtLog.d(TAG,"url = " + url);
        thiz.mFile_name = strs[strs.length - 1];
        return thiz;
    }

    public static boolean isUpdating() {
        return isUpdate;
    }

    public void Update() {

        if (AlphaApplicationValues.getCurrentEdit() == EdtionCode.for_factory_edit)
            return;

        if (isUpdate) {
            return;
        }
        isUpdate = true;
        startDownloadAPK();
    }

    private void startDownloadAPK() {

        Notification notification = null;
        CharSequence contentTitle = mContext.getResources().getString(
                (R.string.ui_robot_info_upgrading));
        CharSequence contentText = mContext.getResources().getString(
                (R.string.ui_about_update_progress)) + "0%";
        if(Build.VERSION.SDK_INT <16) {
            notification = new Notification();
            notification.icon = R.drawable.ic_launcher;
            notification.flags = Notification.FLAG_NO_CLEAR;

            Class clazz = notification.getClass();
            try {
                Method m2 = clazz.getDeclaredMethod("setLatestEventInfo", Context.class,CharSequence.class,CharSequence.class,PendingIntent.class);
                m2.invoke(notification, mContext, contentTitle,
                        contentText, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            notification.setLatestEventInfo(mContext, contentTitle, contentText, null);
        }else{
            notification = new Notification.Builder(mContext)
                    .setAutoCancel(false)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
        }

        mNotificationManager.notify(this.UPDATE_NOTIFICATION_ID, notification);

        GetDataFromWeb.getFileFromHttp(-1, mUrl, FileTools.update_cache + "/"
                + mFile_name, new FileDownloadListener() {

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
                Notification notification = null;
                CharSequence contentTitle = mContext.getResources().getString(
                        (R.string.ui_robot_info_upgrading));
                CharSequence contentText = mContext.getResources().getString(
                        (R.string.ui_about_update_progress))
                        + ((int) progess + "%");
                if(Build.VERSION.SDK_INT <16) {
                    notification = new Notification();
                    notification.icon = R.drawable.ic_launcher;
                    notification.flags = Notification.FLAG_NO_CLEAR;

                    Class clazz = notification.getClass();
                    try {
                        Method m2 = clazz.getDeclaredMethod("setLatestEventInfo", Context.class,CharSequence.class,CharSequence.class,PendingIntent.class);
                        m2.invoke(notification, mContext, contentTitle,
                                contentText, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    notification.setLatestEventInfo(mContext, contentTitle, contentText, null);
                }else {
                    notification = new Notification.Builder(mContext)
                            .setAutoCancel(false)
                            .setContentTitle(contentTitle)
                            .setContentText(contentText)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .build();
                }

//                Notification notification = new Notification();
//                notification.icon = R.drawable.ic_launcher;
//                notification.flags = Notification.FLAG_NO_CLEAR;
//                CharSequence contentTitle = mContext.getResources().getString(
//                        (R.string.ui_robot_info_upgrading));
//                CharSequence contentText = mContext.getResources().getString(
//                        (R.string.ui_about_update_progress))
//                        + ((int) progess + "%");
//                notification.setLatestEventInfo(mContext, contentTitle,
//                        contentText, null);
                mNotificationManager.notify(UPDATE_NOTIFICATION_ID,
                        notification);
            }

            @Override
            public void onDownLoadFileFinish(long request_code, State state) {
                // TODO Auto-generated method stub
                if (state == State.fail) {
                    isUpdate = false;

                    Notification notification = null;
                    CharSequence contentTitle = mContext.getResources()
                            .getString((R.string.ui_robot_info_upgrading));
                    CharSequence contentText = mContext.getResources()
                            .getString((R.string.ui_about_update_fail));
                    if(Build.VERSION.SDK_INT <16) {
                        notification = new Notification();
                        notification.icon = R.drawable.ic_launcher;
                        notification.flags = Notification.FLAG_NO_CLEAR;

                        Class clazz = notification.getClass();
                        try {
                            Method m2 = clazz.getDeclaredMethod("setLatestEventInfo", Context.class,CharSequence.class,CharSequence.class,PendingIntent.class);
                            m2.invoke(notification, mContext, contentTitle,
                                    contentText, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//            notification.setLatestEventInfo(mContext, contentTitle, contentText, null);
                    }else{
                        notification = new Notification.Builder(mContext)
                                .setAutoCancel(false)
                                .setContentTitle(contentTitle)
                                .setContentText(contentText)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .build();
                    }



//                    Notification notification = new Notification();
//                    notification.icon = R.drawable.ic_launcher;
//                    CharSequence contentTitle = mContext.getResources()
//                            .getString((R.string.ui_robot_info_upgrading));
//                    CharSequence contentText = mContext.getResources()
//                            .getString((R.string.ui_about_update_fail));
//                    notification.setLatestEventInfo(mContext, contentTitle,
//                            contentText, null);
                    mNotificationManager.notify(UPDATE_NOTIFICATION_ID,
                            notification);
                } else {
                    isUpdate = false;
                    mNotificationManager.cancel(UPDATE_NOTIFICATION_ID);
                    File apkfile = new File(FileTools.update_cache + "/" + mFile_name);
                    if (!apkfile.exists()){
                        return;
                    }

                    String currentAppSign = SignaturesUtil.getSign(mContext);
                    if(!TextUtils.isEmpty(currentAppSign)){
                        String apkSign = SignaturesUtil.getSignaturesFromApk(apkfile);
                        UbtLog.d(TAG,"currentAppSign = " + currentAppSign + "   apkSign = " + apkSign);
                        if(!TextUtils.isEmpty(apkSign) && !currentAppSign.equalsIgnoreCase(apkSign)){
                            UbtLog.d(TAG,"下载的app签名不一样，则返回，放弃安装");
                            return;
                        }
                    }

                    // google play不自主升级-------------------start
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.fromFile(new File(FileTools.update_cache + File.separator + mFile_name)),
                            "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(install);
                    // google play不自主升级-------------------end

                }
            }
        });

    }
}
