package kagg886.youmucloud.util.nd;

import kagg886.youmucloud.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BNDShare {

    protected String ua = "netdisk;11.29.4;FLA-AL20;android-android;9;JSbridge4.4.0;jointBridge;1.1.0;";
    protected String cookie = "BDUSS=VrTU1qNUlXSnR-YkJ4eW1LS0ZRSmdtSlZGRGZhZVZHWGpFdkhVWjZZQU9VaEJqRVFBQUFBJCQAAAAAAAAAAAEAAADystJSv8mwrrm3ubc4ODYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA7F6GIOxehiQz; PANPSC=14775281574092891722%3AWhZG6HNMmFocit4qa4UEZlcS2d9ns3O5iwCaSaf6VIpv4yEgYz9ioypPymoB8yIZOD8tuTtuXwKzhSX7ZkpCoCZXS6WqLhJBQjQ1mFHTd8V1tt%2Bk9Cane%2BomA%2FKeZTApdS8c7ndIzLFtK1%2FOGTUYuOxKh8a1iLXFo9PMYQu%2F7V2B2grS4HETQWEV5yqVax3OLeXyIp1Ox5tyjOsSrdWYM%2ByClTUgRF8K16qtleHWHw0kV5J3kPr%2BsPxgLAmH8zgE625AVJzIhX%2BSsVZaqyA8TA%3D%3D; STOKEN=5c39f85dbf94af0f8eedbd1d9c53166fa0a1f6a0d72954df0d3a1aaf46b27765";
    protected String bdstoken = "dd1badb8f59545877f0f16bc88a3e5f8";
    protected long shareId;
    protected long uk;

    protected String ranDsk = null;

    protected onVerifyCodeListener listener;

    //获取根文件夹
    public List<BNDFile> getShareInfo() throws Exception {
        if (ranDsk == null) {
            throw new Exception("未验证!");
        }
        Connection c = Jsoup.connect("http://pan.baidu.com/share/list?shareid=" + this.shareId + "&uk=" + this.uk + "&root=1&sekey=" + this.ranDsk + "&sign=4bcb1e00d4c35b8d49d7a19f56873e67432d4601&timestamp=1659424614&share_type=100&bdstoken=dd1badb8f59545877f0f16bc88a3e5f8&devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.29.4&logid=MTY1OTQyNDYxNDg0OSxmZTgwOjpiNDljOmNiZmY6ZmViZDpkNWY4JWR1bW15MCw1MTgwODc&vip=0&firstlaunchtime=1659421930&time=1659424615497&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-FX47CX33IHJE3SO3D4D2SJZHJC5VSHM2-HVNIUZWA&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=20fff074b725d6f4e53da74e1858c567e61abdc8");
        c.ignoreContentType(true);
        c.header("Cookie",cookie);
        c.userAgent(ua);
        List<BNDFile> file = new ArrayList<>();
        JSONArray source = new JSONObject(c.execute().body()).optJSONArray("list");
        Utils.log("getShareInfo回调:",source.toString());
        for (int i = 0; i < source.length(); i++) {
            file.add(new BNDFile(source.optJSONObject(i),this));
        }
        return file;
    }

    //获取文件分享者
    public BNDPerson getSharePerson() throws Exception {
        Connection c = Jsoup.connect("http://pan.baidu.com/api/user/getinfo?devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.29.4&logid=MTY1OTQ5MTY5ODcxMixmZTgwOjpiNDljOmNiZmY6ZmViZDpkNWY4JWR1bW15MCw3NjQyNDI&vip=0&firstlaunchtime=1659491466&time=1659491699100&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-FX47CX33IHJE3SO3D4D2SJZHJC5VSHM2-HVNIUZWA&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=29276b5c4e158c4c66c3e769e13fe96ca1a17386");
        c.ignoreContentType(true);
        c.userAgent(ua);
        c.header("Cookie",cookie);
        c.data("appid","250528");
        c.data("user_list","[" + uk + "]");
        c.data("need_ralation","1");
        c.data("bdstoken",bdstoken);
        c.data("wp_retry_num","2");
        JSONObject object = new JSONObject(c.method(Connection.Method.POST).execute().body());
        Utils.log("getSharePerson回调:",object.toString());
        return new BNDPerson(object);
    }


    //填入提取码
    private void verifyPwd(String pwd,String vcode,String vcode_str) throws Exception {
        Connection c = Jsoup.connect("http://pan.baidu.com/share/verify?shareid=" + this.shareId + "&uk=" + this.uk + "&devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.29.4&logid=MTY1OTQyNDYxMzYyNyxmZTgwOjpiNDljOmNiZmY6ZmViZDpkNWY4JWR1bW15MCwxMDQwNg&vip=0&firstlaunchtime=1659421930&time=1659424614288&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-FX47CX33IHJE3SO3D4D2SJZHJC5VSHM2-HVNIUZWA&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=682e3e06106218f924909d021a89990dadaf28f0");
        c.ignoreContentType(true);
        c.userAgent(ua);
        c.header("Cookie",cookie);
        c.data("pwd",pwd);
        if (vcode != null) {
            c.data("vcode",vcode);
        } else {
            c.data("vcode","");
        }

        if (vcode_str != null) {
            c.data("vcode_str",vcode_str);
        } else {
            c.data("vcode_str","null");
        }
        c.data("bdstoken",bdstoken);
        JSONObject data = new JSONObject(c.method(Connection.Method.POST).execute().body());

        Utils.log("VerifyPWD回调:",data.toString());

        if (data.optInt("errno") == -9) {
            throw new Exception("提取码输入错误");
        }

        if (data.optInt("errno") == -62) {
            throw new Exception("需要验证码");
        }
        ranDsk = data.optString("randsk");
    }

    //填入提取码，但是不需要验证
    private void verifyPwd(String pwd) throws Exception {
        verifyPwd(pwd,null,null);
    }


    //公开的提交提取码接口
    public void verify(String pwd) throws Exception {
        try {
            verifyPwd(pwd);
        } catch (Exception e) {
            if (e.getMessage().equals("需要验证码")) {
                while (ranDsk == null) {
                    String[] verify = getVerifyCode();
                    try {
                        verifyPwd(pwd, verify[0], verify[1]);
                        break;
                    } catch (Exception e1) {
                        if (!e1.getMessage().equals("需要验证码")) {
                            throw e1;
                        }
                    }
                }
            } else {
                throw e;
            }
        }
    }

    //获取验证码
    protected String[] getVerifyCode() throws Exception {
        Connection c = Jsoup.connect("http://pan.baidu.com/api/getcaptcha?devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.29.4&logid=MTY1OTQyODM1NjY0NyxmZTgwOjpiNDljOmNiZmY6ZmViZDpkNWY4JWR1bW15MCwxMTc3NQ&vip=0&firstlaunchtime=1659421930&time=1659428357011&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-FX47CX33IHJE3SO3D4D2SJZHJC5VSHM2-HVNIUZWA&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=980a41053aa454c47f402cadeaae50b33a769548");
        c.ignoreContentType(true);
        c.userAgent(ua);
        c.header("Cookie",cookie);
        c.data("appid","250528");
        c.data("prod","shareverify");
        c.data("bdstoken",bdstoken);
        JSONObject data = new JSONObject(c.method(Connection.Method.POST).execute().body());
        System.out.println("getVerifyCode:" + data);
        String vcode_str = data.optString("vcode_str");
        String img_link = data.optString("vcode_img");
        String vcode = listener.getVCode(img_link);
        if (vcode == null) {
            throw new Exception("验证取消");
        }
        return new String[] {vcode,vcode_str};
    }


    //验证监听器传入验证码图片链接，返回验证码
    public interface onVerifyCodeListener {
        String getVCode(String vCode_link) throws IOException;
    }


    //s后面的字符，和验证监听器
    public BNDShare(String shareCode,onVerifyCodeListener listener) throws Exception {
        this.listener = listener;
        Connection c = Jsoup.connect("http://pan.baidu.com/api/shorturlinfo?type=0&root=1&shorturl=" + shareCode + "&bdstoken=dd1badb8f59545877f0f16bc88a3e5f8&devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.29.4&logid=MTY1OTQyNDYwMzAwNixmZTgwOjpiNDljOmNiZmY6ZmViZDpkNWY4JWR1bW15MCwyNDY1Nzg&vip=0&firstlaunchtime=1659421930&time=1659424603657&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-FX47CX33IHJE3SO3D4D2SJZHJC5VSHM2-HVNIUZWA&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=6cf8054cb35acef65a8e6eccba76a2de1291558e").ignoreContentType(true);
        JSONObject data;
        data = new JSONObject(c.execute().body());

        Utils.log("度盘解析回调:BNDShare",data.toString());

        if (data.optInt("errno") != -9) {
            throw new Exception(data.optString("show_msg"));
        }
        //System.out.println("decodeFromShareId:" + data);
        this.shareId = data.optLong("shareid");
        this.uk = data.optLong("uk");
    }
}
