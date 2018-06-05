package com.ubt.alpha1e_edu.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.data.ISharedPreferensListenet;
import com.ubt.alpha1e_edu.data.JsonTools;
import com.ubt.alpha1e_edu.data.model.AlphaStatics;
import com.ubt.alpha1e_edu.data.model.ThemeInfo;
import com.ubt.alpha1e_edu.net.http.basic.FileDownloadListener;
import com.ubt.alpha1e_edu.net.http.basic.GetDataFromWeb;
import com.ubt.alpha1e_edu.net.http.basic.HttpAddress;
import com.ubt.alpha1e_edu.net.http.basic.IJsonListener;
import com.ubt.alpha1e_edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e_edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e_edu.ui.helper.ISettingUI;
import com.ubt.alpha1e_edu.ui.helper.SettingHelper;
import com.umeng.analytics.MobclickAgent;
import com.zhy.changeskin.SkinManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LanguageActivity extends BaseActivity implements ISettingUI, BaseDiaUI, IJsonListener, FileDownloadListener, ISharedPreferensListenet {


    private ListView mListViewLang;
    private List<Map<String, Object>> lst_map_languages = new LinkedList<>();
    private SimpleAdapter mLangAdapter;
//    private boolean isNeedRefreshLanguage = false;

    public static final String MAP_LANGUAGE_TITLE_KEY = "MAP_LANGUAGE_TITLE_KEY";
    public static final String MAP_LANGUAGE_CONTENT_KEY = "MAP_LANGUAGE_CONTENT_KEY";
    public static final String MAP_LANGUAGE_SET_KEY = "MAP_LANGUAGE_SET_KEY";

    private String languageSet = "";
    private String mLatestUrl = "";
    private String mCurrentUrl = "";
    private String mCurrentLangSet = "";
    private String[] languagesUp;

    private boolean isNeedToRefreshLanguage = false;
    private static final long checkLanguageInfo = 1005;
    private static final long downLoadLanguageInfo = 1006;
    private static final long writeSharedPrefrences = 1008;
    private static final int MSG_TIME_OUT = 1009;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIME_OUT:
                    doChangeSetLanguage(false);
                    break;
                default:
                    break;
            }
        }
    };

    public static void LaunchActivity(Context context)

    {
        Intent intent = new Intent(context, LanguageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MobclickAgent.onEvent(context, AlphaStatics.MY_LANGUAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_language);
        mHelper = new SettingHelper(this, this);
        languageSet = ((SettingHelper) mHelper).getLanguage(((SettingHelper) mHelper).getLanguageCurrentIndex());
        initLanguageTitles();
        initUI();
        initControlListener();
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        initTitle(getStringResources("ui_settings_language"));
    }

    @Override
    protected void initUI() {

        mListViewLang = (ListView) findViewById(R.id.listview_language);
        mLangAdapter = new SimpleAdapter(this, lst_map_languages, R.layout.layout_language_item,
                new String[]{MAP_LANGUAGE_TITLE_KEY, MAP_LANGUAGE_CONTENT_KEY, MAP_LANGUAGE_SET_KEY},
                new int[]{R.id.txt_language_title, R.id.txt_language_content, R.id.img_language_set}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View thiz = super.getView(position, convertView, parent);
                ImageView img = (ImageView) thiz.findViewById(R.id.img_language_set);
                if ((boolean) lst_map_languages.get(position).get(MAP_LANGUAGE_SET_KEY)) {
                    img.setImageResource(R.drawable.menu_setting_language_select);
                    img.setVisibility(View.VISIBLE);
                } else
                    img.setVisibility(View.INVISIBLE);
                return thiz;
            }
        };
        mListViewLang.setAdapter(mLangAdapter);

    }

    public void initLanguageTitles() {
        Resources res = this.getResources();
        String[] languagesTitle;
        String[] languagesContent;
        int index = ((SettingHelper) mHelper).getLanguageCurrentIndex();
        if (SkinManager.getInstance().needChangeSkin()) {
            res = SkinManager.getInstance().getmResources();
            try {
                languagesTitle = res.getStringArray(res.getIdentifier("ui_lanuages_title", "array", FileTools.package_name));
                languagesContent = res.getStringArray(res.getIdentifier("ui_lanuages", "array", FileTools.package_name));
                languagesUp = res.getStringArray(res.getIdentifier("ui_lanuages_up", "array", FileTools.package_name));
            } catch (Exception e) {
                Log.e("lang", "exception: " + e.toString());
                removeCurrentUsingUrl();
                FileTools.DeleteFile(new File(FileTools.theme_pkg_file));
                languagesTitle = getResources().getStringArray(R.array.ui_lanuages_title);
                languagesContent = getResources().getStringArray(R.array.ui_lanuages);
                languagesUp = getResources().getStringArray(R.array.ui_lanuages_up);
            }
        } else {
            languagesTitle = res.getStringArray(R.array.ui_lanuages_title);
            languagesContent = res.getStringArray(R.array.ui_lanuages);
            languagesUp = res.getStringArray(R.array.ui_lanuages_up);
        }

        for (int i = 0; i < languagesTitle.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put(MAP_LANGUAGE_TITLE_KEY, languagesTitle[i]);
            item.put(MAP_LANGUAGE_CONTENT_KEY, languagesContent[i]);
            if (i == index)//设置过语言
            {
                item.put(MAP_LANGUAGE_SET_KEY, true);
            } else
                item.put(MAP_LANGUAGE_SET_KEY, false);
            lst_map_languages.add(item);
        }
//        int index = ((SettingHelper) mHelper).getLanguage//rrentIndex();
//        if (index > -1 && index < lst_map_la//uages.size())
//        lst_map_languages.get(index).put(MAP_LANGUAGE_SET_KEY,true);

    }


    @Override
    protected void initControlListener() {

        initTitleSave(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLangSet != null && !mCurrentLangSet.equals("")) {

                    checkNeedRefreshLangugage();

                } else {
                    Toast.makeText(LanguageActivity.this.getApplicationContext(), getStringResources("ui_settings_select_one"), Toast.LENGTH_SHORT).show();
                }
            }
        }, getStringResources("ui_common_confirm"));


        mListViewLang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (languagesUp != null)
                    languageSet = languagesUp[position];
                if (languageSet.equalsIgnoreCase(mCurrentLangSet)) {
                    return;
                }
                mCurrentLangSet = ((SettingHelper) mHelper).getLanguage(position);
                for (int i = 0; i < lst_map_languages.size(); i++) {
                    if (i == position)
                        lst_map_languages.get(i).put(MAP_LANGUAGE_SET_KEY, true);
                    else
                        lst_map_languages.get(i).put(MAP_LANGUAGE_SET_KEY, false);
                }
                mLangAdapter.notifyDataSetChanged();

            }
        });

    }


    public boolean checkNeedRefreshLangugage() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCoonLoadingDia.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mCoonLoadingDia = LoadingDialog.getInstance(LanguageActivity.this, LanguageActivity.this);
                ((LoadingDialog) mCoonLoadingDia).setDoCancelable(false);
                ((LoadingDialog) mCoonLoadingDia).show();
            }
        });
        doCheckLanguageUpdate();
        mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, 3000);
        return false;
    }


    private void doChangeSetLanguage(boolean isNeedToRefreshLanguage)

    {
        mHandler.removeMessages(MSG_TIME_OUT);
        this.isNeedToRefreshLanguage = isNeedToRefreshLanguage;
        ((SettingHelper) mHelper).doChangeLanguage(mCurrentLangSet,this);
        doCancleDialog();

    }

    private void doCancleDialog() {
        if (mCoonLoadingDia != null && mCoonLoadingDia.isShowing()) {
            mCoonLoadingDia.cancel();
            mCoonLoadingDia = null;
        }


    }


    private String getCurrentUsingUrl() {

        return BasicSharedPreferencesOperator.getInstance(LanguageActivity.this.getApplicationContext(),
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doReadSync(BasicSharedPreferencesOperator.LANGUAGE_USING_INFO);

    }

    private void removeCurrentUsingUrl() {

        BasicSharedPreferencesOperator.getInstance(LanguageActivity.this.getApplicationContext(),
                BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(BasicSharedPreferencesOperator.LANGUAGE_USING_INFO, "-1", null, -1);//清除语言包信息
    }

    private void doCheckLanguageUpdate() {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject();
            jobj.put("themeObjectType", 1);//主题类型，1表示android
            jobj.put("appVersion", LanguageActivity.this.getApplicationContext().getPackageManager().getPackageInfo
                    (LanguageActivity.this.getApplicationContext().getPackageName(), 0).versionName);
            jobj.put("themeSeq", "-9999");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //发起网络请求
        GetDataFromWeb.getJsonByPost(checkLanguageInfo, HttpAddress
                .getRequestUrl(HttpAddress.Request_type.check_threme), HttpAddress
                .getParamsForPost(jobj.toString(),
                        LanguageActivity.this.getApplicationContext()), this);

    }


    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onReadCacheSize(int size) {

    }

    @Override
    public void onClaerCache() {

    }

    @Override
    public void noteWaitWebProcressShutDown() {

    }

    @Override
    public void onGetJson(boolean isSuccess, String json, long request_code) {
        Log.e("json", json.toString());

        if (request_code == checkLanguageInfo) {
            if (!isSuccess) {
                doChangeSetLanguage(false);
                return;
            }
            if (JsonTools.getJsonStatus(json)) {
                JSONArray j_list = JsonTools.getJsonModels(json);
                for (int i = 0; i < j_list.length(); i++) {
                    try {
                        ThemeInfo info = new ThemeInfo().getThiz(j_list
                                .get(i).toString());
                        if (info.themeType.equals("3")) {
                            String themeUrl = info.themeUrl;
                            if (!TextUtils.isEmpty(themeUrl)) {
                                mLatestUrl = themeUrl;//设置最新url
                            }
                            String mCurrentUrl = getCurrentUsingUrl();
                            if (TextUtils.isEmpty(themeUrl) || themeUrl.equals(mCurrentUrl)) {
                                doChangeSetLanguage(false);
                                return;

                            } else {
                                Log.e("lang", "-----down-----");
                                GetDataFromWeb.getFileFromHttp(
                                        downLoadLanguageInfo,
                                        themeUrl,
                                        FileTools.theme_pkg_file,
                                        this);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                doChangeSetLanguage(false);
                return;
            }
            doChangeSetLanguage(false);
            return;
        }

    }

    @Override
    public void onGetFileLenth(long request_code, double file_lenth) {

    }

    @Override
    public void onStopDownloadFile(long request_code, State state) {

    }

    @Override
    public void onReportProgress(long request_code, double progess) {

    }

    @Override
    public void onDownLoadFileFinish(long request_code, State state) {
        Log.e("json", state.toString());
        if (state == State.success) {
            BasicSharedPreferencesOperator.getInstance(LanguageActivity.this.getApplicationContext(),
                    BasicSharedPreferencesOperator.DataType.APP_INFO_RECORD).doWrite(BasicSharedPreferencesOperator.LANGUAGE_USING_INFO, mLatestUrl, this, (int) writeSharedPrefrences);
        } else {
            doChangeSetLanguage(false);
        }

    }

    @Override
    public void onSharedPreferenOpreaterFinish(boolean isSuccess, long request_code, String value) {

        if (!isSuccess) {
            doChangeSetLanguage(false);
        }

        if (request_code == writeSharedPrefrences) {
            doChangeSetLanguage(true);
        }

    }
}
