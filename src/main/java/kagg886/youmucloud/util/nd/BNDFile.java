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

        //TODO 定期更换url和邮箱
        Connection c = Jsoup.connect("https://pan.baidu.com/share/list?shareid=" + context.shareId + "&uk=" + context.uk + "&fid=" + fid + "&page=1&needsublist=1&sekey=PsdhZ3PjTXBtaIlks5KtzKhM8FtsujjcR8OjJPCTVuA%3D&sign=2bcfe8a9b530f87a1f2f2b1bff8d1bb503b5c95b&timestamp=1663073936&share_type=100&bdstoken=1e0ec297e4e579bd6941c4a368c23548&wp_retry_num=2&devuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&clienttype=1&channel=android_9_FLA-AL20_bd-netdisk_1026250y&first_setup_channel=android_9_FLA-AL20_bd-netdisk_1026250y&version=11.31.6&logid=MTY2MzA3MzkzODQ4MixmZTgwOjo3OGVmOjEwZmY6ZmU1NzphYTM0JWR1bW15MCw4MjM2MzI&vip=0&firstlaunchtime=1663072617&time=1663073939978&cuid=60AAECA7B7F4C1D30227B1B9C7B3A76F%7C0&network_type=wifi&c3_aid=A00-YOZY3LARJHAYN4BP5WO5N64SGWYLEDBQ-TBCZ3CHV&c3_oaid=A10-MMYWEMJZMFSDCLJRGI4TILJUMQYGELLBMQ3DALJWGMYWIOJRGJSTEZJVMQ-AUH53Z2Y&freeisp=0&queryfree=0&apn_id=1_0&rand=b8e2a231794f8a17b4b17d33bb5525327074131e");
        c.header("Cookie", "BDUSS=gtQ3ZIY09vN1lmbkxneFZzWXVYYmxCS01oam5NQn55bldhYVlFN1hOYmZCa2hqRVFBQUFBJCQAAAAAAAAAAAEAAADystJSv8mwrrm3ubc4ODYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN95IGPfeSBjVk; PANPSC=10899752288881055277%3ACU2JWesajwApk%2BS96Aa%2BBsWn1hbHg1WZgdoK0uBxE0FhFecqlWsdzi3l8iKdTseb6kor3whZlsFHItjtGGxI6chwL5ax2V3pcbomRJrCcQFxSUEYujkVxZc5%2FLVGfsQCvYDxSi%2F4AFtdTh1MmG8yQ7dYbXyOfq4XMa5pkKMC7NZipl3L3NnXecV35zM8E1i3No8m%2ByaHSIqMVj%2BP8sFRaA%3D%3D; STOKEN=4cc7167e2676f60b82a26f81e3c6124b014030326dd028e92dbfba77fceba64a");
        c.userAgent(context.ua);
        c.ignoreContentType(true);
        JSONObject s = new JSONObject(c.method(Connection.Method.POST).execute().body());
        List<BNDFile> file = new ArrayList<>();
        Utils.log("BNDFileList", s.toString());
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

        StringBuilder b = new StringBuilder();
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
