package me.joelsoep.ctf.util;

import java.util.Properties;

public class Version {

    public static String get() {
        try {
            final Properties properties = new Properties();
            properties.load(Version.class.getClassLoader().getResourceAsStream(".properties"));
            return properties.getProperty("version");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.ERR";
    }
}
