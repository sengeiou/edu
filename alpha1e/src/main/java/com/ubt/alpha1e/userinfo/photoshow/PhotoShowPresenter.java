package com.ubt.alpha1e.userinfo.photoshow;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.mvp.BasePresenterImpl;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.io.File;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class PhotoShowPresenter extends BasePresenterImpl<PhotoShowContract.View> implements PhotoShowContract.Presenter{

    private static final String TAG = PhotoShowPresenter.class.getSimpleName();

    @Override
    public void doSavePhoto() {
        String targetPath = FileTools.PIC_DCIM_PATH + "ubt_wechat.png";
        File file = new File(targetPath);
        if(file.exists()){
            mView.onSavePhoto(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_success"));
        }else {
            try {
                boolean isFileCreateSuccess =  FileTools.writeAssetsToSd("photo/ubt_wechat.png", mView.getContext(), targetPath);
                if(isFileCreateSuccess){
                    mView.onSavePhoto(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_success"));
                }else {
                    mView.onSavePhoto(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_fail"));
                }
            }catch (Exception ex){
                UbtLog.e(TAG,ex.getMessage());
                mView.onSavePhoto(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_fail"));
            }
        }
    }
}
