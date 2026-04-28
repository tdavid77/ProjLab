package motor;

/**
 * Egy parancs vegrehajtasahoz szükseges kontextus-objektum.
 * Egyutt adja at a jatekallapotot (GameState) es a nyers bemeneti sort,
 * igy a CommandHandler implementaciok mindket informaciohoz hozzafernek
 * anelkul, hogy tobb parameterrel kellene hivni a metódusokat.
 */
public final class CommandContext {
    private final GameState state;
    private final String rawLine;

    public CommandContext(GameState state, String rawLine) {
        this.state = state;
        this.rawLine = rawLine;
    }

    /** Visszaadja a parancs vegrehajtasakor ervenyes jatekallapotot. */
    public GameState state() {
        return state;
    }

    /** Visszaadja a nyers, tokenizalás előtti bemeneti sort (hibakijelzeshez hasznos). */
    public String rawLine() {
        return rawLine;
    }
}
