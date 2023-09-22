package me.joelsoep.ctf.game.team;

import lombok.Getter;

@Getter
public enum TeamColor {
    BLUE("<#084acf>ʙʟᴜᴇ<reset>", "<#084acf>"),
    RED("<#cf0826>ʀᴇᴅ<reset>", "<#cf0826>");

    final String prefix;
    final String color;

    TeamColor(String prefix, String color) {
        this.prefix = prefix;
        this.color = color;
    }
}