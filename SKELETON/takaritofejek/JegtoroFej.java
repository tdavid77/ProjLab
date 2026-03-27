package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * A masszív jégpáncél fizikai feltörését végző fej.
 */
public class JegtoroFej extends Fej {
    /**
    * Végrehajtja az adott fej takarítási hatását a megadott sávon és hókotrón.
    * @param s a sav, amelyen a takarítás végrehajtásra kerül
    * @param h a hókotró, amely a takarítást végrehajtja
    */
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("JegtoroFej.takaritHatas() meghivva.");
    }
}
