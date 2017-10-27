package com.ubt.alpha1e.test;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class TestActivity extends MVPBaseActivity<TestContract.View, TestPresenter> implements TestContract.View {
    private static final String TAG = "TestActivity";

    private Button btnLogin = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        UbtLog.d(TAG,"-onCreate-");
        initViews();
        initControllListens();
    }

    private void initViews(){
        btnLogin = (Button) findViewById(R.id.login);
    }

    private void initControllListens(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.login("test","123456");
            }
        });
    }

    @Override
    public void loginSuccess(Object user) {
        Log.d(TAG,"-loginSuccess-");
        Toast.makeText(this,"登陆成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginFailed(String message) {
        Log.d(TAG,"-loginFailed-");
    }
}
