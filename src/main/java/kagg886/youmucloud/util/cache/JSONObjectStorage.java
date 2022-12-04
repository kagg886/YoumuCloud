package kagg886.youmucloud.util.cache;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class JSONObjectStorage extends JSONObject {

	private static ConcurrentHashMap<String,JSONObjectStorage> storagesCache = new ConcurrentHashMap<>(); //缓存池，减少从硬盘的读操作

	public static JSONObjectStorage obtain(String relativeDir) {
		if (storagesCache.containsKey(relativeDir)) {
			return storagesCache.get(relativeDir);
		}
		JSONObjectStorage s = null;
		try {
			s = new JSONObjectStorage(relativeDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		storagesCache.put(relativeDir, s);
		return s;
	}
	
	private String workdir;
	
	private JSONObjectStorage(String relativeDir) throws Exception {
		super(getJSON(relativeDir = Statics.data_dir + relativeDir));
		this.workdir = relativeDir;
	}


	public synchronized boolean save() {
		try {
			Utils.writeStringToFile(workdir, this.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	private static String getJSON(String relativeDir) throws IOException {
		if (relativeDir.equals("")) {
			return "{}";
		}
		String string = Utils.loadStringFromFile(relativeDir);
		if (string.equals("")) {
			return "{}";
		}
		return string;
	}

}
