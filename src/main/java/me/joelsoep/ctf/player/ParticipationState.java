package me.joelsoep.ctf.player;

import lombok.Getter;

@Getter
public enum ParticipationState {
    AVAILABLE("<green>AVAILABLE"),
    SPECTATING("<gold>SPECTATING"),
    UNAVAILABLE("<red>UNAVAILABLE");

    final String color;

    ParticipationState(String color) {
        this.color = color;
    }
}
