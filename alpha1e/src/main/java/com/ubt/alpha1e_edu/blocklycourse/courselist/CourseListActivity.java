package com.ubt.alpha1e_edu.blocklycourse.courselist;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ubt.alpha1e_edu.R;
import com.ubt.alpha1e_edu.base.SPUtils;
import com.ubt.alpha1e_edu.base.ToastUtils;
import com.ubt.alpha1e_edu.base.loading.LoadingDialog;
import com.ubt.alpha1e_edu.blocklycourse.BlocklyCourseActivity;
import com.ubt.alpha1e_edu.blocklycourse.BlocklyUtil;
import com.ubt.alpha1e_edu.blocklycourse.adapter.BlocklyCourseAdapter;
import com.ubt.alpha1e_edu.blocklycourse.model.CourseData;
import com.ubt.alpha1e_edu.mvp.MVPBaseActivity;
import com.ubt.alpha1e_edu.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e_edu.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

import static com.ubt.alpha1e_edu.base.Constant.SP_CURRENT_BLOCK_COURSE_ID;


/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class CourseListActivity extends MVPBaseActivity<CourseListContract.View, CourseListPresenter> implements CourseListContract.View, BaseQuickAdapter.OnItemClickListener {

    private static final String TAG = "CourseListActivity";
    @BindView(R.id.iv_main_back)
    ImageView mIvMainBack;
    @BindView(R.id.recyleview_content)
    RecyclerView mRecyleviewContent;

    List<CourseData> courseList = new ArrayList<CourseData>();
    BlocklyCourseAdapter courseAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UbtLog.d(TAG, "currentId:" + SPUtils.getInstance().getInt(SP_CURRENT_BLOCK_COURSE_ID));
        if(SPUtils.getInstance().getInt(SP_CURRENT_BLOCK_COURSE_ID) != -1){
            mPresenter.updateCurrentCourse(SPUtils.getInstance().getInt(SP_CURRENT_BLOCK_COURSE_ID) );
        }else{
            mPresenter.getBlocklyCourseList(this);
        }
        LoadingDialog.show(this);

    }

    @Override
    protected void initUI() {
        courseAdapter = new BlocklyCourseAdapter(R.layout.item_blockly_course, courseList, this);
        courseAdapter.setOnItemClickListener(this);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 4);
        mRecyleviewContent.setLayoutManager(linearLayoutManager);
        mRecyleviewContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 30;
                outRect.left = 30;
                outRect.top = 50;
            }
        });

        mRecyleviewContent.setAdapter(courseAdapter);

        mIvMainBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_course_list;
    }

    @Override
    public void setBlocklyCourseData(List<CourseData> list) {
        courseList.clear();
        UbtLog.d(TAG, "setBlocklyCourseData list:" + list.toString());
        courseList.addAll(list);
        courseAdapter.notifyDataSetChanged();
        LoadingDialog.dismiss(this);
    }

    @Override
    public void updateFail() {
        mPresenter.getBlocklyCourseList(this);
    }

    @Override
    public void updateSuccess() {
        SPUtils.getInstance().put(SP_CURRENT_BLOCK_COURSE_ID, -1);
        mPresenter.getBlocklyCourseList(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        //判断该课程是否下载完成，下载完成则直接播放，没有则开始下载

        if(download){
            return;
        }

        CourseData courseData = (CourseData) adapter.getItem(position);
        UbtLog.d(TAG, "onItemClick:" + courseData);
        if(!courseData.getStatus().equals("1")){
            return;
        }
        if(TextUtils.isEmpty(courseData.getLocalVideoPath())){

            ProgressBar pbVideo = view.findViewById(R.id.pb_video);
            pbVideo.setVisibility(View.VISIBLE);
            downloadVideo(courseData, view);

        }else{
            File file = new File(courseData.getLocalVideoPath());
            if(file.exists()){
                Intent intent = new Intent(CourseListActivity.this, BlocklyCourseActivity.class);
                intent.putExtra(BlocklyCourseActivity.TRANSITION, true);
                UbtLog.d(TAG, "putExtra:" + courseData);
                intent.putExtra(BlocklyCourseActivity.COURSE_DATA, courseData);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair pair = new Pair<>(view, BlocklyCourseActivity.IMG_TRANSITION);
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            CourseListActivity.this, pair);
                    ActivityCompat.startActivity(CourseListActivity.this, intent, activityOptions.toBundle());
                } else {
                    CourseListActivity.this.startActivity(intent);
                    CourseListActivity.this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }else{
                ProgressBar pbVideo = view.findViewById(R.id.pb_video);
                pbVideo.setVisibility(View.VISIBLE);
                downloadVideo(courseData, view);
            }

        }



    }

    private boolean download = false;
    private void downloadVideo(final CourseData courseData, final View view){
        download = true;
        UbtLog.d(TAG, "path:" + BlocklyUtil.getVideoDir(this) + "_name:" + courseData.getName());
//        FileTools.deleteFile(new File(BlocklyUtil.getVideoPath() + File.separator + courseData.getName()));
        OkHttpClientUtils.getDownloadFile(courseData.getVideoUrl()).execute(new FileCallBack(BlocklyUtil.getVideoDir(this), courseData.getName()+".mp4") {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "downloadVideo onError:" + e.getMessage());
                view.findViewById(R.id.pb_video).setVisibility(View.GONE);
                ToastUtils.showShort("视频文件下载失败");
                download = false;
            }

            @Override
            public void onResponse(File response, int id) {
                UbtLog.d(TAG, "downloadVideo onResponse:" + response.getAbsolutePath());
                download = false;

                view.findViewById(R.id.pb_video).setVisibility(View.GONE);

                courseData.setLocalVideoPath(response.getAbsolutePath());
                courseData.update(courseData.getCid());

                Intent intent = new Intent(CourseListActivity.this, BlocklyCourseActivity.class);
                intent.putExtra(BlocklyCourseActivity.TRANSITION, true);
                intent.putExtra(BlocklyCourseActivity.COURSE_DATA, courseData);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair pair = new Pair<>(view, BlocklyCourseActivity.IMG_TRANSITION);
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            CourseListActivity.this, pair);
                    if(!isFinishing()){
                        ActivityCompat.startActivity(CourseListActivity.this, intent, activityOptions.toBundle());
                    }

                } else {
                    if(!isFinishing()){
                        CourseListActivity.this.startActivity(intent);
                        CourseListActivity.this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    }


                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                UbtLog.d(TAG, "downloadVideo inProgress:" + progress);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }



}
