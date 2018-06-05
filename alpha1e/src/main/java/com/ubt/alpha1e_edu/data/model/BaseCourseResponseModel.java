package com.ubt.alpha1e_edu.data.model;

/**
 * Created by jason on 2017/07/17.
 */
public class BaseCourseResponseModel<T> {

    public int offset;
    public long limit;
    public int total;
    public int size;
    public int pages;
    public int current;
    public boolean searchCount;
    public boolean openSort;
    public boolean optimizeCount;
    public String orderByField;
    public String condition;
    public int offsetCurrent;
    public boolean asc;

    /**返回records*/
    public  T records;
}
