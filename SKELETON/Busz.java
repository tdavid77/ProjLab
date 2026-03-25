/**
 * Előre megadott végállomások között közlekedő eszköz.
 * Felelőssége a menetrend szerinti haladás és a bevétel generálása.
 */
public class Busz extends Jarmu {
    private int jaratSzam;
    private int korokSzama;

    @Override
    public void lep(char cel) {
        System.out.println("Busz.lep() meghivva.");
    }

    @Override
    public void balesetezik() {
        System.out.println("Busz.balesetezik() meghivva.");
    }

    public void korRegisztralasa() {
        System.out.println("Busz.korRegisztralasa() meghivva.");
    }
}
