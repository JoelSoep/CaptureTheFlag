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
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class FlagWarrior extends BaseAdvancement implements ParentGrantedVisibility, IBaseAdvancement {

    private final AdvancementTab tab;

    public FlagWarrior() {
        super("flag_warrior", new AdvancementDisplay(Material.IRON_SWORD, "Flag Warrior", AdvancementFrameType.TASK, true, true, 3, 3, List.of("Play 50 games of Capture the Flag.")), new FlagNovice(), 50);
        this.tab = Main.advancementAPI.getTab();
    }

    @Override
    public void giveReward(@NotNull Player player) {
        Advancement advancement = Main.advancementAPI.getTab().getAdvancement(AdvancementKey.fromString("ctf:flag_master"));
        if (advancement == null) {
            LogModule.ADVANCEMENT.warning("Couldn't give advancement reward because ctf:flag_master is null!");
            return;
        }
        advancement.setProgression(player, 50);
    }


    @Override
    public void registerListener() {
        tab.getEventManager().register(tab, GameEndEvent.class, event -> {
            event.getGame().getPlayers().forEach(player -> {
                Advancement advancement = tab.getAdvancement(AdvancementKey.fromString("ctf:flag_novice"));
                if (advancement == null) {
                    LogModule.ADVANCEMENT.warning("ctf:flag_novice is null, couldnt give parent reward");
                    return;
                }
                if (advancement.getProgression(player.getPlayer()) >= advancement.getMaxProgression()) {
                    incrementProgression(player.getPlayer());
                }
            });
        });
    }
}
