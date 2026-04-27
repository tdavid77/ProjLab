package skeletonprogram;

/**
 * Takarito jarmu, amely fejjel, so-kerozin-zuzottko keszlettel es tisztitasi muveletekkel rendelkezik.
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

    public void takaritSav(Ut ut, int savIndex, GameState state) {
        Sav sav = ut.sav(savIndex);
        if (aktivFej == null) {
            aktivFej = FejFactory.create(FejTipus.SOPROFEJ);
        }
        aktivFej.takaritHatas(this, sav, ut, savIndex, state);
    }

    public void fejCsere(Jatekos jatekos, FejTipus ujFej) {
        if (!jatekos.removeFejFromInventory(ujFej)) {
            throw new IllegalArgumentException("A kivant fej nincs a raktarban: " + ujFej.name());
        }
        if (aktivFej != null) {
            jatekos.addFejToInventory(aktivFej.tipus());
        }
        aktivFej = FejFactory.create(ujFej);
    }

    public void sotoltes(Jatekos jatekos) {
        int price = 50;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz sotolteshez.");
        }
        jatekos.charge(price);
        so = 100;
    }

    public void kerozintoltes(Jatekos jatekos) {
        int price = 60;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz kerozintolteshez.");
        }
        jatekos.charge(price);
        kerozin = 100;
    }

    public void zuzalektoltes(Jatekos jatekos) {
        int price = 40;
        if (!jatekos.canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz zuzalektolteshez.");
        }
        jatekos.charge(price);
        zuzottko = 100;
    }
}
