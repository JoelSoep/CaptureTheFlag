package me.joelsoep.ctf.game.special;

import me.joelsoep.ctf.game.special.specials.Mirror;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * An interface to state whether a special item is reflectable by a {@link Mirror} item
 * @param <T> The event which contains information to reflect
 */

public interface Reflective<T extends Event> {

    /**
     * Use this method in order to reflect the current event occurring.
     * This method is not supposed to use the @EventHandler Annotation.
     * @param event The event to use to reflect.
     */

    void reflect(T event, @NotNull Player player, @NotNull Player reflector);
}
