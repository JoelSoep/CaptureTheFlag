package me.joelsoep.ctf.game.advancement.advancements.playedGames;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.advancement.IBaseAdvancement;
import me.joelsoep.ctf.game.event.GameEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class FlagRecruit extends BaseAdvancement implements IBaseAdvancement {

    private final AdvancementTab tab;

    public FlagRecruit() {
        super("flag_recruit", new AdvancementDisplay(Material.WOODEN_SWORD, "Flag Recruit", AdvancementFrameType.TASK, true, true, 1, 3, List.of("Play your first game of Capture the Flag.")), Main.advancementService.getRoot(), 1);
        this.tab = Main.advancementAPI.getTab();
    }

    @Override
    public void giveReward(@NotNull Player player) {
        Advancement advancement = Main.advancementAPI.getTab().getAdvancement(AdvancementKey.fromString("ctf:flag_novice"));
        if (advancement == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't give advancement reward because ctf:flag_novice is null!");
            return;
        }
        advancement.setProgression(player, 1);
    }

    public void registerListener() {
        tab.getEventManager().register(tab, GameEndEvent.class, event -> {
            event.getGame().getPlayers().forEach(player -> {
                incrementProgression(player.getPlayer());
            });
        });
    }
}
