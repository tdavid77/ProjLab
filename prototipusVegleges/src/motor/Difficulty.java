package motor;

/**
 * A jatekmenet nehezsegi szintjet reprezentalo felsorolas.
 * Minden szinthez tartozik egy alap baleseti valoszinuseg (baseCrashChance),
 * amelyet a Jarmu.maybeCrash() a jeg-vastagság es zuzalek-mod ósitóval egyutt
 * hasznal a csuszasi balesetek kiszamolasahoz.
 * Ertekek: EASY (8%), MEDIUM (18%), HARD (32%).
 */
public enum Difficulty {
    EASY(0.08),
    MEDIUM(0.18),
    HARD(0.32);

    private final double baseCrashChance;

    Difficulty(double baseCrashChance) {
        this.baseCrashChance = baseCrashChance;
    }

    /** Visszaadja az ehhez a nehezsegi szinthez tartozo alap csuszasi valoszinuseget. */
    public double baseCrashChance() {
        return baseCrashChance;
    }

    /** Parszol egy bemeneti sztringet Difficulty ertekke (kis-nagybetu-fuggetlen). Null-t ad, ha ervenytelen. */
    public static Difficulty fromInput(String value) {
        for (Difficulty d : values()) {
            if (d.name().equalsIgnoreCase(value)) {
                return d;
            }
        }
        return null;
    }
}
