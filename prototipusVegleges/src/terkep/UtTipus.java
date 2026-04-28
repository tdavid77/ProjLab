package terkep;

/**
 * Utszakasz tipusat reprezentalo felsorolas.
 * Az uttipus befolyasolja a havazas viselkedeset:
 * ALAGUT - vedett, nem halmozodik ho rajta;
 * HID - normal havazasnak kitett, de tobblet-keresi veszelyt hordoz;
 * NORMAL - standard viselkedes.
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
