package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Alapvető takarítóeszköz, amely a csapadékot a szomszédos sávba tolja.
 */
public class SoproFej extends Fej {
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SoproFej.takaritHatas() meghivva.");
        s.hoAtsepreseJobbra();
    }
}
