package com.ubt.alpha1e.utils;

import android.widget.ImageView;

import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.HashMap;

/**
 * @作者：ubt
 * @日期: 2018/4/2 14:38
 * @描述:
 */


public class RobotInnerActionInfoUtil {
    private static String TAG="RobotInnerActionInfoUtil";
    private static  String[] mActionName = {"Dangerous",
            "Get up",
            "Happy birthday",
            "Sorry sorry",
            "Sweet and sour",
            "Waka waka",
            "We are taking off",
            "叮叮当",
            "俯卧撑",
            "功夫",
            "江南Style",
            "刻舟求剑",
            "木马屠城记",
            "青春修炼手册",
            "生金蛋的鹅",
            "狮子与公牛",
            "太极",
            "体操扩胸运动",
            "体操伸展运动",
            "偷东西的小孩和他的母亲",
            "徒劳的寒鸦",
            "小苹果",
            "瑜伽蹲式",
            "瑜伽金鸡独立式",
            "瑜伽敬礼式",
            "瑜伽展臂式",
            "运盐的驴",
            "周星驰的大笑"
    };
    private static String[] mActionTime = {"1:19",
            "2:36",
            "1:24",
            "1:35",
            "2:36",
            "1:21",
            "2:34",
            "1:10",
            "0:19",
            "1:25",
            "1:59",
            "1:19",
            "1:35",
            "2:21",
            "1:37",
            "1:45",
            "0:51",
            "0:40",
            "0:40",
            "1:35",
            "1:54",
            "1:06",
            "0:11",
            "0:12",
            "0:10",
            "0:10",
            "1:47",
            "0:07"
    };
    private static String[] mActionLogo={"dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "dance",
            "story",
            "story",
            "story",
            "story",
            "story",
            "story",
            "story",
            "sport",
            "sport",
            "sport",
            "sport",
            "yoga",
            "yoga",
            "yoga",
            "yoga",
            "yoga"};
   static  HashMap<String, String > mActionIconInfo=new HashMap<>();
   static  HashMap<String,String> mActionTimeInfo=new HashMap<>();
    public static void init(){
        for(int i=0;i<mActionName.length;i++){
            mActionIconInfo.put(mActionName[i],mActionLogo[i]);
            mActionTimeInfo.put(mActionName[i],mActionTime[i]);
        }

    }

    public static String  getImageIcon(String actionName){
        UbtLog.d(TAG,"actionName"+actionName+"getImageIcon        "+mActionIconInfo.get(actionName));
        return mActionIconInfo.get(actionName);
    };
    public static String getTime(String actionName){
        UbtLog.d(TAG,"actionName"+actionName+"getTime        "+mActionTimeInfo.get(actionName));
        return mActionTimeInfo.get(actionName);
    }

}