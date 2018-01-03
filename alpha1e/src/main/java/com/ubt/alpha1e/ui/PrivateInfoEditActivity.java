package com.ubt.alpha1e.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.ant.country.CountryTool;
import com.facebook.Profile;
import com.sina.weibo.sdk.openapi.models.User;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataCheckTools;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.ImageTools;
import com.ubt.alpha1e.data.model.QQUserInfo;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.data.model.WeiXinUserInfo;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.ui.custom.EditTextCheck;
import com.ubt.alpha1e.ui.custom.RoundImageView;
import com.ubt.alpha1e.ui.dialog.BaseDiaUI;
import com.ubt.alpha1e.ui.dialog.LoadingDialog;
import com.ubt.alpha1e.ui.helper.IPrivateInfoUI;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public class PrivateInfoEditActivity extends BaseActivity implements
        IPrivateInfoUI, BaseDiaUI {

    private static final String TAG = "PrivateInfoEditActivity";
    private com.ubt.alpha1e.ui.custom.RoundImageView img_head;
    private EditText txt_name;

    private EditText txt_email;
    private EditText txt_phone;
    private ImageButton btn_gender_m;
    private ImageButton btn_gender_f;
    private TextView txt_back;
    private LinearLayout lay_back;
    private TextView txt_edit;
    private TextView txt_country;

    private UserInfo mCurrentInfo = null;
    private Uri mImageUri;

    private RelativeLayout lay_head_sel;

    private TextView txt_shooting;
    private TextView txt_from_file;
    private TextView txt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_info_edit);
        mHelper = new PrivateInfoHelper(this, this);
        UbtLog.e(TAG, "mCurrentInfo--="+ mHelper.getCurrentUser());
        mCurrentInfo = UserInfo.doClone(mHelper.getCurrentUser());
        UbtLog.e(TAG, "mCurrentInfo=" + mCurrentInfo);
        initUI();
        initControlListener();

    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(PrivateInfoEditActivity.class.getSimpleName());
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCoonLoadingDia != null && mCoonLoadingDia.isShowing()){
            mCoonLoadingDia.dismiss();
        }

    }

    @Override
    public Bitmap onGetHead(Bitmap img) {
        img_head.setImageBitmap(img);
        return img;
    }

    @Override
    public void onNodeNickNameEmpty() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNodeEmialEmpty() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNodeHeadEmpty() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEditFinish(boolean is_success, String error_info,
                             JSONObject info) {
        if (mCoonLoadingDia != null)
            mCoonLoadingDia.cancel();
        if (is_success) {
            ((PrivateInfoHelper) mHelper).doRecordUser(info);
            this.finish();
        } else {
            Toast.makeText(this, error_info, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQQPrivateInfo(QQUserInfo info) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initUI() {
        txt_back = (TextView) findViewById(R.id.txt_back);
        lay_back = (LinearLayout) findViewById(R.id.lay_back);
        txt_edit = (TextView) findViewById(R.id.txt_edit);
        img_head = (RoundImageView) findViewById(R.id.img_head);
        txt_name = (EditText) findViewById(R.id.txt_name);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_phone = (EditText) findViewById(R.id.txt_phone);
        txt_country = (EditText) findViewById(R.id.txt_country);
        btn_gender_m = (ImageButton) findViewById(R.id.btn_gender_m);
        btn_gender_f = (ImageButton) findViewById(R.id.btn_gender_f);
        lay_head_sel = (RelativeLayout) findViewById(R.id.lay_head_sel);
        txt_shooting = (TextView) findViewById(R.id.txt_shooting);
        txt_from_file = (TextView) findViewById(R.id.txt_from_file);
        txt_cancel = (TextView) findViewById(R.id.txt_del);
        // --------------------------------------------------------
        ((PrivateInfoHelper) mHelper).readUserHead(img_head.getHeight(),img_head.getWidth());
        UbtLog.d(TAG, "mCurrentInfo=" + mCurrentInfo);
        if(mCurrentInfo==null){
            return;
        }
        if(mCurrentInfo != null && mCurrentInfo.userName != null){
            txt_name.setText(mCurrentInfo.userName);
        }
        if (mHelper.getCurrentUser().userGender != null) {
            (mCurrentInfo.userGender.contains("1") ? btn_gender_m
                    : btn_gender_f)
                    .setBackgroundResource(R.drawable.gender_select_path);
        }
        if (mCurrentInfo.userEmail == null || mCurrentInfo.userEmail.equals("")) {
            txt_email.setEnabled(true);
            EditTextCheck.addCheckForEmail(txt_email, this);
        } else {
            txt_email.setText(mCurrentInfo.userEmail);
            txt_email.setEnabled(false);
        }

        if (mCurrentInfo.userPhone == null || mCurrentInfo.userPhone.equals("")) {
            txt_phone.setEnabled(true);
            EditTextCheck.addCheckForPhone(txt_phone, this);
        } else {
            txt_phone.setText(mCurrentInfo.userPhone);
            txt_phone.setEnabled(false);
        }
        /*txt_country.setText(CountryTool.getContryNameByCode(
                mCurrentInfo.countryCode, this));*/
        txt_country.setEnabled(false);

    }

    @Override
    protected void initControlListener() {
        android.view.View.OnClickListener back_listener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PrivateInfoEditActivity.this.finish();
            }
        };

        btn_gender_m.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btn_gender_m.setBackgroundResource(R.drawable.gender_select_path);
                btn_gender_f.setBackgroundResource(R.drawable.gender_normal_path);
                mCurrentInfo.userGender = 1 + "";

            }
        });
        btn_gender_f.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btn_gender_m.setBackgroundResource(R.drawable.gender_normal_path);
                btn_gender_f.setBackgroundResource(R.drawable.gender_select_path);
                mCurrentInfo.userGender = 2 + "";
            }
        });

        txt_back.setOnClickListener(back_listener);
        lay_back.setOnClickListener(back_listener);

        txt_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (txt_name.getText().toString().equals("")) {
                    PrivateInfoEditActivity.this.showToast("ui_perfect_prompt_empty_nickname");
                    return;
                }

                if (!mHelper.isRightName(txt_name.getText().toString(), 32, false, "")) {
                    return;
                }

                if (!txt_phone.getText().toString().equals("")
                        && !DataCheckTools.isPhoneNum(txt_phone.getText().toString())) {
                    PrivateInfoEditActivity.this.showToast("ui_login_prompt_phone_wrong_format");
                    return;
                }

                if (!txt_email.getText().toString().equals("")
                        && !DataCheckTools.isEmail(txt_email.getText().toString())) {
                    PrivateInfoEditActivity.this.showToast("ui_login_prompt_email_wrong_format");
                    return;
                }

                mCurrentInfo.userName = txt_name.getText().toString();

                if (mCurrentInfo.userEmail == null || mCurrentInfo.userEmail.equals("")) {
                    mCurrentInfo.userEmail = txt_email.getText().toString();
                }

                if (mCurrentInfo.userPhone == null || mCurrentInfo.userPhone.equals("")) {
                    mCurrentInfo.userPhone = txt_phone.getText().toString();
                }

                ((PrivateInfoHelper) mHelper).doEditPrivateInfo(mCurrentInfo);
                if (mCoonLoadingDia == null){
                    mCoonLoadingDia = LoadingDialog.getInstance(
                            PrivateInfoEditActivity.this,
                            PrivateInfoEditActivity.this);
                }
                mCoonLoadingDia.show();
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

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File path = new File(FileTools.image_cache);
                if (!path.exists()) {
                    path.mkdirs();
                }
                mImageUri = Uri.fromFile(new File(path, new Date().getTime()+ ""));

                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,mImageUri);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, PrivateInfoHelper.GetUserHeadRequestCodeByShoot);

                lay_head_sel.setVisibility(View.GONE);
            }
        });

        txt_from_file.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PrivateInfoHelper.GetUserHeadRequestCodeByFile);
                lay_head_sel.setVisibility(View.GONE);
            }
        });

        txt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //
                lay_head_sel.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByFile
                || requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByShoot) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == PrivateInfoHelper.GetUserHeadRequestCodeByFile) {
                    if (data == null){
                        return;
                    }

                    String type = cr.getType(data.getData());
                    if (type == null){
                        return;
                    }
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
                                                Bitmap img = ImageTools.ImageCrop(bitmap);
                                                ((PrivateInfoHelper) mHelper).opreaterUserHead(img);
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
    public void onWeiXinPrivateInfo(WeiXinUserInfo info) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWeiBoPrivateInfo(User info) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPreEditFinish(boolean b, Object object) {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteWaitWebProcressShutDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFaceBookProfileInfo(Profile profile, String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTwitterProfileInfo(twitter4j.User user) {
        // TODO Auto-generated method stub

    }

}
