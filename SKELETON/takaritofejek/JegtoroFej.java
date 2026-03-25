package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * A masszív jégpáncél fizikai feltörését végző fej.
 */
public class JegtoroFej extends Fej {
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("JegtoroFej.takaritHatas() meghivva.");
        s.jegFeltorese();
    }
}
