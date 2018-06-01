package com.xinyi.studyabroad.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/25.
 */

public class JsonUtils {


    public static List<Map<String, String>> ArrayToList(JSONArray array, String[] keys) throws JSONException {

        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Map<String, String> map = new HashMap<>();
            JSONObject obj = array.getJSONObject(i);
            for (int j = 0; j < keys.length; j++) {
                map.put(keys[j], obj.getString(keys[j]));
            }
            list.add(map);
        }
        return list;
    }

    public static String map2Json(List<Map<String, String>> data) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> map = data.get(i);
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                try {
                    obj.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                }
            }
            array.put(obj);
        }
        return array.toString();
    }
}
