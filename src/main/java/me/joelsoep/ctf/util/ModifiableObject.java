package me.joelsoep.ctf.util;

public class ModifiableObject {
    private Object object;

    public ModifiableObject(Object input) {
        this.object = input;
    }

    public void setObject(Object input) {
        this.object = input;
    }

    public Object getObject() {
        return object;
    }
}
