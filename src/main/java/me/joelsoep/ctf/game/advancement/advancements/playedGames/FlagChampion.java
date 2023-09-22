package me.joelsoep.ctf.game.advancement.advancements.playedGames;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import com.fren_gor.ultimateAdvancementAPI.visibilities.ParentGrantedVisibility;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.advancement.IBaseAdvancement;
import me.joelsoep.ctf.game.advancement.advancements.playedGames.FlagMaster;
import me.joelsoep.ctf.game.event.GameEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class FlagChampion extends BaseAdvancement implements ParentGrantedVisibility, IBaseAdvancement {

    private final AdvancementTab tab;

    public FlagChampion() {
        super("flag_champion", new AdvancementDisplay(Material.NETHERITE_SWORD, "Flag Champion", AdvancementFrameType.CHALLENGE, true, true, 5, 3, List.of("Play 250 games of Capture the Flag.")), new FlagMaster(), 250);
        this.tab = Main.advancementAPI.getTab();
    }

    @Override
    public void giveReward(@NotNull Player player) {
    }


    @Override
    public void registerListener() {
        tab.getEventManager().register(tab, GameEndEvent.class, event -> {
            event.getGame().getPlayers().forEach(player -> {
                Advancement advancement = tab.getAdvancement(AdvancementKey.fromString("ctf:flag_master"));
                if (advancement == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "ctf:flag_master is null, couldn't give parent reward");
                    return;
                }
                if (advancement.getProgression(player.getPlayer()) >= advancement.getMaxProgression()) {
                    incrementProgression(player.getPlayer());
                }
            });
        });
    }
}
