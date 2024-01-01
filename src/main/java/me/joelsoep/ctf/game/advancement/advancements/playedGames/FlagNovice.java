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

public class FlagNovice extends BaseAdvancement implements ParentGrantedVisibility, IBaseAdvancement {

    private final AdvancementTab tab;

    public FlagNovice() {
        super("flag_novice", new AdvancementDisplay(Material.STONE_SWORD, "Flag Novice", AdvancementFrameType.TASK, true, true, 2, 3, List.of("Play ten games of Capture the Flag.")), new FlagRecruit(), 10);
        this.tab = Main.advancementAPI.getTab();
    }

    @Override
    public void giveReward(@NotNull Player player) {
        Advancement advancement = Main.advancementAPI.getTab().getAdvancement(AdvancementKey.fromString("ctf:flag_warrior"));
        if (advancement == null) {
            LogModule.ADVANCEMENT.warning("Couldn't give advancement reward because ctf:flag_warrior is null!");
            return;
        }
        advancement.setProgression(player, 10);
    }


    @Override
    public void registerListener() {
        tab.getEventManager().register(tab, GameEndEvent.class, event -> {
            event.getGame().getPlayers().forEach(player -> {
                incrementProgression(player.getPlayer());
            });
        });
    }
}
