package com.kagg886.youmucloud.util.storage;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

public class ReflectUtil {

    public static  <T> T  JSONToObject(String json, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();

            JSONObject o = new JSONObject(json);

            Iterator<String> it = o.keys();
            for (String key = it.next(); it.hasNext(); key = it.next()) {
                clazz.getField(key).set(obj,o.opt(key));
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String ObjectToJSON(Object obj) {
        JSONObject object = new JSONObject();
        for (Field field : obj.getClass().getFields()) {
            try {
                object.put(field.getName(), field.get(obj));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return object.toString();
    }
}
