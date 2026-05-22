package jarmuvek;

import jatekosok.Jatekos;
import motor.GameState;
import motor.NamedEntity;
import takaritofejek.Fej;
import takaritofejek.FejFactory;
import takaritofejek.FejTipus;
import terkep.Sav;
import terkep.Ut;
import motor.GameState;

/**
 * Takarito jarmu: cserelheto fejjel, so-, kerozin- es zuzottko-keszlettel rendelkezik.
 *
 * UJ MODELL: a hokotro is csomoponton all alapesetben. Korokent ket lepest tehet
 * (getMaxMovesPerTurn = 2). Mivel canCrash() es canBeBlockedBySnow() egyarant false-t
 * ad vissza, a hokotrot semmi nem tudja elakasztani vagy balesetbe keverni.
 *
 * A takaritSav() barmely (szomszedos) ut savjan elvegezhető, ha a hokotro a
 * csomoponton all - utana a sav uj allapota es az esetleg ott elakadt jarmuvek
 * kiszabadulasa is itt tortenik (RULE12).
 */
public final class Hokotro extends Jarmu {
    private Fej aktivFej;
    public int so;
    public int kerozin;
    public int zuzottko;

    public Hokotro(String name) {
        super(name);
        this.aktivFej = FejFactory.create(FejTipus.SOPROFEJ);
        this.so = 0;
        this.kerozin = 0;
        this.zuzottko = 0;
    }

    @Override
    public Hokotro asHokotro() { return this; }

    @Override
    protected boolean canCrash() { return false; }

    @Override
    protected boolean canBeBlockedBySnow() { return false; }

    /** Hokotro maximum 2 lepest tehet egy korben. */
    @Override
    public int getMaxMovesPerTurn() {
        return 2;
    }

    // Csak konzolos UI-hoz; lasd Jarmu osztaly kommentjet.
    @Override
    public String type() {
        return "Hokotro";
    }

    @Override
    public String statusLine(GameState state) {
        String pos;
        if (currentNode != null) {
            pos = "Csomopont:" + currentNode;
        } else if (currentUt != null) {
            pos = "Ut:" + currentUt + " sav:" + savIndex;
        } else {
            pos = "(ismeretlen)";
        }
        String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
        String fejNev = aktivFej == null ? FejTipus.SOPROFEJ.name() : aktivFej.tipus().name();
        return "Hokotro " + name
            + " | " + pos
            + " | Fej:" + fejNev
            + " | Keszletek:[So:" + so + ", Kerozin:" + kerozin + ", Zuzottko:" + zuzottko + "]"
            + " | Allapot:" + allapot;
    }

    /** Visszaadja a hokotrora szerelt aktiv fejet. */
    public Fej getAktivFej() {
        return aktivFej;
    }

    /** Beallitja a hokotrora szerelt aktiv fejet a megadott tipusra. */
    public void setAktivFej(FejTipus tipus) {
        this.aktivFej = FejFactory.create(tipus);
    }

    /**
     * A hokotro aktiv fejevel takaritja a megadott (szomszedos) ut megadott savjat,
     * majd ATKERUL az ut masik vegpontjara (mert "atutazza" az utat takaritas kozben).
     *
     * A takaritas mozgáslepésnek számít: mind a savot megtisztitja, mind a hokotrot
     * eljuttatja a masik csomopontra. A movesThisRound +1-et kap.
     *
     * Felteteli ellenorzesek:
     *  - Hokotro csomoponton all (currentNode != null)
     *  - Az ut szomszedos a hokotro csomopontjaval
     *  - Ervenyes savIndex
     *  - Van meg lepeskapcaitasa a korben (movesThisRound < max)
     *
     * RULE12: takaritas utan kiszabaditja az adott savon elakadt jarmuveket.
     */
    public void takaritSav(Ut ut, int savIndex, GameState state) {
        if (currentNode == null) {
            throw new IllegalArgumentException("A hokotro nincs csomoponton, nem tud takaritani.");
        }
        if (!ut.hasNode(currentNode)) {
            throw new IllegalArgumentException("Az ut nem szomszedos a hokotro csomopontjaval.");
        }
        if (savIndex < 0 || savIndex >= ut.savSzam()) {
            throw new IllegalArgumentException("Ervenytelen sav index: " + savIndex);
        }
        if (!canMove()) {
            throw new IllegalArgumentException(name + " mozgaskeptelen.");
        }
        if (movesThisRound >= getMaxMovesPerTurn()) {
            throw new IllegalArgumentException(name + " mar elerte a lepeshatart ebben a korben ("
                    + getMaxMovesPerTurn() + ").");
        }

        Sav sav = ut.sav(savIndex);
        if (aktivFej == null) {
            aktivFej = FejFactory.create(FejTipus.SOPROFEJ);
        }

        // 1) Takaritasi muvelet (lehet, hogy kivetelt dob, pl. nincs eleg so) -- ekkor a mozgás sem tortenik meg
        aktivFej.takaritHatas(this, sav, ut, savIndex, state);

        // 2) RULE12: az adott savon elakadt jarmuvek kiszabadulnak
        freeStuckVehiclesOnLane(ut, savIndex, state);


        // 3) Mozgás: a hokotro atvonul a masik csomopontra
        String targetNode = ut.opposite(currentNode);
        if (targetNode != null) {
            String oldNode = currentNode;
            moveTargetNode = targetNode;
            currentNode = targetNode;
            movesThisRound++;
            state.creditKassza(5);
            state.enqueueEvent(name + " a " + ut.name() + " takaritasaval atvonult " + oldNode + " -> " + targetNode);
        }

        
    }

    /**
     * Kiszabaditja az adott (ut, savIndex) parmegjeloleshez tartozó balesetezett
     * jarmuveket: visszaallitja a moveTargetNode-jukre, lenullazza a disabledTime-ot.
     */
    private void freeStuckVehiclesOnLane(Ut ut, int savIndex, GameState state) {
        for (NamedEntity entity : state.getAllEntities()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            if (v.currentNode != null) continue; // mar csomoponton, nem elakadt
            if (v.currentUt == null) continue;
            if (!v.currentUt.equalsIgnoreCase(ut.name())) continue;
            if (v.savIndex != savIndex) continue;
            if (v.disabledTime <= 0) continue;
            // Kiszabaditas
            String dest = v.moveTargetNode != null ? v.moveTargetNode : ut.nodeA;
            v.disabledTime = 0;
            v.currentNode = dest;
            v.currentUt = null;
            v.savIndex = 0;
            v.moveTargetNode = null;
            state.enqueueEvent(v.name + " kiszabadult a takaritas utan -> " + dest);
        }
    }

    /**
     * Kicsereli a hokotro aktiv fejét a jatekos raktaraban levo ujFej-re.
     * A regi fejet visszarakja a jatekos raktaraba.
     */
    public void fejCsere(Jatekos jatekos, FejTipus ujFej) {
        if (!jatekos.removeFejFromInventory(ujFej)) {
            throw new IllegalArgumentException("A kivant fej nincs a raktarban: " + ujFej.name());
        }
        if (aktivFej != null) {
            jatekos.addFejToInventory(aktivFej.tipus());
        }
        aktivFej = FejFactory.create(ujFej);
    }

    /** Feltolti a so-keszletet 100-ra (ar: 50). Kivetelt dob, ha nincs eleg penz. */
    public void sotoltes(Jatekos jatekos) {
        int price = 50;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz sotolteshez.");
        }
        jatekos.charge(price);
        so = 100;
    }

    /** Feltolti a kerozin-keszletet 100-ra (ar: 60). */
    public void kerozintoltes(Jatekos jatekos) {
        int price = 60;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz kerozintolteshez.");
        }
        jatekos.charge(price);
        kerozin = 100;
    }

    /** Feltolti a zuzottko-keszletet 100-ra (ar: 40). */
    public void zuzalektoltes(Jatekos jatekos) {
        int price = 40;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz zuzalektolteshez.");
        }
        jatekos.charge(price);
        zuzottko = 100;
    }
}
