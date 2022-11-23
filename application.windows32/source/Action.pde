public enum Action {


    CREER("CREATE"), /* Etat Initial */
    DEPLACER("MOVE"),
    SUPPRIMER("DELETE"),
    ;

    String name;

    private Action(String name) {
        this.name = name;
    }

    public static Action getByName(String name) {
        for (Action action : values()) {
            if (action.name.equalsIgnoreCase(name)) {
                return action;
            }
        }
        return null;
    }
}
