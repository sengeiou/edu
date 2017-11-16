package com.ubt.alpha1e.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.BuildConfig;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.data.model.ActionRecordInfo;
import com.ubt.alpha1e.utils.YHDCollectionUtils;
import com.ubt.alpha1e.utils.cache.ImageCache;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.ubtechinc.base.ByteHexHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileTools {

    private static final String TAG = "FileTools";

    public static String[] img_suffix = new String[]{"jpg,png,bmp"};
    public static String[] video_suffix = new String[]{"mp4", "rmvb", "3gp",
            "avi"};

    public enum file_type {
        video, photo, unknown
    }


    public static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static String SDCardPath = Environment
            .getExternalStorageDirectory().getPath();
    public static final String current_app_package = BuildConfig.APPLICATION_ID;

    public static final String file_path = SDCardPath + "/ubt_alpha";
    public static final String image_cache = file_path + "/imgs";
    public static final String media_cache = file_path + "/medias/";
    public static final String theme_cache = file_path + "/themes";
    public static final String theme_log_name = "theme_logs";
    public static final String theme_festival_log_name = "theme_festival_log_name";
    public static final String theme_pkg_file = theme_cache + "/language_v1.0.1.ubt";
    public static final String theme_pkg_festival_file = theme_cache + "/festival_language_v1.0.1.ubt";
    public static final String update_cache = file_path + "/update";
    public static final String actions_download_cache = file_path + "/actions";
    public static final String actions_new_cache = file_path + "/creates";
    public static final String actions_new_log_name = "actions_new_logs";
    public static final String package_name = "com.ubt.alpha1e";
    public static final String db_log_cache = file_path + "/logs";
    public static final String db_log_name = "UbtLogs_20160506001";
    public static final String db_log_version = "UbtLogs_version.txt";
    public static final String tmp_file_cache = file_path + "/tmps";

    public static final String course_cache = file_path + "/course";
    public static final String course_image_cache = course_cache + "/imgs";
    public static final String course_task_cache = course_cache + "/task";
    public static final String course_screenshot_cache = course_cache + "/screenshot";

    public static final String record = file_path + "/record";

    public static final String action_robot_file_path = "action";
    public static final String actions_download_robot_path = action_robot_file_path + "/my download";
    public static final String actions_creation_robot_path = action_robot_file_path + "/my creation";
    public static final String actions_gamepad_robot_path = action_robot_file_path + "/gamepad";
    public static final String actions_walk_robot_path = action_robot_file_path + "/walk";  //新增行走文件夹

    public static final String actions_default_head_url = "https://video.ubtrobot.com/default/sec_robot_action.png";

    public static List<Map<String,String>> tempList = null;
    public enum State {
        Success, Fail, Exception, No_file
    }


    public static boolean writeDateToSDCardSync(final InputStream inputStream,
                                                String key) {

        File tmp_File = new File(image_cache, key + "");
        return writeDateToSDCardSync(inputStream,tmp_File);
    }

    public static boolean writeDateToSDCardSync(final InputStream inputStream, File targetFile){

        try {
            File path = new File(targetFile.getParent());
            if (!path.exists()) {
                path.mkdirs();
            }
            targetFile.createNewFile();
            FileOutputStream fout = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024 * 10];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1)
                    break;
                fout.write(buffer, 0, len);
            }
            inputStream.close();
            fout.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long writeDateToSDCard(final InputStream inputStream,
                                         final IFileListener listener) {
        final long requestCode = new Date().getTime();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                boolean result = writeDateToSDCardSync(inputStream, requestCode
                        + "");

                if (result) {
                    listener.onWriteDataFinish(requestCode, State.Success);
                } else {
                    listener.onWriteDataFinish(requestCode, State.Fail);
                }

            }
        });
        return requestCode;
    }

    public static void writeImage(final Bitmap img, final String key_name,
                                  final boolean isReplace) {

        pool.execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(key_name);
                File path = new File(f.getParent());
                if (!path.exists()) {
                    path.mkdirs();
                }
                if (f.exists()) {
                    if (isReplace) {
                        deleteFileSafely(f);
                    } else
                        return;
                } else {
                    try {
                        f.createNewFile();
                        FileOutputStream out = new FileOutputStream(f);
                        img.compress(Bitmap.CompressFormat.JPEG, 50, out);
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    public static Bitmap readImageFromSDCacheSync(final String key_name,
                                                  float newHeight, float newWidth) {

        // 先尝试读缓存
        Bitmap result = null;
        try {
            result = ImageCache.getInstances().getBitmapFromMemCache(key_name);
        } catch (Exception e) {
            result = null;
        }
        if (result != null) {
            MyLog.writeLog("新图片缓存策略", key_name + ":缓存命中");
            return result;
        } else {
            ImageCache.getInstances().removeImageCache(key_name);
        }
        MyLog.writeLog("新图片缓存策略", key_name + ":缓存未命中");
        File f = new File(image_cache, key_name);
        if (f.exists()) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;// 只读边,不读内容
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            newOpts.inJustDecodeBounds = false;
            int rate = 1;
            int rate_tmp = (int) (newOpts.outWidth / newWidth);
            if (rate_tmp > rate)
                rate = rate_tmp;
            rate_tmp = (int) (newOpts.outHeight / newHeight);
            if (rate_tmp > rate)
                rate = rate_tmp;
            newOpts.inSampleSize = rate;// 设置采样率
            newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
            newOpts.inPurgeable = true;// 同时设置才会有效
            newOpts.inInputShareable = true;// 当系统内存不够时候图片自动被回收
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            MyLog.writeLog("新图片缓存策略", key_name + ":加入缓存");
            ImageCache.getInstances().addBitmapToMemoryCache(key_name, result);
            return bitmap;
        } else {
            return null;
        }

    }

    public static Bitmap readImageFromSDCacheSync(ActionInfo info,
                                                  float newHeight, float newWidth) {
        return readImageFromSDCacheSync(Md5.getMD5(info.actionImagePath),
                newHeight, newWidth);
    }

    public static void readImageFromSDCacheASync(final long request_code,
                                                 final String key_name, final float newHeight, final float newWidth,
                                                 final IFileListener listener) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                listener.onReadImageFinish(
                        readImageFromSDCacheSync(key_name, newHeight, newWidth),
                        request_code);
            }
        });
    }

    public static String readFileStringSync(String file_path, String file_name) {
        File file = new File(file_path, file_name);
        if (!file.exists()) {
            // 文件不存在
            return "";
        }
        try {
            FileInputStream f_in = new FileInputStream(file);
            String result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(f_in, "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            // 读取成功
            return result;

        } catch (Exception e) {
            // 文件读取异常
            return "";
        }
    }

    public static void readFileString(final String file_path, final String file_name,
                                      final long request_code, final IFileListener listener) {

        pool.execute(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onReadFileStrFinish("", readFileStringSync(file_path, file_name), true, request_code);
            }
        });


    }

    public static void writeFileString(final String file_path,
                                       final String file_name, final long request_code,
                                       final String content, final IFileListener listener) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File path = new File(file_path);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    File file = new File(file_path, file_name);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file, false);
                    fw.write(content);
                    fw.close();
                    if (listener != null)
                        listener.onWriteFileStrFinish("", true, request_code);
                } catch (Exception e) {
                    if (listener != null)
                        listener.onWriteFileStrFinish(e.getMessage(), false,
                                request_code);
                }
            }
        });
    }

    public static void clearCacheSize(final IFileListener listener) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                File cacheList = new File(FileTools.image_cache);
                String[] cacheListFileNames = cacheList
                        .list(new FilenameFilter() {
                            public boolean accept(File f, String name) {
                                return name.indexOf(".") == -1;
                            }
                        });
                if (cacheListFileNames == null) {
                    listener.onClearCache();
                    return;
                }
                for (int i = 0; i < cacheListFileNames.length; i++) {
                    File file = new File(FileTools.image_cache,
                            cacheListFileNames[i]);
                    if (file.exists()) {
                        try {
                            deleteFileSafely(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                listener.onClearCache();
            }
        });
    }

    public static void readCacheSize(final IFileListener listener) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                File cacheList = new File(FileTools.image_cache);
                String[] cacheListFileNames = cacheList
                        .list(new FilenameFilter() {
                            public boolean accept(File f, String name) {
                                return name.indexOf(".") == -1;
                            }
                        });
                if (cacheListFileNames == null) {
                    listener.onReadCacheSize(0);
                    return;
                }
                int totle = 0;
                for (int i = 0; i < cacheListFileNames.length; i++) {
                    File file = new File(FileTools.image_cache,
                            cacheListFileNames[i]);
                    if (file.exists()) {
                        try {
                            totle += file.length();
                        } catch (Exception e) {
                            totle += 0;
                        }
                    }
                }
                listener.onReadCacheSize(totle);
            }
        });
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    // android 选择文件
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static file_type getType(String file_name) {
        file_type type = file_type.unknown;
        String file_name_s = file_name.toLowerCase();
        for (int i = 0; i < img_suffix.length; i++) {

            if (file_name_s.contains(img_suffix[i])) {
                type = file_type.photo;
                break;
            }
        }
        for (int i = 0; i < video_suffix.length; i++) {
            if (file_name_s.contains(video_suffix[i])) {
                type = file_type.video;
                break;
            }
        }
        return null;

    }

    public static byte[] packData(String saveDir, int maxFrame) {
        byte[] lenFrame = ByteHexHelper.intToTwoHexBytes(maxFrame);// 文件总帧数
        byte[] name;
        try {
            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                name = saveDir.getBytes("UTF-8");
            }else {
                name = saveDir.getBytes("GBK");
            }

            byte lenName = (byte) name.length;
            int len = name.length + 3;
            byte[] data = new byte[len];
            data[0] = lenName;
            System.arraycopy(name, 0, data, 1, name.length);
            data[len - 2] = lenFrame[1];
            data[len - 1] = lenFrame[0];
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator
                    + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    public static void DeleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (File f : childFile) {
                DeleteFile(f);
            }
            deleteFileSafely(file);
        }
    }


    public static boolean writeAssetsToSd(String file_name, Context context, String file_abs_path) {


        File obj_file = new File(file_abs_path);

        if (obj_file.exists()){
            /*UbtLog.d(TAG,"obj_file = " + obj_file.getPath() + "   exists " + obj_file.exists()
                    + "   isFile = " + obj_file.isFile() + "  length = " + obj_file.length());*/
            if(obj_file.isFile() && obj_file.length() !=0 ){
                return true;
            }else {
                obj_file.delete();
            }
        }

        if (!obj_file.getParentFile().exists()) {
            obj_file.getParentFile().mkdirs();
        }


        try {
            obj_file.createNewFile();
            InputStream inputStream = context.getAssets()
                    .open(file_name);
            FileOutputStream fout = new FileOutputStream(obj_file);
            byte[] buffer = new byte[1024 * 10];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                fout.write(buffer, 0, len);
            }
            inputStream.close();
            fout.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkFile(String file_path) {
        if (TextUtils.isEmpty(file_path)) {
            return false;
        }
        File file = new File(file_path);
        if (file.exists())
            return true;
        return false;
    }

    public static List<ActionRecordInfo> getDownloadHtsFile(List<ActionRecordInfo> list){
        if(tempList == null){
            tempList = new ArrayList<>();
        }else {
            tempList.clear();
        }

        File actions_path = new File(FileTools.actions_download_cache);
        if (!actions_path.exists()) {
            return list;
        }

        getHtsFile(actions_path);

        try{
            List<Map<String,String>> mList = new ArrayList<>();
            //此处有时候报空指针异常，正常操作是不会的，暂时不知道为啥.... tempList有null
            tempList.removeAll(YHDCollectionUtils.nullCollection());
            mList.addAll(tempList);

            for (Map<String,String> map : mList){
                //此处有时候报空指针异常，正常操作是不会的，暂时不知道为啥....
                if(map == null || map.get("fileName") == null){
                    UbtLog.e("FileTools","---map--1-" + map + " ---- " + tempList);
                    continue;
                }
                String fileName = map.get("fileName");
                for(ActionRecordInfo info : list){
                    if(fileName.equals(info.action.hts_file_name)){
                        tempList.remove(map);
                        break;
                    }
                }
            }

            mList = new ArrayList<>();
            List<ActionRecordInfo> newList = new ArrayList<>();
            newList.addAll(list);

            mList.addAll(tempList);

            for(Map<String,String> map : mList){
                if(map == null || map.get("fileName") == null){
                    UbtLog.e("FileTools","---map--2-" + map + " ---- " + tempList);
                    continue;
                }
                newList.add(ConvertToModel(map));
            }
            return newList;
        }catch (Exception ex){
            UbtLog.e("FileTools","--ex--"+ ex.getMessage());
            return list;
        }

        //return list;
    }

    public static void getHtsFile(File file){
        try{
            if (file.isFile()){
                if(file.getName().endsWith(".hts")){
                    Map<String,String> map = new HashMap<>();
                    String actionId = file.getParent().substring(FileTools.actions_download_cache.length()+1);
                    //UbtLog.d("lihai","lihai------------file.getName()->>"+file.getName()+"--"+file.getPath()+"----"+actionId);
                    if(actionId.contains(File.separator)){
                        file.delete();
                        //脏数据 ubt_alpha/actions/5012/报川菜1/报川菜1.hts
                        //actionId = actionId.split(File.separator)[0];
                    }else{
                        map.put("fileName",file.getName());
                        map.put("filePath",file.getPath());
                        map.put("actionId",actionId);
                        tempList.add(map);
                    }
                }
            }else {
                File[] files = file.listFiles();
                for (File tmpFile : files){
                    getHtsFile(tmpFile);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            UbtLog.e(TAG,"getHtsFile : " +  ex.getMessage());
        }
    }

    private static ActionRecordInfo ConvertToModel(Map<String,String> map) {

        ActionRecordInfo model = new ActionRecordInfo();
        model.action = new ActionInfo();

        model.action.actionId = Integer.parseInt(map.get("actionId"));
        model.action.actionName = map.get("fileName").split("\\.")[0];
        model.action.actionTitle = "";
        model.action.actionType = 0;
        model.action.actionImagePath = "";
        model.action.actionPath = "";
        model.action.actionVideoPath = "";
        model.action.actionDownloadTime = 0;
        model.action.actionPraiseTime = 0;
        model.action.actionDesciber = null;
        model.action.actionTime = 0;
        model.action.hts_file_name = map.get("fileName");
        model.action.isCollect = 0;
        model.action.isPraise = 0;
        model.action.actionBrowseTime = 0;
        model.isDownLoadSuccess = true;

        return model;
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName
     *            待复制的文件名
     * @param destFileName
     *            目标文件名
     * @param overlay
     *            如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName,
                                   boolean overlay) {
        File srcFile = new File(srcFileName);

        // 判断源文件是否存在
        if (!srcFile.exists()) {
            UbtLog.d(TAG,"源文件：" + srcFileName + "不存在！");
            return false;
        } else if (!srcFile.isFile()) {
            UbtLog.d(TAG,"复制文件失败，源文件：" + srcFileName + "不是一个文件！");
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }else {
                return true;
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //修改文件名称
    public static boolean renameFile(String file, String toFile) {

        File toBeRenamed = new File(file);
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {

            System.out.println("File does not exist: " + file);
            return false;
        }

        File newFile = new File(toFile);

        //修改文件名
        if (toBeRenamed.renameTo(newFile)) {
            System.out.println("File has been renamed.");
            return  true;
        } else {
            System.out.println("Error renmaing file");
            return false;
        }

    }

    /**
     * 读取指定文件夹下所有的文件
     * @param filepath
     * @return
     */

    public static List<String> readFiles(String filepath) {
        List<String> fileNames = new ArrayList<String>();
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                Log.d(TAG, "is not directory");
                return fileNames;

            } else if (file.isDirectory()) {
                String[] fileLists = file.list();
                for (int i = 0; i < fileLists.length; i++) {
                    File readFile = new File(filepath + File.separator + fileLists[i]);
                    if (!readFile.isDirectory()) {
                        System.out.println("path=" + readFile.getPath());
                        System.out.println("absolutePath="
                                + readFile.getAbsolutePath());
                        System.out.println("name=" + readFile.getName());
                        fileNames.add(readFile.getName());
                    } else if (readFile.isDirectory()) {
                        readFiles(filepath + File.separator  + fileLists[i]);
                    }
                }

            }

        } catch (Exception e) {
            Log.d(TAG, "readFiles Exception:" + e.getMessage());
        }
        return fileNames;
    }


    public static String readFileOneLine(String file_path) {
        File file = new File(file_path);
        if (!file.exists()) {
            // 文件不存在
            return "";
        }
        try {
            FileInputStream f_in = new FileInputStream(file);
            String result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(f_in, "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result = line;
                break;
            }
            // 读取成功
            return result;

        } catch (Exception e) {
            // 文件读取异常
            return "";
        }
    }

    /**
     * 获取手机内部存储空间
     *
     * @param context
     * @return 以M,G为单位的容量
     */
    public static String getInternalMemorySSize(Context context) {
        return Formatter.formatFileSize(context, getInternalMemoryLSize(context));
    }

    /**
     * 获取手机内部存储空间
     *
     * @param context
     * @return 以M,G为单位的容量
     */
    public static long getInternalMemoryLSize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        long size = blockCountLong * blockSizeLong;
        return size;
    }
}
