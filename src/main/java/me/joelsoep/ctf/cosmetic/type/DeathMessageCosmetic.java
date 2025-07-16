package me.joelsoep.ctf.cosmetic.type;

import lombok.Getter;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.CosmeticsManager;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class DeathMessageCosmetic extends Cosmetic {
   private final String deathMessage;

    /**
     * Creates a new DeathMessage Cosmetic.
     *
     * <p>
     * This {@link Cosmetic} type should only be created in the {@code me.joelsoep.ctf.cosmetic.type.deathMessage} directory.
     * If this is created outside of this package, it's necessary to register it to the {@link CosmeticsManager} manually.
     * <p>
     *
     * <p>
     * Example:
     * <blockchain><pre>{@code public class ExampleDeathMessage extends DeathMessageCosmetic {
     *
     *     public ExampleDeathMessage() {
     *         super("Example DeathMessage",
     *         CosmeticRarity.COMMON);
     *     }
     *
     *     public @NotNull String getMessage() {
     *         return "player %k killed player %p.";
     *     }
     * }
     * }</pre></blockchain
     * @param name The name for the cosmetic
     * @param rarity The {@link CosmeticRarity } of the item
     */

    public DeathMessageCosmetic(String name, CosmeticRarity rarity) {
        super(name, CosmeticType.DEATH_MESSAGE);
        this.setRarity(rarity);
        this.deathMessage = getMessage();
        this.setLogo(Material.PAPER);
    }

    /**
     * Returns a custom death message.
     * <p>
     * This message should include two placeholders:
     * <ul>
     *   <li>{@code %k} - the name of the {@link Player} who killed the player</li>
     *   <li>{@code %p} - the name of the {@link Player} who died</li>
     * </ul>
     * <p>
     * Example:
     * <pre>
     * "Player %p was slain by %k"
     * </pre>
     * <p>
     * In the example above, {@code %p} will be replaced by the name of the player who died, and {@code %k} will be replaced by the name of the killer.
     *
     * @return The custom death message as a string.
     */

    protected abstract @NotNull String getMessage();
}
