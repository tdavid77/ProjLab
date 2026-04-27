package skeletonprogram;

/**
 * Zuzottkot (grittet) szoro hokotrofej: ideiglenes csuszasveszely-csokkentö vedelmet ad.
 * Egy hasznalat 10 egyseg zuzottkot fogyaszt. Ha nincs eleg (< 10), kivetelt dob.
 * A vedelem 3 korig tart (zuzalekHatralevoIdeje = 3), es -0.12 modositorat
 * ad a baleseti valoszinuseg szamitasakor.
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
