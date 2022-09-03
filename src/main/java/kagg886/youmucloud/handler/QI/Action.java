package kagg886.youmucloud.handler.QI;

import org.json.JSONException;
import org.json.JSONObject;

public class Action extends JSONObject{
	
	public Action(String actionName){
		super();
		this.put("action", actionName);
	}
	
	public Action(String string,boolean f) throws JSONException {
		super(string);
		if (this.isNull("action")) {
			throw new JSONException("This is NOT a action!");
		}
	}
	
	public String getAction() {
		return this.optString("action");
	}
	
	public JSONObject put(String key, String value) {
		try {
			return super.put(key, value);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject put(String key, long value) {
		try {
			return super.put(key, value);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject put(String key, Object value) {
		try {
			return super.put(key, value);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject put(String key, boolean value) {
		try {
			return super.put(key, value);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public JSONObject put(String key,int value) {
		try {
			return super.put(key, value);
		} catch (JSONException e) {
			return null;
		}
	}
}
