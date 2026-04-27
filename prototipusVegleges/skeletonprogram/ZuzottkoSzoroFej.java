package skeletonprogram;

/**
 * Zuzottkot szoro fej, amely ideiglenesen csokkenti a csuszasveszelyt.
 */
public final class ZuzottkoSzoroFej extends Fej {
    public ZuzottkoSzoroFej() {
        super(FejTipus.ZUZOTTKOSZOROFEJ);
    }

    @Override
    public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
        if (h.zuzottko < 10) {
            throw new IllegalArgumentException("Nincs eleg zuzottko a zuzottkoszorofejhez.");
        }
        h.zuzottko -= 10;
        sav.zuzalekHatralevoIdeje = 3;
        state.enqueueEvent("Zuzottko kiszorva: csuszasveszely csokkent 3 korre.");
    }
}
