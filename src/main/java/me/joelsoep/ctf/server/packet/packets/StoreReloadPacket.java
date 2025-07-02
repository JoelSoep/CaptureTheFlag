package me.joelsoep.ctf.server.packet.packets;

import me.joelsoep.ctf.server.packet.CTFPacket;
import me.joelsoep.ctf.server.packet.PacketConstructor;
import org.json.JSONObject;

public final class StoreReloadPacket extends CTFPacket {
    private static final String ACTION = "RELOAD_STORE";

    private StoreReloadPacket(String sender, String key, JSONObject json) {
        super(sender, key, ACTION, json);
    }

    public static class Builder extends CTFPacket.Builder<StoreReloadPacket.Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public StoreReloadPacket build() {
            JSONObject data = new JSONObject();
            data.put("sender", sender);
            data.put("key", key);
            data.put("action", ACTION);
            return new StoreReloadPacket(sender, key, data);
        }
    }

    @PacketConstructor(action = ACTION)
    public static StoreReloadPacket construct(JSONObject data) {
        return new StoreReloadPacket.Builder()
                .withSender(data.getString("sender"))
                .withKey(data.getString("key"))
                .build();
    }
}
