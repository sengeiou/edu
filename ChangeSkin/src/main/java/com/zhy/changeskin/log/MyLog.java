package com.zhy.changeskin.log;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MyLog {

    private static boolean hasSDCard = Environment.getExternalStorageState()
            .equals(android.os.Environment.MEDIA_MOUNTED);
    private static String SDCardPath = Environment
            .getExternalStorageDirectory().getPath();
    private static final boolean isDebug = false;

    public static String writeLog(String title, String msg) {

        return writeLog(title + ":" + msg);
    }

    private static String writeLog(String msg) {
        try {
            if (!isDebug)
                return null;
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy年MM月dd日  HH:mm:ss  ");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String Time = formatter.format(curDate);
            msg = Time + ":" + msg + "\n";

            // 新建目录
            String log_path = "ubt//log";
            File path = new File(SDCardPath + "//" + log_path);
            if (!path.exists()) {
                path.mkdirs();
            }
            log_path = SDCardPath + "//" + log_path;

            File file = new File(log_path + "//" + "yuyong_skin_log.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(log_path + "//"
                    + "yuyong_skin_log.txt", true);
            // 第二个参数表示可以再文件结尾追加写入
            fw.write(msg);
            fw.close();
            return "SUCCESS";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
