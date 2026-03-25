/**
 * A szimulációban részt vevő aktív, döntéshozó entitás absztrakt szerepköre.
 * Kezeli a vagyont és irányítja a járműveket.
 */
public abstract class JatekosManager {
    protected String nev;
    protected int vagyon;

    public void mozgatJarmuvet(char cel, Jarmu j) {
        System.out.println("JatekosManager.mozgatJarmuvet() meghivva.");
    }
}
