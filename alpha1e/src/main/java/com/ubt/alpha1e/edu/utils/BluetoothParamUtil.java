package com.ubt.alpha1e.edu.utils;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubtechinc.base.ConstValue;

import java.io.UnsupportedEncodingException;

/**
 * 将所要传替的值，封装成json格式
 * Created by lihai on 2017/4/14.
 */
public class BluetoothParamUtil {

    /**
     * 将字符串数组转成 Json类型的字符串
     * @param params 需要传参的字符串数组
     * @param cmd 蓝牙指令符
     * @return 返回字符串
     */
    public static String paramsToJsonString(String[] params, byte cmd) {
        String params_str = "";
        switch (cmd) {
            case ConstValue.DV_DO_NETWORK_CONNECT:
                params_str = "{\"ESSID\":\"" + params[0]
                        + "\",\"PassKey\":\"" + params[1] + "\"}";
                break;
            case ConstValue.DV_DO_DOWNLOAD_ACTION:
                params_str = "{\"actionId\":\"" + params[0]
                        + "\",\"actionName\":\"" + params[1]
                        + "\",\"actionPath\":\"" + params[2]+ "\"}";

                break;
            case ConstValue.DV_CONTROL_HIBITS_PLAY:
                params_str = "{\"eventId\":\"" + params[0]
                        + "\",\"playAudioSeq\":\"" + params[1]
                        + "\",\"cmd\":\"" + params[2]+ "\"}";
                break;
            default:
                break;
        }

        return params_str;
    }

    /**
     * 将字符串数组转成 Json类型的byte数组参数
     * @param params 需要传参的字符串数组
     * @param cmd 蓝牙指令符
     * @return 返回byte数组
     */
    public static byte[] paramsToJsonBytes(String[] params, byte cmd){
        String jsonParam = paramsToJsonString(params,cmd);
        byte[] paramBytes = new byte[0];
        try {
            if(((AlphaApplication)AlphaApplication.getBaseActivity().getApplicationContext()).isAlpha1E()){
                paramBytes = jsonParam.getBytes("UTF-8");
            }else {
                paramBytes = jsonParam.getBytes("GBK");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return paramBytes;
    }

    /**
     * 将字符串转成Byte数组
     * @param params
     * @return
     */
    public static byte[] stringToBytes(String params){
        byte[] paramBytes = new byte[0];
        try {
            paramBytes = params.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return paramBytes;
    }

    /**
     * 将Byte数组转成字符串
     * @param params
     * @return
     */
    public static String bytesToString(byte[] params){
        String result = null;
        try {
            result = new String(params,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
