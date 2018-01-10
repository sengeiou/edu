package com.ubt.alpha1e.userinfo.dingdang;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.login.LoginManger;
import com.ubt.alpha1e.mvp.MVPBaseFragment;

import butterknife.BindView;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class DingDangFragment extends MVPBaseFragment<DingDangContract.View, DingDangPresenter> implements DingDangContract.View {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.btn_dingdang)
    Button btnDingDang;


    public static DingDangFragment newInstance(String param1, String param2) {
        DingDangFragment fragment = new DingDangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        btnDingDang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManger.getInstance().toUserCenter();
            }
        });
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_dingdang;
    }

    @Override
    protected void initBoardCastListener() {

    }
}
