package skeletonprogram;

/**
 * A Szkeleton fő programja, amely belépési pontként szolgál.
 * Ebből a fájlból tesztelhető a modell alapvető működése.
 */

import java.util.Scanner;
import jarmuvek.Auto;
import jarmuvek.Busz;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import jatekosok.BuszsoforManager;
import jatekosok.TakaritoManager;
import takaritofejek.HanyoFej;
import takaritofejek.JegtoroFej;
import takaritofejek.SarkanyFej;
import takaritofejek.SoproFej;
import takaritofejek.SoszoroFej;
import terkep.Sav;
import terkep.Uthalozat;

public class SzkeletonProgram {
    
    private static int depth = 0;

    /**
     * Kiírja a megfelelő tabulátorokat a logoláshoz.
     */
    public static void printIndent() {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }

    /**
     * Logolja a metódus hívását, növelve a behívási mélységet.
     * @param callerId A hívó azonosítója, amely megjelenik a logban (pl. osztály vagy objektum neve).
     * @param className A hívott osztály neve.
     * @param methodName A hívott metódus neve.
     * @param params A hívott metódus paramétereinek leírása, amely megjelenik a logban.
     */
    public static void logCall(String callerId, String className, String methodName, String params) {
        printIndent();
        System.out.println("-> " + callerId + ": " + className + "." + methodName + "(" + params + ")");
        depth++;
    }

    /**
     * Logolja a metódus visszatérését, csökkentve a behívási mélységet.
     * @param returnDesc A visszatérés leírása, amely megjelenik a logban.
     */
    public static void logReturn(String returnDesc) {
        depth--;
        printIndent();
        System.out.println("<- " + returnDesc);
    }

    public static void main(String[] args) {
        System.out.println("Szkeleton program inditasa...");
        Scanner scanner = new Scanner(System.in);
        int valasztas;
        do {
            System.out.println("1 - Hókotró fejének cserélése");
            System.out.println("2 - Új hókotró vásárlása");
            System.out.println("3 - Új fej vásárlása");
            System.out.println("4 - Busz haladasa es fizetes");
            System.out.println("5 - Sarkany fej mukodese");
            System.out.println("6 - Hanyo fej mukodese");
            System.out.println("7 - Soszoro fej mukodese");
            System.out.println("8 - Sopro fej mukodese");
            System.out.println("9 - Jegtoro fej mukodese");
            System.out.println("10 - Jatek vege check (egyesitett)");
            System.out.println("12 - Jarmu megcsuszasa");
            System.out.println("13 - Havazas szimulacioja");
            System.out.println("14 - So toltese a soszoro fejbe");
            System.out.println("15 - Biokerozin toltese a sarkany fejbe");
            System.out.println("16 - Auto athaladasa es jegge tomorules");
            System.out.println("17 - Leszort so");
            System.out.println("0 - Kilépés");
            System.out.print("Válassz egy opciót: ");
            valasztas = scanner.nextInt();
            switch (valasztas) {
                case 1:
                    testFejCsere();
                    break;
                case 2:
                    testHokotroVasarlas();
                    break;
                case 3:
                    testFejVasarlas();
                    break;
                case 4:
                    buszHaladasEsFizetesTeszt();
                    break;
                case 5:
                    sarkanyFejMukodeseTeszt();
                    break;
                case 6:
                    hanyoFejMukodeseTeszt();
                    break;
                case 7:
                    soszoroFejMukodeseTeszt();
                    break;
                case 8:
                    soproFejMukodeseTeszt();
                    break;
                case 9:
                    jegtoroFejMukodeseTeszt();
                    break;
                case 10:
                    jatekVegeCheckTeszt();
                    break;
                case 11:
                    jatekVegeCheckTeszt();
                    break;
                case 12:
                    jarmuMegcsuszasaTeszt();
                    break; 
                case 13:
                    havazasSzimulacioTeszt();
                    break;
                case 14:
                    soTolteseTeszt();
                    break;
                case 15:
                    kerozinTolteseTeszt();
                    break;
                case 16:
                    autoAthaladasEsJeggeTomorulesTeszt();
                    break;
                case 17:
                    leszortSoTeszt();
                    break;
                case 0:
                    System.out.println("Kilépés a programból.");
                    break;
                default:
                    System.out.println("Ismeretlen teszt sorszam.");
                    break;
            }
        } while (valasztas != 0);
        scanner.close();
    }
    
    /**
     * 1. Use case: Hókotró takarítófejének cserélése
     * A menedzser a telephelyen leszereli a régi fejet, majd feltesz egy újat.
     */
    public static void testFejCsere() {
        System.out.println("\n1. UseCase: Hókotró takarítófejének cseréje");
        
        // Szükséges objektumok létrehozása
        TakaritoManager manager = new TakaritoManager();
        Hokotro hokotro = new Hokotro();
        SarkanyFej ujFej = new SarkanyFej(); // Az új fej, amit fel akarunk szerelni
        manager.fejCsere(hokotro, ujFej);

        System.out.println("1. UseCase vége\n");

    }

    /**
     * 2. Use case: Új hókotró vásárlása
     * A játékos (menedzser) új hókotrót vásárol a vagyona terhére.
     */
    public static void testHokotroVasarlas() {
        System.out.println("\n2. UseCase: Új hókotró vásárlása");
        // Szükséges objektumok létrehozása
        TakaritoManager manager = new TakaritoManager();
        Hokotro ujHokotro = new Hokotro(); // A megvásárolni kívánt új gép
        // Hókotró megvásárlása (a szkeletonban ellenőrzi a feltételeket és levonja a pénzt)
        manager.hokotroVasarlas(ujHokotro);
        System.out.println("2. UseCase vége\n");
    }

    /**
     * 3. Use case: Új fej vásárlása
     * A menedzser új speciális takarítóeszközt vásárol (pl. Sószóró fej).
     */
    public static void testFejVasarlas() {
        System.out.println("\n3. UseCase: ÚJ fej vásárlása");
        
        // Szükséges objektumok létrehozása
        TakaritoManager manager = new TakaritoManager();
        SoszoroFej ujSoszoroFej = new SoszoroFej(); // A megvásárolni kívánt speciális fej
        
        // Fej megvásárlása (a szkeletonban ellenőrzi a pénzt és raktárba helyezi az eszközt)
        manager.fejVasarlas(ujSoszoroFej);
        
        System.out.println("3. UseCase vége\n");
    }


    /**
     * 4. Use case: Busz haladása és fizetés
     * A busz játékos kezdeményezi a busz léptetését az úthálózaton. 
     * A jármű a megadott cél felé halad, miközben a rendszer ellenőrzi, hogy az adott kereszteződés egy Végállomás-e.
     * Amennyiben a busz végállomásra ér, a program regisztrálja a megtett kört,
     * növeli a teljesített utak számát, és a játékos vagyona gyarapodik a menetdíjból származó bevétellel.
     */
    private static void buszHaladasEsFizetesTeszt() {
        System.out.println("\n4. UseCase: Busz haladása es fizetés");
        BuszsoforManager buszsoforManager = new BuszsoforManager();
        Busz busz = new Busz();
        char celKeresztezodes = 'V';

        buszsoforManager.mozgatJarmuvet(celKeresztezodes, busz);

        System.out.println("4. UseCase vége\n");
    }

    /**
     * 5. Use case: Hókotró sárkányfejének működése
     * A játékos bekapcsolja a hókotró fejét, majd a gép biokerozin fogyasztása közben leszedi a havat és a jeget is a sávról.
     */
    private static void sarkanyFejMukodeseTeszt() {
        System.out.println("\n5. UseCase: Sárkány fej működése");
        Hokotro hokotro = new Hokotro();
        Sav sav = new Sav();
        SarkanyFej sarkanyFej = new SarkanyFej();
        hokotro.setAktualisFej(sarkanyFej);
        hokotro.takaritSavot(sav);
        sarkanyFej.fejKiBeKapcsolasa(true);
        System.out.println("5. UseCase vége\n");
    }

    /**
     * 6. Use case: Hányó fej működése
     * A játékos a hányófejjel felszerelt hókotróval halad végig az adott sávon
     * A hányófej a jeget nem takarítja le, az a sávon marad, viszont a havat felszedi és az út szélére hajítja. 
     */
    private static void hanyoFejMukodeseTeszt() {
        System.out.println("\n6. UseCase: Hányó fej működése");
        Hokotro hokotro = new Hokotro();
        Sav sav = new Sav();
        HanyoFej hanyoFej = new HanyoFej();
        hokotro.setAktualisFej(hanyoFej);
        hokotro.takaritSavot(sav);
        hanyoFej.fejKiBeKapcsolasa(true);
        System.out.println("6. UseCase vége\n");
    }


    /**
     * 7. Use case: Sószóró fej működése
     * A játékos a sószórófejjel felszerelt hókotróval halad végig az adott sávon
     * Ez a fej működés közben csökkenti a gép sókészletét, és a sávhoz egy speciális só-objektumot rendel. 
     * Ez az anyag nemcsak felolvasztja a meglévő havat és jeget,
     * hanem pár körön keresztül megakadályozza az újabb csapadék megmaradását is az adott sávon. 
     */
    private static void soszoroFejMukodeseTeszt() {
        System.out.println("\n7. UseCase: Sószóró fej működése");
        Hokotro hokotro = new Hokotro();
        Sav sav = new Sav();
        SoszoroFej soszoroFej = new SoszoroFej();
        hokotro.setAktualisFej(soszoroFej);
        hokotro.takaritSavot(sav);
        soszoroFej.fejKiBeKapcsolasa(true);
        System.out.println("7. UseCase vége\n");
    }

    /**
     * 8. Use case: Söprő fej működése
     * A játékos a söprőfejjel felszerelt hókotróval halad végig az adott sávon
     * Ez a fej a havat és a feltört jeget a menetirány szerinti jobb oldali szomszédos sávba továbbítja. 
     * Ha a gép a legszélső sávban halad, a csapadék az út mellé kerül, ahol már nem akadályozza tovább a közlekedést.
     */
    private static void soproFejMukodeseTeszt() {
        System.out.println("\n8. UseCase: Söprő fej működése");
        Hokotro hokotro = new Hokotro();
        Sav sav = new Sav();
        SoproFej soproFej = new SoproFej();
        hokotro.setAktualisFej(soproFej);
        hokotro.takaritSavot(sav);
        soproFej.fejKiBeKapcsolasa(true);
        System.out.println("8. UseCase vége\n");
    }

    /**
     * 9. Use case: Jégtörő fej működése
     * A játékos a jégtörő fejjel felszerelt hókotróval halad végig az adott sávon
     * Ez a fej működés közben a jeget feltöri, amely ezután a sávon marad, és hóként viselekdik.
     */
    private static void jegtoroFejMukodeseTeszt() {
        System.out.println("\n9. UseCase: Jégtörő fej működése");
        Hokotro hokotro = new Hokotro();
        Sav sav = new Sav();
        JegtoroFej jegtoroFej = new JegtoroFej();
        hokotro.setAktualisFej(jegtoroFej);
        hokotro.takaritSavot(sav);
        jegtoroFej.fejKiBeKapcsolasa(true);
        System.out.println("9. UseCase vége\n");
    }

    /**
     * 10. Use Case: Játék vége check 
     * A rendszer megkerdezi, hogy a buszok mozgaskepesek-e, illetve hogy
     * a balesetezett autok maximalis szama elerte-e a kritikus hatart.
     * Ha barmelyik feltetel teljesul, a jateknak vege.
     */
    private static void jatekVegeCheckTeszt() {
        System.out.println("\n10. és 11. UseCase egybevonva: Játék vége check");
        Uthalozat halozat = new Uthalozat();
        boolean jatekVege = halozat.jatekVegeCheck();

        if (jatekVege) {
            System.out.println("A jateknak vege.");
        } else {
            System.out.println("A jatek folytatodik.");
        }

        System.out.println("10. UseCase vége\n");
    }
    
    /**
     * 12. Use case: Jármű megcsúszása
     * A jeges sávra lépő járműnél a rendszer baleseti kalkulációt végez. 
     * Sikertelen kimenetel esetén a jármű balesetet szenved és meghatározott 
     * ideig mozgásképtelenné válik, eltorlaszolva az adott sávot.
     */
    public static void jarmuMegcsuszasaTeszt() {
        System.out.println("\n12. UseCase: Jármű megcsúszása");
        Jarmu auto = new Auto();
        Sav jegesSav = new Sav();

        boolean tortentBaleset = jegesSav.balesetKalkulacio(0);
        if (tortentBaleset) {
            auto.balesetezik();
        }
        

        System.out.println("12. UseCase vége\n");
        }

    /**
     * 13. Use case: Havazás szimulációja
     * Az Úthálózat minden körben egyenletesen növeli a hóvastagságot a város 
     * útszakaszain. A folyamat az alagutakat nem érinti, mivel azok 
     * kialakításuknál fogva védettek a csapadéktól.
     */
    public static void havazasSzimulacioTeszt() {
        System.out.println("\n13. UseCase: Havazás szimulációja");
        Uthalozat halozat = new Uthalozat();
        
        halozat.havazas(1);
        System.out.println("A havazás megtörtént az utakon\n12. UseCase vége\n");
    }


    /**
     * 14. Use case: Só töltése a sószóró fejbe
     * A játékos a sószóró fejjel felszerelt hókotróba sót tölt, hogy az használható legyen a sáv takarítására.
     */

    private static void soTolteseTeszt(){
        System.out.println("\n14. UseCase: Só töltése a sószóró fejbe");
        TakaritoManager manager = new TakaritoManager();
        Hokotro hokotro = new Hokotro();
        SoszoroFej soszoro = new SoszoroFej();

        hokotro.setAktualisFej(soszoro);
        manager.soToltes(hokotro);


        System.out.println("14. UseCase vége\n");
    }

      /**
     * 15. Use case: töltése a sárkányfejbe
     * A játékos a sárkány fejjel felszerelt hókotróba biokerozint tölt, hogy az használható legyen a sáv takarítására.
     */

    private static void kerozinTolteseTeszt(){
        System.out.println("\n15. UseCase: Biokerozin töltése a sárkány fejbe");
        TakaritoManager manager = new TakaritoManager();
        Hokotro hokotro3 = new Hokotro();
        SarkanyFej sarkany = new SarkanyFej();

        hokotro3.setAktualisFej(sarkany);
        manager.kerozinToltes(hokotro3);


        System.out.println("15. UseCase vége\n");
    }

    /**
     * 16. Use case: Autó áthaladása és jéggé tömörülés
     * Kellő mennyiségű áthaladt autó esetén a sávon található hó jéggé tömörül,
     * amely megváltoztatja a sáv tulajdonságait és a rajta közlekedő járművek viselkedését.
     */
    private static void autoAthaladasEsJeggeTomorulesTeszt() {
        System.out.println("\n16. UseCase: Autó áthaladása es jéggé tömörülés");
        Sav sav = new Sav();

        sav.autoAthalad();

        System.out.println("16. UseCase vége\n");
    }

    /**
     * 17. Use case: Leszórt só
     * A sószóró fej felolvasztja az úton lévő havat és jeget[cite: 52, 292]. 
     * Az útra juttatott anyag ideiglenesen megakadályozza a csapadék 
     * újbóli megmaradását és a jegesedést[cite: 48, 750].
     */
    public static void leszortSoTeszt() {
        System.out.println("\n17. UseCase: Leszórt só");
        Hokotro h = new Hokotro();
        SoszoroFej fej = new SoszoroFej();
        Sav s = new Sav();

        h.setAktualisFej(fej);
        h.takaritSavot(s);
        
        System.out.println("A só kihelyezve a sávra, a jegesedés megállt.\n");
        System.out.println("17. UseCase vége\n");
    }
}
