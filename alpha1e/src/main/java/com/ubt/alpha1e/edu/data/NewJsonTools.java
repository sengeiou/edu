package com.ubt.alpha1e.edu.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewJsonTools {

    public static boolean getJsonStatus(String json) {

        try {
            return getJsonStatus(new JSONObject(json));
        } catch (Exception e) {
            String info = e.getMessage();
            return false;
        }
    }

    public static boolean getJsonStatus(JSONObject json) {

        try {
            if (json.getString("success").equals("true")){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONObject getJsonData(String json) {
        try {
            return getJsonData(new JSONObject(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getJsonData(JSONObject json) {
        try {
            return json.getJSONArray("data").getJSONObject(0);
        } catch (JSONException e) {
            try {
                return json.getJSONObject("data");
            } catch (Exception e1) {
                return null;
            }

        }
    }

    public static JSONArray getJsonRecords(String json) {
        try {
            return getJsonRecords(new JSONObject(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getJsonRecords(JSONObject json) {
        try {
            return json.getJSONArray("records");
        } catch (JSONException e) {
            return null;
        }
    }

    public static int getErrorCode(String json){
        try {
            return new JSONObject(json).getInt("errCode");
        } catch (JSONException e) {
            return -2;
        }
    }
}
