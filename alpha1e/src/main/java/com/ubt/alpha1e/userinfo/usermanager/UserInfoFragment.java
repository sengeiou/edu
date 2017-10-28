package com.ubt.alpha1e.userinfo.usermanager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.ui.custom.ShapedImageView;
import com.ubt.alpha1e.userinfo.useredit.UserEditActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserInfoFragment extends MVPBaseFragment<UserInfoContract.View, UserInfoPresenter> implements UserInfoContract.View {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.img_head)
    ShapedImageView mImgHead;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_user_sex)
    TextView mTvUserSex;
    @BindView(R.id.tv_user_age)
    TextView mTvUserAge;
    @BindView(R.id.tv_user_grade)
    TextView mTvUserGrade;

    private String mParam1;
    private String mParam2;


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
        mPresenter.getUserInfo(getContext());
    }


    @OnClick({R.id.tv_user_name, R.id.tv_user_sex, R.id.tv_user_age, R.id.tv_user_grade})
    public void toEditActivity(View view) {
        mContext.startActivity(new Intent(mContext, UserEditActivity.class));
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
    }


    @Override
    public void setUserInfo(UserInfo mCurrentUserInfo) {
        if (mCurrentUserInfo != null) {

            Glide.with(this)
                    .load(mCurrentUserInfo.userImage)
                    .fitCenter()
                    .into(mImgHead);

            if (mCurrentUserInfo.userName != null) {
                mTvUserName.setText(mCurrentUserInfo.userName);
            }

            if (mCurrentUserInfo.userGender != null) {

                mTvUserSex.setText(mCurrentUserInfo.userGender);
            } else {
                mTvUserSex.setText("ui_perfect_not_set");
            }

        }
    }
}
