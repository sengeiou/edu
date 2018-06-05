package com.ubt.alpha1e.edu.data.model;

import com.baoyz.pg.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
@Parcelable
public class BannerInfo extends BaseRequestModel {

    public String countryCode;
    public int loginUserId;
    public int recommendId;
    public String recommendUrl;
    public int recommendType;

    /**点击banner跳转id
     * url:1
     * 单张图片：2
     * 图片+列表：3
     * */
    public int recommendForwardType;
    public int recommendOrder;

    /**banner图片列表
     * */
    public String recommendImage;

    public String recommendStatus;

    /**
     * actionUser : 16
     * actionDesciber : 标准动作
     * userImage : http://services.ubtrobot.com:8080//userImage/16/3IEN1Y4M16.jpg
     * actionName : 后退
     * actionType : 1
     * actionCollectTime : 2
     * actionTime : 10
     * actionImagePath : http://services.ubtrobot.com/action/16/1/alpha.jpg
     * actionVideoPath : http://video.ubtrobot.com/后退.mp4
     * actionDownloadTime : 0
     * actionDate : 1447418876000
     * actionCommentTime : 0
     * userName : support
     * actionBrowseTime : 10
     * actionId : 136
     * actionPath : http://services.ubtrobot.com/action/16/1/后退.zip
     * appType : 5
     * actionPraiseTime : 5
     */

    public List<ActionInfo> clickForward;

    public String clickForwardUrl;

    public int actionType;

    public String actionTitle;
}
