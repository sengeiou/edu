package com.ubt.alpha1e.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.ubt.alpha1e.business.thrid_party.MyWeiXin;
import com.ubt.alpha1e.ui.BaseActivity;

public class WXEntryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent inte = new Intent();
        inte.setAction(MyWeiXin.ACTION_WEIXIN_API_CALLBACK);
        inte.putExtras(getIntent().getExtras());
        this.sendBroadcast(inte);
        this.finish();
    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

}
