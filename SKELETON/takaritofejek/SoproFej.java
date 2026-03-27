package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * Alapvető takarítóeszköz, amely a csapadékot a szomszédos sávba tolja.
 */
public class SoproFej extends Fej {
    /**
    * Végrehajtja az adott fej takarítási hatását a megadott sávon és hókotrón.
    * @param s a sav, amelyen a takarítás végrehajtásra kerül
    * @param h a hókotró, amely a takarítást végrehajtja
    */
    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SoproFej.takaritHatas() meghivva.");
    }
}
