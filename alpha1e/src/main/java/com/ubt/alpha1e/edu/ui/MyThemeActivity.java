package com.ubt.alpha1e.edu.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.AlphaStatics;
import com.ubt.alpha1e.edu.data.model.ThemeInfo;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.IThemeUI;
import com.ubt.alpha1e.edu.ui.helper.ThemeHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyThemeActivity extends BaseActivity implements IThemeUI, BaseDiaUI {

    private GridView grv_themes;

    private SimpleAdapter lst_themes_adapter;
    private List<Map<String, Object>> lst_themes_datas;
    private Map<Long, Bitmap> map_themes_img;

    private ThemeInfo mCurrentUsingTheme;


    public static void LaunchActivity(Context context)
    {
        Intent intent = new Intent(context,MyThemeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MobclickAgent.onEvent(context, AlphaStatics.MY_THEME);

    }
    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_theme"));
        grv_themes = (GridView) findViewById(R.id.grv_themes);
        lst_themes_datas = new ArrayList<Map<String, Object>>();
        lst_themes_adapter = new SimpleAdapter(this, lst_themes_datas, R.layout.layout_theme_item,
                new String[]{ThemeHelper.MAP_THEME_NAME_KEY, ThemeHelper.MAP_THEME_SIZ_KEY, ThemeHelper.MAP_THEME_IMG_KEY},
                new int[]{R.id.txt_theme_name, R.id.txt_theme_size, R.id.img_theme_icon}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View thiz = super.getView(position, convertView, parent);


                ThemeInfo info = (ThemeInfo) lst_themes_datas.get(position).get(ThemeHelper.MAP_THEME_INFO_KEY);


                doSetThemeIcon(info, thiz, position);
                doSetThemeItemListener(info, thiz);


                return thiz;
            }
        };
        grv_themes.setAdapter(lst_themes_adapter);
    }

    private void doSetThemeIcon(ThemeInfo info, View rootView, int position) {


        View vew_txt_download_progress = rootView.findViewById(R.id.vew_txt_download_progress);
        TextView txt_download = (TextView) rootView.findViewById(R.id.txt_download);
        ImageView img_applied = (ImageView) rootView.findViewById(R.id.img_applied);

        if (info.downloadState == 0) {
            //下载完成
            if (mCurrentUsingTheme != null && info.themeSeq.equals(mCurrentUsingTheme.themeSeq)) {
                //使用中
                vew_txt_download_progress.setBackgroundResource(R.drawable.bg_btn_download_progress_content);
                txt_download.setVisibility(View.GONE);
                img_applied.setVisibility(View.VISIBLE);
            } else {
                //非使用中
                android.view.ViewGroup.LayoutParams lay_pram = vew_txt_download_progress
                        .getLayoutParams();
                int full_progress = rootView.findViewById(R.id.lay_download).getLayoutParams().width;
                lay_pram.width = full_progress;
                vew_txt_download_progress.setLayoutParams(lay_pram);
                vew_txt_download_progress.setBackgroundResource(R.drawable.bg_btn_download_progress);
                txt_download.setVisibility(View.VISIBLE);
                txt_download.setText(getStringResources("ui_theme_use_immediately"));
                img_applied.setVisibility(View.GONE);
            }


        } else if (info.downloadState == -1) {
            //尚未下载
            android.view.ViewGroup.LayoutParams lay_pram = vew_txt_download_progress
                    .getLayoutParams();
            int full_progress = rootView.findViewById(R.id.lay_download).getLayoutParams().width;
            lay_pram.width = full_progress;
            vew_txt_download_progress.setLayoutParams(lay_pram);
            vew_txt_download_progress.setBackgroundResource(R.drawable.bg_btn_download_progress);
            txt_download.setText(getStringResources("ui_action_online_download"));
        } else {
            //下载中
            txt_download.setText(getStringResources("ui_common_cancel"));
            android.view.ViewGroup.LayoutParams lay_pram = vew_txt_download_progress
                    .getLayoutParams();
            int progress = (Integer) lst_themes_datas.get(position).get(ThemeHelper.MAP_THEME_DOWNLOAD_PROGRESS_KEY);
            int full_progress = rootView.findViewById(R.id.lay_download).getLayoutParams().width;
            lay_pram.width = full_progress * progress / 100;
            vew_txt_download_progress.setLayoutParams(lay_pram);
            vew_txt_download_progress.setBackgroundResource(R.drawable.bg_btn_download_progress_content);
        }


        if (map_themes_img != null) {
            ImageView img_theme = (ImageView) rootView.findViewById(R.id.img_theme_icon);
            Bitmap b_img = map_themes_img.get(Long.parseLong(info.themeSeq));
            if (b_img != null)
                img_theme.setImageBitmap(b_img);
        }
    }

    private void doSetThemeItemListener(final ThemeInfo info, View rootView) {
        ((ImageView) rootView.findViewById(R.id.img_theme_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String j_str_urls = info.themeDetailImage;
                String info_str = ThemeInfo.getModelStr(info);
                Intent inte = new Intent().setClass(MyThemeActivity.this,
                        MyThemeInfoActivity.class);
                inte.putExtra(ThemeHelper.THEME_INFO_KEY, info_str);
                boolean isUsing = false;
                if (mCurrentUsingTheme != null && mCurrentUsingTheme.themeSeq.equals(info.themeSeq)) {
                    isUsing = true;
                }
                inte.putExtra(ThemeHelper.THEME_INFO_KEY, info_str);
                inte.putExtra(ThemeHelper.THEME_IS_CURRENT_USING, isUsing);


                MyThemeActivity.this.startActivity(inte);
            }
        });

        ((TextView) rootView.findViewById(R.id.txt_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (info.downloadState == 0) {
                    if (mCurrentUsingTheme != null && mCurrentUsingTheme.themeSeq.equals(info.themeSeq)) {

                    } else {
                        ((ThemeHelper) mHelper).doApplyTheme(info);
                        if (mCoonLoadingDia == null)
                            mCoonLoadingDia = LoadingDialog.getInstance(MyThemeActivity.this,
                                    MyThemeActivity.this);
                        ((LoadingDialog) mCoonLoadingDia).setDoCancelable(true);
                        mCoonLoadingDia.show();
                    }
                } else {
                    ((ThemeHelper) mHelper).downloadTheme(info);
                    info.downloadState = 1;
                }
            }
        });

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_theme);
        initUI();
        mHelper = new ThemeHelper(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lst_themes_datas.clear();

        ((ThemeHelper) mHelper).doGetUsingTheme();
        ((ThemeHelper) mHelper).doGetThremes();

        if (mCoonLoadingDia == null)
            mCoonLoadingDia = LoadingDialog.getInstance(this,
                    this);
        ((LoadingDialog) mCoonLoadingDia).setDoCancelable(true);
        mCoonLoadingDia.show();
    }

    @Override
    public void onGetThemes(List<ThemeInfo> infos) {

        try {
            mCoonLoadingDia.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadData(infos);
    }

    private void loadData(List<ThemeInfo> infos) {

        if (infos != null) {

            List<Long> ids = new ArrayList<Long>();
            List<String> urls = new ArrayList<String>();

            for (int i = 0; i < infos.size(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put(ThemeHelper.MAP_THEME_INFO_KEY, infos.get(i));
                item.put(ThemeHelper.MAP_THEME_NAME_KEY, infos.get(i).themeContext);
                if (!infos.get(i).themeSize.equals(""))
                    item.put(ThemeHelper.MAP_THEME_SIZ_KEY, infos.get(i).themeSize + "M");
                item.put(ThemeHelper.MAP_THEME_IMG_KEY, R.drawable.sec_local_theme);
                item.put(ThemeHelper.MAP_THEME_DOWNLOAD_PROGRESS_KEY, 0);
                lst_themes_datas.add(item);
                if (infos.get(i).themeSeq.equals("-9999"))
                    continue;
                ids.add(Long.parseLong(infos.get(i).themeSeq));
                urls.add(infos.get(i).themeImage);
            }
            lst_themes_adapter.notifyDataSetChanged();

            //获取网络图片
            //缺少裁剪参数
            ((ThemeHelper) mHelper).doGetImages(ids, urls, -1, -1, -1);

        }

    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetImages(Bitmap img, long id) {
        if (map_themes_img == null)
            map_themes_img = new HashMap<Long, Bitmap>();
        map_themes_img.put(id, img);

        lst_themes_adapter.notifyDataSetChanged();

    }

    @Override
    public void onReportProgress(long id, int progress) {
        for (int i = 0; i < lst_themes_datas.size(); i++) {
            ThemeInfo info = (ThemeInfo) lst_themes_datas.get(i).get(ThemeHelper.MAP_THEME_INFO_KEY);
            if (Long.parseLong(info.themeSeq) == id) {
                lst_themes_datas.get(i).put(ThemeHelper.MAP_THEME_DOWNLOAD_PROGRESS_KEY, progress);
                break;
            }
        }
        lst_themes_adapter.notifyDataSetChanged();
    }

    @Override
    public void onReportDownloadFinish(long id, boolean isSuccess) {

        for (int i = 0; i < lst_themes_datas.size(); i++) {
            ThemeInfo info = (ThemeInfo) lst_themes_datas.get(i).get(ThemeHelper.MAP_THEME_INFO_KEY);
            if (Long.parseLong(info.themeSeq) == id) {
                if (isSuccess)
                    info.downloadState = 0;
                else {
                    info.downloadState = -1;
                    Toast.makeText(MyThemeActivity.this, getStringResources("ui_action_download_fail"), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        lst_themes_adapter.notifyDataSetChanged();

    }

    @Override
    public void onStopDownload(long id) {
        for (int i = 0; i < lst_themes_datas.size(); i++) {
            ThemeInfo info = (ThemeInfo) lst_themes_datas.get(i).get(ThemeHelper.MAP_THEME_INFO_KEY);
            if (Long.parseLong(info.themeSeq) == id) {
                info.downloadState = -1;
                lst_themes_datas.get(i).put(ThemeHelper.MAP_THEME_DOWNLOAD_PROGRESS_KEY, 0);
                break;
            }
        }
        lst_themes_adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetUsingTheme(ThemeInfo info) {
        mCurrentUsingTheme = info;
    }

    @Override
    public void onApplyThemeFinish(ThemeInfo info, boolean is_success) {

        try {
            mCoonLoadingDia.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (is_success) {
            mCurrentUsingTheme = info;
            lst_themes_adapter.notifyDataSetChanged();
            Toast.makeText(this, getStringResources("ui_theme_use_success"), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getStringResources("ui_theme_use_fail"), Toast.LENGTH_SHORT).show();
        }

    }
}

