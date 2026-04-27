package skeletonprogram;

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
