package me.joelsoep.ctf.server.packet.packets;

import lombok.Getter;
import me.joelsoep.ctf.server.packet.CTFPacket;
import me.joelsoep.ctf.server.packet.PacketConstructor;
import org.json.JSONObject;

public final class AlertPacket extends CTFPacket {

    private final static String ACTION = "ALERT";

    @Getter private final String message;

    private AlertPacket(String sender, String key, String message, JSONObject json) {
        super(sender, key, ACTION, json);

        this.message = message;
    }

    public static class Builder extends CTFPacket.Builder<AlertPacket.Builder> {

        private String message;

        @Override
        protected Builder instance() {
            return this;
        }

        public AlertPacket.Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        @Override
        public AlertPacket build() {
            JSONObject data = new JSONObject();
            data.put("key", key);
            data.put("sender", sender);
            data.put("action", ACTION);
            data.put("message", message);
            return new AlertPacket(sender, key, message, data);
        }
    }

    @PacketConstructor(action = ACTION)
    public static AlertPacket construct(JSONObject data) {
        return new AlertPacket.Builder()
                .withKey(data.getString("key"))
                .withSender(data.getString("sender"))
                .withMessage(data.getString("message"))
                .build();
    }
}
