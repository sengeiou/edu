package com.ubt.alpha1e.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class ActionInfo extends BaseModel {

    public long downloadId;
    public String hts_file_name;
    public String countryCode;
    public long loginUserId;
    public long actionId = -1;
    public String actionUrl;
    public String actionName;
    public String actionTitle;
    public String userName;
    public String userImage;
    public String actionLangName;
    public String actionLangDesciber;
    public int actionStatus = 0; //发布状态 默认未发布 9:正在审核 1:审核通过 2:审核失败
    public String actionHeadUrl;
    public String actionStatusDetail;
    public int actionType;
    public int actionSonType;
    public int actionSortType;
    public String actionUser;
    public String actionDate;
    public String actionFilePath;
    public String actionImagePath;
    public String actionPath;
    public String actionVideoPath = "";
    public int startRow;
    public int endRow;
    public long actionTime;
    public int actionSize;
    public String countryGroup;
    public long actionOriginalId;
    public int actionResource;
    public String actionMd5;
    public int pageSize;
    public int page;
    public long actionDownloadTime;
    public long actionCommentTime;
    public long actionPraiseTime;
    public String actionDesciber;
    public long actionBrowseTime;
    public long actionCollectTime;
    public int isCollect;//是否收藏
    public int isPraise;//是否点赞
    public int downloadObjectId;//是否点赞
    public String actionResume;//发布时的心情
    public boolean isFromCreate = false;//是否是创建的动作
    public int actionHot;
    public ActionInfo thiz;

    @Override
    public ActionInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, ActionInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<ActionInfo> getModelList(String json) {
        ArrayList<ActionInfo> result = new ArrayList<ActionInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new ActionInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<ActionInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(ActionInfo  info)
    {
        try {
            return  GsonImpl.get().toJson(info);

        }catch (Exception e)
        {
            return  "";
        }
    }

    @Override
    public String toString() {
        return "ActionInfo{" +
                "downloadId=" + downloadId +
                ", hts_file_name='" + hts_file_name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", loginUserId=" + loginUserId +
                ", actionId=" + actionId +
                ", actionUrl=" + actionUrl +
                ", actionName='" + actionName + '\'' +
                ", actionTitle='" + actionTitle + '\'' +
                ", userName='" + userName + '\'' +
                ", userImage='" + userImage + '\'' +
                ", actionLangName='" + actionLangName + '\'' +
                ", actionLangDesciber='" + actionLangDesciber + '\'' +
                ", actionStatus=" + actionStatus +
                ", actionHeadUrl='" + actionHeadUrl + '\'' +
                ", actionStatusDetail='" + actionStatusDetail + '\'' +
                ", actionType=" + actionType +
                ", actionSonType=" + actionSonType +
                ", actionSortType=" + actionSortType +
                ", actionUser='" + actionUser + '\'' +
                ", actionDate=" + actionDate +
                ", actionFilePath='" + actionFilePath + '\'' +
                ", actionImagePath='" + actionImagePath + '\'' +
                ", actionPath='" + actionPath + '\'' +
                ", actionVideoPath='" + actionVideoPath + '\'' +
                ", startRow=" + startRow +
                ", endRow=" + endRow +
                ", actionTime=" + actionTime +
                ", actionSize=" + actionSize +
                ", countryGroup='" + countryGroup + '\'' +
                ", actionOriginalId='" + actionOriginalId + '\'' +
                ", actionResource=" + actionResource +
                ", actionMd5='" + actionMd5 + '\'' +
                ", pageSize=" + pageSize +
                ", page=" + page +
                ", actionDownloadTime=" + actionDownloadTime +
                ", actionCommentTime=" + actionCommentTime +
                ", actionPraiseTime=" + actionPraiseTime +
                ", actionDesciber='" + actionDesciber + '\'' +
                ", actionBrowseTime=" + actionBrowseTime +
                ", actionCollectTime=" + actionCollectTime +
                ", isCollect=" + isCollect +
                ", isPraise=" + isPraise +
                ", actionHot=" + actionHot +
                '}';
    }
}
