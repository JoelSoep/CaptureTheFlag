package me.joelsoep.ctf.game.board;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Objects;

public class ActionbarService {

    private final CTFPlayer player;
    private final EnumMap<ActionbarPriority, String> messages;
    private int id;

    public ActionbarService(CTFPlayer player) {
        this.player = player;
        this.messages = new EnumMap<>(ActionbarPriority.class);
        this.id = 0;

        messages.put(ActionbarPriority.LOW, "");
        messages.put(ActionbarPriority.NORMAL, "");
        messages.put(ActionbarPriority.HIGH, "");
    }

    public boolean hasActiveActionbar() {
        return this.id != 0;
    }

    public void setMessage(ActionbarPriority priority, String message) {
        messages.put(priority, message);
        updateActionbar();
        if (!hasActiveActionbar()) {
            scheduleActionbar();
        }
    }

    public void setMessage(ActionbarPriority priority, String message, Long ticks) {
        messages.put(priority, message);
        updateActionbar();
        if (!hasActiveActionbar()) {
            scheduleActionbar();
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!player.getPlayer().isOnline()) {
                    return;
                }
                removeMessage(priority);
            }
        }, ticks);
    }

    public String getMessage(ActionbarPriority priority) {
        return messages.get(priority);
    }
    public void removeMessage(ActionbarPriority priority) {
        messages.put(priority, "");
        updateActionbar();
    }

    public boolean hasMessage(ActionbarPriority priority) {
        return !Objects.equals(messages.get(priority), "");
    }

    public void cancelActionbar() {
        if (this.id == 0) {
            LogModule.CORE.log("Tried to cancel actionbar while no actionbar was active.");
            return;
        }
        player.sendActionBar(MM.deserialize(""));
        Bukkit.getScheduler().cancelTask(id);
        this.id = 0;
    }

    private void updateActionbar() {
        String message = "";
        if (hasMessage(ActionbarPriority.LOW)) {
            message = getMessage(ActionbarPriority.LOW);
        }

        if (hasMessage(ActionbarPriority.NORMAL)) {
            message = getMessage(ActionbarPriority.NORMAL);
        }

        if (hasMessage(ActionbarPriority.HIGH)) {
            message = getMessage(ActionbarPriority.HIGH);
        }

        if (Objects.equals(message, "")) {
            cancelActionbar();
            return;
        }
        player.sendActionBar(MM.deserialize(message));
    }

    public void scheduleActionbar() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!player.getPlayer().isOnline()) {
                    cancelActionbar();
                    return;
                }
                updateActionbar();
            }
        }, 0L, 40L);
    }

    public enum ActionbarPriority {
        LOW,
        NORMAL,
        HIGH
    }
}
