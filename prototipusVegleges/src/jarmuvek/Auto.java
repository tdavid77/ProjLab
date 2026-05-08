package jarmuvek;

import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 * Önműködő (NPC) jármű, amely az otthona és a munkahelye között próbál
 * eljutni a legrövidebb úton.
 *
 * UJ MODELL: az Auto a currentNode-on all alapesetben. A npcStep() egy lepest
 * tesz a celja fele a legrovidebb uton (BFS) a moveToNode-on keresztul.
 * Cel elerese eseten celt valt (otthon ↔ munkahely).
 *
 * Baleset eseten a jarmu az uton elakad (a Jarmu.maybeCrashOnRoad logikaja szerint),
 * es csak egy hokotro takaritasaval szabadul (RULE12).
 */
public final class Auto extends Jarmu {

    /** Az otthon csomopont neve. */
    public String otthon;
    /** A munkahely csomopont neve. */
    public String munkahely;
    /** A jelenlegi cel csomopont (otthon vagy munkahely). */
    private String aktualisCel;

    public Auto(String name) {
        super(name);
    }

    // Csak konzolos UI-hoz; lasd Jarmu osztaly kommentjet.
    @Override
    public String type() {
        return "Auto";
    }

    /**
     * NPC autoknak gyakorlatilag nincs lepeshatara: a npcStep() sajat maga
     * mar csak egyszer hivja a moveToNode-ot kor vegen, igy elegendo nagy ertek.
     */
    @Override
    public int getMaxMovesPerTurn() {
        return Integer.MAX_VALUE;
    }

    /**
     * Beallitja az auto otthonat, munkahelyet es kezdo csomopontjat.
     */
    public void setupRoute(String otthon, String munkahely, String startNode) {
        this.otthon = otthon;
        this.munkahely = munkahely;
        this.aktualisCel = munkahely; // alapertelmezett: munkaba megy
        this.currentNode = startNode;
        this.currentUt = null;
    }

    /** Visszaadja a jelenlegi celt (otthon vagy munkahely). */
    public String getAktualisCel() {
        return aktualisCel;
    }

    /**
     * Egy lepest hajt vegre az NPC auto a celja fele a legrovidebb uton (BFS).
     * Ha a celt elerte, celt valt (otthon ↔ munkahely).
     * Ha az ut jarhatatlan (magas ho, lezart sav), a kort kihagy ja az auto
     * (a csomoponton "torlodva" marad).
     */
    public void npcStep(GameState state) {
        if (otthon == null || munkahely == null || aktualisCel == null) return;
        if (!canMove()) return;
        if (currentNode == null) return; // baleset utan elakadva

        // Cel elerese -> valts
        if (currentNode.equalsIgnoreCase(aktualisCel)) {
            aktualisCel = aktualisCel.equalsIgnoreCase(munkahely) ? otthon : munkahely;
        }

        String nextNode = state.nextStepToward(currentNode, aktualisCel);
        if (nextNode == null) return;

        try {
            moveToNode(nextNode, state);
        } catch (RuntimeException ex) {
            state.enqueueEvent(name + " nem tud lepni: " + ex.getMessage());
        }
    }

    /**
     * Forgalmi hatas: a hot tomoriti, ha a sávon mar 5 jarmu áthaladt -> jegges.
     */
    @Override
    protected void onCsomopontElerve(Ut traversedRoad, int laneIdx, GameState state) {
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
