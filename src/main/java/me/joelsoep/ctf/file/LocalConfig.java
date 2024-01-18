package me.joelsoep.ctf.file;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LocalConfig {
    @Getter
    @Setter
    public static boolean allowCustomTeams = true;
}
