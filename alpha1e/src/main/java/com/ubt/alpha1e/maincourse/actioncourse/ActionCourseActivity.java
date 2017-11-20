package com.ubt.alpha1e.maincourse.actioncourse;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.maincourse.adapter.ActionCoursedapter;
import com.ubt.alpha1e.maincourse.courseone.CourseOneActivity;
import com.ubt.alpha1e.maincourse.model.ActionCourseModel;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ActionCourseActivity extends MVPBaseActivity<ActionCourseContract.View, ActionCoursePresenter> implements ActionCourseContract.View, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    @BindView(R.id.recyleview_content)
    RecyclerView mRecyleviewContent;
    private List<ActionCourseModel> mActionCourseModels;
    private ActionCoursedapter mMainCoursedapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mPresenter.getActionCourseData(this);
    }

    @OnClick(R.id.iv_main_back)
    public void onClick(View view) {
        finish();
    }

    @Override
    protected void initUI() {
        mActionCourseModels = new ArrayList<>();
        mMainCoursedapter = new ActionCoursedapter(R.layout.layout_action_cources, mActionCourseModels);
        mMainCoursedapter.setOnItemClickListener(this);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 4);
        mRecyleviewContent.setLayoutManager(linearLayoutManager);
        mRecyleviewContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 30;
                outRect.left = 30;
                if (parent.getChildAdapterPosition(view) == 4) {
                    outRect.top = 50;
                }
            }
        });
        mRecyleviewContent.setAdapter(mMainCoursedapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_action_course_layout;
    }

    @Override
    public void setActionCourseData(List<ActionCourseModel> list) {
        mActionCourseModels.clear();
        mActionCourseModels.addAll(list);
        mMainCoursedapter.notifyDataSetChanged();
        UbtLog.d("MainCourse", "list==" + list.toString());
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        if (mActionCourseModels.get(position).getActionLockType() == 1) {
            return;
        }

        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_action_course_content, null);
        TextView title = contentView.findViewById(R.id.tv_card_name);
        title.setText(mActionCourseModels.get(position).getTitle());
        RecyclerView mrecyle = contentView.findViewById(R.id.recyleview_content);
        mrecyle.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter itemAdapter = new ItemAdapter(R.layout.layout_action_course_dialog, mActionCourseModels.get(position).getList());
        mrecyle.setAdapter(itemAdapter);
        ViewHolder viewHolder = new ViewHolder(contentView);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = (int) ((display.getWidth()) * 0.6); //设置宽度

        DialogPlus.newDialog(this)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentWidth(width)
                .setContentBackgroundResource(R.drawable.alert_backgroud)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.btn_pos) {
                            startActivity(new Intent(ActionCourseActivity.this, CourseOneActivity.class));
                            dialog.dismiss();

                        }
                    }
                })
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {

                    }
                })
                .setCancelable(true)
                .create().show();
    }


    public class ItemAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ItemAdapter(@LayoutRes int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_action_course_item, item);
        }
    }
}
