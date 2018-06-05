package com.ubt.alpha1e.edu.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.ant.country.CountryActivity;
import com.ant.country.CountryTool;*/
import com.facebook.Profile;
//import com.sina.weibo.sdk.openapi.models.User;
import com.ubt.alpha1e.edu.AlphaApplicationValues;
import com.ubt.alpha1e.edu.AlphaApplicationValues.Thrid_login_type;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.DataCheckTools;
import com.ubt.alpha1e.edu.data.DataTools;
import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.ImageTools;
import com.ubt.alpha1e.edu.data.model.QQUserInfo;
import com.ubt.alpha1e.edu.data.model.UserInfo;
import com.ubt.alpha1e.edu.data.model.WeiXinUserInfo;
import com.ubt.alpha1e.edu.net.http.basic.IImageListener;
import com.ubt.alpha1e.edu.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.edu.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.edu.ui.helper.IPrivateInfoUI;
import com.ubt.alpha1e.edu.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.edu.ui.helper.PrivateInfoHelper.EditType;
import com.ubt.alpha1e.edu.ui.helper.PrivateInfoHelper.Gender;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public class PrivateInfoActivity extends BaseActivity implements
        IPrivateInfoUI, BaseDiaUI {

    private static final String TAG = PrivateInfoActivity.class.getSimpleName();

    public static final int MSG_GET_COUNTRY = 10001;

    private EditType mEditType = null;
    private Thrid_login_type mThirdLoginType;
    private Gender mCurrentGender = null;
    private String mCountryName = "";
    private String mCountryNumber = "";
    private UserInfo mCurrentUserInfo;
    private Uri mImageUri;
    private ImageButton btn_gender_m;
    private ImageButton btn_gender_f;
    private TextView txt_next;
    // private Button btn_ignore;
    private RelativeLayout lay_head_sel;
    private TextView txt_shooting;
    private TextView txt_from_file;
    private TextView txt_cancel;
    private ImageView img_head;
    private TextView txt_nick_name_head;
    private String mUserNameThride = "";
    private TextView txt_email_nick_name;
    private EditText edt_email_nick_name;
    private TextView txt_sel_country;
    private ImageView iv_sel_country;
    private boolean isSaveInfoSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG, "PrivateInfoActivity onCreate");
        setContentView(R.layout.activity_private_info);

        try {
            mEditType = (EditType) getIntent().getExtras().get(
                    PrivateInfoHelper.Edit_type);
        } catch (Exception e) {
            mEditType = null;
        }

        try {
            mThirdLoginType = (Thrid_login_type) getIntent().getExtras().get(
                    AlphaApplicationValues.THIRD_lOGIN_TYPE);
        } catch (Exception e) {
            mThirdLoginType = null;
        }

        mHelper = new PrivateInfoHelper(this, this);
        mCurrentUserInfo = UserInfo.doClone(mHelper.getCurrentUser());

        initUI();
        initControlListener();

        if (mEditType == EditType.thired_register_type) {
            ((PrivateInfoHelper) mHelper).doReadThridInfo(mThirdLoginType);
            if (mCoonLoadingDia == null)
                mCoonLoadingDia = LoadingDialog.getInstance(
                        PrivateInfoActivity.this, PrivateInfoActivity.this);
            mCoonLoadingDia.show();
        }else if(mEditType==EditType.complete_info_type)
        {
            if(mThirdLoginType!=null)
            {
                ((PrivateInfoHelper) mHelper).doReadThridInfo(mThirdLoginType);
                if (mCoonLoadingDia == null)
                    mCoonLoadingDia = LoadingDialog.getInstance(
                            PrivateInfoActivity.this, PrivateInfoActivity.this);
                mCoonLoadingDia.show();
            }
        }

    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(PrivateInfoActivity.class.getSimpleName());
        super.onResume();
    }

    @Override
    protected void initUI() {
        img_head = (ImageView) findViewById(R.id.img_head);
        iv_sel_country = (ImageView) findViewById(R.id.im_country_arrow);
        ((PrivateInfoHelper) mHelper).getUserHead(mCurrentUserInfo.userImage,
                -1, -1);

         txt_nick_name_head = (TextView) findViewById(R.id.txt_nick_name_head);
         mUserNameThride = "";
        txt_email_nick_name = (TextView) findViewById(R.id.txt_email_nick_name);
        edt_email_nick_name = (EditText) findViewById(R.id.edt_email_nick_name);
        if (mEditType == EditType.local_register_type) {
            txt_email_nick_name.setText(this.getStringResources("ui_perfect_nickname"));
            edt_email_nick_name.setHint(this.getStringResources("ui_perfect_prompt_empty_nickname"));
            edt_email_nick_name.setMaxEms(32);
        } else if (mEditType == EditType.thired_register_type) {
            mUserNameThride = mHelper.getCurrentUser().userName;
            txt_nick_name_head.setText(mUserNameThride);
            txt_email_nick_name.setText(this.getStringResources("ui_login_email"));
            edt_email_nick_name.setHint(this.getStringResources("ui_login_email_placeholder"));
        } else if (mEditType == EditType.complete_info_type) {
            mUserNameThride = mHelper.getCurrentUser().userName;
             txt_nick_name_head.setText(mUserNameThride);
            mUserNameThride = mHelper.getCurrentUser().userName;
            txt_email_nick_name.setText(this.getStringResources("ui_login_email"));
            if (mCurrentUserInfo.userEmail != null
                    && !mCurrentUserInfo.userEmail.equals("")) {
                edt_email_nick_name.setText(mCurrentUserInfo.userEmail.trim());
                edt_email_nick_name.setEnabled(false);

            } else {
                edt_email_nick_name.setHint(this.getStringResources("ui_login_email_placeholder"));
            }
        }

        // -------------------------------------------------
        lay_head_sel = (RelativeLayout) findViewById(R.id.lay_head_sel);
        txt_shooting = (TextView) findViewById(R.id.txt_shooting);
        txt_from_file = (TextView) findViewById(R.id.txt_from_file);
        txt_cancel = (TextView) findViewById(R.id.txt_del);
        // -------------------------------------------------
        btn_gender_f = (ImageButton) findViewById(R.id.btn_gender_f);
        btn_gender_m = (ImageButton) findViewById(R.id.btn_gender_m);

        mCurrentGender = Gender.M;
        if (mCurrentUserInfo.userGender != null) {
            if (mCurrentUserInfo.userGender.equals("2"))
                mCurrentGender = Gender.F;
        }

        if (mCurrentGender == Gender.M) {
            btn_gender_f.setBackgroundResource(R.drawable.gender_normal_path);
            btn_gender_m.setBackgroundResource(R.drawable.gender_select_path);

        } else {
            btn_gender_f.setBackgroundResource(R.drawable.gender_select_path);
            btn_gender_m.setBackgroundResource(R.drawable.gender_normal_path);
        }

        // -------------------------------------------------
        txt_next = (TextView) findViewById(R.id.txt_next);
        // -------------------------------------------------
        txt_sel_country = (TextView) findViewById(R.id.txt_country_name);
        if (((PrivateInfoHelper) mHelper).getCurrentUserCountryCode() != null) {
            /*txt_sel_country.setText(CountryTool.getContryNameByCode(
                    ((PrivateInfoHelper) mHelper).getCurrentUserCountryCode(),
                    this));
            txt_sel_country.setClickable(false);

            iv_sel_country.setClickable(false);
            mCountryNumber = ((PrivateInfoHelper) mHelper)
                    .getCurrentUserCountryCode();*/
        } else {
            txt_sel_country.setClickable(true);
            iv_sel_country.setClickable(false);
        }

    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
      if(mEditType == EditType.local_register_type)
        {
//            txt_nick_name_head.setText(mHelper.getCurrentUser().userName);
            txt_email_nick_name.setText(this.getStringResources("ui_perfect_nickname"));
            edt_email_nick_name.setHint(this.getStringResources("ui_perfect_prompt_empty_nickname"));
        }else
      {
//          txt_nick_name_head.setText(mHelper.getCurrentUser().userName);
          txt_email_nick_name.setText(this.getStringResources("ui_login_email"));
          edt_email_nick_name.setHint(this.getStringResources("ui_login_email_placeholder"));
      }
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub
        txt_next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mEditType == EditType.local_register_type) {
                    if (mCountryNumber.equals("")) {
                        PrivateInfoActivity.this.showToast("ui_perfect_select_country");
                        return;
                    }

                    if (edt_email_nick_name.getText().toString().equals("")) {
                        onNodeNickNameEmpty();
                        return;
                    }

                    if (!mHelper.isRightName(edt_email_nick_name.getText()
                            .toString(), 32, false, "")) {
                        return;
                    }

                    mCurrentUserInfo.userName = edt_email_nick_name.getText()
                            .toString();
                    mCurrentUserInfo.userGender = (mCurrentGender == Gender.M) ? 1 + ""
                            : 2 + "";
                    mCurrentUserInfo.countryCode = mCountryNumber;

                    ((PrivateInfoHelper) mHelper)
                            .doEditPrivateInfo(mCurrentUserInfo);
                    if (mCoonLoadingDia == null)
                        mCoonLoadingDia = LoadingDialog.getInstance(
                                PrivateInfoActivity.this,
                                PrivateInfoActivity.this);
                    mCoonLoadingDia.show();

                } else if (mEditType == EditType.thired_register_type) {
                    if (edt_email_nick_name.getText().toString().equals("")) {
                        onNodeEmialEmpty();
                        return;
                    }
                    if (!DataCheckTools.isEmail(edt_email_nick_name.getText()
                            .toString())) {
                        PrivateInfoActivity.this.showToast("ui_login_prompt_email_wrong_format");
                        return;
                    }
                    mCurrentUserInfo.userEmail = edt_email_nick_name.getText()
                            .toString();
                     mCurrentUserInfo.userName = txt_nick_name_head.getText()
                     .toString();
                    mCurrentUserInfo.userName = mUserNameThride;
                    mCurrentUserInfo.userGender = (mCurrentGender == Gender.M) ? 1 + ""
                            : 2 + "";
                    ((PrivateInfoHelper) mHelper)
                            .doEditPrivateInfo(mCurrentUserInfo);

                    if (mCoonLoadingDia == null)
                        mCoonLoadingDia = LoadingDialog.getInstance(
                                PrivateInfoActivity.this,
                                PrivateInfoActivity.this);
                    mCoonLoadingDia.show();

                } else if (mEditType == EditType.complete_info_type) {

                    if (edt_email_nick_name.getText().toString().equals("")) {
                        onNodeEmialEmpty();
                        return;
                    }

                    if (!DataCheckTools.isEmail(edt_email_nick_name.getText()
                            .toString())) {
                        PrivateInfoActivity.this.showToast("ui_login_prompt_email_wrong_format");
                        return;
                    }
                    if (mCountryNumber.equals("")) {
                        PrivateInfoActivity.this.showToast("ui_perfect_select_country");
                        return;
                    }
                        mCurrentUserInfo.userEmail = edt_email_nick_name.getText()
                                .toString();
                    mCurrentUserInfo.countryCode = mCountryNumber;
                    mCurrentUserInfo.userGender = (mCurrentGender == Gender.M) ? 1 + ""
                            : 2 + "";
                    ((PrivateInfoHelper) mHelper)
                            .doEditPrivateInfo(mCurrentUserInfo);

                    if (mCoonLoadingDia == null)
                        mCoonLoadingDia = LoadingDialog.getInstance(
                                PrivateInfoActivity.this,
                                PrivateInfoActivity.this);
                    mCoonLoadingDia.show();

                }

            }
        });
        img_head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (lay_head_sel.getVisibility() == View.GONE) {
                    lay_head_sel.setVisibility(View.VISIBLE);
                } else {
                    lay_head_sel.setVisibility(View.GONE);
                }

            }
        });

        txt_shooting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File path = new File(FileTools.image_cache);
                if (!path.exists()) {
                    path.mkdirs();
                }
                mImageUri = Uri.fromFile(new File(path, new Date().getTime()
                        + ""));

                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        mImageUri);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent,
                        PrivateInfoHelper.GetUserHeadRequestCodeByShoot);

                lay_head_sel.setVisibility(View.GONE);
            }
        });

        txt_from_file.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // ���ļ���ѡȡ
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,
                        PrivateInfoHelper.GetUserHeadRequestCodeByFile);
                lay_head_sel.setVisibility(View.GONE);
            }
        });

        txt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // ���ļ���ѡȡ
                lay_head_sel.setVisibility(View.GONE);
            }
        });

        btn_gender_f.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btn_gender_f
                        .setBackgroundResource(R.drawable.gender_select_path);
                btn_gender_m
                        .setBackgroundResource(R.drawable.gender_normal_path);
                mCurrentGender = Gender.F;
            }
        });
        btn_gender_m.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btn_gender_f
                        .setBackgroundResource(R.drawable.gender_normal_path);
                btn_gender_m
                        .setBackgroundResource(R.drawable.gender_select_path);
                mCurrentGender = Gender.M;
            }
        });
        // btn_ignore.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View arg0) {
        // if (1 == 0) {
        // Intent inte = new Intent();
        // inte.setClass(PrivateInfoActivity.this,
        // BindingActivity.class);
        // PrivateInfoActivity.this.startActivity(inte);
        // PrivateInfoActivity.this.finish();
        // } else {
        // Intent inte = new Intent();
        // inte.setClass(PrivateInfoActivity.this,
        // ScanAlphaActivity.class);
        // PrivateInfoActivity.this.startActivity(inte);
        // PrivateInfoActivity.this.finish();
        // }
        // }
        // });
        if (((PrivateInfoHelper) mHelper).getCurrentUserCountryCode() == null) {
            /*txt_sel_country.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.setClass(PrivateInfoActivity.this,
                            CountryActivity.class);
                    startActivityForResult(intent, MSG_GET_COUNTRY);
                }
            });
            iv_sel_country.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(PrivateInfoActivity.this,
                            CountryActivity.class);
                    startActivityForResult(intent, MSG_GET_COUNTRY);
                }
            });*/
        }
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MSG_GET_COUNTRY) {
            if (resultCode == RESULT_OK && data != null) {
                Bundle bundle = data.getExtras();
                mCountryName = bundle.getString("countryName");
                mCountryNumber = bundle.getString("countryNumber").substring(1);
                txt_sel_country.setText(mCountryName);
            }
        } else if (requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByFile
                || requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByShoot) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByFile) {
                    if (data == null)
                        return;
                    String type = cr.getType(data.getData());
                    if (type == null)
                        return;
                    mImageUri = data.getData();
                }
                try {
                    InputStream in = cr.openInputStream(mImageUri);
                    ImageTools.compressImage(in, img_head.getWidth(),
                            img_head.getHeight(), new IImageListener() {
                                @Override
                                public void onGetImage(boolean isSuccess,
                                                       final Bitmap bitmap, long request_code) {
                                    if (isSuccess) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap img = ImageTools
                                                        .ImageCrop(bitmap);
                                                ((PrivateInfoHelper) mHelper)
                                                        .opreaterUserHead(img);
                                            }
                                        });
                                    }
                                }

                            }, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public Bitmap onGetHead(Bitmap img) {

        UbtLog.d(TAG, "onGetHead");

        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
        if (img == null) {
            UbtLog.d(TAG, "img == null");
            return null;
        }

        Bitmap mCurrentHeadimg = null;

        if (mEditType == EditType.thired_register_type) {
            Bitmap _bitmap = ImageTools.compressImage(img, img_head.getWidth(),
                    img_head.getHeight(), true);

            mCurrentHeadimg = ImageTools.ImageCrop(_bitmap);

            UbtLog.d(TAG, mCurrentHeadimg.isRecycled() ? "mCurrentHeadimg Recycled"
                            : "mCurrentHeadimg not Recycled");

             mCurrentUserInfo.userName =
             txt_nick_name_head.getText().toString();
            mCurrentUserInfo.userName = mUserNameThride;
            mCurrentUserInfo.userGender = (mCurrentGender == Gender.M) ? 1 + "" : 2 + "";

            ((PrivateInfoHelper) mHelper).doEditPrivateInfo(mCurrentUserInfo);
            if (mCoonLoadingDia == null){
                mCoonLoadingDia = LoadingDialog.getInstance(PrivateInfoActivity.this, PrivateInfoActivity.this);
            }

            mCoonLoadingDia.show();
        } else {
            mCurrentHeadimg = img;
        }
        UbtLog.d(TAG, "mCurrentHeadimg = " + mCurrentHeadimg);
        img_head.setImageBitmap(mCurrentHeadimg);
        return mCurrentHeadimg;
    }

    @Override
    public void onNodeNickNameEmpty() {
        // TODO Auto-generated method stub
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
        PrivateInfoActivity.this.showToast("ui_perfect_prompt_empty_nickname");

    }

    @Override
    public void onNodeHeadEmpty() {
        // TODO Auto-generated method stub
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
        this.showToast("ui_perfect_set_head");
    }

    @Override
    public void onQQPrivateInfo(QQUserInfo info) {
        // TODO Auto-generated method stub
         txt_nick_name_head.setText(info.nickname);
        mUserNameThride = info.nickname;
        if (info.gender.equals("��")) {
            btn_gender_f.setBackgroundResource(R.drawable.gender_unselected);
            btn_gender_m.setBackgroundResource(R.drawable.gender_selected);
            mCurrentGender = Gender.M;
        } else {
            btn_gender_f.setBackgroundResource(R.drawable.gender_selected);
            btn_gender_m.setBackgroundResource(R.drawable.gender_unselected);
            mCurrentGender = Gender.F;
        }
        ((PrivateInfoHelper) mHelper).getUserHead(info.figureurl_qq_2,
                img_head.getHeight(), img_head.getWidth());
    }

    @Override
    public void onNodeEmialEmpty() {
        // TODO Auto-generated method stub
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
        PrivateInfoActivity.this.showToast("ui_login_email_placeholder");
    }

    @Override
    public void onWeiXinPrivateInfo(WeiXinUserInfo info) {
         txt_nick_name_head.setText(info.nickname);
        mUserNameThride = info.nickname;
        if (info.sex.equals("1")) {
            btn_gender_f.setBackgroundResource(R.drawable.gender_unselected);
            btn_gender_m.setBackgroundResource(R.drawable.gender_selected);
            mCurrentGender = Gender.M;
        } else {
            btn_gender_f.setBackgroundResource(R.drawable.gender_selected);
            btn_gender_m.setBackgroundResource(R.drawable.gender_unselected);
            mCurrentGender = Gender.F;
        }
        ((PrivateInfoHelper) mHelper).getUserHead(info.headimgurl,
                img_head.getHeight(), img_head.getWidth());
    }

//    @Override
//    public void onWeiBoPrivateInfo(User info) {
//
//        // txt_nick_name_head.setText(info.name);
//        mUserNameThride = info.name;
//        if (info.gender.equals("m")) {
//            btn_gender_f.setBackgroundResource(R.drawable.gender_unselected);
//            btn_gender_m.setBackgroundResource(R.drawable.gender_selected);
//            mCurrentGender = Gender.M;
//        } else {
//            btn_gender_f.setBackgroundResource(R.drawable.gender_selected);
//            btn_gender_m.setBackgroundResource(R.drawable.gender_unselected);
//            mCurrentGender = Gender.F;
//        }
//        ((PrivateInfoHelper) mHelper).getUserHead(info.avatar_hd,
//                img_head.getHeight(), img_head.getWidth());
//
//    }

    @Override
    public void onPreEditFinish(boolean b, Object object) {
        // TODO Auto-generated method stub
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
    }

    @Override
    public void onEditFinish(boolean is_success, String error_info,
                             JSONObject info) {
        // TODO Auto-generated method stub
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();

        if (is_success) {
            Intent inte = new Intent();


            if (mEditType == EditType.thired_register_type) {
                mEditType = EditType.complete_info_type;
            } else {
                ((PrivateInfoHelper) mHelper).doRecordUser(info);

                isSaveInfoSuccess = true;
                PrivateInfoActivity.this.finish();
            }
        } else {
            Toast.makeText(this, error_info, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
 /*       if (!isSaveInfoSuccess) {
            UbtLog.d("PrivateInfoActivity", "PrivateInfoActivity onDestroy");
            ((AlphaApplication) getApplication()).clearCurrentUserInfo();
        }*/
    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFaceBookProfileInfo(Profile profile, String url) {
        final String name = profile.getName();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 txt_nick_name_head.setText(name);
                mUserNameThride = name;
            }
        });
        ((PrivateInfoHelper) mHelper).getUserHead(
                url,
                DataTools.dip2px(PrivateInfoActivity.this,
                        img_head.getLayoutParams().height),
                DataTools.dip2px(PrivateInfoActivity.this,
                        img_head.getLayoutParams().width), false);
    }

    @Override
    public void onTwitterProfileInfo(final twitter4j.User user) {
        System.out.println("Twitter---Profile:" + user.getName()+", "+user.getBiggerProfileImageURLHttps());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 txt_nick_name_head.setText(user.getName().toString());
                 mUserNameThride = user.getName().toString();
            }
        });
        ((PrivateInfoHelper) mHelper).getUserHead(
                user.getBiggerProfileImageURLHttps(),
                DataTools.dip2px(PrivateInfoActivity.this,
                        img_head.getLayoutParams().height),
                DataTools.dip2px(PrivateInfoActivity.this,
                        img_head.getLayoutParams().width));
    }

}
