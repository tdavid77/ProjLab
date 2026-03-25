package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Só szórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
 */
public class SoszoroFej extends Fej {
    private boolean fejAllapota;
    private int sokeszlet;

    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SoszoroFej.takaritHatas() meghivva.");
        if (sokeszlet > 0) {
            s.soKihelyezese(3);
            s.hoEsJegAzonnaliOlvasztas();
            sokeszlet--;
        }
    }

    @Override
    public int soToltes(int mennyiseg) {
        System.out.println("SoszoroFej.soToltes() meghivva.");
        sokeszlet += mennyiseg;
        return sokeszlet;
    }
}
