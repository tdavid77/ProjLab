package jarmuvek;
import terkep.Sav;
import skeletonprogram.SzkeletonProgram;

/**
 * A városban mozgó eszközök absztrakt fogalma.
 * Kezeli a helyzetet és a baleset miatti mozgásképtelenséget.
 */
public abstract class Jarmu {
    protected int mozgaskeptelenHatraLevoKor;
    protected String rendszam;
    protected Sav aktualisHelyzet;


    /**
     * Visszaadja a jármű aktuális helyzetét.
     * @return a jármű aktuális helyzete
     */
    public Sav getAktualisHelyzet() {
        SzkeletonProgram.logCall("j", "Jarmu", "getAktualisHelyzet", "");
        SzkeletonProgram.logReturn("Sav");
        return aktualisHelyzet;
    }

    /**
     * Lép a megadott célpontra.
     * @param cel a célpontra
     */
    public void lep(char cel) {
        SzkeletonProgram.logCall("j", "Jarmu", "lep", "cel");
        this.aktualisHelyzet = new Sav();
        System.out.println("A jarmu elindult a cel fele: " + cel);
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Baleset történik.
     */
    public void balesetezik() {
        SzkeletonProgram.logCall("j", "Jarmu", "balesetezik", "");
        mozgaskeptelenHatraLevoKor = 3;
        System.out.println("A jarmu balesetet szenvedett, mozgaskepetlen marad: " + mozgaskeptelenHatraLevoKor + " korig.");
        SzkeletonProgram.logReturn("void");
    }

    /**
     * Regisztrálja a járművet a körben.
     */
    public void korRegisztralasa() {
        SzkeletonProgram.logCall("j", "Jarmu", "korRegisztralasa", "");
        System.out.println("A jarmu korregisztralasa megtortent.");
        SzkeletonProgram.logReturn("void");
    }
}
