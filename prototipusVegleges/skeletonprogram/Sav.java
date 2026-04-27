package skeletonprogram;

/**
 * Egy ut egyetlen savjanak allapotat modellezi.
 * Nyilvantartja a hofeltoltest (ho), a jeg vastagságát (ice), a feltort jeget
 * (feltortJeg), es a so- (soHatralevoIdeje) valamint zuzalek-vedelem
 * (zuzalekHatralevoIdeje) hátralevo körszámát.
 * A tickTime() minden kor vegen csokkenti a vedelmek hatlralevo idejet.
 * A hoingOneUnit() az alagut-vedettseg es a so-vedelem figyelembevételevel
 * noveli a ho-szintet havazas soran.
 */
public final class Sav {
    /** A sav indexe az uton belul (0-tol savSzam-1-ig). */
    final int index;
    /** A savon levo ho mennyisege. Nagy ho elakasztja a buszokat. */
    int ho;
    /** A savon levo jeg vastagasaga; csuszasi veszelyt okoz. */
    int ice;
    /** Feltort jeg mennyisege (JegtoroFej altal kezelheto). */
    int feltortJeg;
    /** Soszoras vedelmnek hátralevo köreinek szama (0 = nem vedett). */
    int soHatralevoIdeje;
    /** Zuzalek-szoras csuszasveszely-csokkentesének hátralevo köreinek szama. */
    int zuzalekHatralevoIdeje;

    public Sav(int index) {
        this.index = index;
    }

    /** A megadott lerakodas-tipusnak megfelelo mezot beallitja a kapott ertekre. */
    public void applyDeposit(DepositType type, int value) {
        if (type == DepositType.HO) {
            ho = Math.max(0, value);
        }
        if (type == DepositType.JEG) {
            ice = Math.max(0, value);
        }
        if (type == DepositType.SO) {
            soHatralevoIdeje = Math.max(0, value);
        }
        if (type == DepositType.FELTORTJEG) {
            feltortJeg = Math.max(0, value);
        }
        if (type == DepositType.ZUZALEK) {
            zuzalekHatralevoIdeje = Math.max(0, value);
        }
    }

    /**
     * Egykor-nyi havazas hatasat alkalmazza a savra (ho += 1).
     * Alagut-vedelem vagy aktiv so-vedelem (soHatralevoIdeje > 0) eseten nem novel.
     */
    public void hoingOneUnit(boolean protectedByTunnel) {
        if (protectedByTunnel) {
            return;
        }
        if (soHatralevoIdeje > 0) {
            return;
        }
        ho += 1;
    }

    /** Minden kor vegekor csokkenti a so- es zuzalek-vedelem hátralevo idejet. */
    public void tickTime() {
        if (soHatralevoIdeje > 0) {
            soHatralevoIdeje -= 1;
        }
        if (zuzalekHatralevoIdeje > 0) {
            zuzalekHatralevoIdeje -= 1;
        }
    }

    /** Rovid szoveges leirast ad a savrol a konzolos kijelzo szamara. */
    public String describeCompact() {
        return "[Sav" + index + "(Ho:" + ho + ", Jeg:" + ice + ", Sozott:" + (soHatralevoIdeje > 0 ? "I" : "N") + ")]";
    }
}
