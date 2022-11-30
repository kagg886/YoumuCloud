//
// Decompiled by Jadx - 989ms
//
package com.kagg886.youmucloud.bot;

import org.json.JSONException;
import org.json.JSONObject;

public class Action extends JSONObject {
    public static Action decodeAction(String str) {
        try {
            return new Action(str);
        } catch (JSONException e) {
            return null;
        }
    }

    public String getMsg() {
        return super.optString("msg");
    }

    public void setMsg(String str) {
        try {
            super.put("msg", str);
        } catch (JSONException e) {
        }
    }

    public String getAction() {
        return super.optString("action");
    }

    public static Action newAction(String str) {
        Action action = new Action();
        try {
            action.put("action", str);
        } catch (JSONException e) {
        }
        return action;
    }

    public Action(String str) throws JSONException {
        super(str);
    }

    public Action() {
    }
}
