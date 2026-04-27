package skeletonprogram;

/**
 *  Megadott állomások között ingázó, a buszsofőr menedzser által irányított jármű.
 * Feladata a pénzteremtés a megtett körök alapján. Az autókhoz hasonlóan súlyával tömöríti a havat. 
 * Baleset esetén mozgásképtelenné válik és eltorlaszolja az utat. * Az onRegistered() hookkal bejelentkezik a GameState buses listajaba,
 * igy a jatekvegezo logika instanceof nelkul tudja szamlalni a buszokat. */
public final class Busz extends Jarmu {
    private int completedTrips;

    public Busz(String name) {
        super(name);
        this.completedTrips = 0;
    }

    /** Regisztralas soran bejelentkezik a GameState busz-listajaba a jatekvege-logikához. */
    @Override
    public void onRegistered(GameState state) {
        state.registerBus(this);
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Busz";
    }

    /**
     * Vegallomas elerese eseten noveli a megtett korok szamat es jovaira a jatekos penzenek.
     * A vegallomas csomopont neve "Vegallomas", "Vegallomas_1" vagy "Vegallomas_2" lehet.
     */
    @Override
    protected void onCelUtElerve(Ut target, GameState state) {
        if (target.hasNode("Vegallomas") || target.hasNode("Vegallomas_1") || target.hasNode("Vegallomas_2")) {
            completedTrips += 1;
            Jatekos ownerEntity = state.getJatekos(owner);
            if (ownerEntity != null) {
                ownerEntity.money += 40;
                state.enqueueEvent("Busz kor teljesitve: " + name + ", jovairas +40.");
            }
        }
    }
}
