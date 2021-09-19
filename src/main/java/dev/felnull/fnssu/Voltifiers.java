package dev.felnull.fnssu;

public enum Voltifiers {
    NON("", "", ""),
    TEST("Votifier Tester", "minestatus.net test vote", "https://minestatus.net/tools/votifier"),
    JMS("JMS", "minecraft.jp", "https://minecraft.jp/servers/play.servg.red:25525"),
    MONO("ものくらふと", "monocraft.net", "https://monocraft.net/servers/dVLmTYApKzEpm1NpjKcL");
    private final String name;
    private final String serviceName;
    private final String url;

    private Voltifiers(String name, String sname, String url) {
        this.name = name;
        this.serviceName = sname;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getServiceName() {
        return serviceName;
    }

    public static Voltifiers getByServiceName(String serviceName) {
        for (Voltifiers value : values()) {
            if (value.getServiceName().equals(serviceName))
                return value;
        }
        return NON;
    }
}
