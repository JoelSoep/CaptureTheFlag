package me.joelsoep.ctf.server.packet.packets;

import lombok.Getter;
import me.joelsoep.ctf.server.packet.CTFPacket;
import me.joelsoep.ctf.server.packet.PacketConstructor;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public final class RetrievePlayerPacket extends CTFPacket {
    private final static String ACTION = "RETRIEVE_PLAYER";

    @Getter private final String uuid;
    @Getter private final boolean asPlayer;
    @Getter private final @Nullable String team;

    private RetrievePlayerPacket(String sender, String key, String uuid, boolean asPlayer, @Nullable String team, JSONObject json) {
        super(sender, key, ACTION, json);
        this.uuid = uuid;
        this.asPlayer = asPlayer;
        this.team = team;
    }

    public static class Builder extends CTFPacket.Builder<RetrievePlayerPacket.Builder> {
        private String uuid;
        private boolean asPlayer;
        private String team;

        public Builder withUUID(String uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        protected Builder instance() {
            return this;
        }

        public Builder asPlayer(boolean asPlayer) {
            this.asPlayer = asPlayer;
            return this;
        }

        public Builder withTeam(String team) {
            this.team = team;
            return this;
        }

        @Override
        public RetrievePlayerPacket build() {
            JSONObject data = new JSONObject();
            data.put("sender", sender);
            data.put("key", key);
            data.put("action", ACTION);
            data.put("uuid", uuid);
            data.put("asPlayer", String.valueOf(asPlayer));
            if (team != null) {
                data.put("team", team);
            }

            if (asPlayer && team == null) {
                throw new RuntimeException("packet cannot contain player without team.");
            }

            return new RetrievePlayerPacket(sender, key, uuid, asPlayer, team, data);
        }
    }

    @PacketConstructor(action = ACTION)
    public static RetrievePlayerPacket construct(JSONObject data) {
        return new RetrievePlayerPacket.Builder()
                .withUUID(data.getString("uuid"))
                .withTeam(data.has("team") ? data.getString("team") : null)
                .asPlayer(Boolean.parseBoolean(data.getString("asPlayer")))
                .withKey(data.getString("key"))
                .withSender(data.getString("sender"))
                .build();
    }
}
