package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamService;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TeamSelectionGUI extends ChestGui {
    private final PatternPane background;
    private final OutlinePane teamsPane;

    public TeamSelectionGUI(Player player) {
        super(3, "Team Selection");

        this.background = new PatternPane(0, 0, 9, 3, Pane.Priority.LOWEST, new Pattern(
                "111111111",
                "111010111",
                "111111111"
        ));

        this.background.bindItem('1', new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        this.teamsPane = new OutlinePane(3, 1, 9, 3, Pane.Priority.HIGHEST);
        this.setOnGlobalClick(event -> { event.setCancelled(true); });
        this.addPane(background);
        this.addPane(teamsPane);
        this.loadTeams(player);
        this.show(player);
    }

    private void loadTeams(Player player) {
        AtomicInteger iteration = new AtomicInteger();
        TeamService.getTeams().forEach(team -> {

            // solution for the time being ig, or forever if i decide that the max amount of teams will stay at 2
            if (iteration.get() == 1) {
                this.teamsPane.addItem(new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
            }
            ItemStack itemStack = new ItemStack(team.getTeamColor().getMaterial());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(MM.deserialize(team.getTeamColor().getPrefix()));
            itemMeta.lore(List.of(
                    MM.deserialize("<gray>%s/%s".formatted(team.getPlayers().size(), Main.getGame().getMap().getMaxPlayers() / TeamService.getTeams().size()))
            ));
            itemStack.setItemMeta(itemMeta);

            if (isPlayerInTeam(player, team)) {
                itemStack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            this.teamsPane.addItem(new GuiItem(itemStack, event -> handleButtonClick(team, player)));
            iteration.getAndIncrement();
        });
    }

    private boolean isPlayerInTeam(Player player, Team team) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            return false;
        }

        if (ctfPlayer.getTeam() == null) {
            return false;
        }
        return team.getPlayers().contains(ctfPlayer);
    }

    private void handleButtonClick(Team team, Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        if (team.getPlayers().contains(ctfPlayer)) {
            team.removePlayer(ctfPlayer);
            new TeamSelectionGUI(player);
            return;
        }

        if (team.getPlayers().size() >= Main.getGame().getMap().getMaxPlayers() / TeamService.getTeams().size()) {
            player.sendMessage(MM.deserialize("<red>Can't join this team as it's full already!"));
            new TeamSelectionGUI(player);
            return;
        }

        if (ctfPlayer.getTeam() != null) {
            ctfPlayer.getTeam().removePlayer(ctfPlayer);
        }
        team.addPlayer(ctfPlayer);
        new TeamSelectionGUI(player);
    }

}
