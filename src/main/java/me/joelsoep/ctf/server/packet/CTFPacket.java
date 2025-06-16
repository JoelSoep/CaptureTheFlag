package me.joelsoep.ctf.server.packet;

import lombok.Getter;
import me.joelsoep.ctf.Main;
import org.json.JSONObject;

public abstract class CTFPacket {

    @Getter protected final String sender;
    @Getter protected final String key;
    @Getter protected final String action;
    private final JSONObject json;

    protected CTFPacket(String sender, String key, String action, JSONObject json) {
        this.sender = sender;
        this.key = key;
        this.action = action;
        this.json = json;
    }

    public JSONObject toJSON() {
        return json;
    }

    public abstract static class Builder<T extends Builder<T>> {
        protected String sender;
        protected String key;
        protected String action;

        public T withSender(String sender) {
            this.sender = sender;
            return instance();
        }

        public T withKey(String key) {
            this.key = key;
            return instance();
        }

        protected T withAction(String action) {
            this.action = action;
            return instance();
        }

        protected abstract T instance();
        public abstract CTFPacket build();

        public T withDefaultValues() {
            withSender(Main.getGameServer().getId());
            withKey(Main.getGameServer().getApiConfig().key());
            withAction(action);
            return instance();
        }
    }

}
