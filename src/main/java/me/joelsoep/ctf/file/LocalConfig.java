package me.joelsoep.ctf.file;

import lombok.Data;
import lombok.Getter;

@Data
public class LocalConfig {
    @Getter
    public static boolean allowCustomTeams = true;
}
