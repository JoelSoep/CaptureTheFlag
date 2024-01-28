package me.joelsoep.ctf.file;

public enum FileType {
    SCHEMATIC("schematics/");

    private final String path;

    FileType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
