package me.joelsoep.ctf.server.packet.packets;

import lombok.Getter;
import me.joelsoep.ctf.server.ServerMode;
import me.joelsoep.ctf.server.packet.CTFPacket;
import me.joelsoep.ctf.server.packet.PacketConstructor;
import org.json.JSONObject;

public final class HelloPacket extends CTFPacket {
    private static final String ACTION = "HELLO";

    @Getter private final String host;
    @Getter private final int port;
    @Getter private final ServerMode mode;

    private HelloPacket(String sender, String key, String host, int port, ServerMode mode, JSONObject json) {
        super(sender, key, ACTION, json);
        this.host = host;
        this.port = port;
        this.mode = mode;
    }

    public static class Builder extends CTFPacket.Builder<HelloPacket.Builder> {
        private String host;
        private int port;
        private ServerMode mode;

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withMode(ServerMode mode) {
            this.mode = mode;
            return this;
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public HelloPacket build() {
            JSONObject data = new JSONObject();
            data.put("key", key);
            data.put("sender", sender);
            data.put("action", ACTION);
            data.put("host", host);
            data.put("port", port);
            data.put("mode", mode.toString());
            return new HelloPacket(sender, key, host, port, mode, data);
        }
    }

    @PacketConstructor(action = ACTION)
    public static HelloPacket construct(JSONObject data) {
        String mode = data.getString("mode");
        ServerMode serverMode = ServerMode.valueOf(mode);
        return new HelloPacket.Builder()
                .withKey(data.getString("key"))
                .withSender(data.getString("sender"))
                .withHost(data.getString("host"))
                .withPort(data.getInt("port"))
                .withMode(serverMode)
                .build();
    }

}
