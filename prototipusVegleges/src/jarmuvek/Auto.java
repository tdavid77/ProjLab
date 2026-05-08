package jarmuvek;

import motor.GameState;
import terkep.Sav;
import terkep.Ut;

/**
 *  Önműködő (NPC) jármű, amely az otthona és a munkahelye között próbál eljutni a legrövidebb úton.
 *  Lépéseivel, súlyát felhasználva hozzájárul a hó jéggé tömörítéséhez. Kiemelt felelőssége a várakozási logika:
 *  ha az út elzáródik, nem tervez újra, nem fordul meg, hanem a legutolsó pontnál feltorlódva várakozik, amíg a hókotrók fel nem szabadítják az utat.
 *
 *  A 2. etap-ban hozzaadtuk az otthon, munkahely es npcStep() logikat: az NPC autok minden
 *  Kor vege gomb-osztanyomas utan egy lepest tesznek a celjuk fele a legrovidebb uton (BFS).
 *  Cel elerese eseten celt valtanak (otthon <-> munkahely).
 */
public final class Auto extends Jarmu {

    /** Az otthon csomopont neve (a varos egy resze). */
    public String otthon;
    /** A munkahely csomopont neve. */
    public String munkahely;
    /** A jelenlegi cel csomopont (otthon vagy munkahely). */
    private String aktualisCel;
    /** Az utolso csomopont, ahol az auto megfordult (a kovetkezo utvonalkereses kiindulasa). */
    private String utolsoCsomopont;

    public Auto(String name) {
        super(name);
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Auto";
    }

    /**
     * Beallitja az auto otthonat, munkahelyet es kezdo cel-csomopontjat.
     * A 'startNode' annak a csomopontnak a neve, ahol az auto eppen "befele" tart
     * (vagy ahova legutobb lepett); ez a graf-bejarasi kiindulopont.
     */
    public void setupRoute(String otthon, String munkahely, String startNode) {
        this.otthon = otthon;
        this.munkahely = munkahely;
        this.aktualisCel = munkahely; // alapertelmezett: munkaba megy
        this.utolsoCsomopont = startNode;
    }

    /** Visszaadja a jelenlegi celt (otthon vagy munkahely). */
    public String getAktualisCel() {
        return aktualisCel;
    }

    /**
     * Egy lepest hajt vegre az NPC auto a celja fele a legrovidebb uton (BFS).
     * Ha a celt elerte, celt valt (otthon <-> munkahely).
     * Ha a kovetkezo ut jarhatatlan (magas ho vagy baleseti torlasz), a vegen
     * az utolso csomopontnal "varakozik", azaz nem lep semmit.
     */
    public void npcStep(GameState state) {
        if (otthon == null || munkahely == null || aktualisCel == null) {
            return; // nincs cel beallitva
        }
        if (!canMove()) {
            return; // mar elakadt vagy balesetet szenvedett
        }

        // A jelenlegi 'pozicio' egy ut (currentUt) ket csomopontja kozott; az
        // utolsoCsomopont mondja meg, hogy melyik fele tartunk eppen.
        String currentNode = utolsoCsomopont;
        if (currentNode == null && currentUt != null) {
            // Ha valamiert nincs beallitva, valasszuk a tavolabbi vegpontot a celtol
            Ut ut = state.getUt(currentUt);
            if (ut != null) {
                currentNode = closerNodeToward(state, ut, aktualisCel);
            }
        }
        if (currentNode == null) return;

        // Ha mar a celban vagyunk, valtsunk celt
        if (currentNode.equalsIgnoreCase(aktualisCel)) {
            aktualisCel = aktualisCel.equalsIgnoreCase(munkahely) ? otthon : munkahely;
        }

        // Kovetkezo csomopont a legrovidebb uton
        String nextNode = state.nextStepToward(currentNode, aktualisCel);
        if (nextNode == null) return;

        // Megkeressuk a kettot osszekoto utat
        Ut nextRoad = state.roadBetween(currentNode, nextNode);
        if (nextRoad == null) return;

        // Lepes -- ha jarhatatlan, vegrehajtLepes() kivetelt dob, akkor varakozunk
        try {
            vegrehajtLepes(nextRoad, savIndex, state);
            utolsoCsomopont = nextNode;
        } catch (RuntimeException ex) {
            // A sav jarhatatlan -- nem lepunk, az auto a legutobbi csomopontnal "torlodik fel"
            state.enqueueEvent(name + " az utat nem tudja folytatni: " + ex.getMessage());
        }
    }

    /** Kivalasztja az ut ket vegpontja kozul azt, amelyik kozelebb van a celhoz. */
    private String closerNodeToward(GameState state, Ut ut, String goal) {
        int distA = state.shortestPath(ut.nodeA, goal).size();
        int distB = state.shortestPath(ut.nodeB, goal).size();
        if (distA == 0 && distB == 0) return ut.nodeA;
        if (distA == 0) return ut.nodeB;
        if (distB == 0) return ut.nodeA;
        return distA <= distB ? ut.nodeA : ut.nodeB;
    }

    //ÚJ LOGIKA: Hó tömörítése az áthaladáskor
    @Override
    protected void onCelUtElerve(Ut target, GameState state) {
        super.onCelUtElerve(target, state);
        Sav sav = target.sav(this.savIndex);

        //Hó tömörítése az áthaladáskor
        if (sav.ho > 0) {
            sav.trafficCount++;
            if (sav.trafficCount >= 5) {
                sav.ice += sav.ho;
                sav.ho = 0;
                state.enqueueEvent(target.name() + " " + this.savIndex + ". savjan a ho jegpancella tomorodott a forgalom miatt.");
                sav.trafficCount = 0; // Visszaállítjuk a számlálót
            }
        }
    }
}
