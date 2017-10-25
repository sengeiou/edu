package com.ubt.alpha1e.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.FindPassWdActivity;

public class FindPswEmailLoginFragment extends BaseRegisterFragment {



    private boolean is_goto_email = false;
    private Button btn_go_to_email;
    private TextView txt_note;
    public static final String USER_ACCOUNT = "user_account";
    private String mUserAccount = "";

    @Override
    public void onResume() {
        super.onResume();
        if(is_goto_email)
            mActivity.finish();
        ((FindPassWdActivity)mActivity).setNextButtonGone();
    }
    public static FindPswEmailLoginFragment newInstance(String param) {
        FindPswEmailLoginFragment fragment = new FindPswEmailLoginFragment();
        Bundle args = new Bundle();
        args.putString(USER_ACCOUNT,param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_find_pwd_email_login;
    }

    @Override
    public void gotoNextStep() {

    }

    @Override
    protected void initViews() {
        txt_note = (TextView) mView.findViewById(R.id.txt_note);
        String str = ((FindPassWdActivity)mActivity).getStringResources("ui_forget_email_prompt");
        int index = str.indexOf("#");
        int last = index + mUserAccount.length();
        String string = str.replace("#", mUserAccount);
        SpannableString sp = new SpannableString(string);
        sp.setSpan(new AbsoluteSizeSpan(17, true), index, last, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(this.getActivity().getResources().getColor(R.color.T5)), index, last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_note.setText(sp);
        btn_go_to_email = (Button) mView.findViewById(R.id.btn_go_to_email);
    }

    @Override
    protected void initListeners() {
        btn_go_to_email.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                is_goto_email = true;
                String url = "http://mail."
                        + mUserAccount.split("@")[1];
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserAccount = getArguments().getString(USER_ACCOUNT);
        }
    }
}
