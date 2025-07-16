package me.joelsoep.ctf.game.setting;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.MapManager;

public enum GameSetting {
    MAX_HEALTH {
        public Object getDefault() {
            return 10;
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
    },

    TIME {
      public Object getDefault() {
          return 10L;
      }
    };

    public abstract Object getDefault();
}
