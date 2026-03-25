package terkep;
import java.util.ArrayList;
import java.util.List;

import jarmuvek.Auto;
import jarmuvek.Jarmu;

/**
 * A város teljes térképét és működését átfogó rendszer motorja.
 * Felelőssége a játékmenet léptetése, az időjárás szimulálása és az autók navigálása.
 */
public class Uthalozat {
    private int jatekNehezsege;
    private char telephely;
    private List<Ut> utak = new ArrayList<>();
    private List<Auto> autok = new ArrayList<>();

    public void jatekInditasa(List<Character> csomopontok) {
        System.out.println("Uthalozat.jatekInditasa() meghivva.");
    }

    public List<Character> legrovidebbUt(char otthon, char munkahely) {
        System.out.println("Uthalozat.legrovidebbUt() meghivva.");
        return new ArrayList<>();
    }

    public void korInditasa() {
        System.out.println("Uthalozat.korInditasa() meghivva.");
    }

    public void havazas(int mennyiseg) {
        System.out.println("Uthalozat.havazas() meghivva.");
    }

    public boolean jatekVegeCheck() {
        System.out.println("Uthalozat.jatekVegeCheck() meghivva.");
        return false;
    }
}
