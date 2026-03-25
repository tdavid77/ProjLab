package jarmuvek;
import terkep.Sav;

/**
 * A városban mozgó eszközök absztrakt fogalma.
 * Kezeli a helyzetet és a baleset miatti mozgásképtelenséget.
 */
public abstract class Jarmu {
    protected int mozgaskeptelenHatraLevoKor;
    protected String rendszam;
    protected Sav aktualisHelyzet;

    public void lep(char cel) {
        System.out.println("Jarmu.lep() meghivva.");
    }

    public void balesetezik() {
        System.out.println("Jarmu.balesetezik() meghivva.");
    }

    public void korRegisztralasa() {
        System.out.println("Jarmu.korRegisztralasa() meghivva (nincs hatas ennnel a jarmunel).");
    }
}
