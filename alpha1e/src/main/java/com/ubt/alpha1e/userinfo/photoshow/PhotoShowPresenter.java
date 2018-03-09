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
        try {
            String targetPath = FileTools.PIC_DCIM_PATH + "ubt_wechat.jpg";
            File file = new File(targetPath);
            if(file.exists()){
                refreshDCIM(file);
                mView.onSavePhoto(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_success"));
            }else {
                boolean isFileCreateSuccess =  FileTools.writeAssetsToSd("photo/ubt_wechat.png", mView.getContext(), targetPath);
                if(isFileCreateSuccess){
                    refreshDCIM(file);
                    mView.onSavePhoto(true,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_success"));
                }else {
                    mView.onSavePhoto(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_fail"));
                }
            }
        }catch (Exception ex){
            UbtLog.e(TAG,ex.getMessage());
            mView.onSavePhoto(false,((MVPBaseActivity)(mView.getContext())).getStringResources("ui_common_save_fail"));
        }
    }

    /**
     * 发送广播刷新相册
     * @param file
     */
    private void refreshDCIM(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mView.getContext().sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
    }
}
