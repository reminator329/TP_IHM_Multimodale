public enum Couleurs {


    ROUGE("RED", 255, 0, 0),
    BLEU("BLUE", 0, 0, 255),
    VERT("GREEN", 0, 255, 0),
    ;

    String name;
    int r, g, b;

    private Couleurs(String name, int r, int g, int b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Couleurs getByName(String name) {
        for (Couleurs couleurs : values()) {
            if (couleurs.name.equalsIgnoreCase(name)) {
                return couleurs;
            }
        }
        return null;
    }

    public int getG() {
        return g;
    }

    public int getR() {
        return r;
    }

    public int getB() {
        return b;
    }
}
