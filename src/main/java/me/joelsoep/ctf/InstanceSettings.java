package me.joelsoep.ctf;

import lombok.Data;

@Data
public final class InstanceSettings {
    private boolean open = true;
    private boolean muted = false;
    private boolean restartAfterGame = false;
    private boolean allowBypassDataSuspicionCheck = false;
}
