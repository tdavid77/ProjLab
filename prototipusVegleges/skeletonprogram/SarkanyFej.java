package skeletonprogram;

public final class SarkanyFej extends Fej {
    public SarkanyFej() {
        super(FejTipus.SARKANYFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        if (h.kerozin < 10) {
            throw new IllegalArgumentException("Nincs eleg kerozin a sarkanyfejhez.");
        }
        h.kerozin -= 10;
        sav.ho = 0;
        sav.ice = 0;
        sav.feltortJeg = 0;
        state.enqueueEvent("Sarkanyfej aktiv: azonnali olvasztas vegrehajtva.");
    }
}
