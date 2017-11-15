package com.ubt.alpha1e.userinfo.usermanager;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.FileUtils;
import com.ubt.alpha1e.base.NetUtil;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.ShapedImageView;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.userinfo.model.UserAllModel;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditContract;
import com.ubt.alpha1e.userinfo.useredit.UserEditPresenter;
import com.ubt.alpha1e.userinfo.util.MyTextWatcher;
import com.ubt.alpha1e.userinfo.util.TVUtils;
import com.ubt.alpha1e.utils.NameLengthFilter;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserInfoFragment extends MVPBaseFragment<UserEditContract.View, UserEditPresenter> implements UserEditContract.View, AndroidAdjustResizeBugFix.OnKeyChangerListeler, MyTextWatcher.WatcherListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //    @BindView(R.id.scrollview_user)
//    ScrollView mScrollviewUser;
    Unbinder unbinder;
    private String mParam1;
    private String mParam2;
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


    Handler mHandler = new Handler();
    private Uri mImageUri;

    AndroidAdjustResizeBugFix assistActivity;
    /**
     * 拍照获取照片
     */
    public static final int GetUserHeadRequestCodeByShoot = 1001;
    /**
     * 相册获取
     */
    public static final int GetUserHeadRequestCodeByFile = 1002;

    public String headPath;

    private UserModel mUserModel = null;
//    private String[] greadeList = new String[]{"幼儿园小班", "幼儿园中班", "幼儿园大班", "小学一年级", "小学二年级", "小学三年级", "小学四年级"
//            , "小学五年级", "小学六年级及以上"};

    private List<String> ageList = new ArrayList<>();
    private List<String> gradeList = new ArrayList<>();

    public UserInfoFragment() {

    }


    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        UbtLog.d("UserInfoFragment", "onCreate");
        assistActivity = new AndroidAdjustResizeBugFix(getActivity());
        assistActivity.setOnKeyChangerListeler(this);
        mPresenter.getLoopData();

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        UbtLog.d("Usercenter", "onHiddenChanged===" + mUserModel.toString());
        if (hidden) {
            initData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UbtLog.d("UserInfoFragment", "onActivityCreated");
        initData();
    }

    private void initData() {
        mUserModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USER_INFO);
        UbtLog.d("Usercenter", "usermode===" + mUserModel.toString());
        InputFilter[] filters = { new NameLengthFilter(20) };
        mTvUserName.setFilters(filters);
        mTvUserName.addTextChangedListener(new MyTextWatcher(mTvUserName, this));
        mTvUserName.setText(mUserModel.getNickName());
        mTvUserAge.setText(mUserModel.getAge());
        mTvUserGrade.setText(mUserModel.getGrade());
        if (!TextUtils.isEmpty(mUserModel.getSex())) {
            if (mUserModel.getSex().equals("1")) {
                mMale.setChecked(true);
            } else if (mUserModel.getSex().equals("2")) {
                mFemale.setChecked(true);
            }
        }
        Glide.with(this).load(mUserModel.getHeadPic()).centerCrop().into(mImgHead);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    if (null != mUserModel && !mUserModel.getSex().equals("1")) {
                        updateUserInfo(Constant.KEY_NICK_SEX, "1");
                    }
                }
                break;
            case R.id.female:
                if (ischanged) {
                    if (null != mUserModel && !mUserModel.getSex().equals("2")) {
                        updateUserInfo(Constant.KEY_NICK_SEX, "2");
                    }
                }
                break;

            default:
                break;
        }
    }

    @OnClick({R.id.img_head, R.id.tv_user_age, R.id.tv_user_grade})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                mPresenter.showImageCenterHeadDialog(getActivity());
                break;
            case R.id.tv_user_age:
                if (NetUtil.isNetWorkConnected(getActivity()) && ageList.size() > 0) {
                    int currentPosition = mPresenter.getPosition(mTvUserAge.getText().toString(), ageList);
                    mPresenter.showAgeDialog(getActivity(), ageList, currentPosition);
                } else {
                    ToastUtils.showShort("Network  unavailable");
                }
                break;
            case R.id.tv_user_grade:
                if (NetUtil.isNetWorkConnected(getActivity()) && gradeList.size() > 0) {
                    int currentPosition1 = mPresenter.getPosition(mTvUserGrade.getText().toString(), gradeList);
                    mPresenter.showGradeDialog(getActivity(), currentPosition1, gradeList);
                } else {
                    ToastUtils.showShort("Network  unavailable");
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UbtLog.d("UserInfoFragment", "onDestroyView");
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UbtLog.d("UserInfoFragment", "onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        UbtLog.d("UserInfoFragment", "onPause");
    }

    @Override
    public void getAgeDataList(List<String> list) {

    }

    /**
     * 拍照
     */
    @Override
    public void takeImageFromShoot() {
        // getShootCamera();
        //首先判断是否开启相机权限，如果开启直接调用，未开启申请
        PermissionUtils.getInstance(getActivity())
                .request(new PermissionUtils.PermissionLocationCallback() {
                    @Override
                    public void onSuccessful() {
                        // ToastUtils.showShort("申请拍照权限成功");
                        getShootCamera();
                    }

                    @Override
                    public void onFailure() {
                        //  ToastUtils.showShort("申请拍照权限失败");
                    }

                    @Override
                    public void onRationSetting() {
                        // ToastUtils.showShort("申请拍照权限已经被拒绝过");
                    }
                }, PermissionUtils.PermissionEnum.CAMERA);

    }

    public void getShootCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String catchPath = FileUtils.getCacheDirectory(getActivity(), "");
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
            mTvUserAge.setText(item);
            if (!mUserModel.getAge().equals(item)) {
                updateUserInfo(Constant.KEY_NICK_AGE, item);
            }
        } else if (type == 1) {
            mTvUserGrade.setText(item);
            if (!mUserModel.getGrade().equals(item)) {
                updateUserInfo(Constant.KEY_NICK_GRADE, item);
            }
        }
    }

    @Override
    public void updateUserModelSuccess(UserModel userModel) {
        this.mUserModel = userModel;
    }

    @Override
    public void updateUserModelFailed() {
        ToastUtils.showShort("update failed");
    }

    @Override
    public void updateLoopData(UserAllModel userAllModel) {
        if (null != userAllModel) {
            if (null != userAllModel.getAgeList() && userAllModel.getAgeList().size() > 0) {
                ageList = userAllModel.getAgeList();
            }
            if (null != userAllModel.getGradeList() && userAllModel.getGradeList().size() > 0) {
                gradeList = userAllModel.getGradeList();
            }
        } else {

        }
    }

    /**
     * 修改相片结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GetUserHeadRequestCodeByFile
                || requestCode == GetUserHeadRequestCodeByShoot) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = getActivity().getContentResolver();
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
                    Bitmap bitmap = FileUtils.getBitmapFormUri(getActivity(), mImageUri);
                    mImgHead.setImageBitmap(bitmap);
                    headPath = FileUtils.SaveImage(getActivity(), "head", bitmap);
                    mPresenter.updateHead(headPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    /**
     * 监听键盘开启关闭
     *
     * @param statu true键盘开启 false 键盘关闭
     */
    @Override
    public void keyBoardOpen(boolean statu) {
        String editText = mTvUserName.getText().toString();
        Log.d("string==", "editText==" + editText);
        if (!statu && !TextUtils.isEmpty(editText)) {
            if (TVUtils.isCorrectStr(editText)) {
                if (!mUserModel.getNickName().equals(editText)) {
                    updateUserInfo(Constant.KEY_NICK_NAME, editText);
                }
            }
        }
    }


    /**
     * 更新用户信息
     *
     * @param type
     * @param value
     */
    public void updateUserInfo(int type, String value) {
        mPresenter.updateUserInfo(type, value);
    }


    /**
     * 字符超过20
     */
    @Override
    public void longEditTextSize() {

    }

    /**
     * 字符串包含异常字符
     */
    @Override
    public void errorEditTextStr() {
        ToastUtils.showShort("仅限汉字、字母及数字");
    }

    @Override
    public void textChange() {

    }
}