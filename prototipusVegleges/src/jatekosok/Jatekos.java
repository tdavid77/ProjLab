package jatekosok;

import java.util.ArrayList;
import java.util.List;

import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import motor.GameState;
import motor.NamedEntity;
import takaritofejek.FejTipus;

/**
 * Jatekos szerepkor alaposztalya: tarolja a penzt, a jarmuparkot es a fej-raktarat.
 * Ket konkret szerepkort tamogat: TakaritoJatekos (hokotrokat iranyit) es
 * BuszosJatekos (buszokat iranyit). A vasarlas-logika (hokotro, fej vasarlas)
 * itt talalhato, a penzlevonassal es keszletkezeléssel egyutt.
 * A relinkEntityName() gondoskodik rola, hogy atnevezes eseten a jarmulista
 * hivatkozasai naprakeszek maradjanak.
 */
public class Jatekos implements NamedEntity {
    protected String name;
    public int money = 1000;
    public final List<String> vehicles = new ArrayList<>();
    protected final List<FejTipus> inventory = new ArrayList<>();

    protected Jatekos(String name) {
        this.name = name;
    }

    @Override
    public Jatekos asJatekos() { return this; }

    @Override
    public void relinkEntityName(String oldName, String newName) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).equalsIgnoreCase(oldName)) {
                vehicles.set(i, newName);
            }
        }
    }

    @Override
    public void attachVehicle(Jarmu vehicle) {
        addVehicle(vehicle.name);
        vehicle.owner = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void renameTo(String newName) {
        this.name = newName;
    }

    // Csak konzolos UI-hoz: statusLine kiírásánál és a 'lista' parancs szűrőjénél
    // szerepel. Nem viselkedési elágazás alapja. GUI-s verzióban el fog tűnni,
    // mert ott a típusazonosítás a nézet rétegben, statikus típusinformáció alapján történik.
    @Override
    public String type() {
        return "Jatekos";
    }

    @Override
    public String statusLine(GameState state) {
        return type() + " " + name + " | Penz:" + money + " | Jarmuvek:" + vehicles + " | Raktar:" + inventory;
    }

    public void setPenz(int amount) {
        this.money = amount;
    }

    /** Igaz, ha a jatekos egyenlege eleg a megadott osszeg kifizetesere. */
    public boolean canAfford(int amount) {
        return money >= amount;
    }

    /** Levonja a megadott osszeget a jatekos penzegyenlegeről. */
    public void charge(int amount) {
        money -= amount;
    }

    /** Hozzaadja a megadott jarmu nevet a jarmuparkhoz, ha meg nem szerepel benne. */
    public void addVehicle(String vehicleName) {
        if (!vehicles.contains(vehicleName)) {
            vehicles.add(vehicleName);
        }
    }

    /** Eltavolitja a megadott fejtipust a raktarbol. Visszaadja, hogy sikeres volt-e. */
    public boolean removeFejFromInventory(FejTipus fejTipus) {
        return inventory.remove(fejTipus);
    }

    /** Hozzaadja a megadott fejtipust a jatekos raktarahoz. */
    public void addFejToInventory(FejTipus fejTipus) {
        inventory.add(fejTipus);
    }

    /**
     * Letrehoz es visszaad egy uj Hokotro peldanyt, es a jatekos jarmuparkahoz rendeli.
     * Levonja az arat (300) a penzegyenlegeből; ha nincs eleg penz, kivetelt dob.
     */
    public Hokotro vasarolHokotro(String hokotroNev) {
        int price = 300;
        if (!canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz. Szukseges: " + price);
        }
        charge(price);
        Hokotro h = new Hokotro(hokotroNev);
        h.owner = name;
        addVehicle(h.name);
        return h;
    }

    /** Megvasarolja a megadott fejet (ar: 80) es a raktarba teszi. Kivetelt dob, ha nincs eleg penz. */
    public void vasarolFej(FejTipus fejTipus) {
        int price = 80;
        if (!canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz a fej vasarlashoz.");
        }
        charge(price);
        addFejToInventory(fejTipus);
    }
}
