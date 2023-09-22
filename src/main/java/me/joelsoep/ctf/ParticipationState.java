package me.joelsoep.ctf;

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
