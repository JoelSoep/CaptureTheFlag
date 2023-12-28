package me.joelsoep.ctf.game;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public class GameQueue {
    private Queue<CTFPlayer> queue;
    private Audience audience;
    private Game game;

    public GameQueue() {
        this.game = Main.game;
        this.queue = new LinkedList<>();
    }

    public void addPlayer(CTFPlayer player) {
        getQueue().add(player);
        player.sendMessage(
                MM.deserialize(
                        "<gray>You've been added to the queue. There are <yellow>%s <gray>players in front of you."
                                .formatted(getQueue().size() - 1)
                )
        );
        this.audience = Audience.audience(queue);
    }

    public void addPlayer(CTFPlayer player, boolean notify) {
        getQueue().add(player);
        if (notify) {
            player.sendMessage(
                    MM.deserialize(
                            "<gray>You've been added to the queue. There are <yellow>%s <gray>players in front of you."
                                    .formatted(getQueue().size() - 1)
                    )
            );
        }
        this.audience = Audience.audience(queue);
    }

    public void removePlayer(CTFPlayer player) {
        getQueue().remove(player);
        player.sendMessage(MM.deserialize("<gray>You left the queue."));
    }

    public @Nullable CTFPlayer peek() { return getQueue().peek(); }

    public @Nullable CTFPlayer getNext() {
        return getQueue().poll();
    }

    public Queue<CTFPlayer> getQueue() {
        return this.queue;
    }

    public @Nullable Audience getAudience() {
        return audience;
    }

    public @Nullable Integer getPosition(CTFPlayer player) {
        int position = 1;
        for (CTFPlayer iteration : this.getQueue()) {
            if (iteration.getPlayer().getUniqueId().equals(player.getPlayer().getUniqueId())) {
                return position;
            } else {
                position++;
            }
        }
        return null;
    }

}
