package skeletonprogram;

/**
 * Jeget toro fej, amely a jegreteget tisztithato ho-jellegu retegre alakitja.
 */
public final class JegtoroFej extends Fej {
    public JegtoroFej() {
        super(FejTipus.JEGTOROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        sav.feltortJeg += sav.ice;
        sav.ice = 0;
        sav.ho += sav.feltortJeg;
        sav.feltortJeg = 0;
        state.enqueueEvent("Jegtorofej feltorte a jeget, ho jellegu retegre valtott.");
    }
}
