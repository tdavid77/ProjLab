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

    /**
     * Visszaadja a hókotrón aktuálisan felszerelt fejet.
     * @return
     */
    public Fej getAktualisFej() {
        System.out.println("Hokotro.getAktualisFej() meghivva.");
        return aktualisFej;
    }

    /**
     * Lép a megadott célpontra.
     * @param cel a célpontra
     */
    @Override
    public void lep(char cel) {
        System.out.println("Hokotro.lep() meghivva.");
    }

    /**
     * Baleset történik.
     */
    @Override
    public void balesetezik() {
        System.out.println("Hokotro immunis a balesetre.");
    }

    
    /**
     * Beállítja a hókotrón aktuális fejét. Ha null értéket kap, akkor leszereli a fejet.
     * @param f a beállítandó fej
     */
    public void setAktualisFej(Fej f) {
        System.out.println("Hokotro.setAktualisFej() meghivva.");
        if (f != null) {
            aktualisFej = f;
        } else {
            aktualisFej = null;
        }
    }

    /**
     * Takarítja a havat a megadott sávban, ha fel van szerelve fejjel.
     * @param s a takarítandó sáv
     */
    public void takaritSavot(Sav s) {
        System.out.println("Hokotro.takaritSavot() meghivva.");
        if(aktualisFej != null) {
            aktualisFej.takaritHatas(s, this);
        }
    }

    /**
     * Feltölti a hókotrón fejét sóval, ha van felszerelve fejjel.
     * @return a feltöltött só mennyisége
     */
    public int soToltesAktualisFejbe() {
        System.out.println("Hokotro.soToltesAktualisFejbe() meghivva.");
        if (aktualisFej == null) {
            return 0;
        }
        return aktualisFej.soToltes(100);
    }

    /**
     * Feltölti a hókotró fejét kerozinnal, ha van felszerelve fejjel.
     * @return a feltöltött kerozin mennyisége
     */
    public int kerozinToltesAktualisFejbe() {
        System.out.println("Hokotro.kerozinToltesAktualisFejbe() meghivva.");
        if (aktualisFej == null) {
            return 0;
        }
        return aktualisFej.kerozinToltes(100);
    }
}
