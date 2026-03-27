package jatekosok;
import jarmuvek.Jarmu;

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
        System.out.println("JatekosManager.mozgatJarmuvet() meghivva.");
    }
}
