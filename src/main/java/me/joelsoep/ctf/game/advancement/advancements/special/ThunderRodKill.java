package me.joelsoep.ctf.game.advancement.advancements.special;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.visibilities.ParentGrantedVisibility;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.advancement.IBaseAdvancement;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.logging.Level;

public class ThunderRodKill /** extends BaseAdvancement implements ParentGrantedVisibility, IBaseAdvancement **/ {
    private AdvancementTab tab;

    public ThunderRodKill() {
    /**    super(
                "thunder_rod_kill",
                new AdvancementDisplay(
                        Material.BLAZE_ROD,
                        "Droit Divin",
                        AdvancementFrameType.TASK,
                        true,
                        true,
                        3,
                        1,
                        "Kill your enemy with a Thunder Rod!"
                ),
                new MirrorUse()
        ); **/
        this.tab = Main.advancementAPI.getTab();
    }

  //  @Override
    public void registerListener() {
        this.tab.getEventManager().register(tab, PlayerDeathEvent.class, EventPriority.HIGHEST, event -> {
            Player player = event.getPlayer();
            Player killer = player.getKiller();

            if (killer == null) {
                //Bukkit.getLogger().info("killer null!!!!");
                return;
            }

            if (player.getLastDamageCause() == null) {
               // Bukkit.getLogger().info("null!!!!!!!");
                return;
            }

            LogModule.ADVANCEMENT.log(player.getLastDamageCause().getCause().toString());
            if (player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LIGHTNING ) {
            //    incrementProgression(killer);
            }

        });
    }
}
