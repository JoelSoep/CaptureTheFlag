package me.joelsoep.ctf.server.packet.packets;

import me.joelsoep.ctf.server.packet.CTFPacket;
import me.joelsoep.ctf.server.packet.PacketConstructor;
import org.json.JSONObject;

public final class ScheduleRestartPacket extends CTFPacket {
    private static final String ACTION = "SCHEDULE_RESTART";

    private ScheduleRestartPacket(String sender, String key, JSONObject json) {
        super(sender, key, ACTION, json);
    }

    public static class Builder extends CTFPacket.Builder<ScheduleRestartPacket.Builder> {

        @Override
        protected ScheduleRestartPacket.Builder instance() {
            return this;
        }

        @Override
        public ScheduleRestartPacket build() {
            JSONObject data = new JSONObject();
            data.put("sender", sender);
            data.put("key", key);
            data.put("action", ACTION);
            return new ScheduleRestartPacket(sender, key, data);
        }
    }

    @PacketConstructor(action = ACTION)
    public static ScheduleRestartPacket construct(JSONObject data) {
        return new ScheduleRestartPacket.Builder()
                .withSender(data.getString("sender"))
                .withKey(data.getString("key"))
                .build();
    }
}
