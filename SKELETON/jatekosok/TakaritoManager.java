package jatekosok;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import takaritofejek.Fej;

/**
 * A hókotró flottát üzemeltető menedzser.
 * Felelőssége a gépek karbantartása, vásárlása és mozgatása.
 */
public class TakaritoManager extends JatekosManager {
    private List<Hokotro> hokotrok = new ArrayList<>();
    private List<Fej> fejek = new ArrayList<>();

    @Override
    public void mozgatJarmuvet(char cel, Jarmu j) {
        System.out.println("TakaritoManager.mozgatJarmuvet() meghivva.");
    }

    public boolean hokotroVasarlas(Hokotro h) {
        System.out.println("TakaritoManager.hokotroVasarlas() meghivva.");
        Scanner s = new Scanner(System.in);

        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();
        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return false;
        }

        System.out.println("Van eleg vagyonod? (I/N)");
        String vagyonValasz = s.nextLine().trim();
        if (!vagyonValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nincs eleg vagyon.");
            return false;
        }

        hokotrok.add(h);
        System.out.println("Uj hokotro hozzaadva a takarito manager hokotro listajahoz.");
        return true;
    }

    public boolean fejVasarlas(Fej k) {
        System.out.println("TakaritoManager.fejVasarlas() meghivva.");
        Scanner s = new Scanner(System.in);

        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();
        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return false;
        }

        System.out.println("Van eleg vagyonod? (I/N)");
        String vagyonValasz = s.nextLine().trim();
        if (!vagyonValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nincs eleg vagyon.");
            return false;
        }

        fejek.add(k);
        System.out.println("Uj fej hozzaadva a takarito manager fejek listajahoz.");
        return true;
    }

    public boolean fejLevetel(Hokotro h) {
        System.out.println("TakaritoManager.fejLevetel() meghivva.");
        return true;
    }

    public boolean fejFelteves(Hokotro h, Fej f) {
        System.out.println("TakaritoManager.fejFelteves() meghivva.");
        return true;
    }

    public boolean fejCsere(Hokotro h, Fej ujFej) {
        System.out.println("TakaritoManager.fejCsere() meghivva.");
        System.out.println("A hókotró telephelyen van? (I/N)");
        Scanner s = new Scanner(System.in);
        String valasz = s.nextLine().trim();

        if (!valasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert a hokotro nincs a telephelyen.");
            return false;
        }

        Fej regiFej = h.getAktualisFej();
        boolean sikeresLevetel = fejLevetel(h);
        if (!sikeresLevetel) {
            return false;
        }

        if (regiFej != null) {
            fejek.add(regiFej);
        }
        fejek.remove(ujFej);

        return fejFelteves(h, ujFej);
    }

    public int soToltes(Hokotro h) {
        System.out.println("TakaritoManager.soToltes() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();

        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return 0;
        }

        return h.soToltesAktualisFejbe();
    }

    public int kerozinToltes(Hokotro h) {
        System.out.println("TakaritoManager.kerozinToltes() meghivva.");
        Scanner s = new Scanner(System.in);
        System.out.println("Telephelyen vagy? (I/N)");
        String telephelyValasz = s.nextLine().trim();

        if (!telephelyValasz.equalsIgnoreCase("I")) {
            System.out.println("A muvelet nem vegezheto el, mert nem telephelyen vagy.");
            return 0;
        }

        return h.kerozinToltesAktualisFejbe();
    }
}
