package com.ubt.alpha1e.action.actioncreate;

import android.os.Bundle;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sina.weibo.sdk.openapi.legacy.AccountAPI.CAPITAL.R;

public class ActionTestActivity extends BaseActivity {

    ActionEditsStandard mActionEdit;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_test);
        mActionEdit = findViewById(R.id.action_edit);
        mActionEdit.setUp(this);
    }


}
