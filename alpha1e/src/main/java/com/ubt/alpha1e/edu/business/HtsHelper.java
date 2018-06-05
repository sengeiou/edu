package com.ubt.alpha1e.edu.business;

import android.util.Log;

import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.FrameActionInfo;
import com.ubt.alpha1e.edu.data.model.NewActionInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/9.
 */
public class HtsHelper {

    public static void test_write() {
        NewActionInfo info = new NewActionInfo();
        info.frameActions = new ArrayList<>();
        info.frameActions.add(FrameActionInfo.getDefaultFrame());
        info.frameActions.add(FrameActionInfo.getDefaultFrame());
        info.frameActions.add(FrameActionInfo.getDefaultFrame());
        writeHts(info, FileTools.tmp_file_cache + "/test.hts", new IHtsHelperListener() {
            @Override
            public void onHtsWriteFinish(boolean isSuccess) {
                Log.i("yuyong----------", "onHtsWriteFinish");
            }

            @Override
            public void onGetNewActionInfoFinish(boolean isSuccess) {

            }
        });
    }

    private static NewActionInfo test_actionInfo;

    public static void test_read() {
        test_actionInfo = new NewActionInfo();
        getNewActionInfoFromHts(test_actionInfo, FileTools.tmp_file_cache + "/test.hts", new IHtsHelperListener() {
            @Override
            public void onHtsWriteFinish(boolean isSuccess) {

            }

            @Override
            public void onGetNewActionInfoFinish(boolean isSuccess) {
                Log.i("yuyong----------", "onHtsReadFinish-->" + NewActionInfo.getString(test_actionInfo));
            }
        });
    }


    public static native void writeHts(NewActionInfo actionInfo, String file_path, IHtsHelperListener listener);

    public static native void getNewActionInfoFromHts(NewActionInfo actionInfo, String hts_file_path, IHtsHelperListener listener);

    static {
        System.loadLibrary("HtsHelper");
    }
}


