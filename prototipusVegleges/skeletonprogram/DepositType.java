package skeletonprogram;

/**
 * A savokra elhelyezheto lerakodasok tipusat sorolja fel.
 * Hasznalata: a 'lerakodas' parancs bemeneti parameterekenel.
 * Minden ertekhez a Sav egy-egy mezo tartozik, amelyet az applyDeposit()
 * allitja be: HO (ho), JEG (ice), SO (soHatralevoIdeje),
 * FELTORTJEG (feltortJeg), ZUZALEK (zuzalekHatralevoIdeje).
 */
public enum DepositType {
    HO,
    JEG,
    SO,
    FELTORTJEG,
    ZUZALEK;

    /** Parszol egy bemeneti sztringet DepositType ertekke (kis-nagybetu-fuggetlen). Null-t ad, ha ervenytelen. */
    public static DepositType fromInput(String value) {
        for (DepositType t : values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        return null;
    }
}
