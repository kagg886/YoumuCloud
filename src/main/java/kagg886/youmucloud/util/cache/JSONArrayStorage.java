package kagg886.youmucloud.util.cache;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
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
		super(getJSON(relativeDir = Statics.data_dir + relativeDir));
		this.workdir = relativeDir;
	}
	
	public boolean save() {
		try {
			Utils.writeStringToFile(workdir, this.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	private static String getJSON(String relativeDir) throws IOException {
		if (relativeDir.equals("")) {
			return "[]";
		}
		String string = Utils.loadStringFromFile(relativeDir);
		if (string.equals("")) {
			return "[]";
		}
		return string;
	}

}
