package takaritofejek;

/**
 * Gyari segedosztaly: FejTipus enum-ertek alapjan letrehoz egy konkret Fej peldanyt.
 * Hasznalata: Hokotro konstruktoraban (kezdeti SOPROFEJ) es fevcserene l.
 * Null tipusargumentum eseten SoproFej-et ad, ismeretlen tipus eseten szinten.
 * A peldanyositas logikajat egyutt tartja, hogy ujabb fejtipusok hozzaadasakor
 * csak itt kelljen modositani.
 */
public final class FejFactory {
    private FejFactory() {
    }

    /**
     * Letrehoz egy uj Fej peldanyt a megadott fejtipus alapjan.
     * Null vagy ismeretlen tipus eseten SoproFej-et ad vissza.
     */
    public static Fej create(FejTipus type) {
        if (type == null) {
            return new SoproFej();
        }
        if (type == FejTipus.HANYOFEJ) {
            return new HanyoFej();
        }
        if (type == FejTipus.JEGTOROFEJ) {
            return new JegtoroFej();
        }
        if (type == FejTipus.SARKANYFEJ) {
            return new SarkanyFej();
        }
        if (type == FejTipus.SOSZOROFEJ) {
            return new SoszoroFej();
        }
        if (type == FejTipus.ZUZOTTKOSZOROFEJ) {
            return new ZuzottkoSzoroFej();
        }
        return new SoproFej();
    }
}
