package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ant.country.CountryTool;
import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.sina.weibo.sdk.openapi.models.User;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.QQUserInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.data.model.WeiXinUserInfo;
import com.ubt.alpha1e.ui.helper.IPrivateInfoUI;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONObject;

public class PrivateInfoShowActivity extends BaseActivity implements
        IPrivateInfoUI {
    private static final String TAG = "PrivateInfoShowActivity";

    private ImageView img_head;
    private TextView txt_name;
    private TextView txt_gender;
    private TextView txt_email;
    private TextView txt_phone;
    private TextView txt_country;
    private LinearLayout lay_back;
    private TextView btn_back;
    private TextView txt_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_info_show);
        mHelper = new PrivateInfoHelper(this, this);
        initUI();
        initControlListener();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(PrivateInfoShowActivity.class.getSimpleName());
        super.onResume();
        initData();
    }

    @Override
    protected void initUI() {

        txt_edit = (TextView) findViewById(R.id.txt_edit);
        lay_back = (LinearLayout) findViewById(R.id.lay_base_back);
        btn_back = (TextView) findViewById(R.id.tv_base_back);
        img_head = (ImageView) findViewById(R.id.img_head);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_gender = (TextView) findViewById(R.id.txt_gender);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_country = (TextView) findViewById(R.id.txt_country);

    }

    private void initData() {
        if(mHelper == null){
            mHelper = new PrivateInfoHelper(this, this);
        }

        UserInfo mCurrentUserInfo = mHelper.getCurrentUser();

        if(mCurrentUserInfo != null){

            Glide.with(PrivateInfoShowActivity.this)
                    .load(mCurrentUserInfo.userImage)
                    .fitCenter()
                    .into(img_head);

            if(mCurrentUserInfo.userName != null){
                txt_name.setText(mCurrentUserInfo.userName);
            }

            if (mCurrentUserInfo.userGender != null) {
                String genderStr = mCurrentUserInfo.userGender.contains("1") ? getStringResources("ui_perfect_gender_male")
                        : getStringResources("ui_perfect_gender_female");
                txt_gender.setText(genderStr);
            }else{
                txt_gender.setText(getStringResources("ui_perfect_not_set"));
            }
            txt_email.setText(mCurrentUserInfo.userEmail);
            txt_phone.setText(mCurrentUserInfo.userPhone);
            UbtLog.d(TAG, "code=" + mCurrentUserInfo.countryCode);
            txt_country.setText(CountryTool.getContryNameByCode(mCurrentUserInfo.countryCode, this));
        }


    }

    @Override
    protected void initControlListener() {

//        initTitle("");
        android.view.View.OnClickListener back_listener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PrivateInfoShowActivity.this.finish();
            }
        };
        txt_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PrivateInfoShowActivity.this.startActivity(new Intent()
                        .setClass(PrivateInfoShowActivity.this,
                                PrivateInfoEditActivity.class));
            }
        });
        lay_back.setOnClickListener(back_listener);
        btn_back.setOnClickListener(back_listener);
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        UbtLog.d(TAG, "---onStop!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UbtLog.d(TAG, "---onDestroy!");
    }

    @Override
    public Bitmap onGetHead(Bitmap img) {

        return img;
    }

    @Override
    public void onNodeNickNameEmpty() {

    }

    @Override
    public void onNodeEmialEmpty() {

    }

    @Override
    public void onNodeHeadEmpty() {

    }

    @Override
    public void onQQPrivateInfo(QQUserInfo info) {

    }

    @Override
    public void onWeiXinPrivateInfo(WeiXinUserInfo info) {

    }

    @Override
    public void onWeiBoPrivateInfo(User info) {

    }

    @Override
    public void onPreEditFinish(boolean b, Object object) {

    }

    @Override
    public void onEditFinish(boolean is_success, String error_info,
                             JSONObject info) {

    }

    @Override
    public void onFaceBookProfileInfo(Profile profile, String url) {

    }

    @Override
    public void onTwitterProfileInfo(twitter4j.User user) {

    }

}
