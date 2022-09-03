package kagg886.youmucloud;

import org.json.JSONObject;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        String az = null;
        try {
            az = request.getHeaders().get("json").get(0);
            new JSONObject(az);
        } catch (Exception e) {
            //since 20220901，需要Base64 decode
            az = new String(Base64.getDecoder().decode(az.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
        }
        if (az.equals("")) {
            az = "{}";
        }
        sec.getUserProperties().put("header",az);
    }
}