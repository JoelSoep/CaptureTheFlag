package me.joelsoep.ctf.game.setting;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.MapManager;

public enum GameSetting {
    MAX_HEALTH {
        public Object getDefault() {
            return 20;
        }
    },

    MAP {
        public Object getDefault() {
            return Main.getGame().getMap();
        }
    },

    CAPTURE_AMOUNT {
        public Object getDefault() {
            return 3;
        }
    };

    // GAME_TIME

    public abstract Object getDefault();
}
