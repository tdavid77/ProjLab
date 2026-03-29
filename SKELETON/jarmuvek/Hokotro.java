package jarmuvek;
import takaritofejek.Fej;
import terkep.Sav;
import skeletonprogram.SzkeletonProgram;

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
        SzkeletonProgram.logCall("h1", "Hokotro", "getAktualisFej", "");
        SzkeletonProgram.logReturn("Fej");
        return aktualisFej;
    }

    /**
     * Lép a megadott célpontra.
     * @param cel a célpontra
     */
    @Override
    public void lep(char cel) {
        SzkeletonProgram.logCall("h1", "Hokotro", "lep", "cel");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Baleset történik.
     */
    @Override
    public void balesetezik() {
        SzkeletonProgram.logCall("h1", "Hokotro", "balesetezik", "");
        SzkeletonProgram.logReturn("void");
    }

    
    /**
     * Beállítja a hókotrón aktuális fejét. Ha null értéket kap, akkor leszereli a fejet.
     * @param f a beállítandó fej
     */
    public void setAktualisFej(Fej f) {
        SzkeletonProgram.logCall("h1", "Hokotro", "setAktualisFej", "f");
        if (f != null) {
            aktualisFej = f;
        } else {
            aktualisFej = null;
        }
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Takarítja a havat a megadott sávban, ha fel van szerelve fejjel.
     * @param s a takarítandó sáv
     */
    public void takaritSavot(Sav s) {
        SzkeletonProgram.logCall("h1", "Hokotro", "takaritSavot", "s_akt");
        if(aktualisFej != null) {
            aktualisFej.takaritHatas(s, this);
        }
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Feltölti a hókotrón fejét sóval, ha van felszerelve fejjel.
     * @return a feltöltött só mennyisége
     */
    public int soToltesAktualisFejbe() {
        SzkeletonProgram.logCall("h1", "Hokotro", "soToltesAktualisFejbe", "");
        if (aktualisFej == null) {
            SzkeletonProgram.logReturn("0");
            return 0;
        }
        int ret = aktualisFej.soToltes(100);
        SzkeletonProgram.logReturn("int");
        return ret;
    }

    /**
     * Feltölti a hókotró fejét kerozinnal, ha van felszerelve fejjel.
     * @return a feltöltött kerozin mennyisége
     */
    public int kerozinToltesAktualisFejbe() {
        SzkeletonProgram.logCall("h1", "Hokotro", "kerozinToltesAktualisFejbe", "");
        if (aktualisFej == null) {
            SzkeletonProgram.logReturn("0");
            return 0;
        }
        int ret = aktualisFej.kerozinToltes(100);
        SzkeletonProgram.logReturn("int");
        return ret;
    }
}
