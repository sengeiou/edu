package com.ubt.alpha1e.edu.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.pg.PG;
import com.bumptech.glide.Glide;
import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.data.TimeTools;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;
import com.ubt.alpha1e.edu.ui.custom.ShapedImageView;
import com.ubt.alpha1e.edu.ui.helper.MyActionsHelper;
import com.ubt.alpha1e.edu.utils.ResourceUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

public class ActionUnpublishedActivity extends BaseActivity {


    private ShapedImageView actionLogo;
    private TextView txtActionNameValue;
    private  TextView txtActionTimeValue;
    private  TextView txtActionTypeValue;
    private TextView txtActionDescription;
    private Button btnLogin;
    private NewActionInfo actionInfo;
    public static final int REQUSET_CODE = 1001;
    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    public static String ACTION_INFO = "ACTION_INFO";
    public static String ACTION_SYNC_STATE = "action_sync_state";
    public static String KEY_ACTION_SYNC_STATE = "key_action_sync_state";

    private TextView tvGoDub;
    private String actionSyncState;
    private String TAG = ActionUnpublishedActivity.class.getSimpleName();

    public static void launchActivity(Context context, Parcelable parcelable,String schemeId, String schemeName, String actionSyncState) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, ActionUnpublishedActivity.class);
        intent.putExtra(ACTION_INFO,parcelable);
        intent.putExtra(SCHEME_ID,schemeId);
        intent.putExtra(SCHEME_NAME,schemeName);
        intent.putExtra(ACTION_SYNC_STATE, actionSyncState);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UbtLog.d(TAG, "actionSyncState onCreate");
        setContentView(R.layout.activity_my_creation_unpublish);
        actionInfo = getIntent().getParcelableExtra(ACTION_INFO);
        actionSyncState = getIntent().getStringExtra(ACTION_SYNC_STATE);
        saveActionSyncState(actionSyncState);
        initUI();
        initControlListener();
    }

    @Override
    protected void initUI() {
        initTitle(getStringResources("ui_square_action_detail"));
        actionLogo = (ShapedImageView)findViewById(R.id.action_logo);
        txtActionNameValue = (TextView) findViewById(R.id.txt_action_name_value);
        txtActionTimeValue = (TextView)findViewById(R.id.txt_action_time_value);
        txtActionTypeValue = (TextView)findViewById(R.id.txt_action_type_value);
        txtActionDescription = (TextView)findViewById(R.id.txt_action_description);
        btnLogin = (Button)findViewById(R.id.btn_login);
        txtActionNameValue.setText(actionInfo.actionName);
        txtActionTimeValue.setText(TimeTools.getMMTimeForPublish((int)actionInfo.actionTime)+"");
        String name = ResourceUtils.getActionType(actionInfo.actionSonType,this);
        txtActionTypeValue.setText(name);
        txtActionDescription.setText(actionInfo.actionDesciber);
        Glide.with(this).load(actionInfo.actionHeadUrl).placeholder(R.drawable.sec_action_logo).centerCrop().into(actionLogo);

        tvGoDub = (TextView)findViewById(R.id.go_dub);

    }

    @Override
    protected void initControlListener() {
       btnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DynamicActivity.launchActivity(ActionUnpublishedActivity.this,actionInfo,REQUSET_CODE,
                       getIntent().getStringExtra(SCHEME_ID),getIntent().getStringExtra(SCHEME_NAME));

           }
       });

        tvGoDub.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MyActionsHelper.getInstance(ActionUnpublishedActivity.this).stopPlayAction();
                DubActivity.launchActivity(ActionUnpublishedActivity.this, PG.convertParcelable(actionInfo),DubActivity.TYPE_CREATE, actionSyncState);
            }
        });
    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG, "actionSyncState=" + actionSyncState);
        if(!actionSyncState.equalsIgnoreCase(MyActionsHelper.Action_download_state.send_finish.toString()) && isBulueToothConnected()){
            if(!readActionSyncState().equalsIgnoreCase("NO_VALUE")){
                actionSyncState = readActionSyncState();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUSET_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                finish();
            }

        }else if(requestCode == 12306){
            DynamicActivity.launchActivity(ActionUnpublishedActivity.this,actionInfo,REQUSET_CODE,
                    getIntent().getStringExtra(SCHEME_ID),getIntent().getStringExtra(SCHEME_NAME));
        }

    }
}
