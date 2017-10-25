package com.ubt.alpha1e.business;

import android.content.Context;

import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.JsonTools;
import com.ubt.alpha1e.utils.log.MyLog;
import com.ubt.alpha1e.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.net.http.basic.HttpAddress.Request_type;
import com.ubt.alpha1e.net.http.basic.IJsonListener;

import org.json.JSONArray;

import java.util.concurrent.LinkedBlockingQueue;

public class ResourcesDownloadManager implements IJsonListener {

    private static ResourcesDownloadManager thiz;
    private static Context mContext;
    private static LinkedBlockingQueue<String[]> file_paths;
    // --------------------------------------------start
    private static long msg_get_actio_lib_banas = 1001;


    // --------------------------------------------end
    private ResourcesDownloadManager() {
    }

    ;

    public static ResourcesDownloadManager getInstance(Context _context) {
        if (thiz == null) {
            thiz = new ResourcesDownloadManager();
        }
        thiz.mContext = _context;
        if (file_paths == null)
            file_paths = new LinkedBlockingQueue<String[]>();
        file_paths.clear();
        return thiz;
    }

    public void startDownloadBanas() {
        MyLog.writeLog("Service_test", "startDownloadBanas");
        GetDataFromWeb.getJsonByPost(msg_get_actio_lib_banas, HttpAddress
                .getRequestUrl(Request_type.get_bana_imgs), HttpAddress
                .getParamsForPost(new String[]{"3"},
                        Request_type.get_bana_imgs, mContext), this);

        // --------------------------------
//        downLoadThread.start();
    }

    public void addDownLoadTask(String url, String file_path) {
        try {
            file_paths.put(new String[]{url, file_path});
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        if (msg_get_actio_lib_banas == request_code
                && JsonTools.getJsonStatus(json)) {
            JSONArray list = JsonTools.getJsonModels(json);
            for (int i = 0; i < list.length(); i++) {
                try {
                    MyLog.writeLog("Service_test", list.getJSONObject(i)
                            .getString("recommendImage"));
                    String url = list.getJSONObject(i).getString(
                            "recommendImage");
                    String file_name = url.substring(url.lastIndexOf("/") + 1);
                    file_paths.put(new String[]{url,
                            FileTools.image_cache + "/" + file_name});
                } catch (Exception e) {
                    MyLog.writeLog("Service_test", e.getMessage());
                }
            }
        }

    }

    private Thread downLoadThread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    String[] url_filePath = file_paths.take();
                    MyLog.writeLog("Service_test", url_filePath[0] + "-->"
                            + url_filePath[1]);
                    GetDataFromWeb.getFileFromHttp(-1, url_filePath[0],
                            url_filePath[1], null);
                } catch (InterruptedException e) {
                    MyLog.writeLog("Service_test", e.getMessage());
                }
            }
        }
    };
}
