package com.ubt.alpha1e.edu.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.ubt.alpha1e.edu.R;

public class LowPowerDialog extends BaseDialog {
    public static LowPowerDialog mDia;
    private Context mContext;
    private View mRootView;
    // -----------------------------------
    private TextView txt_note_charge;
    private TextView txt_ok;

    private LowPowerDialog(Context context) {
        super(context);
    }

    public static LowPowerDialog getInstance(Context _context) {

        try {
            mDia.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDia = new LowPowerDialog(_context);
        mDia.mContext = _context;
        mDia.initDia();
        mDia.initControlListener();
        return mDia;
    }

    private void initDia() {
        mRootView = View.inflate(mContext, R.layout.dialog_low_power, null);
        // ------------------------------------------------
        txt_note_charge = (TextView) mRootView
                .findViewById(R.id.txt_note_charge);
//        int index = txt_note_charge.getText().toString().lastIndexOf("?");
//        String power = "10%";
//        String note = txt_note_charge.getText().toString().replace("?", power);
//        SpannableStringBuilder builder = new SpannableStringBuilder(note);
//        ForegroundColorSpan readSpan = new ForegroundColorSpan(mContext
//                .getResources().getColor(R.color.txt_charge_power));
//        builder.setSpan(readSpan, index, index + power.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        txt_note_charge.setText(builder);
        txt_ok = (TextView) mRootView.findViewById(R.id.txt_ok);
        // ------------------------------------------------
        this.setContentView(mRootView);
        ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setCancelable(false);
    }

    private void initControlListener() {
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDia.cancel();
            }
        });
    }

}
