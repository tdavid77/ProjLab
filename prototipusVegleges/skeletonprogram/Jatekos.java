package skeletonprogram;

import java.util.ArrayList;
import java.util.List;

public class Jatekos implements NamedEntity {
    protected String name;
    protected int money = 1000;
    protected final List<String> vehicles = new ArrayList<>();
    protected final List<FejTipus> inventory = new ArrayList<>();

    protected Jatekos(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void renameTo(String newName) {
        this.name = newName;
    }

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

    public boolean canAfford(int amount) {
        return money >= amount;
    }

    public void charge(int amount) {
        money -= amount;
    }

    public void addVehicle(String vehicleName) {
        if (!vehicles.contains(vehicleName)) {
            vehicles.add(vehicleName);
        }
    }

    public boolean removeFejFromInventory(FejTipus fejTipus) {
        return inventory.remove(fejTipus);
    }

    public void addFejToInventory(FejTipus fejTipus) {
        inventory.add(fejTipus);
    }

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

    public void vasarolFej(FejTipus fejTipus) {
        int price = 80;
        if (!canAfford(price)) {
            throw new IllegalArgumentException("Nincs eleg penz a fej vasarlashoz.");
        }
        charge(price);
        addFejToInventory(fejTipus);
    }
}
