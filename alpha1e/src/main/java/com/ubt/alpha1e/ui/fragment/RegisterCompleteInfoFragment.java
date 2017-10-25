package com.ubt.alpha1e.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DataTools;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.model.UserInfo;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.ui.RegisterNextStepActivity;
import com.ubt.alpha1e.ui.dialog.alertview.AlertView;
import com.ubt.alpha1e.ui.dialog.alertview.OnItemClickListener;
import com.ubt.alpha1e.ui.helper.ActionsEditHelper;
import com.ubt.alpha1e.ui.helper.PrivateInfoHelper;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
   * User: wilson
   * Description: 注册流程完善信息界面
   * Time: 2016/7/14 16:48
   */

public class RegisterCompleteInfoFragment extends BaseRegisterFragment {


     private ImageView img_profile;
     private EditText edt_nickname;
     private Uri mImageUri;
     private Bitmap headBitmap;
     private int height,width;
     private UserInfo mCurrentUserInfo;
     private PrivateInfoHelper privateInfoHelper;
     public RegisterCompleteInfoFragment() {
        // Required empty public constructor
    }

    public static RegisterCompleteInfoFragment newInstance() {
        RegisterCompleteInfoFragment fragment = new RegisterCompleteInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        privateInfoHelper = ((RegisterNextStepActivity)getActivity()).mPrivateInfoHelper;

    }

    @Override
     protected int getLayoutResourceId() {
         return R.layout.fragment_register_complete_info;
     }

     @Override
     public void gotoNextStep() {
         privateInfoHelper.setmCurrentHead(headBitmap);
         mCurrentUserInfo.userName = edt_nickname.getText().toString();
         privateInfoHelper.doEditPrivateInfo(mCurrentUserInfo);
     }

     @Override
     protected void initViews() {
         img_profile = (ImageView)mView.findViewById(R.id.img_register_complete_profile);
         edt_nickname = (EditText)mView.findViewById(R.id.edt_register_complete_nick);
         ((RegisterNextStepActivity)mActivity).setNextButtonEnable(false);
          mCurrentUserInfo = UserInfo.doClone(((AlphaApplication) mActivity.getApplicationContext())
                  .getCurrentUserInfo());
     }

     @Override
     protected void initListeners() {
         img_profile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //先关闭键盘
                 ((RegisterNextStepActivity)mActivity).doCloseKeyboard();

                 new AlertView(null, null, ((BaseActivity)mActivity).getStringResources("ui_common_cancel"), new String[]{
                         ((BaseActivity)mActivity).getStringResources("ui_distribute_take_photo"),
                         ((BaseActivity)mActivity).getStringResources("ui_distribute_phone_library")},
                         null,
                         mActivity, AlertView.Style.ActionSheet, new OnItemClickListener(){
                     public void onItemClick(Object o,int position){
                         switch (position)
                         {
                             case 0:
                                 fromTakingPhoto();
                                 break;
                             case 1:
                                 fromFileSelect();
                                 break;
                             case 2:
                                 break;
                         }

                     }
                 }).show();

             }
         });
         edt_nickname.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 ((RegisterNextStepActivity)mActivity).setNextButtonEnable(s.length()>0);
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
     }

     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    /**
     * photo taking
     */
    private void fromTakingPhoto()
    {
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File path = new File(FileTools.image_cache);
        if (!path.exists()) {
            path.mkdirs();
        }
        mImageUri = Uri.fromFile(new File(path, System.currentTimeMillis()
                + ""));

        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                mImageUri);
        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent,
                ActionsEditHelper.GetUserHeadRequestCodeByShoot);
    }

    /**
     * from file
     */
    private void fromFileSelect()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,
                ActionsEditHelper.GetUserHeadRequestCodeByFile);
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
                Glide.with(mActivity).load(uri).centerCrop().into(img_profile);
                 height = img_profile.getHeight();
                 width = img_profile.getWidth();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    headBitmap = Glide.with(mActivity).load(uri).asBitmap().into((int) DataTools.dip2px(mActivity, height),
                            (int)DataTools.dip2px(mActivity,width)).get();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActionsEditHelper.GetUserHeadRequestCodeByShoot
                || requestCode == ActionsEditHelper.GetUserHeadRequestCodeByFile) {
            if (resultCode == mActivity.RESULT_OK) {
                ContentResolver cr = mActivity.getContentResolver();
                if (requestCode == ActionsEditHelper.GetUserHeadRequestCodeByFile) {
                    if (data == null)
                        return;
                    String type = cr.getType(data.getData());
                    if (type == null)
                        return;
                    mImageUri = data.getData();
                }
                getCameraImage(mImageUri);

            }
        }
    }
}
