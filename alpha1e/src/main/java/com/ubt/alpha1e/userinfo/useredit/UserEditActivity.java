package com.ubt.alpha1e.userinfo.useredit;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.FileUtils;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.RequstMode.UpdateUserInfoRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.base.loading.LoadingDialog;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.custom.ShapedImageView;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.ui.main.MainActivity;
import com.ubt.alpha1e.userinfo.model.UserAllModel;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.util.MyTextWatcher;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.NameLengthFilter;
import com.ubt.alpha1e.utils.StringUtils;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserEditActivity extends MVPBaseActivity<UserEditContract.View, UserEditPresenter> implements UserEditContract.View, MyTextWatcher.WatcherListener {


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

    private List<String> ageList = new ArrayList<>();
    private List<String> gradeList = new ArrayList<>();
    private String sex = "1";
    private String age;
    private String grade;
    private String path;

    private UserModel mUserModel;
    private static final String TAG = "UserEditActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }


    @Override
    public void onResume() {
        super.onResume();
        mUserModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
        UbtLog.d(TAG, "mUserModel:" + mUserModel.toString());

        InputFilter[] filters = { new NameLengthFilter(20) };
        mTvUserName.setFilters(filters);
        mTvUserName.addTextChangedListener(new MyTextWatcher(mTvUserName, this));
        if(mTvUserName.getText().toString().length()==0){
            String name = FileUtils.utf8ToString(mUserModel.getNickName());
            mTvUserName.setText(name);
            mTvUserName.setSelection(mTvUserName.getText().length());
        }
        if(TextUtils.isEmpty(age)){
            mTvUserAge.setText(TextUtils.isEmpty(mUserModel.getAge())?"未填写":mUserModel.getAge());
        }
        if(TextUtils.isEmpty(grade)){
            mTvUserGrade.setText(TextUtils.isEmpty(mUserModel.getGrade())?"未填写":mUserModel.getGrade());
        }

        if(TextUtils.isEmpty(path)){
            Glide.with(this).load(mUserModel.getHeadPic()).centerCrop().placeholder(R.drawable.sec_action_logo).into(mImgHead);
        }

        checkSaveEnable();


        mPresenter.getLoopData();

    }

    private void checkSaveEnable(){
        if(mTvUserName.getText().toString().trim().length()>0 && !mTvUserName.getText().toString().trim().equals("") && !mTvUserAge.getText().toString().equals("未填写")
                && !mTvUserGrade.getText().toString().equals("未填写")){
            ivSave.setEnabled(true);
        }else{
            ivSave.setEnabled(false);
        }
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
                    sex = "1";
                }
                break;
            case R.id.female:
                if (ischanged) {
                    sex = "2";
                }
                break;

            default:
                break;
        }
    }

    @OnClick({R.id.img_head, R.id.tv_user_age, R.id.tv_user_grade, R.id.iv_complete_info})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                mPresenter.showImageCenterHeadDialog(UserEditActivity.this);
                break;
            case R.id.tv_user_age:
                if(ageList.size()==0){
                    ToastUtils.showShort("网络异常");
                    return;
                }
                mPresenter.showAgeDialog(UserEditActivity.this, ageList, mPresenter.getPosition(age,ageList));
                break;
            case R.id.tv_user_grade:
                if(gradeList.size()==0){
                    ToastUtils.showShort("网络异常");
                    return;
                }
                mPresenter.showGradeDialog(UserEditActivity.this, mPresenter.getPosition(grade,gradeList), gradeList);
                break;
            case R.id.iv_complete_info:

//                if(!mTvUserName.getText().toString().equals(mUserModel.getNickName())){
//                    if(!TVUtils.isCorrectStr(mTvUserName.getText().toString())) {
//                        ToastUtils.showShort("仅限汉字、字母及数字");
//                        return;
//                    }
//                }

                LoadingDialog.show(UserEditActivity.this);
                UpdateUserInfoRequest request = new UpdateUserInfoRequest();
                request.setAge(StringUtils.getAgeByType(age));
                request.setGrade(StringUtils.getGradeByType(grade));
                request.setNickName(FileUtils.stringToUtf8(mTvUserName.getText().toString()));
                request.setSex(sex);
                File file = null;
                if(!TextUtils.isEmpty(path)){
                    file = new File(path);
                }
                OkHttpClientUtils.getJsonByPostRequest(HttpEntity.UPDATE_USERINFO, file, request, 11).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        UbtLog.d("userEdit", "Exception:" + e.getMessage());
                        LoadingDialog.dismiss(UserEditActivity.this);
                        ToastUtils.showShort("用户信息保存失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UbtLog.d("userEdit", "response:" + response);
                        LoadingDialog.dismiss(UserEditActivity.this);
                        BaseResponseModel<UserModel> baseResponseModel = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<UserModel>>() {
                                }.getType());
                        if (baseResponseModel.status) {
                            UbtLog.d("userEdit", "userModel:" + baseResponseModel.models);

                            UserModel model = baseResponseModel.models;
                            UserModel userModel = new UserModel();
                            userModel.setAge(StringUtils.getAgeStringBytype(model.getAge()));
                            userModel.setSex(model.getSex());
                            userModel.setPhone(model.getPhone());
                            userModel.setHeadPic(model.getHeadPic());
                            userModel.setGrade(StringUtils.getGradeStringBytype(model.getGrade()));
                            userModel.setNickName(model.getNickName());
                            SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, userModel);
//                            SPUtils.getInstance().saveObject(Constant.SP_USER_INFO, baseResponseModel.models);
                            Intent intent = new Intent();
                            intent.setClass(UserEditActivity.this, MainActivity.class);
                            startActivity(intent);
                            UserEditActivity.this.finish();
                        }else{
                            if(baseResponseModel.info.equals("11300")){
                                ToastUtils.showShort("不能输入非法字符");
                            }
                        }

                    }
                });
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;//拦截事件传递,从而屏蔽back键。
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 拍照
     */
    @Override
    public void takeImageFromShoot() {
         // getShootCamera();
        //首先判断是否开启相机权限，如果开启直接调用，未开启申请
        PermissionUtils.getInstance(this)
                .request(new PermissionUtils.PermissionLocationCallback() {
                    @Override
                    public void onSuccessful() {
                       //  ToastUtils.showShort("申请拍照权限成功");
                        getShootCamera();
                    }

                    @Override
                    public void onFailure() {
                         //ToastUtils.showShort("申请拍照权限失败");
                    }

                    @Override
                    public void onRationSetting() {
                        // ToastUtils.showShort("申请拍照权限已经被拒绝过");
                    }

                    @Override
                    public void onCancelRationSetting() {
                    }


                }, PermissionUtils.PermissionEnum.CAMERA,this);

    }

    public void getShootCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String catchPath = FileUtils.getCacheDirectory(this, "");
        // File path = new File(FileTools.image_cache);
        File path = new File(catchPath + "/images");
        if (!path.exists()) {
            path.mkdirs();
        }
        mImageUri = Uri.fromFile(new File(path, System.currentTimeMillis() + ""));
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
        if (type == 0) {
            age = item;
            mTvUserAge.setText(age);
            checkSaveEnable();
        } else if (type == 1) {
            grade = item;
            mTvUserGrade.setText(grade);
            checkSaveEnable();
        }
    }

    @Override
    public void updateUserModelSuccess(UserModel userModel) {

    }

    /**
     * 更新信息失败
     */
    @Override
    public void updateUserModelFailed(String str) {

    }

    @Override
    public void updateLoopData(UserAllModel userAllModel) {
        if (null != userAllModel) {
            ageList = StringUtils.getAgeList(userAllModel.getAgeList());
            gradeList = StringUtils.getGradeList(userAllModel.getGradeList());
        } else {

        }
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

                    //android gao ban ben
                    String h_type = cr.getType(data.getData());
                    //android di ban ben
                    String l_type = data.getType();
                    UbtLog.d(TAG,"h_type:"+h_type  + "   l_type:"+l_type);
                    if (h_type == null && l_type == null){
                        return;
                    }
                    mImageUri = data.getData();
                }
                try {
                    Bitmap bitmap = FileUtils.getBitmapFormUri(this, mImageUri);
                   // mImgHead.setImageBitmap(bitmap);
                    path = FileUtils.SaveImage(this, "head", bitmap);
                    File file = new File(path);
                    Glide
                            .with(this)
                            .load(file)
                            .into(mImgHead);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void initUI() {


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

    @Override
    public void longEditTextSize() {

    }

    @Override
    public void errorEditTextStr() {
        if(!mTvUserName.getText().toString().equals(mUserModel.getNickName())){
//            ToastUtils.showShort("仅限汉字、字母及数字");
        }

    }

    @Override
    public void textChange() {
        checkSaveEnable();
    }
}
