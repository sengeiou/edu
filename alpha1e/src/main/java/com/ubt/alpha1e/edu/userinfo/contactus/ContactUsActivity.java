package com.ubt.alpha1e.edu.userinfo.contactus;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.login.HttpEntity;
import com.ubt.alpha1e.edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e.edu.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.edu.ui.dialog.alertview.SheetAlertView;
import com.ubt.alpha1e.edu.userinfo.photoshow.PhotoShowActivity;
import com.ubt.alpha1e.edu.utils.log.UbtLog;
import com.ubt.alpha1e.edu.webcontent.WebContentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ContactUsActivity extends MVPBaseActivity<ContactUsContract.View, ContactUsPresenter> implements ContactUsContract.View {

    private static final String TAG = ContactUsActivity.class.getSimpleName();

    @BindView(R.id.ll_base_back)
    LinearLayout llBaseBack;
    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.rl_cust_phone)
    RelativeLayout rlCustPhone;
    @BindView(R.id.rl_cust_email)
    RelativeLayout rlCustEmail;
    @BindView(R.id.rl_cust_website)
    RelativeLayout rlCustWebsite;

    private static final int CALL_PHONE = 1;
    private static final int SEND_EMAIL = 2;
    private static final int WEBSITE_UBT = 3;


    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, ContactUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_contact_title"));
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_contact_us_mvp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.tv_base_title_name, R.id.rl_cust_phone, R.id.rl_cust_email, R.id.rl_cust_website, R.id.iv_ubt_wechat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                ContactUsActivity.this.finish();
                break;
            case R.id.rl_cust_phone: {

                String menuStr = getStringResources("ui_contact_phone_tel") + getStringResources("ui_setting_cust_phone_value");
                SpannableString menuStyle = new SpannableString(menuStr);
                int startIndex = getStringResources("ui_contact_phone_tel").length();
                menuStyle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.T24)), startIndex, menuStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alertDialog(CALL_PHONE, menuStyle);
            }
            break;
            case R.id.rl_cust_email: {
                /*String menuStr = getStringResources("ui_contact_email_send") + getStringResources("ui_setting_cust_email_value");
                SpannableString menuStyle = new SpannableString(menuStr);
                int startIndex = getStringResources("ui_contact_email_send").length();
                menuStyle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.T24)), startIndex, menuStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alertDialog(SEND_EMAIL, menuStyle);*/

                WebContentActivity.launchActivity(this, HttpEntity.EMAIL_REPORT, getStringResources("ui_setting_email_report"), true);
            }
            break;
            case R.id.rl_cust_website: {
                String menuStr = getStringResources("ui_contact_website") + getStringResources("ui_about_url");
                SpannableString menuStyle = new SpannableString(menuStr);
                int startIndex = getStringResources("ui_contact_website").length();
                menuStyle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.T24)), startIndex, menuStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alertDialog(WEBSITE_UBT, menuStyle);
            }
            break;
            case R.id.iv_ubt_wechat:
                PhotoShowActivity.LaunchActivity(getContext());
                break;
        }
    }

    public void alertDialog(final int operationType, SpannableString menuStr) {

        new SheetAlertView(null, null, null,
                new SpannableString[]{menuStr, new SpannableString(getStringResources("ui_common_cancel"))},
                null,
                ContactUsActivity.this, SheetAlertView.Style.ActionSheet, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                UbtLog.d(TAG, "position = " + position);
                switch (position) {
                    case 0:
                        if (operationType == CALL_PHONE) {
                            callPhone();
                        } else if (operationType == SEND_EMAIL) {
                            sendEmail();
                        } else {
                            gotoWebsiteUbt();
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
    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + ContactUsActivity.this.getStringResources("ui_setting_cust_phone_value"));
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * 发邮件
     */
    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/plain"); //模拟器请使用这行
        i.setType("message/rfc822"); // 真机上使用这行
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{ContactUsActivity.this.getStringResources("ui_setting_cust_email_value")});
        i.putExtra(Intent.EXTRA_SUBJECT, getStringResources("ui_contact_advice"));
        i.putExtra(Intent.EXTRA_TEXT, getStringResources("ui_contact_advice_hope"));
        startActivity(Intent.createChooser(i, getStringResources("ui_contact_select_email_app")));
    }

    /**
     * 打开官网
     */
    private void gotoWebsiteUbt() {
        UbtLog.d(TAG, "gotoWebsiteUbT ");
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getStringResources("ui_about_url")));
        //intent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
        //intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        //getContext().startActivity(intent);

        WebContentActivity.launchActivity(this, getStringResources("ui_about_url"), "", true);
    }

}
