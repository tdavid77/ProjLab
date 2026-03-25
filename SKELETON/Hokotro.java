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
        return true;
    }

    public void takaritSavot(Sav s) {
        System.out.println("Hokotro.takaritSavot() meghivva.");
        if(aktualisFej != null) {
            aktualisFej.takaritHatas(s, this);
        }
    }
}
