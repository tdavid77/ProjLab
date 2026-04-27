package skeletonprogram;

public final class HanyoFej extends Fej {
    public HanyoFej() {
        super(FejTipus.HANYOFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        sav.ho = 0;
        sav.feltortJeg = 0;
        state.enqueueEvent("Hanyofej az ut melle tavolitotta a csapadekot.");
    }
}
