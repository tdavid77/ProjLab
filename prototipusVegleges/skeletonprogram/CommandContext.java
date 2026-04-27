package skeletonprogram;

public final class CommandContext {
    private final GameState state;
    private final String rawLine;

    public CommandContext(GameState state, String rawLine) {
        this.state = state;
        this.rawLine = rawLine;
    }

    public GameState state() {
        return state;
    }

    public String rawLine() {
        return rawLine;
    }
}
