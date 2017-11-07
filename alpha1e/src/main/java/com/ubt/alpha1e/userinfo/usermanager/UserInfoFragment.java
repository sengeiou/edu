package com.ubt.alpha1e.userinfo.usermanager;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.ImageTools;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.net.http.basic.IImageListener;
import com.ubt.alpha1e.ui.custom.ShapedImageView;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.ubt.alpha1e.userinfo.useredit.UserEditContract;
import com.ubt.alpha1e.userinfo.useredit.UserEditPresenter;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

public class UserInfoFragment extends MVPBaseFragment<UserEditContract.View, UserEditPresenter> implements UserEditContract.View, AndroidAdjustResizeBugFix.OnKeyChangerListeler {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.scrollview_user)
    ScrollView mScrollviewUser;
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

    private UserModel mUserModel = null;
    private String[] greadeList = new String[]{"幼儿园小班", "幼儿园中班", "幼儿园大班", "小学一年级", "小学二年级", "小学三年级", "小学四年级"
            , "小学五年级", "小学六年级及以上"};

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
        mUserModel = (UserModel) SPUtils.getInstance().readObject(Constant.SP_USERINFOMODEL);

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
                    ToastUtils.showShort("男孩子哦");

                }
                break;
            case R.id.female:
                if (ischanged) {
                    ToastUtils.showShort("女孩子哦");
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
                mPresenter.showImageCenterHeadDialog((Activity) mContext);
                break;
            case R.id.tv_user_age:
                mPresenter.showAgeDialog((Activity) mContext, 0);
                break;
            case R.id.tv_user_grade:
                List<String> list = new ArrayList<>();
                for (String grade : greadeList) {
                    list.add(grade);
                }
                mPresenter.showGradeDialog((Activity) mContext, 1, list);
                break;
        }
    }

    @Override
    protected void initUI() {

        mTvUserName.addTextChangedListener(new MyTextWatcher(mTvUserName,20,mContext));
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
                ContentResolver cr = mContext.getContentResolver();
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

    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public class MyTextWatcher implements TextWatcher {
        private int limit;// 字符个数限制
        private EditText text;// 编辑框控件
        private Context context;// 上下文对象

        int cursor = 0;// 用来记录输入字符的时候光标的位置
        int before_length;// 用来标注输入某一内容之前的编辑框中的内容的长度

        public MyTextWatcher(EditText text, int limit,
                             Context context) {
            this.limit = limit;
            this.text = text;
            this.context = context;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            before_length = s.length();
        }

        /**
         * s 编辑框中全部的内容 、start 编辑框中光标所在的位置（从0开始计算）、count 从手机的输入法中输入的字符个数
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            cursor = start;
//      Log.e("此时光标的位置为", cursor + "");
        }

        @Override
        public void afterTextChanged(Editable s) {
            // 这里可以知道你已经输入的字数，大家可以自己根据需求来自定义文本控件实时的显示已经输入的字符个数
            Log.e("此时你已经输入了", "" + s.length());

            int after_length = s.length();// 输入内容后编辑框所有内容的总长度
            // 如果字符添加后超过了限制的长度，那么就移除后面添加的那一部分，这个很关键
            if (after_length > limit) {
                // 比限制的最大数超出了多少字
                int d_value = after_length - limit;
                // 这时候从手机输入的字的个数
                int d_num = after_length - before_length;

                int st = cursor + (d_num - d_value);// 需要删除的超出部分的开始位置
                int en = cursor + d_num;// 需要删除的超出部分的末尾位置
                // 调用delete()方法将编辑框中超出部分的内容去掉
                Editable s_new = s.delete(st, en);
                // 给编辑框重新设置文本
                text.setText(s_new.toString());
                // 设置光标最后显示的位置为超出部分的开始位置，优化体验
                text.setSelection(st);
                // 弹出信息提示已超出字数限制
                Toast.makeText(context, "已超出最大字数限制", Toast.LENGTH_SHORT).show();
            }
            String str = text.getText().toString();
            String filter = stringFilter(str);
            if (!str.equals(filter)) {
                ToastUtils.showShort("不能有异常字符");
            }
        }

    }

}