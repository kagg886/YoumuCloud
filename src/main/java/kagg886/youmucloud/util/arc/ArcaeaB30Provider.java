package kagg886.youmucloud.util.arc;

import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.brotli.dec.BrotliInputStream;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @projectName: test
 * @package: kagg886.youmucloud.util.arc
 * @className: Main
 * @author: kagg886
 * @description: Arcaea B30查分器，API源自
 * @date: 2022/12/23 16:50
 * @version: 1.0
 */
public class ArcaeaB30Provider extends WebSocketClient {

    private String id;

    private JSONObject songTitle, songImg;

    private JSONObject userInfo;

    private ArrayList<Score> scores;

    private Listener listener;


    public ArcaeaB30Provider(String id, Listener listener) {
        super(URI.create("wss://arc.estertion.win:616/"));
        this.id = id;
        scores = new ArrayList<>();
        this.listener = listener;
        this.songImg = JSONObjectStorage.obtain("res/arc/arcImage.json");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            BrotliInputStream stream = new BrotliInputStream(new ByteArrayInputStream(bytes.array()));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int c;
            while ((c = stream.read()) != -1) {
                outputStream.write(c);
            }
            stream.close();
            outputStream.close();

            String s = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            this.onMessage(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.onMessage(bytes);
    }

    @Override
    public boolean connectBlocking() throws InterruptedException {
//        new Thread(() -> {
//            try {
//                BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\iveou\\Desktop\\t.txt"));
//                String s;
//                while ((s = reader.readLine()) != null) {
//                    onMessage(s);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//        return true;
        try {
            boolean a = super.connectBlocking();
            super.send(id);
            return a;
        } catch (Exception e) {
            listener.onFailed(e);
            return false;
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onMessage(String s) {
        if (s.equals("bye")) {
            if (scores.size() > 1) {
                scores.sort(Comparator.comparing(Score::getRating).reversed());
            }
            listener.onSuccess(scores, userInfo);
            return;
        }
        if (s.equals("queued") || s.equals("queried")) {
            return;
        }

        JSONObject cmd;
        try {
            cmd = new JSONObject(s);
        } catch (Exception ignored) {
            listener.onFailed(new Exception(s));
            return;
        }
        switch (cmd.optString("cmd")) {
            case "songtitle":
                songTitle = cmd.optJSONObject("data");
                break;
            case "userinfo":
                userInfo = cmd.optJSONObject("data");
                break;
            case "scores":
                scores.add(new Score(cmd.optJSONArray("data").optJSONObject(0)));
                break;

        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {
        listener.onFailed(e);
    }

    public interface Listener {
        void onSuccess(ArrayList<Score> scores, JSONObject userInfo);

        void onFailed(Exception e);
    }

    public class Score {
        public int pPure, pure, far, lost;
        public int score;
        public long time;
        public double rating;

        public int diff;

        public String id, title, imgUrl;

        public double constant;

        public Score(JSONObject object) {
            this.constant = object.optDouble("constant");
            this.pPure = object.optInt("shiny_perfect_count");
            this.pure = object.optInt("perfect_count");
            this.far = object.optInt("near_count");
            this.lost = object.optInt("miss_count");
            this.score = object.optInt("score");
            this.time = object.optLong("time_played");
            this.rating = object.optDouble("rating");
            this.diff = object.optInt("difficulty");
            this.id = object.optString("song_id");
            this.title = songTitle.optJSONObject(id).optString("en");
            JSONObject imgUnit = songImg.optJSONObject(id);
            for (Iterator<String> p = imgUnit.keys(); p.hasNext(); ) {
                String a = p.next();
                if (a.contains(String.valueOf(diff))) {
                    this.imgUrl = imgUnit.optString(a);
                    return;
                }
            }
            this.imgUrl = imgUnit.optString(imgUnit.names().optString(0));
        }

        double getRating() {
            return rating;
        }
    }
}
