package com.ubt.alpha1e.action.actioncreate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class SaveSuccessActivity extends Activity implements View.OnClickListener{

    private static String TAG = "SaveSuccessActivity";

    ImageView ivClose;
    Button btn_to_other;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_action_success);
        ivClose = (ImageView) findViewById(R.id.img_cancel);
        ivClose.setOnClickListener(this);
        btn_to_other = (Button) findViewById(R.id.btn_to_other) ;
        btn_to_other.setOnClickListener(this);
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_cancel:
                UbtLog.d(TAG, "img_cancel");
                finish();
                this.overridePendingTransition(0, R.anim.activity_close_down_up);
                break;
            case R.id.btn_to_other:
                ToastUtils.showShort("社区暂未开放");
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}