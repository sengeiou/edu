package com.ubt.alpha1e.ui;

import android.content.Intent;
import android.os.Bundle;

import com.ubt.alpha1e.data.BasicSharedPreferencesOperator;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;


public class StartInitSkinActivity extends BaseActivity {

    private static final String TAG = "StartInitSkinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //测试版本提交，删除数据库
        if (BasicSharedPreferencesOperator
                .getInstance(this, BasicSharedPreferencesOperator.DataType.USER_USE_RECORD)
                .doReadSync(BasicSharedPreferencesOperator.IS_FIRST_USE_APP_KEY)
                .equals(BasicSharedPreferencesOperator.IS_FIRST_USE_APP_VALUE_TRUE)) {

        }else {
            File file = new File(FileTools.db_log_cache + "/" + FileTools.db_log_name);
            UbtLog.d(TAG,"IS_FIRST_USE_APP_KEY =>>" + file.exists());
            //if(file.exists()){
                //file.delete();
            //}
            UbtLog.d(TAG,"IS_FIRST_USE_APP_KEY == " + file.exists());
        }

        UbtLog.d(TAG,"--onCreate--");
        Intent intent = new Intent();
        intent.setClass(this,StartActivity.class);
        startActivity(intent);
        this.finish();

    }

    @Override
    protected void initUI() {
        // TODO Auto-generated method stub


    }

    @Override
    protected void initControlListener() {


    }

    @Override
    protected void onResume() {
        super.onResume();

        //首次启动，要重新再设一次
        doCheckLanguage();
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }




}
