package com.kagg886.youmucloud.util.storage;

import com.kagg886.youmucloud.util.IOUtil;
import org.json.JSONArray;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class JSONArrayStorage extends JSONArray {
	private String workdir;

	private static ConcurrentHashMap<String,JSONArrayStorage> storagesCache = new ConcurrentHashMap<>(); //缓存池，减少从硬盘的读操作

	public static JSONArrayStorage obtain(String relativeDir) throws Exception {
		if (storagesCache.containsKey(relativeDir)) {
			return storagesCache.get(relativeDir);
		}
		JSONArrayStorage s = new JSONArrayStorage(relativeDir);
		storagesCache.put(relativeDir,s);
		return s;
	}

	private JSONArrayStorage(String relativeDir) throws Exception {
		super(getJSON(relativeDir));
		this.workdir = relativeDir;
	}
	
	public boolean save() {
		try {
			IOUtil.writeStringToFile(workdir, this.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	private static String getJSON(String relativeDir) throws IOException {
		if (relativeDir.equals("")) {
			return "[]";
		}
		String string = IOUtil.loadStringFromFile(relativeDir);
		if (string.equals("")) {
			return "[]";
		}
		return string;
	}

}
