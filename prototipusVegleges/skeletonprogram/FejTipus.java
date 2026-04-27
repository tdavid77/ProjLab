package skeletonprogram;

import java.util.Locale;

/**
 * A tamogatott hokotrofejek tipusa.
 */
public enum FejTipus {
    SOPROFEJ,
    HANYOFEJ,
    JEGTOROFEJ,
    SARKANYFEJ,
    SOSZOROFEJ,
    ZUZOTTKOSZOROFEJ;

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
