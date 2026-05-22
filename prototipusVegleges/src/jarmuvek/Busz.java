package jarmuvek;

import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 * Megadott állomások között ingázó, a buszsofőr menedzser által irányított jármű.
 * Feladata a pénzteremtés a megtett körök alapján.
 *
 * UJ MODELL: a busz csomoponton all (currentNode), korokent egyszer lephet
 * (getMaxMovesPerTurn = 1). A vegallomast (barmely "Vegallomas_*" csomopont)
 * elerve novekszik a completedTrips es jovairas tortenik a buszosjatekos vagyonan.
 *
 * Az autokhoz hasonloan súlyával tömöríti a havat, és baleset eseten az uton
 * elakad — csak hokotro takarítasaval szabadul.
 */
public final class Busz extends Jarmu {
    private int completedTrips;

    public Busz(String name) {
        super(name);
        this.completedTrips = 0;
    }

    /** A buszos jatekos legfeljebb egy lepest tehet egy korben (per busz). */
    @Override
    public int getMaxMovesPerTurn() {
        return 1;
    }

    private static boolean isVegallomasNode(String node) {
        if (node == null) return false;
        String lower = node.toLowerCase(java.util.Locale.ROOT);
        return lower.equals("vegallomas") || lower.startsWith("vegallomas_");
    }

    /** Regisztralas soran bejelentkezik a GameState busz-listajaba a jatekvege-logikához. */
    @Override
    public void onRegistered(GameState state) {
        state.registerBus(this);
    }

    // Csak konzolos UI-hoz; lasd Jarmu osztaly kommentjet.
    @Override
    public String type() {
        return "Busz";
    }

    /**
     * Vegallomas elerese eseten noveli a megtett korok szamat es jovairja a jatekos penzenek.
     * Athaladaskor (mint az autok) tomoríti a havat.
     */
    @Override
    protected void onCsomopontElerve(Ut traversedRoad, int laneIdx, GameState state) {
        // Vegallomas-bonus: ha a celcsomopont vegallomas
        if (isVegallomasNode(currentNode)) {
            completedTrips += 1;
            // A jutalom a kozos kasszaba kerul (ahonnan a takarito is fizet)
            state.creditKassza(50);
            state.enqueueEvent("Busz kor teljesitve: " + name + ", kozos kassza +50.");
        }

        // Forgalmi hatas: hot tomoriti
        Sav sav = traversedRoad.sav(laneIdx);
        if (sav.ho > 0) {
            sav.trafficCount++;
            if (sav.trafficCount >= 5) {
                sav.ice += sav.ho;
                sav.ho = 0;
                state.enqueueEvent(traversedRoad.name() + " " + laneIdx + ". savjan a ho jegpancella tomorodott a forgalom miatt.");
                sav.trafficCount = 0;
            }
        }
    }
}
