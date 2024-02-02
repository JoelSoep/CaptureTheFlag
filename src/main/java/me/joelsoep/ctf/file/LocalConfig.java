package me.joelsoep.ctf.file;

import lombok.Data;

@Data
public class LocalConfig {
    public static boolean ALLOW_CUSTOM_TEAMS = true;

    public static String MONGO_CONNECTION_STRING = "mongodb+srv://admin:uFrJ2m8zDDhALE9i@ctf.bh3mqpu.mongodb.net/?retryWrites=true&w=majority";


}
