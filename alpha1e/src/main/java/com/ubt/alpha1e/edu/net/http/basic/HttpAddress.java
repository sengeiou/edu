package com.ubt.alpha1e.edu.net.http.basic;

import android.content.Context;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.BuildConfig;
import com.ubt.alpha1e.edu.data.Md5;
import com.ubt.alpha1e.edu.data.TimeTools;
import com.ubt.alpha1e.edu.data.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class HttpAddress {

    public enum Request_type {
        login,

        new_phone_login,

        new_email_login,

        register,

        new_phone_register,

        new_email_register,

        check_update,

        check_threme,

        get_themes,

        edit_private_info,

        get_user_head_img,

        bind,

        get_actions_on_line,

        get_actions_by_type,

        get_actions_by_type_new,

        get_action_comments,

        do_action_comment,

        do_action_praise,

        do_base_actions_check_update,

        base_action_video,

        for_get_pwd,

        get_verification,

        check_verification,

        thrid_login,

        new_thrid_login,

        reset_pwd,

        scan_robot_gest,
        action_help,

        get_bana_imgs,

        get_weixin_login_info,

        get_weixin_user_info,

        get_share_url,

        do_activate,

        ubt_web_sit,

        do_get_my_collocations,

        do_collocatate_action,

        do_remove_collocatate_action,

        do_feed_back,

        get_action_detial,

        get_version_info,

        get_help_doc,

        get_intro_doc,

        get_messages_new,

        get_spring_evening,

        get_new_action_help,

        add_download,

        /**
         * 我的创建动作上传
         */
        createaction_upload,

        /**
         * 我的创建动作历史记录查询
         */
        createaction_find,
        /**
         * 我的创建动作删除
         */
        createaction_delete,
        /**
         * 我的下载历史记录查询
         */
        download_find,
        /**
         * 我的下载历史记录删除
         */
        download_delete,
        /**
         * 内置动作查询
         */
        detailById,

        /**
         * 获取七牛存储token
         */
        getQiniuToken,

        /**
         * 上传新建动作
         */
        uploadAction,

        /**
         * 根据动作类型获取列表
         */
        getListByPage,

        /**
         * H5页面详情
         */
        getDetailIos,

        /**
         * 取消动作发布
         */
        cancelPublish,

        /**
         * 删除我的创建后取消发布
         */
        deleteCreationPublish,

        /**
         * 获取活动主题
         */
        getScheme,

        /**
         * 获取活动主题信息
         */
        getSchemeInfo,

        /**
         * 服务条款
         */
        getServiceRule,

        /**
         * 记录固件版本
         */
        doRecordSoftVersion,

        /**
         * 获取IShow列表
         */
        getIshowList,

        /**
         * 获取我发布的IShow列表
         */
        getMyShow,

        /**
         * 删除我发布的ishow数据
         */
        deleteMyShow,

        /**
         * 获取热门推荐动作
         */
        get_popular_actions_on_line,

        /**
         * 获取主题推荐
         */
        get_theme_recommend,

        /**
         * 获取主题推荐动作详细
         */
        get_theme_recommend_detail,

        /**
         * 获取公共数据平台访问token
         */
        get_access_token,

        /**
         * 获取机器人本体软件更新信息
         */
        check_robot_soft_version_info,

        /**
         * 保存课程任务结果
         */
        save_user_task,

        /**
         * 批量保存课程任务结果
         */
        save_batch_user_task,

        /**
         * 获取课程任务结果列表
         */
        query_user_tasks,

        /**
         * 获取课时
         */
        query_user_lessons,

        /*************************Alpha 1E***********************************/

        /**
         * 获取用户信息
         */
        getUserInfo,
        updateUserInfo //更新用户新

        /*************************Alpha 1E***********************************/

    }

    public static final String NO_URL = "NO_URL";

    public static final String WebAddressDevelop = "10.10.1.14";
    public static final String WebAddressTest = "10.10.1.12";

    public static final String WeiXinServiceAddress = "https://api.weixin.qq.com/sns";

    //public static final String WebServiceAdderss = "http://10.10.1.12:8080/ubx";//测试部测试环境
    //public static final String WebServiceAdderss = "http://10.10.1.14:8080/ubx";//研发测试环境
    public static final String WebServiceAdderss = "https://services.ubtrobot.com/ubx";//正式环境
    //public static final String WebServiceAdderss = "https://10.10.1.14:8443/ubx";//研发测试环境

    //public static final String WebServiceAdderss = BuildConfig.WebServiceAdderss;
    public static final String WebServiceAdderss_Image = "https://services.ubtrobot.com/ubx";
    public static final String WebServiceAdderss_Video = "https://services.ubtrobot.com";
    public static final String[] WebHostnames = {"services.ubtrobot.com",
            "video.ubtrobot.com",
            "www.qq.com",
            "api.weixin.qq.com",
            "api.twitter.com",
            "www.facebook.com",
            "graph.facebook.com",
            "twitter.com",
            "account.ubtrobot.com",
            "10.10.1.14",
            "10.10.1.12",
            "test79.ubtrobot.com",
            "prodapi.ubtrobot.com"};

    public static final String WebDefaultAppLauncherAddress = "https://services.ubtrobot.com/userImage/ic_launcher.png";
    public static final String WebDefaultAppWechatAddress = "https://video.ubtrobot.com/alpha1/ubt_wechat.png";

    //公共数据平台，软件更新
    public static final String WebPublicDataPlatformAdderss = "http://10.10.20.30:8020";//研发测试环境

    //课程
    //public static final String WebCourseAdderss = "http://10.10.20.30:8039/course-rest";//研发测试环境
    public static final String WebCourseAdderss = "https://account.ubtrobot.com/course-rest";//正式测试环境
    //public static final String WebCourseAdderss = BuildConfig.WebCourseAdderss;

    //Blockly更新URL
    //public static final String WebBlocklyUpdateAdderss = "http://10.10.1.12:8030/alpha1/zip?versionId=";
    public static final String WebBlocklyUpdateAdderss = "https://account.ubtrobot.com/alpha1e-rest/alpha1/zip?versionId=";
    //public static final String WebBlocklyUpdateAdderss = BuildConfig.WebBlocklyUpdateAdderss;

    //用户注册登录
    //public static final String WebUserDataPlatformAdderss = "http://10.10.1.52:8010";//研发测试环境
    public static final String WebUserDataPlatformAdderss = "http://202.170.139.168:8087/user-service-rest";//研发测试环境


    /***********************Alpha 1E*********************************/
    //public static final String WebServiceAdderssFor1E = BuildConfig.WebServiceAdderssFor1E;
    public static final String WebServiceAdderssFor1E = "https://api.ubtrobot.com/ubx";


    // -----------------------------------------------------------------------------------------
    public static final String APP_TYPE_KEY = "appType";
    public static final String APP_TYPE_VALUE = "1";
    public static final String SERVICE_VERSION_KEY = "serviceVersion";
    public static final String SERVICE_VERSION_VALUE = "V" + BuildConfig.VERSION_NAME;
    public static final String REQUEST_KEY = "requestKey";
    public static final String REQUEST_TIME_KEY = "requestTime";
    public static final String ENCRYPTION_KEY = "UBTech832%1293*6";
    public static final String TOKEN_KEY = "token";
    public static final String SYSTEM_CIG_LANGUAGE = "systemLanguage";

    public static String getRequestUrl(Request_type type) {
        switch (type) {
            case login:
                return WebServiceAdderss + "/user/login";
            case new_phone_login:
            case new_email_login:
                return WebUserDataPlatformAdderss + "/user/login";
            case register:
                return WebServiceAdderss + "/user/register";
            case new_phone_register:
            case new_email_register:
                return WebUserDataPlatformAdderss + "/user/register";
            case edit_private_info:
                return WebServiceAdderss + "/user/edit";
            case for_get_pwd:
                return WebServiceAdderss + "/user/fopassword";
            case get_actions_on_line:
                return WebServiceAdderss + "/action/last";
            case get_popular_actions_on_line:
                return WebServiceAdderss + "/action/findRecommendAction";
            case get_actions_by_type:
                return WebServiceAdderss + "/action/getListByPage";
            case get_actions_by_type_new:
                return WebServiceAdderss + "/action/getActionListByPage";
            case get_action_comments:
                return WebServiceAdderss + "/comment/find";
            case do_action_comment:
                return WebServiceAdderss + "/comment/add";

            // case do_action_praise:
            // return WebServiceAdderss + "/action/nice";

            case do_action_praise:
                return WebServiceAdderss + "/action/praise";

            case do_base_actions_check_update:
                return WebServiceAdderss + "/action/find";
            case bind:
                return WebServiceAdderss + "/equipment/active";
            case get_verification:
                return WebServiceAdderss + "/system/getSmsCode";
            case check_verification:
                return WebServiceAdderss + "/system/verifySms";
            case thrid_login:
                return WebServiceAdderss + "/user/otherUser";
            case new_thrid_login:
                return WebUserDataPlatformAdderss + "/user/login/third";
            case check_update:
                // return WebServiceAdderss + "/version/check";
                return WebServiceAdderss + "/version/verify";
            case get_themes:
                return WebServiceAdderss + "/theme/getThemeAll";
            case check_threme:
                return WebServiceAdderss + "/theme/getThemeInfo";
            case reset_pwd:
                return WebServiceAdderss + "/user/rpasswordPhone";
            case scan_robot_gest:
                return WebServiceAdderss + "/system/guide";
            case action_help:
                return WebServiceAdderss + "/actionHelp/actionHelp.html";
            case ubt_web_sit:
                return "http://www.ubtrobot.com";
            case get_bana_imgs:
                return WebServiceAdderss + "/recommend/find";
            case get_weixin_login_info:
                return WeiXinServiceAddress + "/oauth2/access_token";
            case get_weixin_user_info:
                return WeiXinServiceAddress + "/userinfo";
            case get_share_url:
                return WebServiceAdderss + "/system/getShare";
            case do_activate:
                return WebServiceAdderss + "/equipment/active";
            case do_get_my_collocations:
                return WebServiceAdderss + "/collect/find";
            case do_collocatate_action:
                return WebServiceAdderss + "/collect/add";
            case do_remove_collocatate_action:
                return WebServiceAdderss + "/collect/cancel";
            case do_feed_back:
                return WebServiceAdderss + "/system/feedback";
            case get_action_detial:
                return WebServiceAdderss + "/action/detail";
            case get_version_info:
                return WebServiceAdderss + "/version/resume";
            case get_help_doc:
                return WebServiceAdderss + "/instr_help.html";
            case get_intro_doc:
                return WebServiceAdderss + "/system/introduction";
            case get_messages_new:
                return WebServiceAdderss + "/message/findHistory";
            case get_spring_evening:
                return WebServiceAdderss + "/theme/find";
            case get_new_action_help:
                return WebServiceAdderss + "/action/newaction.html";
            case add_download:
                return WebServiceAdderss + "/action/addDownload";
            case createaction_upload:
                return WebServiceAdderss + "/createaction/upload";
            case createaction_find:
                return WebServiceAdderss + "/createaction/find";
            case createaction_delete:
                return WebServiceAdderss + "/createaction/delete";
            case download_find:
                return WebServiceAdderss + "/download/find";
            case download_delete:
                return WebServiceAdderss + "/download/delete";
            case detailById:
                return WebServiceAdderss + "/action/detailById";
            case getQiniuToken:
                return WebServiceAdderss + "/system/getQiniuToken";
            case uploadAction:
                return WebServiceAdderss + "/action/uploadAction";
            case getListByPage:
                return WebServiceAdderss + "/action/getListByPage";
            case getDetailIos:
                return WebServiceAdderss + "/action/detailIos";
            case cancelPublish:
                return WebServiceAdderss + "/action/cancle";
            case deleteCreationPublish:
                return WebServiceAdderss + "/createaction/deleteMyCreate";
            case getScheme:
                return WebServiceAdderss + "/action/getScheme";
            case getSchemeInfo:
                return WebServiceAdderss + "/scheme.html";
            case getServiceRule:
                return WebServiceAdderss + "/service_rule.html";
            case doRecordSoftVersion:
                return WebServiceAdderss + "/equipment/sumVersion";
            case getIshowList:
                return WebServiceAdderss + "/action/findShowList";
            case getMyShow:
                return WebServiceAdderss + "/action/getMyShow";
            case deleteMyShow:
                return WebServiceAdderss + "/action/deleteMyShow";
            case get_theme_recommend:
                return WebServiceAdderss + "/recommend/findRecommand";
            case get_theme_recommend_detail:
                return WebServiceAdderss + "/recommend/findRecommandDetail";
            case get_access_token:
                return WebPublicDataPlatformAdderss + "/oauth/token";
            case check_robot_soft_version_info:
                return WebPublicDataPlatformAdderss + "/file/download";
            case save_user_task:
                return WebCourseAdderss + "/course/saveUserTask";
            case save_batch_user_task:
                return WebCourseAdderss + "/course/saveBatchUserTask";
            case query_user_tasks:
                return WebCourseAdderss + "/course/queryUserTasks";
            case query_user_lessons:
                return WebCourseAdderss + "/course/queryUserLessons";

            case getUserInfo:
                return WebServiceAdderssFor1E + "/getUserInfo";

            default:
                break;
        }
        return NO_URL;
    }

    public static String getRequestUrl(Request_type type, String params) {
        switch (type) {
            case get_user_head_img:
                return WebServiceAdderss_Image + "/" + params;
            case base_action_video:
                return WebServiceAdderss_Video + "/" + params;
            default:
                return "";
        }
    }

    public static String getParamsForGet(String[] params, Request_type type) {
        switch (type) {
            case get_weixin_login_info:
                return "?appid=" + params[0] + "&secret=" + params[1] + "&code="
                        + params[2] + "&grant_type=" + params[3];
            case get_weixin_user_info:
                return "?access_token=" + params[0] + "&openid=" + params[1];
            case get_version_info:
                return "?versionName=" + params[0] + "&systemLanguage=" + params[1];
            case get_help_doc:
                return "?systemLanguage=" + params[0];
            case get_intro_doc:
                return "?systemLanguage=" + params[0];
            case get_new_action_help:
                return "?systemLanguage=" + params[0];
            case scan_robot_gest:
                return "?systemLanguage=" + params[0];
            case action_help:
                return "?lang=" + params[0];
            case get_access_token:
                return "?client_id=" + params[0] + "&client_secret=" + params[1] + "&grant_type=" + params[2]
                        + "&username=" + params[3] + "&password=" + params[4];
            case check_robot_soft_version_info:
                return "?access_token=" + params[0] + "&model=" + params[1] + "&robot_id=" + params[2]
                        + "&robot_type=" + params[3];
            case query_user_tasks:
                String param = "?courseId=" + params[0];
                if (params.length > 1) {
                    param += "&lessonId=" + params[1];
                }
                return param;
            case query_user_lessons:
                return "?current=" + params[0] + "&size=" + params[1] + "&courseId=" + params[2]
                        + "&language=" + params[3] + "&timeMillis=" + params[4];
            default:
                return "";
        }
    }

    // 需要登录的接口
    public static String getParamsForPost(String[] params, Request_type type,
                                          UserInfo mCurrentUser, Context context) {

        String params_str = getParamsForPost(params, type, context);
        String tmp = params_str.substring(0, params_str.length() - 1);
        params_str = tmp + ",\"" + TOKEN_KEY + "\":\""
                + (mCurrentUser == null ? "" : mCurrentUser.token) + "\"}";
        return params_str;

    }

    public static String[] getRequestInfo() {
        String[] req = new String[2];
        req[0] = TimeTools.getTimeVal();
        req[1] = Md5.getMD5(req[0]
                + ENCRYPTION_KEY, 32);
        return req;
    }


    // 基本接口
    public static String getParamsForPost(String params_str, Context context) {
        if (params_str != null && (!params_str.equals(""))) {
            String[] req = getRequestInfo();
            String REQUEST_TIME_VALUE = req[0];
            String REQUEST_VALUE = req[1];
            String tmp = params_str.substring(0, params_str.length() - 1);
            params_str = tmp
                    + ",\""
                    + APP_TYPE_KEY
                    + "\":\""
                    + APP_TYPE_VALUE
                    + "\",\""
                    + SERVICE_VERSION_KEY
                    + "\":\""
                    + SERVICE_VERSION_VALUE
                    + "\",\""
                    + REQUEST_TIME_KEY
                    + "\":\""
                    + REQUEST_TIME_VALUE
                    + "\",\""
                    + SYSTEM_CIG_LANGUAGE
                    + "\":\""
                    //+ context.getResources().getConfiguration().locale.getCountry()
                    + AlphaApplication.getBaseActivity().getStandardLocale(AlphaApplication.getBaseActivity().getAppCurrentLanguage())
                    + "\",\"" + REQUEST_KEY + "\":\""
                    + REQUEST_VALUE + "\"}";
            return params_str;
        } else {
            return NO_URL;
        }
    }

    // 基本接口，不需要额外传参数
    public static String getBasicParamsForPost(Context context) {
        String basicParams = "";
        String[] req = getRequestInfo();
        String REQUEST_TIME_VALUE = req[0];
        String REQUEST_VALUE = req[1];
        basicParams = "{\"" +
                APP_TYPE_KEY
                + "\":\""
                + APP_TYPE_VALUE
                + "\",\""
                + SERVICE_VERSION_KEY
                + "\":\""
                + SERVICE_VERSION_VALUE
                + "\",\""
                + REQUEST_TIME_KEY
                + "\":\""
                + REQUEST_TIME_VALUE
                + "\",\""
                + SYSTEM_CIG_LANGUAGE
                + "\":\""
                //+ context.getResources().getConfiguration().locale.getCountry()
                + AlphaApplication.getBaseActivity().getStandardLocale(AlphaApplication.getBaseActivity().getAppCurrentLanguage())
                + "\",\"" + REQUEST_KEY + "\":\""
                + REQUEST_VALUE + "\"}";
        return basicParams;
    }

    public static Map<String, String> getBasicParamsMap(Context context) {
        Map<String, String> basicMap = new HashMap<>();
        String[] req = getRequestInfo();
        String REQUEST_TIME_VALUE = req[0];
        String REQUEST_VALUE = req[1];
        basicMap.put(APP_TYPE_KEY, APP_TYPE_VALUE);
        basicMap.put(SERVICE_VERSION_KEY, SERVICE_VERSION_VALUE);
        basicMap.put(REQUEST_TIME_KEY, REQUEST_TIME_VALUE);
        //basicMap.put(SYSTEM_CIG_LANGUAGE, context.getResources().getConfiguration().locale.getCountry());
        basicMap.put(SYSTEM_CIG_LANGUAGE, AlphaApplication.getBaseActivity().getStandardLocale(AlphaApplication.getBaseActivity().getAppCurrentLanguage()));
        basicMap.put(REQUEST_KEY, REQUEST_VALUE);
        return basicMap;


    }

    // 通用接口
    public static String getParamsForPost(String[] params, Request_type type,
                                          Context context) {

        String params_str = "";

        switch (type) {
            case login:
                params_str = "{\"userName\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1] + "\"}";
                break;
            case new_phone_login:
                params_str = "{\"appType\":\"10"
                        + "\",\"userPhone\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1] + "\"}";
                break;
            case new_email_login:
                params_str = "{\"appType\":\"10"
                        + "\",\"userEmail\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1] + "\"}";
                break;
            case register:
                params_str = "{\"userName\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1] + "\",\"type\":\""
                        + params[2] + "\",\"countryCode\":\"" + params[3] + "\"}";
                break;

            case new_phone_register:
                params_str = "{\"appType\":\"10"
                        + "\",\"userPhone\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1]
                        + "\",\"verificationCode\":\"" + params[2] + "\"}";
                break;
            case new_email_register:
                params_str = "{\"appType\":\"10"
                        + "\",\"userEmail\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1]
                        + "\",\"verificationCode\":\"" + params[2] + "\"}";
                break;

            case edit_private_info:
                params_str = "{\"userId\":\"" + params[0] + "\",\"userName\":\""
                        + params[1] + "\",\"userGender\":\"" + params[2]
                        + "\",\"userImage\":\"" + params[3] + "\",\"userEmail\":\""
                        + params[4] + "\",\"countryCode\":\"" + params[5] + "\"}";
                break;
            case get_actions_on_line:
                params_str = "{\"endRow\":\"" + params[0] + "\",\"countryCode\":\""
                        + params[1] + "\"}";
                break;
            case get_popular_actions_on_line:
                params_str = "{\"page\":\"" + params[0]
                        + "\",\"pageSize\":\"" + params[1]
                        + "\",\"countryCode\":\"" + params[2]
                        + "\",\"actionResource\":\"" + params[3]
                        + "\",\"actionSonType\":\"" + params[4] + "\"}";
                break;
            case get_actions_by_type:
                params_str = "{\"actionType\":\"" + params[0]
                        + "\",\"pageSize\":\"" + params[1] + "\",\"page\":\""
                        + params[2] + "\",\"countryCode\":\"" + params[3] + "\"}";
                break;
            case get_action_comments:
                params_str = "{\"actionId\":\"" + params[0] + "\"}";
                break;
            case do_action_comment:
                params_str = "{\"commentContext\":\"" + params[0]
                        + "\",\"actionId\":\"" + params[1]
                        + "\",\"commentUserId\":\"" + params[2] + "\"}";
                break;

            case reset_pwd:
                params_str = "{\"userPhone\":\"" + params[0]
                        + "\",\"userPassword\":\"" + params[1] + "\",\"code\":\""
                        + params[2] + "\"}";
                break;

            case do_action_praise:
                params_str = "{\"actionId\":\"" + params[0] + "\"}";
                break;
            case do_base_actions_check_update:
                params_str = "{\"actionType\":\"" + params[0] + "\"}";
                break;
            case bind:
                params_str = "{\"userId\":\"" + params[0]
                        + "\",\"equipmentSeq\":\"" + params[1] + "\"}";
                break;
            case for_get_pwd:
                params_str = "{\"userEmail\":\"" + params[0] + "\"}";
                break;
            case get_verification:
                params_str = "{\"code\":\"" + params[0] + "\"}";
                break;

            case check_verification:
                params_str = "{\"phoneNumber\":\"" + params[0] + "\",\"code\":\""
                        + params[1] + "\"}";
                break;

            case thrid_login:
                params_str = "{\"userRelationId\":\"" + params[0]
                        + "\",\"userRelationType\":\"" + params[1] + "\"}";
                break;
            case new_thrid_login:
                params_str = "{\"accessToken\":\"" + params[0]
                        + "\",\"appId\":\"" + params[1]
                        + "\",\"loginType\":\"" + params[2]
                        + "\",\"openId\":\"" + params[3] + "\"}";
                return params_str;
            case check_update:
                params_str = "{\"versionProd\":\"" + params[0]
                        + "\",\"versionProdType\":\"" + params[1]
                        + "\",\"versionSystemType\":\"" + params[2] + "\"}";
                break;
            case get_bana_imgs:
                params_str = "{\"recommendType\":\"" + params[0] + "\"}";
                break;
            case get_share_url:
                params_str = "{\"type\":\"" + params[0] + "\",\"code\":\""
                        + params[1] + "\"}";
                break;
            case do_activate:
                params_str = "{\"equipmentSeq\":\"" + params[0]
                        + "\",\"equipmentType\":\"" + params[1]
                        + "\",\"userId\":\"" + params[2] + "\",\"activeArea\":\""
                        + params[3] + "\",\"equipmentUid\":\"" + params[4] + "\"}";
                break;
            case do_get_my_collocations:
                params_str = "{\"collectUserId\":\"" + params[0] + "\"}";
                break;
            case do_collocatate_action:
                params_str = "{\"collectRelationId\":\"" + params[0]
                        + "\",\"collectUserId\":\"" + params[1]
                        + "\",\"collectType\":\"" + params[2] + "\"}";
                break;
            case do_remove_collocatate_action:
                params_str = "{\"collectRelationId\":\"" + params[0]
                        + "\",\"collectUserId\":\"" + params[1]
                        + "\",\"collectType\":\"" + params[2] + "\"}";
                break;
            case get_spring_evening:
                params_str = "{\"themeType\":\"" + params[0] + "\"}";
                break;
            case createaction_upload:
                params_str = "{\"userId\":\"" + params[0] + "\",\"actionId\":\""
                        + params[1] + "\",\"actionName\":\"" + params[2]
                        + "\",\"actionDesc\":\"" + params[3] + "\",\"actionType\":\""
                        + params[4] + "\"}";
                break;
            case createaction_find:
                params_str = "{\"actionUserId\":\"" + params[0] + "\"}";
                break;
            case createaction_delete:
                params_str = "{\"actionIds\":\"" + params[0] + "\"}";
                break;
            case download_find:
                params_str = "{\"downloadUserId\":\"" + params[0] + "\",\"token\":\"" + params[1] + "\"}";
                break;
            case download_delete:
                params_str = "{\"downloadIds\":\"" + params[0] + "\"}";
                break;
            case detailById:
                params_str = "{\"actionOriginalIds\":\"" + params[0] + "\"}";
                break;
            //    actionUser  动作上传者
            //    actionDesciber  动作描述
            //    actionType 动作类型
            //    actionVideoPath 视频地址
            //    actionOriginalId 动作ID
            //    actionPath 服务器hts路径
            //    actionName 动作名称
            //    actionHeadUrl 服务器动作缩略图路径
            case uploadAction:
                params_str = "{\"actionUser\":\"" + params[0]
                        + "\",\"actionDesciber\":\"" + params[1]
                        + "\",\"actionType\":\"" + params[2]
                        + "\",\"actionVideoPath\":\"" + params[3]
                        + "\",\"actionOriginalId\":\"" + params[4]
                        + "\",\"actionPath\":\"" + params[5]
                        + "\",\"actionPath\":\"" + params[6]
                        + "\",\"actionHeadUrl\":\"" + params[7]
                        + "\"}";
                break;
            case getListByPage:
                params_str = "{\"page\":\"" + params[0]
                        + "\",\"pageSize\":\"" + params[1]
                        + "\",\"actionSortType\":\"" + params[2]
                        + "\",\"actionSonType\":\"" + params[3]
                        + "\",\"countryCode\":\"" + params[4]
                        + "\",\"token\":\"" + params[5]
                        + "\",\"userId\":\"" + params[5]
                        + "\",\"actionResource\":\"" + params[6]
                        + "\"}";
                break;
            case getDetailIos:
                params_str = "{\"actionId\":\"" + params[0] + "\",\"userId\":\"" + params[1] + "\"}";
                break;
            case cancelPublish:
                params_str = "{\"actionOriginalId\":\"" + params[0] + "\"}";
                break;
            case deleteCreationPublish:
                params_str = "{\"actionOriginalId\":\"" + params[0] + "\"}";
                break;
            case getScheme:
                params_str = "{\"countryCode\":\"" + params[0] + "\"}";
                break;
            case doRecordSoftVersion:
                params_str = "{\"equipmentVersion\":\"" + params[0] + "\",\"equipmentUid\":\"" + params[1] + "\",\"serialNumber\":\"" + params[2] + "\"}";
                break;
            case getIshowList:
                params_str = "{\"page\":\"" + params[0]
                        + "\",\"pageSize\":\"" + params[1]
                        + "\",\"actionSortType\":\"" + params[2]
                        + "\",\"actionSonType\":\"" + params[3]
                        + "\",\"countryCode\":\"" + params[4]
                        + "\",\"token\":\"" + params[5]
                        + "\",\"userId\":\"" + params[5]
                        + "\",\"actionResource\":\"" + params[6]
                        + "\"}";
                break;
            case getMyShow:
                params_str = "{\"page\":\"" + params[0]
                        + "\",\"pageSize\":\"" + params[1]
                        + "\",\"actionSortType\":\"" + params[2]
                        + "\",\"actionSonType\":\"" + params[3]
                        + "\",\"countryCode\":\"" + params[4]
                        + "\",\"token\":\"" + params[5]
                        + "\",\"actionUser\":\"" + params[5]
                        + "\",\"actionResource\":\"" + params[6]
                        + "\"}";
                break;
            case deleteMyShow:
                params_str = "{\"actionId\":\"" + params[0] + "\"}";
                break;
            case get_theme_recommend_detail:
                params_str = "{\"recommendId\":\"" + params[0] + "\"}";
                break;
            case save_user_task:
                params_str = "{\"courseId\":\"" + params[0]
                        + "\",\"lessonId\":\"" + params[1]
                        + "\",\"taskId\":\"" + params[2]
                        + "\",\"efficiencyStar\":\"" + params[3]
                        + "\",\"qualityStar\":\"" + params[4]
                        + "\"}";
                break;
            case save_batch_user_task:
                params_str = "{\"data\":" + params[0] + "}";
                return params_str;
            default:
                params_str = "";
                break;
        }

        return getParamsForPost(params_str, context);
    }
}
