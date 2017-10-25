package com.ubt.alpha1e.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplicationValues;
import com.ubt.alpha1e.R;

import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e.ui.helper.AboutUsHelper;
import com.ubt.alpha1e.ui.helper.IAboutUsUI;
import com.ubt.alpha1e.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.utils.log.UbtLog;

public class ContactUsActivity extends BaseActivity implements IAboutUsUI {

    private static final String TAG = "ContactUsActivity";

    private RelativeLayout layFeedback;

    private RelativeLayout lay_cust_phone;
    private View view_app_spit;
    private TextView txtCustPhone;
    private TextView txtCustEmail;
    private TextView txtPublicName;
    private ImageView img_ubt_wechat;

    private static final int CALL_PHONE = 1;
    private static final int SEND_EMAIL = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);
        mHelper = new AboutUsHelper(this, this);

        initUI();
        initControlListener();
    }

    @Override
    protected void onResume() {
        setCurrentActivityLable(ContactUsActivity.class.getSimpleName());

        super.onResume();
    }

    @Override
    protected void initUI() {


        lay_cust_phone = (RelativeLayout) findViewById(R.id.lay_cust_phone);
        view_app_spit = (View) findViewById(R.id.view_app_spit);

        txtCustPhone = (TextView) findViewById(R.id.txt_cust_phone);
        txtCustEmail = (TextView) findViewById(R.id.txt_cust_email);
        txtPublicName = (TextView) findViewById(R.id.txt_public_name);
        img_ubt_wechat = (ImageView) findViewById(R.id.img_ubt_wechat);
        layFeedback = (RelativeLayout) findViewById(R.id.lay_feedback);

        if(AlphaApplicationValues.getChannelName() == AlphaApplicationValues.ChannelName.google){
            lay_cust_phone.setVisibility(View.GONE);
            view_app_spit.setVisibility(View.GONE);
        }else {
            lay_cust_phone.setVisibility(View.VISIBLE);
            view_app_spit.setVisibility(View.VISIBLE);
        }

        Glide.with(this).load(HttpAddress.WebDefaultAppWechatAddress).crossFade().placeholder(R.drawable.ubt_wechat).into(img_ubt_wechat);
    }

    @Override
    protected void initControlListener() {
        // TODO Auto-generated method stub

        layFeedback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent().setClass(ContactUsActivity.this, FeedBackActivity.class));
            }
        });

        txtCustPhone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                String menuStr = ContactUsActivity.this.getStringResources("ui_contact_phone_tel")
                        + ContactUsActivity.this.getStringResources("ui_setting_cust_phone_value");
                alertDialog(CALL_PHONE,menuStr);
            }
        });

        txtCustEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                String menuStr = ContactUsActivity.this.getStringResources("ui_contact_email_send")
                        + ContactUsActivity.this.getStringResources("ui_setting_cust_email_value");
                alertDialog(SEND_EMAIL,menuStr);
            }
        });

        txtPublicName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipboardManager cmb = (ClipboardManager) ContactUsActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(txtPublicName.getText().toString().trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                cmb.getText();//获取粘贴信息

                Toast.makeText(ContactUsActivity.this,getStringResources("ui_contact_copy_success"),Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void alertDialog(final int operationType,String menuStr){

        new AlertView(null, null, ContactUsActivity.this.getStringResources("ui_common_cancel"),
                new String[]{menuStr},
                null,
                ContactUsActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int position){
                UbtLog.d(TAG,"position = " + position);
                switch (position)
                {
                    case 0:
                        if(operationType == CALL_PHONE){
                            callPhone();
                        }else {
                            sendEmail();
                        }
                        break;
                    case 2:
                        break;
                }
            }
        }).show();
    }

    /**
     * 拨打电话
     */
    private void callPhone(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + ContactUsActivity.this.getStringResources("ui_setting_cust_phone_value"));
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * 发邮件
     */
    private void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/plain"); //模拟器请使用这行
        i.setType("message/rfc822"); // 真机上使用这行
        i.putExtra(Intent.EXTRA_EMAIL,new String[] { ContactUsActivity.this.getStringResources("ui_setting_cust_email_value") });
        i.putExtra(Intent.EXTRA_SUBJECT, getStringResources("ui_contact_advice"));
        i.putExtra(Intent.EXTRA_TEXT, getStringResources("ui_contact_advice_hope"));
        startActivity(Intent.createChooser(i,getStringResources("ui_contact_select_email_app")));
    }

    @Override
    public void onSkinChanged() {
        super.onSkinChanged();
        initTitle(getStringResources("ui_contact_title"));
    }

    @Override
    protected void initBoardCastListener() {
        // TODO Auto-generated method stub

    }

    @Override
    public void noteNewestVersion() {

    }

    @Override
    public void noteApkUpsateFail(final String info) {

    }

    @Override
    public void noteApkUpdate(final String versionPath,
                              final String versionNameSizeInfo) {

    }

}
