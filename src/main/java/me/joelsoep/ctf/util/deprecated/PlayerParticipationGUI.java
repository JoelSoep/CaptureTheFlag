package me.joelsoep.ctf.util.deprecated;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.task.CountdownTask;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.ParticipationState;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Deprecated
public class PlayerParticipationGUI extends ChestGui implements Listener {

    private final Player player;
    private Game game;
    private final StaticPane participationPane;
    private final StaticPane idlePane;
    private final StaticPane spectatorPane;
    private final PatternPane background;

    public PlayerParticipationGUI(Player player) {
        super(3, "Participation Menu");
        this.player = player;
        this.game = Main.game;

        this.participationPane = new StaticPane(2, 1, 1, 1);
        this.idlePane = new StaticPane(4, 1, 1, 1);
        this.spectatorPane = new StaticPane(6, 1, 1, 1);

        this.background = new PatternPane(0, 0, 9, 3, new Pattern(
                "111111111",
                "110101011",
                "111111111"
        ));

        this.background.bindItem('1', new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        this.addPane(participationPane);
        this.addPane(idlePane);
        this.addPane(spectatorPane);
        this.addPane(background);
        openParticipationMenu();
    }

    private void openParticipationMenu() {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        this.setOnGlobalClick(event -> { event.setCancelled(true); });

        ItemStack participationEnabled = new ItemStack(Material.LEVER);
        ItemMeta enabledMeta = participationEnabled.getItemMeta();
        enabledMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enabledMeta.displayName(MM.deserialize(ParticipationState.AVAILABLE.getColor()));
        enabledMeta.lore(List.of(
                MM.deserialize("<gray>Set your status to <green>AVAILABLE"),
                MM.deserialize("<gray>If you want to opt in to play games.")
        ));
        participationEnabled.setItemMeta(enabledMeta);

        ItemStack participationDisabled = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta disabledMeta = participationDisabled.getItemMeta();
        disabledMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        disabledMeta.displayName(MM.deserialize(ParticipationState.UNAVAILABLE.getColor()));
        disabledMeta.lore(List.of(
                MM.deserialize("<gray>Set your status to <red>UNAVAILABLE"),
                MM.deserialize("<gray>If you would like to hang around in the lobby.")
        ));
        participationDisabled.setItemMeta(disabledMeta);

        ItemStack spectator = new ItemStack(Material.ENDER_EYE);
        ItemMeta spectatorMeta = spectator.getItemMeta();
        spectatorMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spectatorMeta.displayName(MM.deserialize(ParticipationState.SPECTATING.getColor()));
        spectatorMeta.lore(List.of(
                MM.deserialize("<gray>Set your status to <gold>SPECTATING"),
                MM.deserialize("<gray>If you would like to spectate games.")
        ));
        spectator.setItemMeta(spectatorMeta);

        GuiItem participationEnabledItem = new GuiItem(participationDisabled, event -> {
            if (ctfPlayer.getParticipationState() == ParticipationState.UNAVAILABLE) {
                return;
            }
            ctfPlayer.setParticipationState(ParticipationState.UNAVAILABLE);
            player.sendMessage(MM.deserialize("<gray>Updated your status to " + ctfPlayer.getParticipationState().getColor()));
            if (game.getPlayers().contains(ctfPlayer)) {
                game.removePlayer(ctfPlayer);
                ctfPlayer.sendMessage(MM.deserialize("<red>You left the game."));
                game.getAudience().sendMessage(MM.deserialize("<gray>[<red>-<gray>] <red>%s <gray>left the game.".formatted(player.getName())));
            }

            if (game.getSpectators().contains(ctfPlayer)) {
                game.removeSpectator(ctfPlayer);
            }

            if (game.getPlayers().size() < game.getMinPlayers() && game.isStarting()) {
                if (Main.countdownTask != null) {
                    Main.countdownTask.cancel();
                    game.setStarting(false);
                    game.setSpedUp(false);
                    game.getAudience().sendMessage(MM.deserialize("<red>Game preparation has been canceled as there are too few players to start."));
                }
            }
            new PlayerParticipationGUI(player);
        });

        GuiItem participationDisabledItem = new GuiItem(participationEnabled, event -> {
            if (ctfPlayer.getParticipationState() == ParticipationState.AVAILABLE) {
                return;
            }
           ctfPlayer.setParticipationState(ParticipationState.AVAILABLE);
           player.sendMessage(MM.deserialize("<gray>Updated your status to " + ctfPlayer.getParticipationState().getColor()));
            if (game.getSpectators().contains(ctfPlayer)) {
                game.removeSpectator(ctfPlayer);
            }
           if (game.getState() == GameState.WAITING && game.getPlayerCount() < game.getMaxPlayers()) {

               game.addPlayer(ctfPlayer);
               game.getAudience().sendMessage(MM.deserialize("""
                    <gray>[</gray><green>+</green><gray>] %s has joined the game! (%s/%s)"""
                       .formatted(player.getName(), game.getPlayerCount(), game.getMaxPlayers())));

               if (Main.countdownTask != null) {
                   Main.countdownTask.updateAudience(game.getAudience());
               }

               if (game.getPlayerCount() >= game.getMinPlayers() && !game.isStarting()) {
                   Main.countdownTask = new CountdownTask(
                           plugin,
                           MM.deserialize("<#63f02b>Game has started!"),
                           game.getAudience(),
                           45,
                           true,
                           game
                   );
                   game.setStarting(true);
               }

            if (game.getPlayerCount() >= game.getMaxPlayers() && game.isStarting() && !game.isSpedUp()) {
                if (Main.countdownTask != null && Main.countdownTask.getSeconds() > 10) {
                    Main.countdownTask.cancel();
                }
                Main.countdownTask = new CountdownTask(
                        plugin,
                        MM.deserialize("<#63f02b>Game has started!"),
                        game.getAudience(),
                        10,
                        true,
                        game
                );
                game.setSpedUp(true);
            }

           }
            new PlayerParticipationGUI(player);
        });

        GuiItem spectatorItem = new GuiItem(spectator, event -> {
            if (ctfPlayer.getParticipationState() == ParticipationState.SPECTATING) {
                return;
            }
            if (game.getPlayers().contains(ctfPlayer)) {
                game.removePlayer(ctfPlayer);
                ctfPlayer.sendMessage(MM.deserialize("<red>You left the game."));
                game.getAudience().sendMessage(MM.deserialize("<gray>[<red>-<gray>] <red>%s <gray>left the game.".formatted(player.getName())));
            }

            ctfPlayer.setParticipationState(ParticipationState.SPECTATING);
            if (!game.getSpectators().contains(ctfPlayer)) {
                game.addSpectator(ctfPlayer);
            }
            if (game.getState() == GameState.READY || game.getState() == GameState.RUNNING) {
                ctfPlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
                ctfPlayer.getPlayer().teleport(new Location(Bukkit.getWorld("CaptureTheFlag"), -118, -17, -97));
            }

            if (game.getPlayers().size() < game.getMinPlayers() && game.isStarting()) {
                if (Main.countdownTask != null) {
                    Main.countdownTask.cancel();
                    game.setStarting(false);
                    game.setSpedUp(false);
                    game.getAudience().sendMessage(MM.deserialize("<red>Game preparation has been canceled as there are too few players to start."));
                }
            }

            player.sendMessage(MM.deserialize("<gray>Updated your status to " + ctfPlayer.getParticipationState().getColor()));
            new PlayerParticipationGUI(player);
        });

        switch (ctfPlayer.getParticipationState()) {
            case UNAVAILABLE -> { participationEnabledItem.getItem().addUnsafeEnchantment(Enchantment.DURABILITY, 1); }
            case SPECTATING -> { spectatorItem.getItem().addUnsafeEnchantment(Enchantment.DURABILITY, 1); }
            case AVAILABLE -> { participationDisabledItem.getItem().addUnsafeEnchantment(Enchantment.DURABILITY, 1); }
        }

        this.participationPane.addItem(participationEnabledItem, 0, 0);
        this.idlePane.addItem(participationDisabledItem, 0, 0);
        this.spectatorPane.addItem(spectatorItem, 0, 0);

        this.update();
        this.show(player);
    }
}
