package takaritofejek;

import java.util.Locale;

/**
 * A tamogatott hokotrofejek tipusat sorolja fel.
 * Minden ertek egy konkret Fej alosztálynak felel meg:
 * SOPROFEJ=SoproFej, HANYOFEJ=HanyoFej, JEGTOROFEJ=JegtoroFej,
 * SARKANYFEJ=SarkanyFej, SOSZOROFEJ=SoszoroFej, ZUZOTTKOSZOROFEJ=ZuzottkoSzoroFej.
 * A FejFactory.create() metódus ez alapjan peldanyosit.
 */
public enum FejTipus {
    SOPROFEJ,
    HANYOFEJ,
    JEGTOROFEJ,
    SARKANYFEJ,
    SOSZOROFEJ,
    ZUZOTTKOSZOROFEJ;

    /** Parszol egy bemeneti sztringet FejTipus ertekke (kis-nagybetu-fuggetlen). Null-t ad, ha ervenytelen. */
    public static FejTipus fromInput(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (FejTipus type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        return null;
    }
}
