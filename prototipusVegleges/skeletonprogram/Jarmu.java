package skeletonprogram;

/**
 * Minden mozgo jarmu (Auto, Busz, Hokotro) kozos alaposztalya.
 * Tarolja a poziciot (aktualis ut neve, savindex), a tulajdonos (owner) nevet
 * es a baleseti varakozasi szamlalot (disabledTime).
 * A vegrehajtLepes() metodus vegzi az ut-szomszedossag-ellenorzest, a
 * baleseti szamitast (maybeCrash) es az alosztaly-specifikus hook meghivasat.
 * A canCrash() template method teszi lehetove, hogy bizonyos jarmuvek
 * (pl. a Hokotro) immunisak legyenek a jegcsuszasi balesetre.
 */
public class Jarmu implements NamedEntity {
    protected String name;
    protected String owner;
    protected String currentUt;
    protected int savIndex;
    protected int disabledTime;

    protected Jarmu(String name) {
        this.name = name;
        this.currentUt = null;
        this.savIndex = 0;
        this.disabledTime = 0;
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

    /** Atnevezes eseten frissiti a currentUt hivatkozast, ha az egyezik a regi ut nevevel. */
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
        if (currentUt == null) {
            pos = "Telephely";
        } else {
            pos = currentUt + ", " + savIndex;
        }
        String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
        return type() + " " + name + " | Poz:" + pos + " | Allapot:" + allapot;
    }

    /** Igaz, ha a jarmu mozgaskeptelensege nem all fenn (disabledTime == 0, nincs aktiv baleset). */
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

    /** Minden kor vegekor csokkenti a baleseti varakozasi szamlalot (disabledTime), ha aktiv. */
    @Override
    public void tickTime() {
        if (disabledTime > 0) {
            disabledTime -= 1;
        }
    }

    /** Alapból minden jármű elakad a hóban (a Hókotró ezt felülírja) */
    protected boolean canBeBlockedBySnow() {
        return true;
    }

    /**
     * A jarmut a megadott ut megadott savjara lepeti.
     * Ellenorzi, hogy a cel ut szomszedos-e a jelenlegivel, majd frissiti a poziciot,
     * meghivja a baleseti szamitast (maybeCrash) es az alosztaly hookot (onCelUtElerve).
     */
    public void vegrehajtLepes(Ut target, int targetSav, GameState state) {
        if (currentUt != null) {
            Ut current = state.getUt(currentUt);
            if (current == null) {
                throw new IllegalArgumentException("Nincs ilyen ut: " + currentUt);
            }
            boolean sharesNode = current.hasNode(target.nodeA) || current.hasNode(target.nodeB);
            if (!sharesNode && !current.name().equalsIgnoreCase(target.name())) {
                throw new IllegalArgumentException("A celut nem szomszedos a jelenlegi uttal.");
            }
        }

        // ÚJ LOGIKA: Ha a hó >= 20, a jármű elakad
        if (target.sav(targetSav).ho >= 20) {
            throw new IllegalArgumentException("A sav jarhatatlan a magas ho miatt, a lepes meghiusult.");
        }

        currentUt = target.name();
        savIndex = targetSav;

        maybeCrash(target.sav(targetSav), state);
        onCelUtElerve(target, state);
    }

    /**
     * Hook metodus: a cel ut elerese utan fut le a vegrehajtLepes() vegen.
     * Alosztaly felulirhatja egyedi viselkedes hozzaadasahoz (pl. Busz koreszamlalo novelese).
     */
    protected void onCelUtElerve(Ut target, GameState state) {
        // alapertelmezetten nincs extra logika
    }

    /**
     * Valoszinusegi alapon eldonti, hogy a jarmu megcsuszik-e a jeges savon.
     * A veszely merteket a nehezsegi szint, a jeg vastagasaga, es az esetleges
     * zuzalek-vedelem befolyasolja. Baleset eseten 2 korre kiesik a jarmu.
     */
    private void maybeCrash(Sav sav, GameState state) {
        if (!canCrash()) {
            return;
        }
        if (sav.ice <= 0) {
            return;
        }
        double modifier = sav.zuzalekHatralevoIdeje > 0 ? -0.12 : 0.0;
        double chance = Math.max(0.0, Math.min(0.95, state.difficulty.baseCrashChance() + (sav.ice * 0.03) + modifier));
        if (state.random.nextDouble() < chance) {
            disabledTime = 2;
            state.accidents += 1;
            state.enqueueEvent(name + " megcsuszott jeges savon, 2 korre kiesett.");
        }
    }
}
