package skeletonprogram;

/**
 * Takarito jarmu: cserelheto fejjel, so-, kerozin- es zuzottko-keszlettel rendelkezik.
 * A szerelt fej (Fej peldany) donti el, milyen takaritasi muveletet vegez a savon
 * (sopres, jegbontas, olvasztas, sozes, zuzalekszovas).
 * Vasarlas, keszlettoltes es fejcsere csak telephelyen (currentUt == null) engedelyezett,
 * amit az akcio-reteg (GameActions) ellenőriz.
 * A canCrash() false-t ad vissza, igy jeges savon sem szenvedhet balesetet.
 */
public final class Hokotro extends Jarmu {
    private Fej aktivFej;
    int so;
    int kerozin;
    int zuzottko;

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

    //A Hókotró nem akad el a nagy hóban sem
    @Override
    protected boolean canBeBlockedBySnow() { return false; }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Hokotro";
    }

    @Override
    public String statusLine(GameState state) {
        String pos;
        if (currentUt == null) {
            pos = "Telephely";
        } else {
            pos = currentUt + ", " + savIndex;
        }
        String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
        String fejNev = aktivFej == null ? FejTipus.SOPROFEJ.name() : aktivFej.tipus().name();
        return "Hokotro " + name
            + " | Poz:" + pos
            + " | Fej:" + fejNev
            + " | Keszletek:[So:" + so + ", Kerozin:" + kerozin + ", Zuzottko:" + zuzottko + "]"
            + " | Allapot:" + allapot;
    }

    /** Visszaadja a hokotro aktualis ut-objektumat, vagy kivetelt dob, ha nincs uton. */
    public Ut aktualisUt(GameState state) {
        if (currentUt == null) {
            throw new IllegalArgumentException("A hokotro nincs uton, nincs mit takaritani.");
        }
        Ut ut = state.getUt(currentUt);
        if (ut == null) {
            throw new IllegalArgumentException("Nincs ilyen ut: " + currentUt);
        }
        return ut;
    }

    /**
     * A hokotro aktiv fejevel takaritja a megadott savindexű savot.
     * Ha nincs aktiv fej beallitva, alapertelmezetten SoproFej-jel dolgozik.
     */
    public void takaritSav(Ut ut, int savIndex, GameState state) {
        Sav sav = ut.sav(savIndex);
        if (aktivFej == null) {
            aktivFej = FejFactory.create(FejTipus.SOPROFEJ);
        }
        aktivFej.takaritHatas(this, sav, ut, savIndex, state);
    }

    /**
     * Kicsereli a hokotro aktiv fejét a jatekos raktaraban levo ujFej-re.
     * A regi fejet visszarakja a jatekos raktaraba. Kivetelt dob, ha nincs meg az uj fej.
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

    /** Feltolti a kerozin-keszletet 100-ra (ar: 60). Kivetelt dob, ha nincs eleg penz. */
    public void kerozintoltes(Jatekos jatekos) {
        int price = 60;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz kerozintolteshez.");
        }
        jatekos.charge(price);
        kerozin = 100;
    }

    /** Feltolti a zuzottko-keszletet 100-ra (ar: 40). Kivetelt dob, ha nincs eleg penz. */
    public void zuzalektoltes(Jatekos jatekos) {
        int price = 40;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz zuzalektolteshez.");
        }
        jatekos.charge(price);
        zuzottko = 100;
    }
}
