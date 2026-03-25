package jarmuvek;
import takaritofejek.Fej;
import takaritofejek.SarkanyFej;
import takaritofejek.SoszoroFej;
import terkep.Sav;

/**
 * Takarító munkagép, amely a hó és a jég eltávolításáért felelős.
 * Fizikailag immunis a megcsúszásra és elakadásra.
 */
public class Hokotro extends Jarmu {
    private int ar;
    private Fej aktualisFej;

    @Override
    public void lep(char cel) {
        System.out.println("Hokotro.lep() meghivva.");
    }

    @Override
    public void balesetezik() {
        System.out.println("Hokotro immunis a balesetre.");
    }

    public boolean fejKiBeKapcsolas(Fej f) {
        System.out.println("Hokotro.fejKiBeKapcsolas() meghivva.");
        aktualisFej = f;
        return true;
    }

    public void fejjelTakarit(Sav s, Fej f) {
        System.out.println("Hokotro.fejjelTakarit() meghivva.");
        fejKiBeKapcsolas(f);
        takaritSavot(s);
    }

    public void takaritSavot(Sav s) {
        System.out.println("Hokotro.takaritSavot() meghivva.");
        if(aktualisFej != null) {
            aktualisFej.takaritHatas(s, this);
        }
    }

    public int soToltesAktualisFejbe() {
        System.out.println("Hokotro.soToltesAktualisFejbe() meghivva.");
        if (aktualisFej instanceof SoszoroFej) {
            return ((SoszoroFej) aktualisFej).soToltes(100);
        }
        return 0;
    }

    public int kerozinToltesAktualisFejbe() {
        System.out.println("Hokotro.kerozinToltesAktualisFejbe() meghivva.");
        if (aktualisFej instanceof SarkanyFej) {
            return ((SarkanyFej) aktualisFej).kerozinToltes(100);
        }
        return 0;
    }
}
