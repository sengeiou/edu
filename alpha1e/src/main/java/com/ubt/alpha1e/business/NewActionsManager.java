package com.ubt.alpha1e.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.BasicSharedPreferencesOperator.DataType;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.FileTools.State;
import com.ubt.alpha1e.data.IFileListener;
import com.ubt.alpha1e.data.ZipTools;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.DubActivity;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;

public class NewActionsManager implements IFileListener {

    private static final String TAG = "NewActionsManager";
    private static NewActionsManager thiz;
    public boolean isSaveSuccess = false;
    private Context mContext;
    private List<NewActionsManagerListener> mListenerLists;

    private int do_read_new_actions_for_play = 10001;
    private int do_read_new_actions_for_update = 10002;
    private int do_read_new_actions_for_delete = 10003;
    private int do_read_new_actions_for_save = 10004;
    private int do_record_new_actions_for_change = 10005;
    private int do_write_sync_actions = 10006;

    public  ExecutorService single_pool = Executors
            .newSingleThreadExecutor();
    public NewActionInfo mChangeNewActionInfo;
    private List<NewActionInfo> mSyncActionInfos = new ArrayList<>();

    private NewActionsManager() {
    }


    public static NewActionsManager getInstance(Context _context) {
        if (thiz == null) {
            thiz = new NewActionsManager();
            thiz.mListenerLists = new ArrayList<NewActionsManagerListener>();
        }
        thiz.mContext = _context.getApplicationContext();
        return thiz;
    }

    // ��Ӽ�����
    public void addListener(NewActionsManagerListener listener) {
        if (!mListenerLists.contains(listener))
            mListenerLists.add(listener);
    }

    // �Ƴ�������
    public void removeListener(NewActionsManagerListener listener) {
        if (mListenerLists.contains(listener))
            mListenerLists.remove(listener);
    }

    public void doRead() {

        FileTools.readFileString(FileTools.actions_new_cache,
                FileTools.actions_new_log_name,
                do_read_new_actions_for_play, this);
    }

    public void doWriteSyncListToFile(List<NewActionInfo> newActionInfos) {
        mSyncActionInfos = newActionInfos;
        FileTools.readFileString(FileTools.actions_new_cache,
                FileTools.actions_new_log_name,
                do_write_sync_actions, thiz);
    }


    public void doSave(NewActionInfo info, final String musicDir) {
        mChangeNewActionInfo = info;
        mChangeNewActionInfo.actionOriginalId = System.currentTimeMillis();
        mChangeNewActionInfo.actionId = mChangeNewActionInfo.actionOriginalId;
        final String mDir = FileTools.actions_new_cache + File.separator + mChangeNewActionInfo.actionOriginalId + "";
        String mFileName = mChangeNewActionInfo.actionOriginalId + ".hts";
        String filePath = mDir + File.separator + mFileName;
        mChangeNewActionInfo.actionPath_local = filePath;
        mChangeNewActionInfo.actionDir_local = mDir;
        mChangeNewActionInfo.actionZip_local = FileTools.actions_new_cache + File.separator + mChangeNewActionInfo.actionOriginalId + ".zip";
        File path = new File(mDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        UbtLog.d(TAG, "mChangeNewActionInfo=" + mChangeNewActionInfo.toString());
        UbtLog.d(TAG, "musicDir=" + musicDir);



        HtsHelper.writeHts(mChangeNewActionInfo, filePath, new IHtsHelperListener() {
            @Override
            public void onHtsWriteFinish(boolean isSuccess) {
                if (isSuccess) {
                    if(musicDir != "" && musicDir != null){
                        copyFile(musicDir, mDir + File.separator+mChangeNewActionInfo.actionOriginalId + ".mp3" );
                    }
                    zipHtsActions(mChangeNewActionInfo);
                    saveActionToServer();

                } else {
                    isSaveSuccess = false;
                }
            }

            @Override
            public void onGetNewActionInfoFinish(boolean isSuccess) {

            }
        });

    }


    public void doSave(NewActionInfo info, long dubTag, int type) {
        mChangeNewActionInfo = info;
        mChangeNewActionInfo.actionOriginalId = System.currentTimeMillis();
        UbtLog.d(TAG, "actionOriginalId=" +   mChangeNewActionInfo.actionOriginalId + "---dubTag" + dubTag);
        String convert = "" +  mChangeNewActionInfo.actionOriginalId + 1;
        UbtLog.d(TAG, "convert=" + convert);
        mChangeNewActionInfo.actionOriginalId = Long.parseLong(convert);
        mChangeNewActionInfo.actionId = mChangeNewActionInfo.actionOriginalId;
        String mDir = FileTools.actions_new_cache + File.separator + mChangeNewActionInfo.actionOriginalId + "";
        String mFileName = mChangeNewActionInfo.actionOriginalId + ".hts";
        String filePath = mDir + File.separator + mFileName;
        mChangeNewActionInfo.actionPath_local = filePath;
        mChangeNewActionInfo.actionDir_local = mDir;
        mChangeNewActionInfo.actionZip_local = FileTools.actions_new_cache + File.separator + mChangeNewActionInfo.actionOriginalId + ".zip";
        File path = new File(mDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        UbtLog.d(TAG, "mChangeNewActionInfo=" + mChangeNewActionInfo.toString());
        //开始拷贝文件
        if(type == DubActivity.TYPE_CREATE){
            UbtLog.d(TAG, "create new action");
            File fileFold =  new File(FileTools.actions_new_cache + File.separator+ dubTag);
            String[] action_files = fileFold.list(new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return name.endsWith(".hts");
                }
            });

            if(action_files != null && action_files.length !=0){
                UbtLog.d(TAG, "action_files[0]=" + action_files[0].toString());
                copyFile(fileFold + File.separator +  action_files[0], filePath);
            }else{
                UbtLog.e(TAG, "not have hts file!");
            }

//            copyFile(FileTools.actions_new_cache + File.separator+ dubTag + File.separator + "1476342972456.hts", filePath);
        }else if(type == DubActivity.TYPE_DOWNLOAD){

            File fileFold =  new File(FileTools.actions_download_cache + File.separator+ dubTag);
            String[] action_files = fileFold.list(new FilenameFilter() {
                public boolean accept(File f, String name) {
                    return name.endsWith(".hts");
                }
            });

            if(action_files != null && action_files.length !=0){
                copyFile(fileFold + File.separator +  action_files[0], filePath);
            }

        }


        copyFile( FileTools.actions_new_cache+File.separator+"tep_audio_recorder_for_mp3.mp3", mDir + File.separator+mChangeNewActionInfo.actionOriginalId + ".mp3" );
        zipHtsActions(mChangeNewActionInfo);
        saveActionToServer();

    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
           UbtLog.e(TAG, "复制单个文件操作出错");
            e.printStackTrace();

        }
    }


    public void zipHtsActions(NewActionInfo info) {
        try {
            ZipTools.doZip(info.actionDir_local, info.actionZip_local);
        } catch (IOException e) {
            e.printStackTrace();
            isSaveSuccess = false;
        }


    }

    /**
     * 保存动作到服务器
     */
    public void saveActionToServer() {

        try {
//            if(((AlphaApplication) mContext
//            ).getCurrentUserInfo() == null)
//            {
//                isSaveSuccess = false;
//                notifyListeners();
//                Toast.makeText(mContext,"请先登录",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            long actionUserId = ((AlphaApplication) mContext).getCurrentUserInfo().userId;
            String actionUserId = SPUtils.getInstance().getString(Constant.SP_USER_ID);
            UbtLog.d(TAG, "actionUserId:" + actionUserId);

            File file = new File(FileTools.actions_new_cache + File.separator + mChangeNewActionInfo.actionId + ".zip");
            File imageFile;
            if (!TextUtils.isEmpty(mChangeNewActionInfo.actionHeadUrl)) {
                imageFile = new File(mChangeNewActionInfo.actionHeadUrl);
            } else {
                imageFile = new File(FileTools.actions_new_cache + File.separator + "Images/" + "default.jpg");

            }


//            Map<String, File> fileMap = new HashMap<>();
//            fileMap.put(imageFile.getName(),imageFile);
//            fileMap.put(file.getName(),file);
            Map<String, String> params =HttpAddress.getBasicParamsMap(mContext);

            params.put("actionOriginalId", mChangeNewActionInfo.actionId + "");
            params.put("actionUserId", actionUserId + "");
            params.put("actionName", mChangeNewActionInfo.actionName);
            params.put("actionDesciber", mChangeNewActionInfo.actionDesciber);
            params.put("actionType", mChangeNewActionInfo.actionType + "");
            params.put("actionTime", mChangeNewActionInfo.actionTime+ "");
            String url = HttpAddress.getRequestUrl(HttpAddress.Request_type.createaction_upload);
            OkHttpUtils.post()//
                    .addFile("mFile1", file.getName(), file)//
                    .addFile("mFile2", imageFile.getName(), imageFile)
                    .url(url)//
                    .params(params)//
                    .build()//
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e,int i) {
                            UbtLog.d(TAG, "onResponse:" + e.getMessage());
                            isSaveSuccess = false;
                            notifyListeners();
                        }

                        @Override
                        public void onResponse(String s,int i) {
                            UbtLog.d(TAG, "onResponse:" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                if ((Boolean) json.get("status")) {
                                    isSaveSuccess = true;
                                    NewActionInfo newActionInfo = new Gson().fromJson(json.get("models").toString(), NewActionInfo.class);
                                    mChangeNewActionInfo.actionHeadUrl = newActionInfo.actionHeadUrl;
                                    mChangeNewActionInfo.actionUrl = newActionInfo.actionUrl;
                                    FileTools.readFileString(FileTools.actions_new_cache,
                                            FileTools.actions_new_log_name,
                                            do_read_new_actions_for_save, thiz);

                                }
                            } catch (Exception e) {
                                isSaveSuccess = false;
                                notifyListeners();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            isSaveSuccess = false;
            notifyListeners();
        }

    }

    public void doRename(NewActionInfo info) {
        doUpdate(info);
    }

    public void doUpdate(NewActionInfo info) {
        mChangeNewActionInfo = info;

        FileTools.readFileString(FileTools.actions_new_cache,
                FileTools.actions_new_log_name,
                do_read_new_actions_for_update, this);
    }

    public void doDelete(NewActionInfo info) {
        mChangeNewActionInfo = info;

        FileTools.readFileString(FileTools.actions_new_cache,
                FileTools.actions_new_log_name,
                do_read_new_actions_for_delete, this);
    }

    @Override
    public void onReadImageFinish(Bitmap img, long request_code) {
        // TODO Auto-generated method stub
        UbtLog.d(TAG, "wmma onReadImageFinish");

    }

    @Override
    public void onReadFileStrFinish(String erroe_str, String result,
                                    boolean result_state, long request_code) {
        UbtLog.d(TAG, "wmma onReadFileStrFinish");

        if (request_code == do_read_new_actions_for_play
                || request_code == do_read_new_actions_for_update
                || request_code == do_read_new_actions_for_delete
                || request_code == do_read_new_actions_for_save
                || request_code == do_write_sync_actions) {

            ArrayList<NewActionInfo> infos = new ArrayList<NewActionInfo>();

            if (!result.equals(BasicSharedPreferencesOperator.NO_VALUE)) {
                try {
                    JSONArray action_list;
                    //UbtLog.d(TAG, "result=" + result);
                    if(!result.equals("") || !result.equals("null")){
                        action_list = new JSONArray(result);//""
                        for (int i = 0; i < action_list.length(); i++) {
                            JSONObject jsonObject = action_list.getJSONObject(i);
                            infos.add(new NewActionInfo().getThiz(jsonObject));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (request_code == do_read_new_actions_for_play) {
                for (int i = 0; i < mListenerLists.size(); i++) {
                    mListenerLists.get(i).onReadNewActionsFinish(infos);
                }
                return;
            } else {

                if (request_code == do_read_new_actions_for_save) {
                    //倒序,添加到第一个
                    infos.add(0,mChangeNewActionInfo);
                } else if (request_code == do_write_sync_actions) {
                    infos.addAll(0,mSyncActionInfos);
                } else {

                    for (int i = 0; i < infos.size(); i++) {
                        if (infos.get(i).actionOriginalId == mChangeNewActionInfo.actionOriginalId) {
                            if (request_code == do_read_new_actions_for_delete) {
                                infos.remove(i);
                            } else {
                                infos.remove(i);
                                infos.add(i, mChangeNewActionInfo);
                            }
                        }

                    }
                }

                FileTools.writeFileString(FileTools.actions_new_cache,
                        FileTools.actions_new_log_name,
                        do_record_new_actions_for_change,
                        NewActionInfo.getString(infos), this);
            }

        }
    }

    private void notifyListeners() {
        for (int i = 0; i < mListenerLists.size(); i++) {
            mListenerLists.get(i).onChangeNewActionsFinish();
        }
    }

    @Override
    public void onWriteFileStrFinish(String erroe_str, boolean result,
                                     long request_code) {

        if (request_code == do_record_new_actions_for_change) {
            for (int i = 0; i < mListenerLists.size(); i++) {
                mListenerLists.get(i).onChangeNewActionsFinish();
            }

            // �޸��±༭����״̬
            BasicSharedPreferencesOperator.getInstance(mContext,
                    DataType.USER_USE_RECORD).doWrite(
                    BasicSharedPreferencesOperator.IS_NEW_NEW_ACTION,
                    BasicSharedPreferencesOperator.IS_NEW_NEW_ACTION_TRUE,
                    null, -1);

            return;
        }
    }

    @Override
    public void onWriteDataFinish(long requestCode, State state) {
        // TODO Auto-generated method stub
        UbtLog.d(TAG, "wmma onWriteDataFinish");

    }

    @Override
    public void onReadCacheSize(int size) {
        // TODO Auto-generated method stub
        UbtLog.d(TAG, "wmma onReadCacheSize");

    }

    @Override
    public void onClearCache() {
        // TODO Auto-generated method stub
        UbtLog.d(TAG, "wmma onClearCache");

    }
}
