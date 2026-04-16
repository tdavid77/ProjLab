package jatekosok;
import jarmuvek.Jarmu;
import skeletonprogram.SzkeletonProgram;

/**
 * A szimulációban részt vevő aktív, döntéshozó entitás absztrakt szerepköre.
 * Kezeli a vagyont és irányítja a járműveket.
 */
public abstract class JatekosManager {
    protected String nev;
    protected int vagyon;

    /**
     * Mozgatja a megadott járművet a megadott célpontra.
     * @param cel a célpontra
     * @param j a jármű
     */
    public void mozgatJarmuvet(char cel, Jarmu j) {
        SzkeletonProgram.logCall("jm", "JatekosManager", "mozgatJarmuvet", "cel, j");
        SzkeletonProgram.logReturn("void");
    }

    public void setPenz(int osszeg) {
        this.vagyon = osszeg;
    }
}
