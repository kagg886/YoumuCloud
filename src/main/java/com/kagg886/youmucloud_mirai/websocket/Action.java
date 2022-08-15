package com.kagg886.youmucloud_mirai.websocket;

import org.json.JSONException;
import org.json.JSONObject;

public class Action extends JSONObject {
   public static Action decodeAction(String json) {
	   try {
		   return new Action(json);
	   } catch (JSONException e) {}
	   return null;
   }
   
   public String getMsg() {
	   return super.optString("msg");
   }
   
	public void setMsg(String msg) {
	    try {
			super.put("msg", msg);
		} catch (JSONException e) {}
	}
   
   public String getAction() {
	   return super.optString("action");
	   }
   
	public static Action newAction(String type) {
		Action a = new Action();
		try {
			a.put("action",type);
		} catch (JSONException e) {}
		return a;
	}
   
   public Action(String json) throws JSONException {
	   super(json);
   }
   
	public Action(){
		super();
	}
}
