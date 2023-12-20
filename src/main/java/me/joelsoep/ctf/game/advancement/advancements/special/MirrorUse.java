package me.joelsoep.ctf.game.advancement.advancements.special;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.visibilities.ParentGrantedVisibility;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.advancement.IBaseAdvancement;
import me.joelsoep.ctf.game.advancement.advancements.captures.Capturer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MirrorUse extends BaseAdvancement implements ParentGrantedVisibility, IBaseAdvancement {

    private AdvancementTab tab;

    public MirrorUse() {
        super("mirror_use",
                new AdvancementDisplay(
                        Material.END_CRYSTAL,
                        "Instant Karma",
                        AdvancementFrameType.TASK,
                        true,
                        true,
                        2,
                        1,
                        "Murder your enemy by using a Mirror!"
                        ),
                new Capturer(),
                1
        );
        this.tab = Main.advancementAPI.getTab();
    }

    @Override
    public void registerListener() {
        this.tab.getEventManager().register(tab, PlayerDeathEvent.class, event -> {
            Player player = event.getPlayer();
            Player killer = player.getKiller();

            if (killer == null) {
                return;
            }

            if (player.getLastDamageCause() == null) {
                return;
            }

            if (player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.DRAGON_BREATH ) {
                incrementProgression(killer);
            }

        });
    }


}
