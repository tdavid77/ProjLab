package skeletonprogram;

/**
 * Szemelyautot modellezo jarmu, amely az alap Jarmu viselkedest hasznalja.
 */
public final class Auto extends Jarmu {
    public Auto(String name) {
        super(name);
    }

    @Override
    public String type() {
        return "Auto";
    }
}
