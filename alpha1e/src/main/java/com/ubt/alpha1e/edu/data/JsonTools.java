package com.ubt.alpha1e.edu.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonTools {
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

            if (json.getString("status").equals("true"))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static JSONObject getJsonModel(String json) {
        try {
            return getJsonModel(new JSONObject(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getJsonModel(JSONObject json) {
        try {
            return json.getJSONArray("models").getJSONObject(0);
        } catch (JSONException e) {
            try {
                return json.getJSONObject("models");
            } catch (Exception e1) {
                return null;
            }

        }
    }

    public static JSONArray getJsonModels(String json) {
        try {
            return getJsonModels(new JSONObject(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getJsonModels(JSONObject json) {
        try {
            return json.getJSONArray("models");
        } catch (JSONException e) {
            return null;
        }
    }

    public static Integer getJsonInfo(String json) {
        try {
            return getJsonInfo(new JSONObject(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public static Integer getJsonInfo(JSONObject json) {
        try {
            return json.getInt("info");
        } catch (JSONException e) {
            return null;
        }
    }
}
