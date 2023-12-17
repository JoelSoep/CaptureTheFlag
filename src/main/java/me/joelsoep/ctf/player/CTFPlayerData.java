package me.joelsoep.ctf.player;

import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.kit.BaseKit;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Data
public class CTFPlayerData {

    private final CTFPlayer player;
    private final String name;

    private HashMap<String, HashMap<Integer, ItemStack>> sortedKits = new HashMap<>();
    private Date lastSeen;
    private Integer kills;
    private Integer deaths;
    private Double kd;

    public CTFPlayerData(CTFPlayer player) {
        this.player = player;
        this.name = player.getPlayer().getName();

        if (Main.mongo.findOneDocumentAsync(Main.mongo.getUserCollection(), new Document("_id", player.getPlayer().getUniqueId().toString())).join() == null) {
            Main.mongo.insertDocumentAsync(Main.mongo.getUserCollection(), new Document(
               "_id", player.getPlayer().getUniqueId().toString())
                    .append("player_name", name)
                    .append("player_kills", 0)
                    .append("player_deaths", 0)
                    .append("player_login_streak", 1)
                    .append("player_kd", (double) 1)
            );
            this.kills = 0;
            return;
        }

        CompletableFuture<Document> future = Main.mongo.findOneDocumentAsync(Main.mongo.getUserCollection(), new Document("_id", player.getPlayer().getUniqueId().toString()));

        future.thenAccept(result -> {
          if (result != null) {
              if (this.kills == null) {
                  this.kills = 0;
              }

              if (this.deaths == null) {
                  this.deaths = 0;
              }

              if (this.kd == null) {
                  this.kd = (double) 1;
              }

              this.kills = result.getInteger("player_kills");
              this.deaths = result.getInteger("player_deaths");

              this.kd = result.getDouble("player_kd");

              List<Object> resultList = result.getList("sortedKits", Object.class);
              if (resultList == null) {
                  return;
              }

              HashMap<String, HashMap<Integer, Material>> parsedData = new HashMap<>();
              resultList.forEach(object -> {
                  String dataString = object.toString();

                  // Remove unnecessary parts of the input string
                  dataString = dataString.replace("Document{{", "").replace("}}", "");

                  // Split the string into parts using "="
                  String[] parts = dataString.split("=");
                      String name = parts[0];
                      StringBuilder innerDataBuilder = new StringBuilder();

                      // Concatenate parts 1 onwards to form innerData
                      for (int i = 1; i < parts.length; i++) {
                          innerDataBuilder.append(parts[i]);
                          if (i < parts.length - 1) {
                              innerDataBuilder.append("=");
                          }
                      }

                      String innerData = innerDataBuilder.toString();
                      HashMap<Integer, Material> innerMap = new HashMap<>();

                      // Split the innerData into key-value pairs using ", "
                      String[] keyValues = innerData.split(", ");
                      for (String pair : keyValues) {
                          String[] keyValue = pair.split("=");
                          if (keyValue.length == 2) {
                              int key = Integer.parseInt(keyValue[0]);
                              String value = keyValue[1];
                              Material material = Material.valueOf(value);
                              innerMap.put(key, material);
                      }

                      parsedData.put(name, innerMap);
                  }
              });

// Now you have the parsed data in the parsedData HashMap
              parsedData.forEach((name, innerMap) -> {
                  Bukkit.getLogger().info("Name: " + name);
                  Bukkit.getLogger().info("Inner Map: " + innerMap);
              });

              HashMap<String, HashMap<Integer, ItemStack>> serialized = new HashMap<>();

              parsedData.forEach((kitName, data) -> {
                  Optional<BaseKit> optionalKit = Main.kitService.getAllKits().stream().filter(kitIterate -> Objects.equals(kitIterate.getName(), kitName)).findFirst();
                  if (optionalKit.isEmpty()) {
                      return;
                  }

                  Bukkit.getLogger().info("Data: " + data.toString());

                  BaseKit kit = optionalKit.get();
                  HashMap<Integer, ItemStack> mappedStack = new HashMap<>();
                  data.forEach((dataInteger, material) -> {
                      kit.getItems().forEach((kitInteger, item) -> {
                          if (item.getType() == material) {
                              Bukkit.getLogger().info("Placement: " + dataInteger + " material: " + item.getType());
                              mappedStack.put(dataInteger, item);
                          }
                      });
                  });
                  serialized.put(kitName, mappedStack);
              });

              this.sortedKits = serialized;
          } else {
              System.out.println("Document not found for player " + player.getPlayer().getName());
          }
        }).exceptionally(exception -> {
            System.err.println("Error occurred: " + exception.getMessage());
            return null;
        });
    }
}
