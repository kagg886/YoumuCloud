package kagg886.youmucloud.util.nd;

import org.json.JSONObject;

public class BNDPerson {
    private String nick;
    private String avatar;

    protected BNDPerson(JSONObject obj) {
        obj = obj.optJSONArray("records").optJSONObject(0);
        nick = obj.optString("uname");
        avatar = obj.optString("avatar_url");
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNick() {
        return nick;
    }
}
