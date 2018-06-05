package com.ubt.alpha1e_edu.net.http.basic;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.ubt.alpha1e_edu.business.OngetThumbnailsListener;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.data.ImageTools;
import com.ubt.alpha1e_edu.data.Md5;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener.State;
import com.ubt.alpha1e_edu.utils.cache.ImageCache;
import com.ubt.alpha1e_edu.utils.connect.ConnectClientUtil;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.ubt.alpha1e_edu.utils.log.MyLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class GetDataFromWeb {

    private static final String TAG = "GetDataFromWeb";
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    /*
   * Gets the number of available cores
   * (not always the same as the maximum number of cores)
   */
    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    private static int MAX_OF_CORES = NUMBER_OF_CORES*2+1;
    // Creates a thread pool manager
    // A queue of Runnables
    private static final BlockingQueue<Runnable> sPoolWorkQueue  = new LinkedBlockingQueue<Runnable>();
    private static final BlockingQueue<Runnable> sGetJsonPoolWorkQueue  = new LinkedBlockingQueue<Runnable>();
    private static final BlockingQueue<Runnable> sDownloadPoolWorkQueue  = new LinkedBlockingQueue<Runnable>();
    //此时NUMBER_OF_CORES个线程并发，如果还有任务进来，就放入sPoolWorkQueue队列中等待运行
    public static ExecutorService pool = new ThreadPoolExecutor(NUMBER_OF_CORES,MAX_OF_CORES,KEEP_ALIVE_TIME,KEEP_ALIVE_TIME_UNIT,sPoolWorkQueue);
    public static ExecutorService get_json_pool = new ThreadPoolExecutor(NUMBER_OF_CORES,MAX_OF_CORES,KEEP_ALIVE_TIME,KEEP_ALIVE_TIME_UNIT,sGetJsonPoolWorkQueue);
    public static ExecutorService download_pool = new ThreadPoolExecutor(NUMBER_OF_CORES,MAX_OF_CORES,KEEP_ALIVE_TIME,KEEP_ALIVE_TIME_UNIT,sDownloadPoolWorkQueue);
    public static ExecutorService single_pool = Executors.newSingleThreadExecutor();

    public static final long STOP_FINISH_CODE = -9999;
    public static long stop_down_load_request_code = STOP_FINISH_CODE;

    public static long getJsonByGet(final String _url, final String _params,
                                    final IJsonListener _listener) {
        MyLog.writeLog("网络功能", _url + "-->" + _params);
        final long request_code = new Date().getTime();
        pool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    String urlStr = _url + _params;
                    HttpURLConnection conn = getHttpURLConnectionByUrl(urlStr);

                    conn.setConnectTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        String jsonStr = "";
                        if (1 == 0) {
                            int byteRead;
                            while ((byteRead = in.read()) != -1) {
                                jsonStr += (char) byteRead;
                            }
                        } else {
                            InputStreamReader inputStreamReader = new InputStreamReader(
                                    in, "UTF-8");
                            BufferedReader b_in = new BufferedReader(
                                    inputStreamReader);
                            jsonStr = b_in.readLine().toString();
                        }
                        in.close();
                        MyLog.writeLog("网络功能", "收到回复：" + jsonStr);
                        _listener.onGetJson(true, jsonStr, request_code);
                    } else {
                        MyLog.writeLog("网络功能", "数据请求失败："
                                + _listener.RETURN_FAIL);
                        _listener.onGetJson(false, _listener.RETURN_FAIL,
                                request_code);
                    }
                } catch (Exception e) {
                    MyLog.writeLog("网络功能", "数据请求失败：" + e.getMessage());
                    _listener.onGetJson(false, e.getMessage(), request_code);
                }
            }
        });

        return request_code;

    }

    /**
     * 获取json数据
     * @param request_code
     * @param _url
     * @param _params
     * @param _listener
     * @return
     */
    public static GetJsonByPostRunnable getJsonByPost(long request_code,
                                                      String _url, String _params, IJsonListener _listener) {
        MyLog.writeLog("网络功能", _url + "-->" + _params);
        GetJsonByPostRunnable runnable = new GetJsonByPostRunnable(
                request_code, _url, _params, _listener);
        //pool.execute(runnable);
        get_json_pool.execute(runnable);
        return runnable;
    }

    public static void getImageFromHttp(final String url_path,
                                        final long _request_code, final IImageListener _listener,
                                        final float h, final float w, final int kb,
                                        final boolean isConvert, final int corners) {

        // 转非标准网络路径

        single_pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int the_lastString = -1;
                    String _name = "";
                    try {
                        the_lastString = url_path.lastIndexOf("/");
                        String name = url_path.substring(the_lastString + 1,
                                url_path.length());

                        if (isConvert)
                            _name = android.net.Uri.encode(name, "US-ASCII");
                        else
                            _name = name;
                    } catch (Exception e) {
                        _listener.onGetImage(false, null, _request_code);
                        return;
                    }
                    final String new_url = url_path
                            .substring(0, the_lastString) + "/" + _name;
                    MyLog.writeLog("新图片缓存策略", "转换URL：" + url_path + "-->"
                            + new_url);
                    // 尝试内存缓存
                    Bitmap result = null;
                    try {
                        result = ImageCache.getInstances()
                                .getBitmapFromMemCache(Md5.getMD5(new_url));

                    } catch (Exception e) {
                        MyLog.writeLog("新图片缓存策略", new_url + ":缓存读取异常");
                    }

                    if (result != null) {
                        _listener.onGetImage(true, result, _request_code);
                        MyLog.writeLog("新图片缓存策略", new_url + ":缓存命中");
                        return;
                    } else {
                        ImageCache.getInstances().removeImageCache(
                                Md5.getMD5(new_url));
                    }
                    // 尝试外存缓存
                    File tmp_File = new File(FileTools.image_cache, Md5
                            .getMD5(new_url) + "");
                    if (tmp_File.exists()) {
                        result = ImageTools
                                .compressImage(tmp_File, w, h, false);
                        if (corners > 0) {
                            result = ImageTools.toRoundCorner(result, corners);
                        }
                        MyLog.writeLog("新图片缓存策略", new_url + ":外存缓存命中");
                        _listener.onGetImage(true, result, _request_code);
                        MyLog.writeLog("新图片缓存策略", new_url + ":加入缓存");
                        ImageCache.getInstances().addBitmapToMemoryCache(
                                Md5.getMD5(new_url), result);
                        return;
                    }

                    // 联网获取
                    HttpURLConnection connection = getHttpURLConnectionByUrl(new_url);
                    connection.setConnectTimeout(5000);
                    connection.connect();
                    if (connection.getResponseCode() == 200) {

                        final InputStream inputStream = connection
                                .getInputStream();

                        result = ImageTools.compressImageSync(inputStream, w,
                                h, Md5.getMD5(new_url));

                        if (kb > 0) {
                            result = ImageTools.compressImage(result, kb);
                        }

                        if (corners > 0) {
                            result = ImageTools.toRoundCorner(result, corners);
                        }

                        MyLog.writeLog("新图片缓存策略", new_url + ":下载完成");
                        _listener.onGetImage(true, result, _request_code);
                        MyLog.writeLog("新图片缓存策略", new_url + ":加入缓存");

                        ImageCache.getInstances().addBitmapToMemoryCache(
                                Md5.getMD5(new_url), result);
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        _listener.onGetImage(false, null, _request_code);
                    }
                } catch (Exception e) {
                    _listener.onGetImage(false, null, _request_code);
                    MyLog.writeLog("新图片缓存策略", e.getMessage());
                }
            }
        });
    }

    // 带自动的缓存策略
    public static void getImageFromHttp(final String url_path,
                                        final long _request_code, final IImageListener _listener,
                                        final float h, final float w, final int kb, final int corners) {
        getImageFromHttp(url_path, _request_code, _listener, h, w, kb, true,
                corners);
    }


    public static boolean getFileFromHttpSync(String url_web, String obj_file_obs_name) {

        url_web = url_web.trim();
        int lastString_index = -1;
        String web_name = "";
        try {
            lastString_index = url_web.lastIndexOf("/");
            String last_name = url_web.substring(lastString_index + 1, url_web.length());
            web_name = android.net.Uri.encode(last_name, "US-ASCII");
        } catch (Exception e) {
            return false;
        }
        if (url_web.isEmpty()) {
            return false;
        }
        final String url_str = url_web.substring(0, lastString_index) + "/" + web_name;
        MyLog.writeLog("网络功能", "下载文件：" + url_str + ",下载位置：" + obj_file_obs_name);
        final String file_name_tmp = obj_file_obs_name + ".tmp";
        File tmp_File = new File(file_name_tmp);
        try {
            // 正在下载的文件后缀名是XXX.tmp
            String file_path = tmp_File.getParent();
            File path = new File(file_path);
            if (!path.exists()) {
                path.mkdirs();
            }
            // 如果文件存在则删除
            if (tmp_File.exists()) {
                FileTools.DeleteFile(tmp_File);
            }

            HttpURLConnection connection = getHttpURLConnectionByUrl(url_str);
            connection.setConnectTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                tmp_File.createNewFile();
                FileOutputStream fout = new FileOutputStream(tmp_File);
                byte[] buffer = new byte[1024 * 10];
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1)
                        break;
                    fout.write(buffer, 0, len);
                }
                inputStream.close();
                fout.close();
                // 下载成功，删除原来的文件
                File old_file = new File(obj_file_obs_name);
                if (old_file.exists()) {
                    FileTools.DeleteFile(old_file);
                }
                // 重命名临时文件
                tmp_File.renameTo(old_file);
                return true;
            } else {
                // 连接失败，删除临时文件
                if (tmp_File.exists()) {
                    FileTools.DeleteFile(tmp_File);
                }
                return false;
            }
        } catch (Exception e) {
            // 下载失败，删除临时文件
            if (tmp_File.exists()) {
                FileTools.DeleteFile(tmp_File);
            }
            return false;
        }
    }

    public static void getFileFromHttp(final long request_code,
                                       final String file_url, final String _file_path_name,
                                       final FileDownloadListener _listener) {
        if (TextUtils.isEmpty(file_url)) {
            if (_listener != null){
                _listener.onDownLoadFileFinish(request_code, State.fail);
            }
            return;
        }

        final String http_url = file_url.trim();
        int the_lastString = -1;
        String _name = "";
        try {
            the_lastString = http_url.lastIndexOf("/");
            String name = http_url.substring(the_lastString + 1, http_url.length());
            _name = android.net.Uri.encode(name, "US-ASCII");
        } catch (Exception e) {
            _listener.onDownLoadFileFinish(request_code, State.fail);
            return;
        }

        final String _url = http_url.substring(0, the_lastString) + "/" + _name;
        MyLog.writeLog("网络功能", "下载文件：" + _url + ",下载位置：" + _file_path_name);

        final String file_name_tmp = _file_path_name + ".tmp";

        //pool.execute(new Runnable() {
        download_pool.execute(new Runnable() {

            @Override
            public void run() {
                File tmp_File = new File(file_name_tmp);
                try {
                    // 正在下载的文件后缀名是XXX.tmp
                    String file_path = tmp_File.getParent();
                    File path = new File(file_path);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    // 如果文件存在则删除
                    if (tmp_File.exists()) {
                        FileTools.DeleteFile(tmp_File);
                        // tmp_File.delete();
                    }

                    HttpURLConnection connection = getHttpURLConnectionByUrl(_url);
                    connection.setConnectTimeout(5000);
                    connection.connect();
                    if (connection.getResponseCode() == 200) {

                        double lenth = connection.getContentLength();
                        // 回报文件长度
                        double finish_lenth = 0;
                        if (_listener != null)
                            _listener.onGetFileLenth(request_code, lenth);
                        InputStream inputStream = connection.getInputStream();
                        tmp_File.createNewFile();
                        FileOutputStream fout = new FileOutputStream(tmp_File);
                        byte[] buffer = new byte[1024 * 10];
                        while (true) {
                            if (stop_down_load_request_code == request_code) {
                                break;
                            }
                            int len = inputStream.read(buffer);
                            if (len == -1)
                                break;
                            finish_lenth += len;
                            // 回报进度
                            if (_listener != null){
                                _listener.onReportProgress(request_code,(finish_lenth * 100 / lenth));
                            }

                            fout.write(buffer, 0, len);
                        }
                        inputStream.close();
                        fout.close();
                        if (stop_down_load_request_code == request_code) {
                            //停止完成，删除临时文件
                            if (tmp_File.exists()) {
                                FileTools.DeleteFile(tmp_File);
                            }
                            if (_listener != null)
                                _listener.onStopDownloadFile(request_code,
                                        State.success);
                            stop_down_load_request_code = STOP_FINISH_CODE;

                        } else {
                            // 下载成功，删除原来的文件
                            File old_file = new File(_file_path_name);
                            if (old_file.exists()) {
                                FileTools.DeleteFile(old_file);

                            }
                            // 重命名临时文件
                            tmp_File.renameTo(old_file);
                            if (_listener != null)
                                _listener.onDownLoadFileFinish(request_code,
                                        State.success);
                        }
                    } else {
                        // 连接失败，删除临时文件
                        if (tmp_File.exists()) {
                            FileTools.DeleteFile(tmp_File);

                        }
                        if (_listener != null)
                            _listener.onDownLoadFileFinish(request_code,
                                    State.connect_fail);
                    }

                } catch (Exception e) {
                    // 下载失败，删除临时文件
                    if (tmp_File.exists()) {
                        FileTools.DeleteFile(tmp_File);

                    }
                    if (_listener != null)
                        _listener
                                .onDownLoadFileFinish(request_code, State.fail);
                }
            }
        });

    }


    public static HttpURLConnection getHttpURLConnectionByUrl(String urlStr){
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlStr);
            if(urlStr.startsWith("https") && urlStr.contains(HttpAddress.WebAddressDevelop)){
                urlConnection = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(ConnectClientUtil.getSocketFactory());
            }else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return urlConnection;
    }

    public static void doStopFileDownLoad(long request_code) {
        if (stop_down_load_request_code == STOP_FINISH_CODE) {
            stop_down_load_request_code = request_code;
        }

    }

    public static void getWebVideoThumbnails(final String url, final int width,
                                             final int height, final OngetThumbnailsListener listener)
    {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                int kind = MediaStore.Video.Thumbnails.MINI_KIND;

                try {
                    if (Build.VERSION.SDK_INT >= 14) {
                        retriever.setDataSource(url, new HashMap<String, String>());
                    } else {
                        retriever.setDataSource(url);
                    }
                    bitmap = retriever.getFrameAtTime();

                } catch (IllegalArgumentException ex) {
                    // Assume this is a corrupt video file
                } catch (RuntimeException ex) {
                    // Assume this is a corrupt video file.
                } finally {
                    try {
                        retriever.release();
                    } catch (RuntimeException ex) {
                        // Ignore failures while cleaning up.
                    }
                }
                UbtLog.d(TAG,"getWebVideoThumbnails-url:" + url + "    bitmap:"+bitmap);

                if (/*kind == MediaStore.Images.Thumbnails.MICRO_KIND && */bitmap != null) {
                    if(bitmap.getWidth() > width){
                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    }
                }
                listener.onGetVideoThumbnail(bitmap);

            }
        });
    }
}