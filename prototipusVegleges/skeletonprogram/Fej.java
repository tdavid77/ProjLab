package skeletonprogram;

/**
 * Hokotrokra szerelheto takaritofejek absztrakt alaposztalya.
 */
public abstract class Fej {
    private final FejTipus tipus;

    protected Fej(FejTipus tipus) {
        this.tipus = tipus;
    }

    public FejTipus tipus() {
        return tipus;
    }

    public abstract void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state);
}
