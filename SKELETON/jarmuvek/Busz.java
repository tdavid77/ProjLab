package jarmuvek;
/**
 * Előre megadott végállomások között közlekedő eszköz.
 * Felelőssége a menetrend szerinti haladás és a bevétel generálása.
 */
public class Busz extends Jarmu {
    private int jaratSzam;
    private int korokSzama;

    /**
     * Lép a megadott célpontra.
     * @param cel a célpontra
     */
    @Override
    public void lep(char cel) {
        System.out.println("Busz.lep() meghivva.");
    }

    /**
     * Baleset történik.
     */
    @Override
    public void balesetezik() {
        System.out.println("Busz.balesetezik() meghivva.");
    }

    /**
     * Növeli a megtett körök számát, ha a busz elérte valamelyik végállomását és kifizeti a sofőrt.
     */
    public void korRegisztralasa() {
        System.out.println("Busz.korRegisztralasa() meghivva.");
        
        System.out.println("Busz.korokSzama novelve.");
    }
}
