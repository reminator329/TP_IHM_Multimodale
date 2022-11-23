public enum Formes {


    RECTANGLE("RECTANGLE"), /* Etat Initial */
    LOSANGE("DIAMOND"),
    CERCLE("CIRCLE"),
    TRIANGLE("TRIANGLE"),
    ;

    String name;

    private Formes(String name) {
        this.name = name;
    }

    public static Formes getByName(String name) {
        for (Formes forme : values()) {
            if (forme.name.equalsIgnoreCase(name)) {
                return forme;
            }
        }
        return null;
    }
}
