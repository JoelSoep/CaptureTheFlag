package me.joelsoep.ctf.player;

import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.economy.EconomyManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@Setter
public class CTFPlayer implements Audience {

    private final Player player;
    private final CTFPlayerData data;
    private final Balance balance;
    private final Cosmetics cosmetics;

    private Game game;
    private ParticipationState participationState;
    private Team team;
    private Integer killStreak;
    private BaseKit kit;
    private FastBoard board;
    private Long cooldown;
    private Integer level;

    protected CTFPlayer(Player player) {
        this.player = player;
        this.data = new CTFPlayerData(this);
        this.balance = new Balance(this);
        this.cosmetics = new Cosmetics();
        this.participationState = ParticipationState.AVAILABLE;
        this.killStreak = 0;
        this.level = 1;
        this.cooldown = System.currentTimeMillis();
        reset();
    }

    public boolean hasCooldown() {
        return cooldown > System.currentTimeMillis();
    }

    public void setCooldown(Long amount) {
        this.cooldown = System.currentTimeMillis() + amount;
    }

    public void reset() {
        this.setGame(null);
        this.setTeam(null);
        this.setKillStreak(0);

        player.getInventory().clear();
        player.playerListName(MM.deserialize("<gray>" + player.getName() + " ✪" + this.getLevel()));
        player.displayName(MM.deserialize("<gray>" + player.getName()));
        player.setGameMode(GameMode.ADVENTURE);
        player.setGlowing(false);
        player.setLastDamageCause(null);
        player.setLastDeathLocation(null);
        player.setKiller(null);
        player.setKiller(null);
        player.setLastDamage(0);
        player.setHealth(20);
        player.setFoodLevel(20);

        player.getActivePotionEffects().forEach(effect -> {
            player.removePotionEffect(effect.getType());
        });
    }

    public static class Cosmetics {
        private final HashMap<CosmeticType, Cosmetic> appliedCosmetics = new HashMap<>();

        public @Nullable Cosmetic getAppliedCosmetic(CosmeticType type) {
            return appliedCosmetics.get(type);
        }

        public void setAppliedCosmetic(CosmeticType type, Cosmetic cosmetic) {
            appliedCosmetics.put(type, cosmetic);
        }
    }

    public static class Balance {
        private final CTFPlayer player;

        public Balance(CTFPlayer player) {
            this.player = player;
        }

        public int getCoins() {
            return EconomyManager.get(player);
        }

        public void giveCoins(int amount) {
            boolean success = EconomyManager.increment(player, amount);
            if (success) {
                player.sendMessage(MM.deserialize("<gray>You've received <gold>%s <gray>coins!".formatted(amount)));
            } else {
                player.sendMessage(MM.deserialize("<red>A transaction for <gold>%s <red>coins failed. Please contact an administrator."));
            }
        }

        public void takeCoins(int amount) {
            boolean success = EconomyManager.subtract(player, amount);
            if (success) {
                player.sendMessage(MM.deserialize("<gold>%s <gray>coins were taken from your account.".formatted(amount)));
            } else {
                player.sendMessage(MM.deserialize("<red>You do not have enough coins to complete this transaction!"));
            }
        }
    }



    @Override
    public void showTitle(@NotNull Title title) {
        player.showTitle(title);
    }

    @Override
    public void sendMessage(@NotNull ComponentLike message) {
        player.sendMessage(message);
    }

    @Override
    public void deleteMessage(@NotNull SignedMessage signedMessage) {
        player.deleteMessage(signedMessage);
    }

    @Override
    public @NotNull Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
        return player.filterAudience(filter);
    }

    @Override
    public void forEachAudience(@NotNull Consumer<? super Audience> action) {
        player.forEachAudience(action);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(message);
    }

    public void sendMessage(@NotNull String message) { player.sendMessage(MM.deserialize(message)); }

    @Override
    public void sendMessage(@NotNull Component message, ChatType.@NotNull Bound boundChatType) {
        player.sendMessage(message, boundChatType);
    }

    @Override
    public void sendMessage(@NotNull ComponentLike message, ChatType.@NotNull Bound boundChatType) {
        player.sendMessage(message, boundChatType);
    }

    @Override
    public void sendMessage(@NotNull SignedMessage signedMessage, ChatType.@NotNull Bound boundChatType) {
        player.sendMessage(signedMessage, boundChatType);
    }

    @Override
    public void deleteMessage(SignedMessage.@NotNull Signature signature) {
        player.deleteMessage(signature);
    }

    @Override
    public void sendActionBar(@NotNull ComponentLike message) {
        player.sendActionBar(message);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        player.sendActionBar(message);
    }

    @Override
    public void sendPlayerListHeader(@NotNull ComponentLike header) {
        player.sendPlayerListHeader(header);
    }

    @Override
    public void sendPlayerListHeader(@NotNull Component header) {
        player.sendPlayerListHeader(header);
    }

    @Override
    public void sendPlayerListFooter(@NotNull ComponentLike footer) {
        player.sendPlayerListFooter(footer);
    }

    @Override
    public void sendPlayerListFooter(@NotNull Component footer) {
        player.sendPlayerListFooter(footer);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        player.sendTitlePart(part, value);
    }

    @Override
    public void clearTitle() {
        player.clearTitle();
    }

    @Override
    public void resetTitle() {
        player.resetTitle();
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        player.showBossBar(bar);
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        player.hideBossBar(bar);
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        player.playSound(sound);
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        player.playSound(sound, x, y, z);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        player.playSound(sound, emitter);
    }

    @Override
    public void stopSound(@NotNull Sound sound) {
        player.stopSound(sound);
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        player.stopSound(stop);
    }

    @Override
    public void openBook(Book.@NotNull Builder book) {
        player.openBook(book);
    }

    @Override
    public void openBook(@NotNull Book book) {
        player.openBook(book);
    }
}
