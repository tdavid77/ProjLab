package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Nagyteljesítményű eszköz, amely a havat és jeget véglegesen eltávolítja a pályáról.
 */
public class HanyoFej extends Fej {
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("HanyoFej.takaritHatas() meghivva.");
    }
}
