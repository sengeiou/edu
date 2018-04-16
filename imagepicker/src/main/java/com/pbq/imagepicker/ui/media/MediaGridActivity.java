package com.pbq.imagepicker.ui.media;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pbq.imagepicker.ImageDataSource;
import com.pbq.imagepicker.ImagePicker;
import com.pbq.imagepicker.R;
import com.pbq.imagepicker.VideoDataSource;
import com.pbq.imagepicker.VideoPicker;
import com.pbq.imagepicker.adapter.image.ImageFolderAdapter;
import com.pbq.imagepicker.adapter.image.ImageGridAdapter;
import com.pbq.imagepicker.bean.ImageFolder;
import com.pbq.imagepicker.bean.ImageItem;
import com.pbq.imagepicker.bean.VideoFolder;
import com.pbq.imagepicker.bean.VideoItem;
import com.pbq.imagepicker.jcamera.util.CheckPermission;
import com.pbq.imagepicker.jcamera.util.FileUtil;
import com.pbq.imagepicker.ui.image.ImageBaseActivity;
import com.pbq.imagepicker.ui.image.ImageCropActivity;
import com.pbq.imagepicker.ui.image.ImagePreviewActivity;
import com.pbq.imagepicker.ui.video.VideoPlayPreviewActivity;
import com.pbq.imagepicker.view.FolderPopUpWindow;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MediaGridActivity extends ImageBaseActivity implements ImageDataSource.OnImagesLoadedListener,VideoDataSource.OnVideosLoadedListener, ImageGridAdapter.OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener {

    private static final String TAG = MediaGridActivity.class.getSimpleName();

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    private static final int LOAD_IMAGE_FINISH = 0x03;
    private static final int LOAD_VIDEO_FINISH = 0x04;
    private static final int REQUEST_VIDEO_PERMISSIONS = 0x05;

    //视频权限
    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };


    private ImagePicker imagePicker;
    private VideoPicker videoPicker;

    private boolean isOrigin = false;  //是否选中原图
    private GridView mGridView;  //图片展示控件
    private View mFooterBar;     //底部栏
    private TextView tvFinish;       //确定按钮
    private Button mBtnPre;      //预览按钮

    private LinearLayout llTitleName = null;
    private TextView tvTitleName = null;
    private ImageView tvTitleUpDown = null;

    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器

    private List<VideoFolder> mVideoFolders;   //所有的视频文件夹

    private boolean mLoadingAllImage = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOAD_IMAGE_FINISH:
                    new VideoDataSource(MediaGridActivity.this, null, MediaGridActivity.this);

                    break;
                case LOAD_VIDEO_FINISH:

                    if(mImageFolders.size() > 0){
                        ArrayList<ImageItem> imageItems = mImageFolders.get(0).images;

                        if(mVideoFolders.size() > 0){

                            ArrayList<ImageItem> imageItemTemp = new ArrayList<>();
                            ArrayList<VideoItem> videoItems = mVideoFolders.get(0).videos;
                            ImageItem imageItem = null;

                            for(VideoItem videoItem : videoItems){
                                imageItem = new ImageItem();
                                imageItem.name = videoItem.name;
                                imageItem.path = videoItem.path;
                                imageItem.size = videoItem.size;
                                imageItem.width = videoItem.width;
                                imageItem.height = videoItem.height;
                                imageItem.mimeType = videoItem.mimeType;
                                imageItem.addTime = videoItem.addTime;
                                imageItem.timeLong = videoItem.timeLong;
                                imageItemTemp.add(imageItem);
                            }

                            if(videoItems.size() > 0){
                                //构造所有视频的集合
                                ImageFolder allImagesFolder = new ImageFolder();
                                allImagesFolder.name = getResources().getString(R.string.all_videos);
                                allImagesFolder.path = "/";
                                //把第一张设置缩略图
                                allImagesFolder.cover = imageItemTemp.get(0);
                                allImagesFolder.images = imageItemTemp;
                                mImageFolders.add(1, allImagesFolder);  //确保第一条是所有图片
                            }

                            imageItems.addAll(imageItemTemp);
                            Collections.sort(imageItems, new Comparator<ImageItem>() {
                                @Override
                                public int compare(ImageItem item1, ImageItem item2) {
                                    Collator collator = Collator.getInstance(Locale.CHINA);
                                    return collator.compare(item2.addTime+"", item1.addTime+"");
                                }
                            });

                            mImageGridAdapter.refreshData(imageItems);
                            mImageFolderAdapter.refreshData(mImageFolders);
                        }
                    }

                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagepick_activity_media_grid);

        imagePicker = ImagePicker.getInstance();
        imagePicker.clear();
        videoPicker = VideoPicker.getInstance();
        videoPicker.clear();
        //图片加载完成是回调该接口
        imagePicker.addOnImageSelectedListener(this);
        //videoPicker.addOnVideoSelectedListener(this);

        findViewById(R.id.ll_base_back).setOnClickListener(this);

        tvFinish = (TextView) findViewById(R.id.tv_base_right);
        tvFinish.setOnClickListener(this);

        llTitleName = (LinearLayout) findViewById(R.id.ll_base_title_name);
        tvTitleName = (TextView) findViewById(R.id.tv_base_title_name);
        tvTitleUpDown = (ImageView) findViewById(R.id.iv_title_up_down);
        tvTitleName.setText(getString(R.string.all_images_videos));
        llTitleName.setOnClickListener(this);

        mBtnPre = (Button) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mFooterBar = findViewById(R.id.footer_bar);
        if (imagePicker.isMultiMode()) {
            tvFinish.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            tvFinish.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);

        onImageSelected(0, null, false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG,"Loading data ....");
                mLoadingAllImage = true;
                new ImageDataSource(this, null, this);
                //new VideoDataSource(this, null, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");

        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLoadingAllImage = true;
                new ImageDataSource(this, null, this);
            } else {
                showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_IMAGE_TAKE);
            } else {
                showToast("权限被禁止，无法打开相机");
            }
        }else if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.length == VIDEO_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,"您没有相机和录音权限，请到系统设置里授权",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else {
                Toast.makeText(this,"您没有相机和录音权限，请到系统设置里授权",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        //videoPicker.removeOnVideoSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_base_right) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_IMAGE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
            finish();
        } else if (id == R.id.ll_base_title_name) {
            Log.d(TAG,"ll_base_title_name-");
            if (mImageFolders == null) {
                Log.i("MediaGridActivity", "您的手机没有图片");
                return;
            }

            //点击文件夹按钮
            createPopupFolderList();
            mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据

            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                tvTitleUpDown.setImageResource(R.drawable.imagepick_icon_arrow_up);
                Log.d(TAG,"llTitleName.getHeight() => " + llTitleName.getHeight());

                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, llTitleName.getHeight());

                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {

            if(imagePicker.getSelectImageCount() > 0 ){
                if(imagePicker.getSelectedImages().get(0).isVideo()){

                    Intent intent = new Intent(MediaGridActivity.this, VideoPlayPreviewActivity.class);
                    intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEM, imagePicker.getSelectedImages().get(0));
                    startActivityForResult(intent, ImagePicker.REQUEST_VIDEO_PREVIEW);
                }else {
                    Intent intent = new Intent(MediaGridActivity.this, ImagePreviewActivity.class);
                    intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
                    intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getSelectedImages());
                    intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
                    startActivityForResult(intent, ImagePicker.REQUEST_IMAGE_PREVIEW);
                }
            }

        } else if (id == R.id.ll_base_back) {
            //点击返回按钮
            finish();
        }
    }

    /** 创建弹出的ListView */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();

                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
                    mImageGridAdapter.refreshData(imageFolder.images);
                    tvTitleName.setText(imageFolder.name);
                }
                mGridView.smoothScrollToPosition(0);//滑动到顶部
            }
        });

        mFolderPopupWindow.setMargin(llTitleName.getHeight());

        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tvTitleUpDown.setImageResource(R.drawable.imagepick_icon_arrow_down);
            }
        });
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0){
            mImageGridAdapter.refreshData(null);
        }  else {
            mImageGridAdapter.refreshData(imageFolders.get(0).images);

            for(ImageItem imageItem : imageFolders.get(0).images){
                //Log.d(TAG,"imageItem = " + imageItem.path + "   = " + imageItem.mimeType);
            }
        }
        mImageGridAdapter.setOnImageItemClickListener(this);
        mGridView.setAdapter(mImageGridAdapter);
        mImageFolderAdapter.refreshData(imageFolders);

        Log.d(TAG,"imageFolders = " + imageFolders.size());
        for(ImageFolder ifm : imageFolders){
            //Log.d(TAG,"ifm = " + ifm.path + "   ");
        }

        if(mLoadingAllImage){
            mLoadingAllImage = false;
            mHandler.sendEmptyMessage(LOAD_IMAGE_FINISH);
        }
    }

    @Override
    public void onVideosLoaded(List<VideoFolder> videoFolders) {
        this.mVideoFolders = videoFolders;
        videoPicker.setVideoFolders(videoFolders);

        if (videoFolders.size() == 0) {
            //mVideoGridAdapter.refreshData(null);
        } else {
            //Log.d(TAG,"videoFolders.get(0).videos = " + videoFolders.get(0).videos);
            //mVideoGridAdapter.refreshData(videoFolders.get(0).videos);
        }
        mHandler.sendEmptyMessage(LOAD_VIDEO_FINISH);
        //视频点击的监听事件
        //mVideoGridAdapter.setOnVideoItemClickListener(this);
        //mGridView.setAdapter(mVideoGridAdapter);
        //mVideoFolderAdapter.refreshData(videoFolders);
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            /*Intent intent = new Intent(MediaGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getCurrentImageFolderItems());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_IMAGE_PREVIEW);  //如果是多选，点击图片进入预览界面*/

        } else {
            imagePicker.clearSelectedImages();
            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(MediaGridActivity.this, ImageCropActivity.class);
                startActivityForResult(intent, ImagePicker.REQUEST_IMAGE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                setResult(ImagePicker.RESULT_IMAGE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {

        if(item != null){
            Log.d(TAG,"position = " + position + "  isAdd = " + isAdd + " item => " + item.path);
        }

        if (imagePicker.getSelectImageCount() > 0) {
            tvFinish.setText(getString(R.string.select_complete_single, imagePicker.getSelectImageCount()));
            tvFinish.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            tvFinish.setText(getString(R.string.complete));
            tvFinish.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            if(resultCode==ImagePicker.RESULT_MEDIA_BACK){
                boolean isPhoto = data.getBooleanExtra("isPhoto", false);
                if(!isPhoto) {
                    String videoPath = data.getStringExtra("videoPath");
                    long totalTime = data.getLongExtra("totalTime", 0);
                    int videoWidth = data.getIntExtra("videoWidth", 0);
                    int videoHeight = data.getIntExtra("videoHeight", 0);

                    if (FileUtil.isNotEmpty(videoPath)) {

                        File videoFile = new File(videoPath);
                        VideoPicker.galleryAddPic(this, videoFile);

                        //TODO 获取视频
                        Toast.makeText(this,"视频文件："+videoPath,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    String imageUrl = data.getStringExtra("imageUrl");
                    String type = data.getStringExtra("type");
                    File picFile = new File(imageUrl);

                    Log.d(TAG,"imageUrl = " + imageUrl);
                    //发送广播通知图片增加了
                    ImagePicker.galleryAddPic(this, picFile);

                    ImageItem imageItem = new ImageItem();
                    imageItem.path = picFile.getAbsolutePath();
                    imagePicker.clearSelectedImages();
                    imagePicker.addSelectedImageItem(0, imageItem, true);
                    if (imagePicker.isCrop()) {
                        Intent intent = new Intent(MediaGridActivity.this, ImageCropActivity.class);
                        startActivityForResult(intent, ImagePicker.REQUEST_IMAGE_CROP);  //单选需要裁剪，进入裁剪界面
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                        setResult(ImagePicker.RESULT_IMAGE_ITEMS, intent);   //单选不需要裁剪，返回数据
                        finish();
                    }

                    //TODO 获取到图片
                    Toast.makeText(this,"图片文件："+imageUrl,Toast.LENGTH_SHORT).show();
                }
            }else if(resultCode == ImagePicker.RESULT_VIDEO_BACK ){
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                setResult(ImagePicker.RESULT_IMAGE_ITEMS, intent);
                finish();

            }else if (resultCode == ImagePicker.RESULT_IMAGE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
            } else {
                //从拍照界面返回
                //点击 X , 没有选择照片
                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) == null) {
                    //什么都不做
                } else {
                    //说明是从裁剪页面过来的数据，直接返回就可以
                    setResult(ImagePicker.RESULT_IMAGE_ITEMS, data);
                    finish();
                }
            }

        } else {
            //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
            if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_IMAGE_TAKE) {
                //发送广播通知图片增加了
                ImagePicker.galleryAddPic(this, imagePicker.getTakeImageFile());

                ImageItem imageItem = new ImageItem();
                imageItem.path = imagePicker.getTakeImageFile().getAbsolutePath();
                imagePicker.clearSelectedImages();
                imagePicker.addSelectedImageItem(0, imageItem, true);
                if (imagePicker.isCrop()) {
                    Intent intent = new Intent(MediaGridActivity.this, ImageCropActivity.class);
                    startActivityForResult(intent, ImagePicker.REQUEST_IMAGE_CROP);  //单选需要裁剪，进入裁剪界面
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                    setResult(ImagePicker.RESULT_IMAGE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                }
            }
        }
    }

    public void startCapture(){

        if (!hasPermissionsGranted(VIDEO_PERMISSIONS) && CheckPermission.getRecordState() != CheckPermission.STATE_SUCCESS) {
            requestVideoPermissions();
            return;
        }

        if(Build.VERSION.SDK_INT >= 21){
            Intent intent = new Intent(this, VideoCameraActivity.class);
            startActivityForResult(intent, 1);
        }else {
            Intent intent = new Intent();
            intent.setClass(this, CaptureImageVideoActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    /********************** 权限相关********************************/

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Requests permissions needed for recording video.
     */
    private void requestVideoPermissions() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            Toast.makeText(this,"您没有相机和录音权限，请到系统设置里授权",Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ActivityCompat.requestPermissions(this, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}