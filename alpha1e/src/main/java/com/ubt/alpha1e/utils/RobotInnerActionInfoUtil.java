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
            "瑜伽前伸展式",
            "运盐的驴",
            "周星驰大笑"

    };
    private static String[] mActionTime = {
            "01:19",
            "02:36",
            "01:24",
            "01:35",
            "02:36",
            "01:21",
            "02:34",
            "01:10",
            "00:19",
            "01:25",
            "01:59",
            "01:19",
            "01:35",
            "02:21",
            "01:37",
            "01:45",
            "00:51",
            "00:40",
            "00:40",
            "01:35",
            "01:54",
            "01:06",
            "00:11",
            "00:12",
            "00:10",
            "00:10",
            "00:19",
            "01:47",
            "00:07"
    };
    private static String[] mActionLogo={
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
            "yoga",
            "sport"};
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
