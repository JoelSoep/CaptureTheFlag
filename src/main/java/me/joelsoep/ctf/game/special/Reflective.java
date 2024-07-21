package me.joelsoep.ctf.game.special;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public interface Reflective<T extends Event> {

    /**
     * Use this method in order to reflect the current event occurring.
     * This method is not supposed to use the @EventHandler Annotation.
     * @param event The event to use to reflect.
     */

    void reflect(T event, @NotNull Player player, @NotNull Player reflector);
}
