package kagg886.youmucloud;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        String az = null;
        try {
            az = request.getHeaders().get("json").get(0);
        } catch (Exception ignored) {}
        if (az == null || az.equals("")) {
            az = "{}";
        }
        sec.getUserProperties().put("header",az);
    }
}