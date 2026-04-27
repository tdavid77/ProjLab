package skeletonprogram;

/**
 * Jatek nehezsegi szintje, amely az alap baleseti valoszinuseget adja.
 */
public enum Difficulty {
    EASY(0.08),
    MEDIUM(0.18),
    HARD(0.32);

    private final double baseCrashChance;

    Difficulty(double baseCrashChance) {
        this.baseCrashChance = baseCrashChance;
    }

    public double baseCrashChance() {
        return baseCrashChance;
    }

    public static Difficulty fromInput(String value) {
        for (Difficulty d : values()) {
            if (d.name().equalsIgnoreCase(value)) {
                return d;
            }
        }
        return null;
    }
}
