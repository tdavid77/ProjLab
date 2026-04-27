package skeletonprogram;

/**
 * Sopro fej, amely a havat a szomszedos sav fele tereli.
 */
public final class SoproFej extends Fej {
    public SoproFej() {
        super(FejTipus.SOPROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        int movedHo = sav.ho + sav.feltortJeg;
        sav.ho = 0;
        sav.feltortJeg = 0;
        int rightIndex = savIndex + 1;
        if (movedHo > 0 && rightIndex < ut.savSzam()) {
            Sav rightSav = ut.sav(rightIndex);
            rightSav.ho += movedHo;
        }
        state.enqueueEvent("Soprofej atterelte a csapadekot.");
    }
}
