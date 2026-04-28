package motor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import jarmuvek.Busz;
import jarmuvek.Hokotro;
import jarmuvek.Jarmu;
import jatekosok.Jatekos;
import jatekosok.TakaritoJatekos;
import terkep.Sav;
import terkep.Ut;

/**
 * A jatek teljes allapotat egy helyen tarolja: jatekos- es jarmu-entitasok,
 * uthalozat, esemenysor es globalis parameterek (nehezseg, korok, balesetek).
 * A get*() metódusok polimorf modon kerdezik le a megfelelo tipusu entitasokat,
 * instanceof nelkul.
 * Az evaluateGameOver() ellenorzi a jatekvegezo feltételeket minden havazas utan.
 * A putEntity() hivja az onRegistered() hookot, lehetove teve, hogy egyes
 * entitasok (pl. Busz) kulonleges regisztraciot vegezzenek.
 */
public final class GameState {
    /** Az osszes nevesitett entitas (jatekosok, jarmuvek) nev szerint indexelve. */
    final Map<String, NamedEntity> entities = new LinkedHashMap<>();
    /** Az uthalozat utjai nev szerint indexelve. */
    final Map<String, Ut> utak = new LinkedHashMap<>();
    /** Csomopont -> szomszedos utak neve; a navigaciohoz hasznalt graf. */
    final Map<String, List<String>> graph = new HashMap<>();
    /** Az osszes regisztralt Busz; a jatekvegezo feltetel szamitasahoz. */
    final List<Busz> buses = new ArrayList<>();
    /** Determinisztikus veletlengenerator a baleseti szamitashoz (seed: 42). */
    public final Random random = new Random(42);
    /** Megakadályozza a körkoros fajlbetoltest (rekurzio-vedelem). */
    final Set<String> fileLoadStack = new HashSet<>();
    /** Az aszinkron esemenyek sora; flushEvents() irja ki a konzolra. */
    final Deque<String> eventQueue = new ArrayDeque<>();

    /** A jatekban beallitott nehezsegi szint (alapertelmezett: MEDIUM). */
    public Difficulty difficulty = Difficulty.MEDIUM;
    /** A jelenleg kivalasztott entitas neve (null, ha nincs kivalasztva). */
    String selectedName;
    /** Jelzi, hogy a jatekmotor tovabb fusson-e. */
    boolean running = true;
    /** Osszes balesetszam; 5 felett jatekveget okoz. */
    public int accidents;
    /** A telephely csomopont neve az ut-halozatban. */
    String depotNode = "Telephely_1";

    /** Visszaadja a jelenleg kivalasztott entitast, vagy null-t, ha nincs kivalasztva. */
    public NamedEntity selected() {
        if (selectedName == null) {
            return null;
        }
        return entities.get(selectedName.toLowerCase(Locale.ROOT));
    }

    public NamedEntity getEntity(String name) {
        if (name == null) {
            return null;
        }
        return entities.get(name.toLowerCase(Locale.ROOT));
    }

    public Jarmu getJarmu(String name) {
        NamedEntity e = getEntity(name);
        return e == null ? null : e.asJarmu();
    }

    public Hokotro getHokotro(String name) {
        NamedEntity e = getEntity(name);
        return e == null ? null : e.asHokotro();
    }

    public TakaritoJatekos getTakaritoJatekos(String name) {
        NamedEntity e = getEntity(name);
        return e == null ? null : e.asTakaritoJatekos();
    }

    public Jatekos getJatekos(String name) {
        NamedEntity e = getEntity(name);
        return e == null ? null : e.asJatekos();
    }

    public boolean existsName(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        return entities.containsKey(key) || utak.containsKey(key);
    }

    /**
     * Regisztralja az entitast a nev szerint indexelt terkepbe,
     * majd meghivja az onRegistered() hookjat a specializalt regisztracio vegett.
     */
    public void putEntity(NamedEntity entity) {
        entities.put(entity.name().toLowerCase(Locale.ROOT), entity);
        entity.onRegistered(this);
    }

    /** Hozzaadja a Busz peldanyt a buses listahoz, ha meg nincs benne. */
    public void registerBus(Busz busz) {
        if (!buses.contains(busz)) {
            buses.add(busz);
        }
    }

    public void removeEntity(String name) {
        entities.remove(name.toLowerCase(Locale.ROOT));
    }

    public Ut getUt(String name) {
        if (name == null) {
            return null;
        }
        return utak.get(name.toLowerCase(Locale.ROOT));
    }

    public void putUt(Ut ut) {
        utak.put(ut.name().toLowerCase(Locale.ROOT), ut);
        graph.computeIfAbsent(ut.nodeA.toLowerCase(Locale.ROOT), key -> new ArrayList<>()).add(ut.name());
        graph.computeIfAbsent(ut.nodeB.toLowerCase(Locale.ROOT), key -> new ArrayList<>()).add(ut.name());
    }

    public void removeUt(String name) {
        utak.remove(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Igaz, ha a jatekosnak legalabb egy jarmuve telephelyen all (currentUt == null),
     * vagy ha meg egyaltalan nincs jarmuva.
     */
    public boolean jatekosAtTelephely(Jatekos jatekos) {
        for (String vehicleName : jatekos.vehicles) {
            Jarmu vehicle = getJarmu(vehicleName);
            if (vehicle != null && vehicle.currentUt == null) {
                return true;
            }
        }
        return jatekos.vehicles.isEmpty();
    }

    /** Hozzaad egy esemeny-uzenetet az aszinkron esemenysorhoz. */
    public void enqueueEvent(String line) {
        eventQueue.addLast(line);
    }

    /** Kiirja es torli az osszes varakozo esemenyt a konzolra. */
    public void flushEvents() {
        while (!eventQueue.isEmpty()) {
            System.out.println("[ESEMENY] " + eventQueue.removeFirst());
        }
    }

    /**
     * Egy kor elteleleset modelezi: csokkenti az osszes sav es jarmu
     * ido-szamlaloit (so-vedelem, zuzalek, baleseti varakozas).
     */
    public void tickTime() {
        for (Ut ut : utak.values()) {
            for (Sav sav : ut.savok) {
                sav.tickTime();
            }
        }
        for (NamedEntity entity : entities.values()) {
            entity.tickTime();
        }
    }

    /**
     * Ellenorzi a jatekvegezo feltételeket: minden busz mozgaskeptelenné valt,
     * vagy a balesetek szama elerte az 5-ot.
     */
    public void evaluateGameOver() {
        long busCount = buses.size();
        long disabledBusCount = buses.stream().filter(b -> !b.canMove()).count();
        if (busCount > 0 && disabledBusCount == busCount) {
            enqueueEvent("JATEK VEGE: minden busz mozgaskepetlen.");
            running = false;
            return;
        }
        if (accidents >= 5) {
            enqueueEvent("JATEK VEGE: kijarasi tilalom, kritikus balesetszam.");
            running = false;
        }
    }
}
