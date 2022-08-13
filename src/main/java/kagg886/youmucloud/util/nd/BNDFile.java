package kagg886.youmucloud.util.nd;

import kagg886.youmucloud.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class BNDFile {

    private boolean isDirectory;
    private String downloadLink = null;
    private String path;
    private String fid;
    private BNDShare context;
    private BNDFile parent;
    private long size;

    //是否为文件夹
    public boolean isDirectory() {
        return isDirectory;
    }

    public long getSize() {
        return size;
    }

    //是否为根目录
    public boolean isRoot() {
        return parent == null;
    }

    //获取文件夹内的子文件(夹)
    public List<BNDFile> listFiles() throws Exception {
        if (!isDirectory) {
            throw new Exception("这不是文件夹!");
        }
        Connection c = Jsoup.connect("https://pan.baidu.com/share/list?shareid=" + context.shareId + "&uk=" + context.uk + "&fid=" + fid + "&page=1&needsublist=1&sekey=81PaLgf4qDkwVCuLGMs5cAEsAXlQp6B1yajjkvxLQ8k%3D&sign=af19d61c31e626ee37907e40bcfc532ac88a6348&timestamp=1660355312&share_type=100&bdstoken=9b40b91b4e3b0a0b3955696898cb5be5&devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.30.2&logid=MTY2MDM1NTMxMjczMSxmZTgwOjoxODkwOjdmZjpmZTUwOjFhMjglZHVtbXkwLDYyNjQ5NA&vip=0&firstlaunchtime=1660355162&time=1660355313824&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-YOZY3LARJHAYN4BP5WO5N64SGWYLEDBQ-TBCZ3CHV&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=e215c71239d1fb4d0f83bc9243285f66d3f5351e");
        c.header("Cookie","BDUSS=WJTQm9OQjRTakozNzRBS0w2Wmw2Z2ZJWERhaUtheFZYQUtibVZzdlVRVUlYUlJqRVFBQUFBJCQAAAAAAAAAAAEAAADystJSv8mwrrm3ubc4ODYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAjQ7GII0Oxic; PANPSC=14724249197678910851%3ACU2JWesajwApk%2BS96Aa%2BBsWn1hbHg1WZgdoK0uBxE0FhFecqlWsdzi3l8iKdTseb6kor3whZlsFHItjtGGxI6dQ5W%2B09U3GuwPDXbA0wt1JxSUEYujkVxZc5%2FLVGfsQCvYDxSi%2F4AFtdTh1MmG8yQ7dYbXyOfq4XMa5pkKMC7NZipl3L3NnXecV35zM8E1i3No8m%2ByaHSIqMVj%2BP8sFRaA%3D%3D; STOKEN=f60e0d3af345abc69e9d5c5cd972b89e10b4137338c39b67fdf9bceebf1b860b");
        c.userAgent(context.ua);
        c.ignoreContentType(true);
        JSONObject s = new JSONObject(c.method(Connection.Method.POST).execute().body());
        List<BNDFile> file = new ArrayList<>();
        Utils.log("BNDFileList",s.toString());
        JSONArray source = s.optJSONArray("list");
        for (int i = 0; i < source.length(); i++) {
            file.add(new BNDFile(source.optJSONObject(i), context, this));
        }
        return file;
    }

    //获取下载链接
    public String getDownloadLink() throws Exception {
        if (isDirectory()) {
            throw new Exception("这不是文件!");
        }
        return downloadLink;
    }

    //获取文件的路径
    public String getPath() {
        if (isRoot()) {
            return path;
        }

        List<String> layer = new ArrayList<>();
        BNDFile lays = this;
        layer.add(lays.getFileName());
        do {
            lays = lays.getParent();
            layer.add(lays.getFileName());
        } while (!lays.isRoot());

        StringBuffer b = new StringBuffer();
        for (int i = layer.size() - 1; i >= 0 ; i--) {
            b.append("/");
            b.append(layer.get(i));
        }

        //手动释放内存
        layer.clear();
        layer = null;

        return b.toString();
    }

    //获取父文件夹，当父文件夹为根目录时为null
    public BNDFile getParent() {
        return parent;
    }

    //获取文件名
    public String getFileName() {
        return path.replace("/", "");
    }

    public BNDFile(JSONObject fileSource, BNDShare context) {
        getBNDFile(fileSource, context, null);
    }

    public BNDFile(JSONObject fileSource, BNDShare context, BNDFile parent) {
        getBNDFile(fileSource, context, parent);
    }

    private void getBNDFile(JSONObject fileSource, BNDShare context, BNDFile parent) {
        this.context = context;
        this.parent = parent;
        this.fid = fileSource.optString("fs_id");
        this.path = fileSource.optString("path");
        this.size = fileSource.optLong("size");
        isDirectory = fileSource.optString("isdir").equals("1");
        if (!isDirectory) {
            downloadLink = fileSource.optString("dlink");
        }
    }
}
