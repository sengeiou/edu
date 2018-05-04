package com.pbq.imagepicker;

/**
 * Created by Administrator on 2018/4/8.
 */

public class Constant {

    public static final int LOADER_IMAGE_ALL = 0;         //加载所有图片
    public static final int LOADER_IMAGE_CATEGORY = 1;    //分类加载图片
    public static final int LOADER_VIDEO_ALL = 2;         //加载所有视频
    public static final int LOADER_VIDEO_CATEGORY = 3;    //分类加载视频

    public final static String SP_PERMISSION_LOCATION = "sp_location_permission";
    public final static String SP_PERMISSION_STORAGE = "sp_storage_permission";
    public final static String SP_PERMISSION_CAMERA = "sp_camera_permission";
    public final static String SP_PERMISSION_MICROPHONE = "sp_microphone_permission";

    public static final String CAMERA_TYPE = "CAMERA_TYPE";//0 拍照 1 录像 2 两者都可以
    public static final int CAMERA_TYPE_ONLY_CAPTURE = 0;
    public static final int CAMERA_TYPE_ONLY_RECORDER = 1;
    public static final int CAMERA_TYPE_BOTH = 2;
}
