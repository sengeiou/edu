package com.ubt.alpha1e_edu.data.model;

/**
 * Created by wilson on 2016/6/8.
 */
public class BaseResponseModel<T> {

    /**返回状态*
     *true/false
     */
    public boolean status;

    /**返回状态码
     * success:0000
     * */
    public String info;

    /**返回model*/
    public  T models;




}
