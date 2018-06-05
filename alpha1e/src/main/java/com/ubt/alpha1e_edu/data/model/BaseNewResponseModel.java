package com.ubt.alpha1e_edu.data.model;

/**
 * Created by jason on 2017/07/17.
 */
public class BaseNewResponseModel<T> {

    /**返回状态*
     *true/false
     */
    public boolean success;

    /**返回错误码
     * errCode
     * */
    public int errCode;

    /**返回msg
     *
     **/
    public String msg;

    /**返回data*/
    public  T data;

    /**
     * 服务器响应时间
     */
    public long responseTime;
}
