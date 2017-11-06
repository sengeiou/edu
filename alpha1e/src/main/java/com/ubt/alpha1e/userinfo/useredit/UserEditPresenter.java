package com.ubt.alpha1e.userinfo.useredit;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.userinfo.model.UserModel;
import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class UserEditPresenter extends BasePresenterImpl<UserEditContract.View> implements UserEditContract.Presenter {

    /**
     * 修改头像对话框
     *
     * @param activity
     */
    @Override
    public void showImageHeadDialog(Activity activity) {
        ViewHolder viewHolder = new ViewHolder(R.layout.dialog_useredit_head);
        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.tv_take_photo) {
                            mView.takeImageFromShoot();
                        } else if (view.getId() == R.id.tv_take_ablum) {
                            mView.takeImageFromAblum();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create().show();
    }


    /**
     * 显示年龄对话框
     *
     * @param activity
     * @param currentPosition
     */
    @Override
    public void showAgeDialog(Activity activity, int currentPosition) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_useredit_wheel, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.loopView);
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 5; i < 15; i++) {
            list.add(String.valueOf(i));
        }
        loopView.setItems(list);
        loopView.setInitPosition(0);

         loopView.setCurrentPosition(currentPosition);
        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_sure) {
                            if (isAttachView()) {
                                mView.ageSelectItem(0, list.get(loopView.getSelectedItem()));
                            }
                            //ToastUtils.showShort("item " + loopView.getSelectedItem());
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create().show();


    }


    /**
     * 年级选择框
     *
     * @param activity
     * @param currentPosition
     * @param list
     */
    @Override
    public void showGradeDialog(Activity activity, int currentPosition, final List<String> list) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_useredit_wheel, null);
        ViewHolder viewHolder = new ViewHolder(contentView);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.loopView);
        TextView textView = contentView.findViewById(R.id.tv_wheel_name);
        textView.setVisibility(View.GONE);
        DialogPlus.newDialog(activity)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_sure) {
                            if (isAttachView()) {
                                mView.ageSelectItem(1, list.get(loopView.getSelectedItem()));
                                Log.d("showGradeDialog", "string==" + list.get(loopView.getSelectedItem()));
                                //ToastUtils.showShort("item " + loopView.getSelectedItem());
                            }
                            // ToastUtils.showShort("item " + loopView.getSelectedItem());
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
        // 设置原始数据
        loopView.setItems(list);
        loopView.setInitPosition(0);

        loopView.setCurrentPosition(currentPosition);
    }

    /**
     * 获取用户信息
     */
    public void getUserModel() {
    }


    public void upDataUserInfo(UserModel userModel){

    }

}
