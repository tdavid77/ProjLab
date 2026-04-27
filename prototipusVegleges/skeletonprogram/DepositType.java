package skeletonprogram;

/**
 * Savokra kerulo lerakodasok tipusa a bemeneti nyelvben.
 */
public enum DepositType {
    HO,
    JEG,
    SO,
    FELTORTJEG,
    ZUZALEK;

    public static DepositType fromInput(String value) {
        for (DepositType t : values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        return null;
    }
}
