package com.ttm.tlrb.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gson 解析帮助类
 */
public class GsonUtil {
	public static final String TAG = GsonUtil.class.getSimpleName();

	public static <T> T fromJson(String json, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}


	public static Map<String,JsonElement> fromJson2MapJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json,new TypeToken<Map<String, JsonElement>>() {}.getType());
	}

	public static <T> ArrayList<T> fromJson2List(String json, Class<T> clazz) {
		if(!TextUtils.isEmpty(json)) {
			Gson gson = new Gson();
			try {
				ArrayList<JsonObject> list = gson.fromJson(json, new TypeToken<List<JsonObject>>() {}.getType());
				if (list != null) {
					ArrayList<T> resultList = new ArrayList<>();
					for (JsonObject t : list) {
						resultList.add(gson.fromJson(t, clazz));
					}
					return resultList;
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		return null;
	}

	public static Map<String,?> fromJson2Map(String json){
		Gson gson = new Gson();
		return gson.fromJson(json,new TypeToken<Map<String, ?>>() {}.getType());
	}

	public static String fromMap2Json(Map<?, ?> map){
		Gson gson = new Gson();
		return gson.toJson(map);
	}

	public static String fromObject2Json(Object o){
		return new Gson().toJson(o);
	}

	public static String fromList2Json(List<?> list){
		Gson gson = new Gson();
		return gson.toJson(list);
	}

	public static String getJsonByKey(String json, String key){
		if(!TextUtils.isEmpty(json)) {
			JsonParser jp = new JsonParser();
			JsonElement jsonElement = jp.parse(json);
			if (null != jsonElement && !jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
				JsonObject totalJson = jsonElement.getAsJsonObject();
				JsonElement jsonValue = totalJson.get(key);
				return jsonValue.toString();
			}
		}
		return "";
	}

}
