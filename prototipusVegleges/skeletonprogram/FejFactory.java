package skeletonprogram;

/**
 * Gyari segedosztaly, amely fejtipusbol konkret Fej peldanyt hoz letre.
 */
public final class FejFactory {
    private FejFactory() {
    }

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
