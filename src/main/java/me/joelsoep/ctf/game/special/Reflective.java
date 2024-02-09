package me.joelsoep.ctf.game.special;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface Reflective<T extends Event> {

    /**
     * Use this method in order to reflect the current event occurring.
     * This method is not supposed to use the @EventHandler Annotation.
     * @param event The event to use to reflect.
     */

    void reflect(T event, Player player, Player reflector);
}
