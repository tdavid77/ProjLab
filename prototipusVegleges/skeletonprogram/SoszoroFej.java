package skeletonprogram;

public final class SoszoroFej extends Fej {
    public SoszoroFej() {
        super(FejTipus.SOSZOROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        if (h.so < 10) {
            throw new IllegalArgumentException("Nincs eleg so a soszorofejhez.");
        }
        h.so -= 10;
        sav.ho = 0;
        sav.ice = 0;
        sav.soHatralevoIdeje = 3;
        state.enqueueEvent("Soszoro fej aktiv: ho es jeg eltunt, 3 korig vedett a sav.");
    }
}
