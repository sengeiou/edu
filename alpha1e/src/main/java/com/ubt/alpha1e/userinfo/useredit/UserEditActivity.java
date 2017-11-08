package com.ubt.alpha1e.userinfo.useredit;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.RequstMode.UpdateUserInfoRequest;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.ImageTools;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.ui.custom.ShapedImageView;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.weigan.loopview.LoopView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserEditActivity extends MVPBaseActivity<UserEditContract.View, UserEditPresenter> implements UserEditContract.View {

    @BindView(R.id.img_head)
    ShapedImageView mImgHead;
    @BindView(R.id.edit_user_name)
    EditText mTvUserName;
    @BindView(R.id.male)
    RadioButton mMale;
    @BindView(R.id.female)
    RadioButton mFemale;
    @BindView(R.id.radio_group_sex)
    RadioGroup mRadioGroupSex;
    @BindView(R.id.tv_user_age)
    TextView mTvUserAge;
    @BindView(R.id.tv_user_grade)
    TextView mTvUserGrade;
    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    @BindView(R.id.tv_main_title)
    TextView mTvMainTitle;
    @BindView(R.id.iv_complete_info)
    ImageView ivSave;

    private Dialog mDialog;

    private Uri mImageUri;
    public static final int GetUserHeadRequestCodeByShoot = 1001;
    public static final int GetUserHeadRequestCodeByFile = 1002;


    private String[] greadeList = new String[]{"幼儿园小班", "幼儿园中班", "幼儿园大班", "小学一年级", "小学二年级", "小学三年级", "小学四年级"
            , "小学五年级", "小学六年级及以上"};

    private String  sex = "1";
    private String age ;
    private String grade;
    private String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initUI();
    }

    /**
     * 性别选中事件
     *
     * @param view
     * @param ischanged
     */
    @OnCheckedChanged({R.id.male, R.id.female})
    public void onRadioCheck(CompoundButton view, boolean ischanged) {
        switch (view.getId()) {
            case R.id.male:
                if (ischanged) {
                    ToastUtils.showShort("男孩子哦");
                    sex = "1";
                }
                break;
            case R.id.female:
                if (ischanged) {
                    ToastUtils.showShort("女孩子哦");
                    sex = "2";
                }
                break;

            default:
                break;
        }
    }

    @OnClick({R.id.img_head, R.id.tv_user_age, R.id.tv_user_grade,R.id.iv_complete_info})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                mPresenter.showImageHeadDialog(UserEditActivity.this);
                break;
            case R.id.tv_user_age:
                mPresenter.showAgeDialog(UserEditActivity.this, 3);
                break;
            case R.id.tv_user_grade:
                List<String> list = new ArrayList<>();
                for (String grade : greadeList) {
                    list.add(grade);
                }
                mPresenter.showGradeDialog(UserEditActivity.this, 2, list);
                break;
            case R.id.iv_complete_info:
                UpdateUserInfoRequest request = new UpdateUserInfoRequest();
                request.setAge(age);
                request.setGrade(grade);
                request.setNickName(mTvUserName.getText().toString());
                request.setSex(sex);
//                if(!TextUtils.isEmpty(path)){
//
//                }
                File file = new File(path);
                OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_USERINFO,file,request,11).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("userEdit", "Exception:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("userEdit", "response:" + response);
                    }
                });
                break;
            default:
                break;
        }
    }



    /**
     * 拍照
     */
    @Override
    public void takeImageFromShoot() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File path = new File(FileTools.image_cache);
        if (!path.exists()) {
            path.mkdirs();
        }
        mImageUri = Uri.fromFile(new File(path, new Date().getTime() + ""));

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent, GetUserHeadRequestCodeByShoot);
    }

    /**
     * 从相册获取照片
     */
    @Override
    public void takeImageFromAblum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PrivateInfoHelper.GetUserHeadRequestCodeByFile);
    }

    /**
     * 年龄选择滚动回调
     *
     * @param type 0表示Age 1表示grade
     * @param item 选择内容
     */
    @Override
    public void ageSelectItem(int type, String item) {
        ToastUtils.showShort(item);
        if(type==0){
            age = item;
        }else if(type==1){
            grade = item;
        }
    }

    @Override
    public void updateUserModel(UserModel userModel) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GetUserHeadRequestCodeByFile
                || requestCode == GetUserHeadRequestCodeByShoot) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == GetUserHeadRequestCodeByFile) {
                    if (data == null) {
                        return;
                    }

                    String type = cr.getType(data.getData());
                    if (type == null) {
                        return;
                    }
                    mImageUri = data.getData();
                }
                try {
                    InputStream in = cr.openInputStream(mImageUri);
                    ImageTools.compressImage(in, mImgHead.getWidth(),
                            mImgHead.getHeight(), new IImageListener() {
                                @Override
                                public void onGetImage(boolean isSuccess,
                                                       final Bitmap bitmap, long request_code) {
                                    if (isSuccess) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap img = ImageTools.ImageCrop(bitmap);
                                                mImgHead.setImageBitmap(img);
                                                path = ImageTools.SaveImage("head",img);
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
    protected void initUI() {
        View outerView = LayoutInflater.from(this).inflate(R.layout.dialog_useredit_wheel, null);
        LoopView loopView = (LoopView) outerView.findViewById(R.id.loopView);

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_user_edit;
    }

    @Override
    public void getAgeDataList(List<String> list) {

    }
}
