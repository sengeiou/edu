package com.ubt.alpha1e.edu.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.model.ThemeInfo;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.IThemeUI;
import com.ubt.alpha1e.edu.ui.helper.ThemeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyThemeInfoActivity extends BaseActivity implements IThemeUI, BaseDiaUI {

    private LinearLayout lay_img_all;
    private TextView txt_theme_name;
    private TextView txt_theme_size;
    private View vew_progress;
    private View vew_bg;
    private Button btn_download;

    private ThemeInfo mThemeInfo;
    private boolean isUsing;
    private Map<Integer, Bitmap> mBImgs;
    List<ImageView> mImageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_theme_info);
        mHelper = new ThemeHelper(this, this);
        try {
            mThemeInfo = new ThemeInfo().getThiz((String) getIntent().getExtras().get(ThemeHelper.THEME_INFO_KEY));
            isUsing = (Boolean) getIntent().getExtras().get(ThemeHelper.THEME_IS_CURRENT_USING);
        } catch (Exception e) {
            mThemeInfo = null;
            isUsing = false;
        }
        initUI();
        initControlListener();
    }

    private String[] getImgs() {
        return mThemeInfo.themeDetailImage.split(";");
    }

    private void doGetImages() {

        if (getImgs() == null)
            return;
        List<Long> ids = new ArrayList<Long>();
        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < getImgs().length; i++) {
            ids.add((long) i);
            try {
                urls.add(getImgs()[i].toString());
            } catch (Exception e) {
                urls.add("");
            }
        }

        //获取网络图片
        //缺少裁剪参数
        ((ThemeHelper) mHelper).doGetImages(ids, urls, -1, -1, -1);
    }

    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_theme_detial"));
        lay_img_all = (LinearLayout) findViewById(R.id.lay_img_all);
        mImageViews = new ArrayList<ImageView>();
        lay_img_all.removeAllViews();
        addImages();
        doGetImages();
        //-----------------------------------------------------------------
        txt_theme_name = (TextView) findViewById(R.id.txt_theme_name);
        txt_theme_size = (TextView) findViewById(R.id.txt_theme_size);
        txt_theme_name.setText(mThemeInfo.themeContext);
        if (!mThemeInfo.themeSize.equals(""))
            txt_theme_size.setText(mThemeInfo.themeSize + "M");
        else {
            txt_theme_size.setText("");
        }
        //-----------------------------------------------------------------
        vew_progress = findViewById(R.id.vew_progress);
        vew_bg = findViewById(R.id.vew_bg);
        btn_download = (Button) findViewById(R.id.btn_download);
        if (mThemeInfo.downloadState == 0) {
            doSetDownloadFinish();
        }
        if (isUsing) {
            btn_download.setText(getStringResources("ui_theme_using"));
        }
    }

    private void addImages() {

        if (getImgs() == null)
            return;

        if (getImgs().length == 1) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) lay_img_all.getLayoutParams();
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lay_img_all.setLayoutParams(lp);
        }

        int[] img_default = new int[]{R.drawable.default_1, R.drawable.default_2, R.drawable.default_3, R.drawable.default_4};

        for (int i = 0; i < getImgs().length; i++) {
            View theme_info_item = View.inflate(this,
                    R.layout.layout_theme_img_item, null);
            ImageView imgView = ((ImageView) theme_info_item.findViewById(R.id.img_info_icon));

            if (i < img_default.length) {
                imgView.setBackgroundResource(img_default[i]);
            } else {
                imgView.setBackgroundResource(R.drawable.sec_local_theme);
            }

            lay_img_all.addView(theme_info_item);
            mImageViews.add(imgView);
        }
    }

    private void doSetImgs(int id) {
        mImageViews.get(id).setImageBitmap(mBImgs.get(id));
    }

    @Override
    protected void initControlListener() {
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mThemeInfo.downloadState == 0) {
                    if (isUsing) {

                    } else {
                        ((ThemeHelper) mHelper).doApplyTheme(mThemeInfo);
                        if (mCoonLoadingDia == null)
                            mCoonLoadingDia = LoadingDialog.getInstance(MyThemeInfoActivity.this,
                                    MyThemeInfoActivity.this);
                        ((LoadingDialog) mCoonLoadingDia).setDoCancelable(true);
                        mCoonLoadingDia.show();
                    }
                } else {
                    ((ThemeHelper) mHelper).downloadTheme(mThemeInfo);
                    mThemeInfo.downloadState = 1;
                }
            }
        });
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGetThemes(List<ThemeInfo> infos) {

    }

    @Override
    public void onGetImages(Bitmap img, long id) {
        if (mBImgs == null)
            mBImgs = new HashMap<Integer, Bitmap>();
        mBImgs.put((int) id, img);
        doSetImgs((int) id);
    }

    @Override
    public void onReportProgress(long id, int progress) {
        if (id != Long.parseLong(mThemeInfo.themeSeq))
            return;
        android.view.ViewGroup.LayoutParams lay_pram = vew_progress
                .getLayoutParams();
        int full_progress = vew_bg.getLayoutParams().width;
        lay_pram.width = full_progress * progress / 100;
        vew_progress.setLayoutParams(lay_pram);
        vew_progress.setVisibility(View.VISIBLE);
        btn_download.setText(getStringResources("ui_common_cancel"));
    }

    @Override
    public void onReportDownloadFinish(long id, boolean isSuccess) {
        if (id != Long.parseLong(mThemeInfo.themeSeq))
            return;
        if (isSuccess) {
            doSetDownloadFinish();
        } else {
            //通知下载失败
        }
    }

    private void doSetDownloadFinish() {
        android.view.ViewGroup.LayoutParams lay_pram = vew_progress
                .getLayoutParams();
        int full_progress = vew_bg.getLayoutParams().width;
        lay_pram.width = full_progress;
        vew_progress.setLayoutParams(lay_pram);
        vew_progress.setVisibility(View.VISIBLE);
        btn_download.setText(getStringResources("ui_theme_use_immediately"));
        mThemeInfo.downloadState = -0;
    }

    @Override
    public void onStopDownload(long id) {
        if (id != Long.parseLong(mThemeInfo.themeSeq))
            return;
        mThemeInfo.downloadState = -1;
        vew_progress.setVisibility(View.GONE);
        btn_download.setText(this.getStringResources("ui_common_complete"));
    }

    @Override
    public void onGetUsingTheme(ThemeInfo info) {

    }

    @Override
    public void onApplyThemeFinish(ThemeInfo info, boolean is_success) {
        if (!info.themeSeq.equals(mThemeInfo.themeSeq))
            return;
        try {
            mCoonLoadingDia.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (is_success) {
            isUsing = true;
            btn_download.setText(getStringResources("ui_theme_using"));
            Toast.makeText(this, getStringResources("ui_theme_use_success"), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getStringResources("ui_theme_use_fail"), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void noteWaitWebProcressShutDown() {

    }
}
