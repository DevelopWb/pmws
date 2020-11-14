package com.basenetlib.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:wang_sir
 * Time:2018/7/7 18:16
 * Description:This is mGsonManager
 */
public class GsonManager {
    private static Gson mGson = null;

    private GsonManager() {
        if (mGson == null) {
            mGson = new Gson();
        }
    }

    public static GsonManager getInstance() {
        return GsonManagerHolder.MGSON;
    }

    private static class GsonManagerHolder {
        private static GsonManager MGSON = new GsonManager();
    }

    /**
     * 把一个map变成json字符串
     *
     * @param map
     * @return
     */
    public String parseMapToJson(Map<?, ?> map) {
        try {
            return mGson.toJson(map);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 把一个json字符串变成对象
     *
     * @param json
     * @param cls
     * @return
     */
    public  <T> T parseJsonToBean(String json, Class<T> cls) {
        T t = null;
        try {
            t = mGson.fromJson(json, cls);
        } catch (Exception e) {

        }
        return t;
    }

    /**
     * 把json字符串变成map
     *
     * @param json
     * @return
     */
    public static HashMap<String, Object> parseJsonToMap(String json) {
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        HashMap<String, Object> map = null;
        try {
            map = mGson.fromJson(json, type);
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * 把json字符串变成集合
     * params: new TypeToken<List<yourbean>>(){}.getType(),
     *
     * @param json
     * @param type new TypeToken<List<yourbean>>(){}.getType()
     * @return
     */
    public static List<?> parseJsonToList(String json, Type type) {
        List<?> list = mGson.fromJson(json, type);
        return list;
    }

    /**
     * 获取json串中某个字段的值，注意，只能获取同一层级的value
     *
     * @param json
     * @param key
     * @return
     */
    public static String getFieldValue(String json, String key) {
        if (TextUtils.isEmpty(json))
            return null;
        if (!json.contains(key))
            return "";
        JSONObject jsonObject = null;
        String value = null;
        try {
            jsonObject = new JSONObject(json);
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

//    /**
//     * 格式化json
//     * @param uglyJSONString
//     * @return
//     */
//    public static String jsonFormatter(String uglyJSONString){
//        mGson mGson = new mGsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(uglyJSONString);
//        String prettyJsonString = mGson.toJson(je);
//        return prettyJsonString;
//    }
}
