package jarmuvek;
import java.util.ArrayList;
import java.util.List;

/**
 * A város lakóit szállító önműködő (NPC) jármű.
 * Súlyával tömöríti a havat, elakadás esetén várakozik.
 */
public class Auto extends Jarmu {
    private char otthon;
    private char munkahely;
    private List<Character> utvonal = new ArrayList<>();

    @Override
    public void lep(char cel) {
        System.out.println("Auto.lep() meghivva.");
    }

    @Override
    public void balesetezik() {
        System.out.println("Auto.balesetezik() meghivva.");
    }
}
