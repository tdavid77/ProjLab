package jarmuvek;

import motor.GameState;
import motor.NamedEntity;
import terkep.Sav;
import terkep.Ut;

/**
 * Minden mozgo jarmu (Auto, Busz, Hokotro) kozos alaposztalya.
 *
 * UJ POZIcioMODELL: a jarmu alapesetben egy CSOMOPONTON all (currentNode != null,
 * currentUt == null). Csak akkor "kerul le" az ut sav-jara, ha balesetet
 * szenvedett (currentNode == null, currentUt + savIndex jelolve, disabledTime > 0).
 *
 * A mozgas csomopontrol csomopontra tortenik a moveToNode() metoduson keresztul:
 * a jarmu egy szomszedos csomopontba lep at, kozben "atutazik" az osszekoto
 * uton (sav 0-n alapertelmezetten). Az utazas alatt aktivalodik a forgalmi
 * hatas (Auto/Busz savot tomorit) es a balesetkalkulacio (sav.ice > 0 esetén).
 *
 * Mozgaslimit: minden jarmu legfeljebb getMaxMovesPerTurn() lepest tehet egy
 * korben (a Hokotro 2-t, a Busz 1-et, az Auto -- mint NPC -- gyakorlatilag
 * korlátlanul). A korszamlalo a GameActions.endRound()-ban resetelodik.
 */
public class Jarmu implements NamedEntity {
    public String name;
    public String owner;

    /** A csomopont neve, ahol a jarmu eppen all (vagy null, ha balesetet szenvedett). */
    public String currentNode;
    /** Az ut neve, ahol a jarmu eppen "elakadt" baleset miatt (vagy null, ha csomoponton all). */
    public String currentUt;
    /** A sav indexe, ahol a jarmu elakadt (csak akkor jelentos, ha currentUt != null). */
    public int savIndex;
    /** Hany korre kell meg varakoznia a balesetes jarmunek (0 = mozgokepes). */
    public int disabledTime;
    /** Hany lepest tett a jarmu az aktualis korben. endRound()-nal reset 0-ra. */
    public int movesThisRound;
    /** A celcsomopont, ahova lepni probalt: baleset eseten ide kerul majd, ha kiszabadul. */
    public String moveTargetNode;

    protected Jarmu(String name) {
        this.name = name;
        this.currentNode = null;
        this.currentUt = null;
        this.savIndex = 0;
        this.disabledTime = 0;
        this.movesThisRound = 0;
        this.moveTargetNode = null;
    }

    @Override
    public Jarmu asJarmu() { return this; }

    /** Atnevezes eseten frissiti az owner hivatkozast, ha az egyezik a regi nevvel. */
    @Override
    public void relinkEntityName(String oldName, String newName) {
        if (oldName.equalsIgnoreCase(owner)) {
            owner = newName;
        }
    }

    /** Atnevezes eseten frissiti a currentUt es moveTargetNode hivatkozast. */
    @Override
    public void relinkUtName(String oldName, String newName) {
        if (oldName.equalsIgnoreCase(currentUt)) {
            currentUt = newName;
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void renameTo(String newName) {
        this.name = newName;
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Jarmu";
    }

    @Override
    public String statusLine(GameState state) {
        String pos;
        if (currentNode != null) {
            pos = "Csomopont:" + currentNode;
        } else if (currentUt != null) {
            pos = "Ut:" + currentUt + " sav:" + savIndex + " (elakadt)";
        } else {
            pos = "(ismeretlen)";
        }
        String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
        return type() + " " + name + " | " + pos + " | Allapot:" + allapot;
    }

    /** Igaz, ha a jarmu mozgaskeptelensege nem all fenn (disabledTime == 0). */
    public boolean canMove() {
        return disabledTime <= 0;
    }

    /**
     * Template method: megadja, hogy a jarmu balesetet szenvedhet-e jeges savon.
     * Alapertelmezetten igaz; Hokotro felulirja false-ra, mert takaritojarmukent immun.
     */
    protected boolean canCrash() {
        return true;
    }

    /** Alapból minden jármű elakad a hóban (a Hókotró ezt felülírja). */
    protected boolean canBeBlockedBySnow() {
        return true;
    }

    /**
     * Maximalis lepesek szama egy korben.
     * Default: 1 (busz). Hokotro felulirja 2-re, Auto NPC felulirja Integer.MAX_VALUE-ra.
     */
    public int getMaxMovesPerTurn() {
        return 1;
    }

    /**
     * RULE12-vel osszhangban: a baleset utan NEM auto-recoverel idoflyamattal,
     * a jarmu az utat csak akkor hagyja el, ha egy hokotro takaritja a sav-jat.
     * (lasd Hokotro.takaritSav).
     */
    @Override
    public void tickTime() {
        // Szandekosan ures: a baleseti kieses csak a Hokotro takarítassal oldhato fel.
    }

    /**
     * Csomopontrol csomopontra mozgas — automatikus sav-valasztassal (NPC autokhoz, CLI-hoz).
     * A laneIdx == -1 jelzi, hogy az elso nem-blokkolt sav valasztodjon ki.
     */
    public void moveToNode(String targetNode, GameState state) {
        moveToNode(targetNode, -1, state);
    }

    /**
     * UJ ELSODLEGES MOZGASI METODUS: csomopontrol csomopontra mozgas.
     * A laneIdx parameter megadja, hogy melyik savot kell hasznalni az atutazashoz:
     *   - laneIdx >= 0: a megadott sav (ervenyesseget, blokkoltsagat ellenorzi)
     *   - laneIdx == -1: az elso nem-blokkolt sav valasztodik (NPC-knek hasznos)
     *
     * @throws IllegalArgumentException ha a mozgas nem hajthato vegre.
     */
    public void moveToNode(String targetNode, int laneIdxRequested, GameState state) {
        if (!canMove()) {
            throw new IllegalArgumentException(name + " mozgaskeptelen meg " + disabledTime + " korig.");
        }
        if (currentNode == null) {
            throw new IllegalArgumentException(name + " jelenleg az uton elakadt, takarítas szukseges.");
        }
        if (movesThisRound >= getMaxMovesPerTurn()) {
            throw new IllegalArgumentException(name + " ebben a korben mar elerte a lepeshatart ("
                    + getMaxMovesPerTurn() + ").");
        }
        Ut road = state.roadBetween(currentNode, targetNode);
        if (road == null) {
            throw new IllegalArgumentException("Nincs kozvetlen ut '" + currentNode + "' es '" + targetNode + "' kozott.");
        }

        // Sav megkeresese (explicit vagy auto-pick)
        int laneIdx;
        if (laneIdxRequested >= 0) {
            if (laneIdxRequested >= road.savSzam()) {
                throw new IllegalArgumentException("Ervenytelen sav index: " + laneIdxRequested);
            }
            if (state.isLaneBlocked(road, laneIdxRequested)) {
                throw new IllegalArgumentException("A " + laneIdxRequested + ". sav le van zarva baleset miatt.");
            }
            laneIdx = laneIdxRequested;
        } else {
            laneIdx = -1;
            for (int i = 0; i < road.savSzam(); i++) {
                if (!state.isLaneBlocked(road, i)) {
                    laneIdx = i;
                    break;
                }
            }
            if (laneIdx < 0) {
                throw new IllegalArgumentException("Az ut osszes savja le van zarva baleset miatt.");
            }
        }

        Sav sav = road.sav(laneIdx);
        if (canBeBlockedBySnow() && sav.ho >= 20) {
            throw new IllegalArgumentException("A " + road.name() + " " + laneIdx + ". savja jarhatatlan a magas ho miatt.");
        }

        // Mozgas vegrehajtas — proper-case csomopont-nevet a road-tol kerunk
        // (igy biztosan az eredeti irasmoddal kerul a currentNode-ba, fuggetlenul attol,
        // hogy a hivo milyen kis/nagy betus formaban adta at a targetNode-ot)
        String properTargetNode = road.opposite(currentNode);
        if (properTargetNode == null) {
            properTargetNode = targetNode; // fallback
        }
        moveTargetNode = properTargetNode;
        currentNode = properTargetNode;
        movesThisRound++;

        // Forgalmi hatas (autok/buszok tomoritik a havat)
        onCsomopontElerve(road, laneIdx, state);

        // Csuszas-ellenorzes
        maybeCrashOnRoad(road, laneIdx, state);
    }

    /**
     * Hook a leszarmazottaknak: az adott uton es savon athaladasra reagalas.
     * Default: nincs hatas. Auto/Busz felulirja, hogy tomoritse a havat,
     * a Busz a vegallomas-bonus-t is itt kezeli.
     */
    protected void onCsomopontElerve(Ut traversedRoad, int laneIdx, GameState state) {
        // default: nincs muvelet
    }

    /**
     * Valoszinusegi alapon eldonti, hogy a jarmu megcsuszik-e a jeges savon.
     * Csuszas eseten a jarmu az uton "ott marad" (currentNode = null,
     * currentUt + savIndex beallitva, disabledTime = 2). Csak takaritas szabaditja ki.
     */
    private void maybeCrashOnRoad(Ut road, int laneIdx, GameState state) {
        if (!canCrash()) return;
        Sav sav = road.sav(laneIdx);
        if (sav.ice <= 0) return;
        double modifier = sav.zuzalekHatralevoIdeje > 0 ? -0.12 : 0.0;
        double chance = Math.max(0.0, Math.min(0.95, state.difficulty.baseCrashChance() + (sav.ice * 0.03) + modifier));
        if (state.random.nextDouble() < chance) {
            // Baleset! A jarmu lekerul a sav-ra es elakadtnak jelolodik.
            currentNode = null;
            currentUt = road.name();
            savIndex = laneIdx;
            disabledTime = 2;
            state.accidents += 1;
            state.enqueueEvent(name + " megcsuszott jeges savon (" + road.name() + " sav " + laneIdx + ").");
        }
    }

    /**
     * RETROKOMPATIBILIS adapter a regi CLI-hoz: a meglevo `lepes` parancs hivja.
     * Az uton tarolt celcsomopontot kiszamolja a jelenlegi csomopontbol es
     * delegalja a moveToNode-ra. A targetSav parameter most figyelmen kivul marad.
     */
    @Deprecated
    public void vegrehajtLepes(Ut target, int targetSav, GameState state) {
        if (currentNode == null) {
            throw new IllegalArgumentException(name + " jelenleg balesetes, nem lephet.");
        }
        String otherNode = target.opposite(currentNode);
        if (otherNode == null) {
            throw new IllegalArgumentException("Az ut nem szomszedos a jelenlegi csomoponttal: " + currentNode);
        }
        moveToNode(otherNode, state);
    }
}
