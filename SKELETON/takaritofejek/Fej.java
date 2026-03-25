package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * A hókotrókra szerelhető, cserélhető munkaeszközök absztrakt fogalma.
 * Definiálja az egységes takarítási interfészt.
 */
public abstract class Fej {
    protected String azonosito;
    protected boolean fejAllapota;

    public void fejKiBeKapcsolasa(boolean fejAllapota) {
        System.out.println("Nem tortent semmi, mert nem fogyoeszkozos fejt kapcsoltal.");
    }

    public abstract void takaritHatas(Sav s, Hokotro h);

    public int soToltes(int mennyiseg) {
        System.out.println("Fej.soToltes() meghivva (ez a fej nem tamogat so toltest).");
        return 0;
    }

    public int kerozinToltes(int mennyiseg) {
        System.out.println("Fej.kerozinToltes() meghivva (ez a fej nem tamogat kerozin toltest).");
        return 0;
    }
}
