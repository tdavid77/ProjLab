package takaritofejek;
import jarmuvek.Hokotro;
import terkep.Sav;

/**
 * A hókotrókra szerelhető, cserélhető munkaeszközök absztrakt fogalma.
 * Definiálja az egységes takarítási interfészt.
 */
public abstract class Fej {
    protected String azonosito;

    public abstract void takaritHatas(Sav s, Hokotro h);
}
