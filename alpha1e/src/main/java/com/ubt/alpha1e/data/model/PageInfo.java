package com.ubt.alpha1e.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class PageInfo<T> extends BaseModel {

    public int current;
    public int pages;
    public int total;

    /**返回datas*/
    public  T records;

    public PageInfo thiz;

    @Override
    public PageInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, PageInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<PageInfo> getModelList(String json) {
        ArrayList<PageInfo> result = new ArrayList<PageInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new PageInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<PageInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(PageInfo info)
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
        return "PageInfo{" +
                "current=" + current +
                ", pages=" + pages +
                ", total=" + total +
                '}';
    }
}
