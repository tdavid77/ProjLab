package jarmuvek;
import takaritofejek.Fej;
import terkep.Sav;

/**
 * Takarító munkagép, amely a hó és a jég eltávolításáért felelős.
 * Fizikailag immunis a megcsúszásra és elakadásra.
 */
public class Hokotro extends Jarmu {
    private int ar;
    private Fej aktualisFej;

    public Fej getAktualisFej() {
        System.out.println("Hokotro.getAktualisFej() meghivva.");
        return aktualisFej;
    }


    @Override
    public void lep(char cel) {
        System.out.println("Hokotro.lep() meghivva.");
    }

    @Override
    public void balesetezik() {
        System.out.println("Hokotro immunis a balesetre.");
    }

    

    public void setAktualisFej(Fej f) {
        System.out.println("Hokotro.setAktualisFej() meghivva.");
        if (f != null) {
            aktualisFej = f;
        } else {
            aktualisFej = null;
        }
    }


    public void takaritSavot(Sav s) {
        System.out.println("Hokotro.takaritSavot() meghivva.");
        if(aktualisFej != null) {
            aktualisFej.takaritHatas(s, this);
        }
    }

    public int soToltesAktualisFejbe() {
        System.out.println("Hokotro.soToltesAktualisFejbe() meghivva.");
        if (aktualisFej == null) {
            return 0;
        }
        return aktualisFej.soToltes(100);
    }

    public int kerozinToltesAktualisFejbe() {
        System.out.println("Hokotro.kerozinToltesAktualisFejbe() meghivva.");
        if (aktualisFej == null) {
            return 0;
        }
        return aktualisFej.kerozinToltes(100);
    }
}
