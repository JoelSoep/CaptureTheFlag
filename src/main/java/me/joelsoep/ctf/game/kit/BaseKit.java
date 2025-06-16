package me.joelsoep.ctf.game.kit;

import com.destroystokyo.paper.Namespaced;
import lombok.Getter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseKit {
    protected String name;
   @Getter protected String version;
    protected ItemStack[] armor;
    protected Map<Integer, ItemStack> items; // Map to store items and their enchantments
    protected Material icon;
    protected Component styledName;
    protected ItemStack[] hand;

    public BaseKit(String name, String version, Material icon) {
        this.name = name;
        this.version = version;
        this.icon = icon;
        this.styledName = Component.text(name);
        this.armor = new ItemStack[4];
        this.items = new HashMap<>();
        this.hand = new ItemStack[1];
        setArmor();
        setItems();
        setHand();
    }

    public BaseKit(String name, String version, Material icon, Component styledName) {
        this.name = name;
        this.version = version;
        this.icon = icon;
        this.styledName = styledName;
        this.armor = new ItemStack[4];
        this.items = new HashMap<>();
        this.hand = new ItemStack[1];
        setArmor();
        setItems();
        setHand();
    }

    public abstract void setArmor();

    public abstract void setItems();

    public abstract void setHand();

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public Component getStyledName() {
        return styledName;
    }

    public Map<Integer, ItemStack> getItems() {
        return this.items;
    }

    public void giveKitToPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();
        Game game = Main.getGame();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());

        if (ctfPlayer == null) {
            return;
        }

        Team team = ctfPlayer.getTeam();
        if (team == null) {
            return;
        }
        // Clear player's inventory before giving the kit
        inventory.clear();

        // Give the armor pieces
        for (ItemStack itemStack : armor) {
            if (itemStack != null) {
                inventory.setArmorContents(armor);
            }
        }

        // helmet color depending on team
        Color color = team.equals(game.getBlue()) ? Color.BLUE : Color.RED;
        inventory.setHelmet(getHelmet(color));

        // give items based on how they are sorted by the player
        HashMap<Integer, ItemStack> sorted = ctfPlayer.getData().getSortedKits().get(this.getName());
        HashMap<Integer, ItemStack> finalSorted = sorted != null ? new HashMap<>(sorted.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))) : null;
        // if player hasn't sorted the items yet, give them the default layout
        if (sorted == null) {
            sorted = new HashMap<>(this.items);
            HashMap<Integer, ItemStack> finalSorted1 = sorted;
            sorted.forEach((integer, itemStack) -> {
               if (itemStack == null) {
                   return;
               }
                if (itemStack.getType() == Material.GRAY_CONCRETE) {
                    finalSorted1.put(integer, getBlocks(team, 64));
                }
            });
            sorted = finalSorted1;
        } else {
            sorted = new HashMap<>(sorted.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            sorted.forEach((integer, itemStack) -> {
                if (itemStack == null) {
                    return;
                }

                ArrayList<ItemStack> originalList = new ArrayList<>(this.items.values());
                if (originalList.stream().noneMatch(item -> item.getType() == itemStack.getType())) {
                    LogModule.KIT.warning("itemStack is null but shouldnt be null @ giveKitToPlayer " + player.getName() + itemStack.getType());
                    return;
                }

                //Bukkit.getLogger().info("Material: " + itemStack.getType() + " Amount: " + originalList.stream().filter(item -> item.getType() == itemStack.getType()).findFirst().get().getAmount());

                itemStack.setAmount(originalList.stream().filter(item -> item.getType() == itemStack.getType()).findFirst().get().getAmount());

                if (itemStack.getType() == Material.GRAY_CONCRETE) {
                    finalSorted.put(integer, getBlocks(team, 64));
                }
            });
        }

        HashMap<Integer, ItemStack> finalList = finalSorted == null ? sorted : finalSorted;
        for (Map.Entry<Integer, ItemStack> entry : finalList.entrySet()) {
            // remove NBT data from stackables so they stack on top of each other, might not be handy for all items tho

            ItemStack item;
            if (entry.getValue().getItemFlags().contains(ItemFlag.HIDE_DYE)) {
                item = entry.getValue();
            } else {
                item = entry.getValue().getMaxStackSize() > 1 ? new ItemStack(entry.getValue().getType(), entry.getValue().getAmount()) : entry.getValue();
            }
            int slot = entry.getKey();
            inventory.setItem(slot, item);
        }

        if (!inventory.contains(getBlocks(team, 64).getType())) {
            inventory.addItem(getBlocks(team, 64));
        }
        if (hand != null && hand[0] != null) {
            inventory.setItemInOffHand(hand[0]);
        }
    }

    public static ItemStack getHelmet(Color color) {
        ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        meta.setUnbreakable(true);
        meta.setColor(color);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getBlocks(Team team, int amount) {
        ItemStack itemStack;
        Material material = switch (team.getTeamColor()) {
            case RED -> Material.RED_CONCRETE;
            case BLUE -> Material.BLUE_CONCRETE;
        };

        itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<Namespaced> list = new ArrayList<>(Material.values().length);
        List.of(Material.values()).forEach(value -> list.add(value.getKey()));
        itemMeta.setPlaceableKeys(list);
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getDefaultPickaxe() {
        ItemStack itemStack = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ArrayList<Namespaced> list = new ArrayList<>(List.of(Material.BLUE_CONCRETE.getKey(), Material.RED_CONCRETE.getKey(), Material.RED_CONCRETE_POWDER.getKey(), Material.BLUE_CONCRETE_POWDER.getKey()));
        itemMeta.setUnbreakable(true);
        itemMeta.setDestroyableKeys(list);
        itemStack.setItemMeta(itemMeta);
        itemStack.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);
        return itemStack;
    }
}