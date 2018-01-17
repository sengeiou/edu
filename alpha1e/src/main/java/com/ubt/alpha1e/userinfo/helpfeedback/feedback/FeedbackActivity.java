package com.ubt.alpha1e.userinfo.helpfeedback.feedback;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.FeedbackRecyclerAdapter;
import com.ubt.alpha1e.base.FileUtils;
import com.ubt.alpha1e.base.PermissionUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.data.DataCheckTools;
import com.ubt.alpha1e.data.DataTools;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.ui.custom.ClearableEditText;
import com.ubt.alpha1e.ui.dialog.SLoadingDialog;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;
import com.ubt.alpha1e.userinfo.helpfeedback.adapter.PhotoRecyclerAdapter;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class FeedbackActivity extends MVPBaseActivity<FeedbackContract.View, FeedbackPresenter> implements FeedbackContract.View {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    private static final int ADD_PHOTO_UPDATE = 1;
    public static final int DEL_PHOTO_UPDATE = 2;

    public static final int GetUserHeadRequestCodeByShoot = 1001;
    public static final int GetUserHeadRequestCodeByFile = 1002;

    @BindView(R.id.tv_base_title_name)
    TextView tvBaseTitleName;
    @BindView(R.id.edt_feedback)
    ClearableEditText edtFeedback;
    @BindView(R.id.edt_email)
    ClearableEditText edtEmail;
    @BindView(R.id.edt_phone)
    ClearableEditText edtPhone;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rv_add_photo)
    RecyclerView rvAddPhoto;
    @BindView(R.id.rl_add_photo)
    RelativeLayout rlAddPhoto;

    private List<Map<String,Object>> mPhotoInfoDatas = null;
    private PhotoRecyclerAdapter mAdapter = null;
    private Uri mImageUri;

    protected Dialog mCoonLoadingDia;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ADD_PHOTO_UPDATE:
                    mAdapter.notifyDataSetChanged();
                    if(mPhotoInfoDatas.size() == 6){
                        rlAddPhoto.setVisibility(View.GONE);
                    }
                    break;
                case DEL_PHOTO_UPDATE:
                    if(mPhotoInfoDatas.size() < 6){
                        rlAddPhoto.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static void LaunchActivity(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void initUI() {
        tvBaseTitleName.setText(getStringResources("ui_setting_idea_feedback"));
        ivTitleRight.setVisibility(View.VISIBLE);

        initRecyclerViews();

        mCoonLoadingDia = SLoadingDialog.getInstance(this);
    }

    public void initRecyclerViews() {
        mPhotoInfoDatas = new ArrayList<>();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvAddPhoto.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator animator = rvAddPhoto.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mAdapter = new PhotoRecyclerAdapter(getContext(), mPhotoInfoDatas, mHandler);
        rvAddPhoto.setAdapter(mAdapter);
    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.ll_base_back, R.id.iv_title_right,R.id.rl_add_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                FeedbackActivity.this.finish();
                break;
            case R.id.rl_add_photo:
                showAddPhotoDialog();
                break;
            case R.id.iv_title_right:

                doCommit();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        UbtLog.d(TAG,"requestCode = " + requestCode + "     resultCode = " + resultCode);
        if (requestCode == GetUserHeadRequestCodeByFile
                || requestCode == GetUserHeadRequestCodeByShoot) {
            if (resultCode == RESULT_OK) {
                ContentResolver cr = this.getContentResolver();
                if (requestCode == GetUserHeadRequestCodeByFile) {
                    if (data == null) {
                        return;
                    }

                    String type = cr.getType(data.getData());
                    if (type == null) {
                        return;
                    }
                    mImageUri = data.getData();
                }
                getCameraImage(mImageUri);
            }
        }
    }

    private void doCommit() {
        UbtLog.d(TAG, "doCommit");
        UbtLog.d(TAG,"mPhotoInfoDatas = " + mPhotoInfoDatas.size());


        String content = edtFeedback.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShort(getStringResources("ui_about_feedback_empty"));
            return;
        }

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(getStringResources("ui_setting_contact_info_request"));
            return;
        }

        if (!TextUtils.isEmpty(email) && !DataCheckTools.isEmail(email)) {
            ToastUtils.showShort(getStringResources("ui_login_prompt_email_wrong_format"));
            return;
        }

        Map<String,File> fileMap = null;
        if(mPhotoInfoDatas != null && !mPhotoInfoDatas.isEmpty()){
            fileMap = new HashMap<>();
            for(Map<String,Object> map : mPhotoInfoDatas){

                File file = new File((String) map.get("path"));
                if(file.isFile()){
                    UbtLog.d(TAG,"map = " + map.get("path") + " " +file.isFile() + "    file = " + file.getName());
                    fileMap.put(file.getName(),file);
                }
            }
            UbtLog.d(TAG,"fileMap = " + fileMap.size());
        }

        mCoonLoadingDia.show();
        //mPresenter.doFeedBack(content, email, phone,fileMap);
        mPresenter.doFeedBack(content, email, phone, null);
    }

    @Override
    public void onFeedbackFinish(boolean isSuccess, String errorMsg) {
        if (mCoonLoadingDia.isShowing()) {
            mCoonLoadingDia.cancel();
        }

        if (!TextUtils.isEmpty(errorMsg)) {
            ToastUtils.showShort(errorMsg);
        }

        if (isSuccess) {
            FeedbackActivity.this.finish();
        }
    }

    private void showAddPhotoDialog(){
        ViewHolder viewHolder = new ViewHolder(R.layout.dialog_userecenter_head);
        DialogPlus.newDialog(getContext())
                .setContentHolder(viewHolder)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(R.drawable.bg_edit_user)
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if (view.getId() == R.id.tv_take_photo) {
                            UbtLog.d(TAG,"tv_take_photo-");
                            takeImageFromShoot();
                        } else if (view.getId() == R.id.tv_take_ablum) {
                            takeImageFromAblum();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create().show();
    }

    public void takeImageFromShoot() {
        //首先判断是否开启相机权限，如果开启直接调用，未开启申请
        PermissionUtils.getInstance(this)
                .request(new PermissionUtils.PermissionLocationCallback() {
                    @Override
                    public void onSuccessful() {
                        //  ToastUtils.showShort("申请拍照权限成功");
                        UbtLog.d(TAG,"申请拍照权限成功-");
                        getShootCamera();
                    }

                    @Override
                    public void onFailure() {
                        //ToastUtils.showShort("申请拍照权限失败");
                    }

                    @Override
                    public void onRationSetting() {
                        // ToastUtils.showShort("申请拍照权限已经被拒绝过");
                    }

                    @Override
                    public void onCancelRationSetting() {
                    }


                }, PermissionUtils.PermissionEnum.CAMERA,this);

    }

    public void getShootCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String catchPath = FileUtils.getCacheDirectory(this, "");
        // File path = new File(FileTools.image_cache);
        File path = new File(catchPath + "/images");
        if (!path.exists()) {
            path.mkdirs();
        }

        mImageUri = Uri.fromFile(new File(path, System.currentTimeMillis() + ""));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent, GetUserHeadRequestCodeByShoot);
    }

    /**
     * 从相册获取照片
     */
    public void takeImageFromAblum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GetUserHeadRequestCodeByFile);
    }

    /***
     *获取相机或本地图片
     */
    private void getCameraImage(final Uri uri)
    {

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Glide.with(mContext).load(uri).centerCrop().into(img_action_logo);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Bitmap bitmap = Glide.with(getContext()).load(mImageUri).asBitmap().into((int) DataTools.dip2px(getContext(), 60),
                            (int)DataTools.dip2px(getContext(),60)).get();

                    String path = FileTools.image_cache + File.separator + System.currentTimeMillis() + ".jpg";
                    //Bitmap bitmap = FileUtils.getBitmapFormUri(this, mImageUri);

                    UbtLog.d(TAG,"bitmap = " + bitmap);
                    FileTools.writeImage(bitmap, path, true);

                    Map<String,Object> map = new HashMap<>();
                    map.put("path",path);
                    map.put("bitmap",bitmap);
                    mPhotoInfoDatas.add(map);
                    mHandler.sendEmptyMessage(ADD_PHOTO_UPDATE);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }.execute();


    }

}
