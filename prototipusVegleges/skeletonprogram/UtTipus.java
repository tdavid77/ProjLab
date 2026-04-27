package skeletonprogram;

/**
 * Utszakasz tipusa a palyan (normal, hid, alagut).
 */
public enum UtTipus {
    NORMAL,
    HID,
    ALAGUT;

    public static UtTipus fromInput(String value) {
        for (UtTipus t : values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        return null;
    }
}
