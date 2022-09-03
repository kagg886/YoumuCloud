package kagg886.youmucloud.util;

import java.util.HashMap;
import java.util.Map;

public class WaitService {
	public static final HashMap<String, CallBack> queues = new HashMap<>();


	public static boolean addCall(String string,String source) {
		for (Map.Entry<String, CallBack> element : queues.entrySet()) {
			if (element.getKey().equals(string)) {
				element.getValue().setSource(source);
				return true;
			}
		}
		
		return false;
	}

	public static boolean hasKey(String string) {
		for (Map.Entry<String, CallBack> element : queues.entrySet()) {
			if (element.getKey().equals(string)) {
				return true;
			}
		}
		return false;
	}

	public static String wait(String string) {
		return wait(string,10);
	}

	public static String wait(String string,int timeout) {
		if (timeout >= 60) {
			timeout = 60;
		}
		CallBack callBack = new CallBack();
		queues.put(string, callBack);
		for (int i = 0; i < timeout; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (callBack.getSource() != null) {
				break;
			}
		}
		queues.remove(string);
		return callBack.getSource();
	}



	public static class CallBack {
		private String source = null;

		public void setSource(String source) {
			this.source = source;
		}

		public String getSource() {
			return source;
		}


		@Override
		public String toString() {
			if (source == null) {
				return String.format("source:null");
			}
			return String.format("source:%s",source);
		}
	}

}
