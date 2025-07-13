package me.joelsoep.ctf.game.special;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoCollection;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.RandomUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.*;

public class SpecialService {
    private final Map<String, BaseSpecial<?>> specials;
    private final Map<String, BaseSpecial<?>> specialsIncludingUnavailable;

    public SpecialService() {
        this.specials = new HashMap<>();
        this.specialsIncludingUnavailable = new HashMap<>();
    }

    public void registerSpecial(BaseSpecial<?> special) {
        specials.put(special.getName().toLowerCase(Locale.ROOT), special);
        specialsIncludingUnavailable.put(special.getName().toLowerCase(Locale.ROOT), special);
    }

    public void loadSpecials() {
        Reflections reflections = new Reflections(FileManager.SPECIAL_PATH);
        Set<Class<? extends BaseSpecial>> specialClasses = reflections.getSubTypesOf(BaseSpecial.class);

        for (Class<? extends BaseSpecial> specialClass : specialClasses) {
            try {
                BaseSpecial<?> special = specialClass.getDeclaredConstructor().newInstance();
                Bukkit.getPluginManager().registerEvents(special, Main.getInstance());

                registerSpecial(special);
                LogModule.SPECIAL.log("Registered special item " + special.getName());
            } catch (ReflectiveOperationException e) {
                // ReflectiveOperationException is the parent class of various reflective exceptions
                e.printStackTrace();
            }
        }
        verify();
    }

    public List<BaseSpecial> getAllSpecials() {
        return new ArrayList<>(specials.values());
    }

    public List<BaseSpecial> getAllSpecialsIncludingUnavailable() { return new ArrayList<>(specialsIncludingUnavailable.values()); }

    public BaseSpecial<?> getSpecial(String special) {
        return specialsIncludingUnavailable.get(special.toLowerCase());
    }

    public void giveSpecial(CTFPlayer player, String specialName) {
        BaseSpecial<?> special = getSpecial(specialName);
        if (special != null) {
            special.giveSpecialToPlayer(player);
            return;
        }
        MM.deserialize("<#f2180c>This special doesn't exist. Please report this to an administrator.");
    }

    public BaseSpecial<?> getRandomSpecial() {
        ArrayList<BaseSpecial> list = new ArrayList<>(specials.values());
        if (list.isEmpty()) {
            return null;
        }
        int randomIndex = RandomUtil.get().nextInt(list.size());
        BaseSpecial special = list.get(randomIndex);
        if (special == null) {
            LogModule.SPECIAL.log("Special is null");
            return null;
        }
        return special;
    }

    public void registerSpecialToDatabase(BaseSpecial<?> special) {
        Document update = new Document("_id", special.getName())
                .append("enabled", true);

        MongoManager.insertOne(MongoManager.getCollection("special_item"), update);
    }

    public List<Document> getRegisteredFromDatabase() {
        MongoCollection<Document> collection = MongoManager.getCollection("special_item");
        return MongoManager.findAll(collection).join();
    }

    // run after loadSpecials

    public void verifyItems() {
        List<Document> documents = getRegisteredFromDatabase();
        Set<String> loadedSpecials = specials.keySet();
        if (documents.isEmpty()) {
            specials.values().forEach(this::registerSpecialToDatabase);
            return;
        }

        for (Document document : documents) {
            boolean found = false;
            String foundName = "";
            for (String name : loadedSpecials) {
                if (name.equals(document.getString("_id"))) {
                    if (document.getBoolean("enabled") == false) {
                        specials.remove(name);
                    }
                    found = true;
                    foundName = name;
                }
            }
            if (!found) {
                registerSpecialToDatabase(specialsIncludingUnavailable.get(foundName));
            }
        }
    }

    public void verify() {
        Map<String, Document> dbMap = new HashMap<>();
        for (Document doc : getRegisteredFromDatabase()) {
            dbMap.put(doc.getString("_id").toLowerCase(Locale.ROOT), doc);
        }

        Iterator<String> iterator = specials.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            Document document = dbMap.get(name);
            if (document == null) {
                registerSpecialToDatabase(specialsIncludingUnavailable.get(name));
            } else if (!document.getBoolean("enabled", true)) {
                iterator.remove();
            }
        }
    }


}
