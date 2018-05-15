package com.pbq.imagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.pbq.imagepicker.bean.VideoFolder;
import com.pbq.imagepicker.bean.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengbangqin on 2016/12/27.
 * 加载手机视频实现类  异步加载数据 数据改变数重新更新
 */

public class VideoDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = VideoDataSource.class.getSimpleName();


    private final String[] IMAGE_PROJECTION = {     //查询视频需要的数据列
            MediaStore.Video.Media.DISPLAY_NAME,   //视频的显示名称  aaa.jpg
            MediaStore.Video.Media.DATA,           //视频的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Video.Media.SIZE,           //视频的大小，long型  132492
            MediaStore.Video.Media.WIDTH,          //视频的宽度，int型  1920
            MediaStore.Video.Media.HEIGHT,         //视频的高度，int型  1080
            MediaStore.Video.Media.MIME_TYPE,      //视频的类型     image/jpeg
            MediaStore.Video.Media.DATE_ADDED,     //视频被添加的时间，long型  1450518608
            MediaStore.Video.Media.DURATION };     //视频的时长

    private FragmentActivity activity;
    private OnVideosLoadedListener loadedListener;                     //视频加载完成的回调接口
    private ArrayList<VideoFolder> videoFolders = new ArrayList<>();   //所有的视频文件夹

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有视频
     * @param loadedListener 视频加载完成的监听
     */
    public VideoDataSource(FragmentActivity activity, String path, OnVideosLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;
        Log.d(TAG,"path ==> " + path);
        //得到LoaderManager对象
        LoaderManager loaderManager = activity.getSupportLoaderManager();

        //初始化loader
        if (path == null) {
            Log.d(TAG,"loaderManager = " + loaderManager.getLoader(Constant.LOADER_VIDEO_ALL));

            loaderManager.initLoader(Constant.LOADER_VIDEO_ALL, null, this);//加载所有的视频
        } else {
            //加载指定目录的视频
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(Constant.LOADER_VIDEO_CATEGORY, bundle, this);
        }
    }

    /**
     * 指定ID不存在 触发该方法返回一个新的loader对象
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        Log.d(TAG,"id = " + id);

        String selection = IMAGE_PROJECTION[7] + " > 0 ";

        //扫描所有视频
        //查询ContentResolver并返回一个Cursor对象
        if (id == Constant.LOADER_VIDEO_ALL){
            Log.d(TAG,"EXTERNAL_CONTENT_URI = " + MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, selection, null, IMAGE_PROJECTION[6] + " DESC");
        }

        //扫描某个视频文件夹
        if (id == Constant.LOADER_VIDEO_CATEGORY){
            selection = selection + " and " + IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%' ";
            cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, selection, null, IMAGE_PROJECTION[6] + " DESC");
        }

        return cursorLoader;
    }

    /**
     * 完成对UI界面的更新
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished =>>> ");
        videoFolders.clear();
        if (data != null) {
            ArrayList<VideoItem> allVideos = new ArrayList<>();   //所有视频的集合,不分文件夹
            Log.d(TAG,"onLoadFinished data.getCount() = " + data.getCount());

            while (data.moveToNext()) {
                //查询数据
                String videoName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String videoPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long videoSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                int videoWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                int videoHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                String videoMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                long videoAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                //Log.d(TAG,"videoMimeType = " + videoMimeType + "    videoPath = " + videoPath);
                long videoTimeLong = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));

                File videoFile = new File(videoPath);
                if(!videoFile.exists()){
                    Log.d(TAG,"videoPath = " + videoPath + "    videoFile.exists() = " + videoFile.exists());
                    continue;
                }

                //封装实体
                VideoItem videoItem = new VideoItem();
                videoItem.name = videoName;
                videoItem.path = videoPath;
                videoItem.size = videoSize;
                videoItem.width = videoWidth;
                videoItem.height = videoHeight;
                videoItem.mimeType = videoMimeType;
                videoItem.addTime = videoAddTime;
                videoItem.timeLong = videoTimeLong;
                allVideos.add(videoItem);

                //根据父路径分类存放视频
                //根据视频的路径获取到视频所在文件夹的路径和名称

                File videoParentFile = videoFile.getParentFile();
                VideoFolder videoFolder = new VideoFolder();
                videoFolder.name = videoParentFile.getName();
                videoFolder.path = videoParentFile.getAbsolutePath();
                //判断这个文件夹是否已经存在  如果存在直接添加视频进去  否则将文件夹添加到文件夹的集合中
                if (!videoFolders.contains(videoFolder)) {
                    ArrayList<VideoItem> images = new ArrayList<>();
                    images.add(videoItem);
                    //缩略图
                    videoFolder.cover = videoItem;
                    videoFolder.videos = images;
                    videoFolders.add(videoFolder);
                } else {
                    videoFolders.get(videoFolders.indexOf(videoFolder)).videos.add(videoItem);
                }
            }
            Log.d(TAG,"onLoadFinished allVideos.size() = " + allVideos.size());
            //防止没有视频报异常
            if (data.getCount() > 0 && allVideos.size() > 0) {
                //构造所有视频的集合
                VideoFolder allVideosFolder = new VideoFolder();
                allVideosFolder.name = activity.getResources().getString(R.string.all_images);
                allVideosFolder.path = "/";
                //把第一张设置缩略图
                allVideosFolder.cover = allVideos.get(0);
                allVideosFolder.videos = allVideos;
                videoFolders.add(0, allVideosFolder);  //确保第一条是所有图片
            }
        }

        //回调接口，通知视频数据准备完成
        VideoPicker.getInstance().setVideoFolders(videoFolders);
        loadedListener.onVideosLoaded(videoFolders);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }

    /** 所有视频加载完成的回调接口 */
    public interface OnVideosLoadedListener {
        void onVideosLoaded(List<VideoFolder> videoFolders);
    }
}
