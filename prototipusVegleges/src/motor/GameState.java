package motor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    /** Jelzi, hogy a jatekmotor tovabb fusson-e (false = jatek vege). */
    public boolean running = true;
    /** Osszes balesetszam; 5 felett jatekveget okoz. */
    public int accidents;
    /** Az aktualis kor sorszama (1-tol indul); minden Kor vege gomb-osztanyomas utan novekedik. */
    public int currentRound = 1;
    /** A Pass'N'Play modban aktualisan kovetkezo jatekos neve (Takarito1 vagy Buszos1). */
    public String activePlayerName = "Takarito1";
    /** Ha a jatek veget ert, itt taroljuk az okot a UI-nak. */
    public String gameOverReason;
    /** A legfrissebb esemenyek a UI-nak (a flushEvents nem ti orli ezt). */
    private final Deque<String> recentEvents = new ArrayDeque<>();
    /** Hany esemenyt tartson nyilvan a UI szamara. */
    private static final int MAX_RECENT_EVENTS = 12;
    /** A telephely csomopont neve az ut-halozatban. */
    String depotNode = "Telephely_1";

    /**
     * Kozos kassza: a Takarito es a Buszos jatekos egyutt gyujtik a penzt.
     * A kassza forrasa minden penzmuveletnek (vasarlas, fizetes).
     */
    public int kassza = 1000;

    /** GUI/view-feliratkozok listaja (push-szeru ertesiteshez). */
    private final List<GameStateListener> listeners = new ArrayList<>();

    /** Feliratkoztat egy listenert (idempotens: ugyanazt nem regisztrálja kétszer). */
    public void addListener(GameStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Leiratkoztat egy listenert. */
    public void removeListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Ertesiti az osszes feliratkozott listenert, hogy az allapot valtozott.
     * Egy hibasan visszaado listener nem akadalyozza a tobbiek meghivasat.
     */
    public void fireStateChanged() {
        for (GameStateListener l : new ArrayList<>(listeners)) {
            try {
                l.onStateChanged(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Beallitja a kivalasztott entitas nevet, majd ertesiti a listenereket.
     * Ezt a metodust hivja a GUI controller az egerkattintas hatasara.
     */
    public void setSelectedName(String name) {
        this.selectedName = name;
        fireStateChanged();
    }

    /** Visszaadja az osszes regisztralt entitast (csak olvashato view). */
    public Collection<NamedEntity> getAllEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }

    /** Visszaadja az osszes regisztralt utat (csak olvashato view). */
    public Collection<Ut> getAllUtak() {
        return Collections.unmodifiableCollection(utak.values());
    }

    /** Visszaadja a telephely csomopont nevet. */
    public String getDepotNode() {
        return depotNode;
    }

    /**
     * Igaz, ha a kozos kasszaban van eleg penz a megadott osszeg kifizetesere.
     */
    public boolean canAffordKassza(int amount) {
        return kassza >= amount;
    }

    /**
     * Levonja a megadott osszeget a kozos kasszabol, es a megjelenites-mezoket szinkronizalja
     * (minden Jatekos.money fielden ugyanaz a kassza-ertek lesz).
     */
    public void chargeKassza(int amount) {
        setKassza(kassza - amount);
    }

    /** Hozzaadja a megadott osszeget a kozos kasszahoz (pl. buszos kor-jutalom). */
    public void creditKassza(int amount) {
        setKassza(kassza + amount);
    }

    /**
     * Beallitja a kozos kasszaban levo penzosszeget, es szinkronizalja minden
     * Jatekos.money mezojet, hogy a megjelenites azonnal helyes legyen.
     */
    public void setKassza(int amount) {
        this.kassza = amount;
        for (NamedEntity entity : entities.values()) {
            Jatekos j = entity.asJatekos();
            if (j != null) {
                j.money = amount;
            }
        }
    }

    /**
     * BFS-szel megkeresi a legrovidebb utat ket csomopont kozott.
     * A graf a putUt() altal hozzaadott utakbol epul fel.
     *
     * @return csomopont-nevek listaja from-bol to-ig (mindketto bezarolag),
     *         vagy ures lista, ha nincs ut.
     */
    public List<String> shortestPath(String from, String to) {
        if (from == null || to == null) {
            return new ArrayList<>();
        }
        String fromKey = from.toLowerCase(Locale.ROOT);
        String toKey = to.toLowerCase(Locale.ROOT);
        if (fromKey.equals(toKey)) {
            List<String> single = new ArrayList<>();
            single.add(from);
            return single;
        }
        Map<String, String> parent = new HashMap<>();
        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        // properCase: a kis-betus kulcsbol az eredeti, nagybetus csomopont-nevre val mappinget tarol
        Map<String, String> properCase = new HashMap<>();
        properCase.put(fromKey, from);
        queue.add(fromKey);
        visited.add(fromKey);

        while (!queue.isEmpty()) {
            String nodeKey = queue.pollFirst();
            String nodeName = properCase.getOrDefault(nodeKey, nodeKey);
            List<String> incidentRoads = graph.get(nodeKey);
            if (incidentRoads == null) continue;
            for (String roadName : incidentRoads) {
                Ut ut = utak.get(roadName.toLowerCase(Locale.ROOT));
                if (ut == null) continue;
                String other = ut.opposite(nodeName);
                if (other == null) continue;
                String otherKey = other.toLowerCase(Locale.ROOT);
                if (visited.contains(otherKey)) continue;
                visited.add(otherKey);
                properCase.put(otherKey, other);
                parent.put(otherKey, nodeKey);
                if (otherKey.equals(toKey)) {
                    return reconstructPath(parent, properCase, fromKey, otherKey);
                }
                queue.addLast(otherKey);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Visszafele bejarja a parent-mapet a celtol a kiindulopontig, es az eredeti
     * (proper-case) csomopont-neveket adja vissza, hogy a hivok (pl. UI-rajzolas)
     * a GameLayout-ban megfelelo kulcsokat kapjanak.
     */
    private List<String> reconstructPath(Map<String, String> parent,
                                         Map<String, String> properCase,
                                         String fromKey, String toKey) {
        List<String> path = new ArrayList<>();
        String currentKey = toKey;
        while (currentKey != null) {
            path.add(0, properCase.getOrDefault(currentKey, currentKey));
            if (currentKey.equals(fromKey)) break;
            currentKey = parent.get(currentKey);
        }
        return path;
    }

    /**
     * Megkeresi a 'from' csomopontbol a 'to' csomopont fele vezeto egyetlen kovetkezo csomopontot.
     * Hasznos az NPC autok lepteteseshez (egy lepes a legrovidebb uton).
     *
     * @return a kovetkezo csomopont neve, vagy null, ha nincs ut vagy mar a celban vagyunk.
     */
    public String nextStepToward(String from, String to) {
        List<String> path = shortestPath(from, to);
        if (path.size() < 2) return null;
        return path.get(1);
    }

    /**
     * Megkeresi azt az utat, ami ket adott csomopontot kozvetlenul osszekot.
     * @return az ut, vagy null ha nincs kozvetlen kapcsolat.
     */
    public Ut roadBetween(String nodeA, String nodeB) {
        if (nodeA == null || nodeB == null) return null;
        List<String> incidents = graph.get(nodeA.toLowerCase(Locale.ROOT));
        if (incidents == null) return null;
        for (String roadName : incidents) {
            Ut ut = utak.get(roadName.toLowerCase(Locale.ROOT));
            if (ut != null && ut.hasNode(nodeB)) {
                return ut;
            }
        }
        return null;
    }

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
     * Igaz, ha a jatekosnak legalabb egy jarmuve a telephely csomoponton all,
     * vagy ha meg egyaltalan nincs jarmuva.
     */
    public boolean jatekosAtTelephely(Jatekos jatekos) {
        for (String vehicleName : jatekos.vehicles) {
            Jarmu vehicle = getJarmu(vehicleName);
            if (vehicle != null && "Telephely".equalsIgnoreCase(vehicle.currentNode)) {
                return true;
            }
        }
        return jatekos.vehicles.isEmpty();
    }

    /**
     * Igaz, ha az adott ut adott savjat egy elakadt (balesetezett) jarmu blokkolja.
     * A moveToNode hasznalja a sav-valasztashoz.
     */
    public boolean isLaneBlocked(Ut ut, int laneIdx) {
        if (ut == null) return false;
        for (NamedEntity entity : entities.values()) {
            Jarmu v = entity.asJarmu();
            if (v == null) continue;
            if (v.currentUt == null) continue;
            if (!v.currentUt.equalsIgnoreCase(ut.name())) continue;
            if (v.savIndex != laneIdx) continue;
            if (v.disabledTime > 0) return true;
        }
        return false;
    }

    /**
     * Igaz, ha az aktualis jatekosnak (activePlayerName) van olyan jarmuve, amelyik
     * meg lephet ebben a korben (canMove() && movesThisRound < maxMovesPerTurn).
     * A HUD ezzel emeli ki a "Kesz vagyok" gombot.
     */
    public boolean activePlayerHasMovesLeft() {
        Jatekos jatekos = getJatekos(activePlayerName);
        if (jatekos == null) return true;
        if (jatekos.vehicles.isEmpty()) return false;
        for (String vehicleName : jatekos.vehicles) {
            Jarmu v = getJarmu(vehicleName);
            if (v == null) continue;
            if (!v.canMove()) continue;
            if (v.movesThisRound < v.getMaxMovesPerTurn()) {
                return true;
            }
        }
        return false;
    }

    /** Hozzaad egy esemeny-uzenetet az aszinkron esemenysorhoz, es a UI-buffeerbe is. */
    public void enqueueEvent(String line) {
        eventQueue.addLast(line);
        recentEvents.addLast(line);
        while (recentEvents.size() > MAX_RECENT_EVENTS) {
            recentEvents.pollFirst();
        }
    }

    /** Visszaadja a legutobbi esemenyeket UI-megjeleniteshez (legfrissebb az utolsoval). */
    public List<String> getRecentEvents() {
        return new ArrayList<>(recentEvents);
    }

    /** Visszavalt a Takarito jatekosra (uj kor inditasahoz). */
    public void resetActivePlayer() {
        this.activePlayerName = "Takarito1";
    }

    /** Atvalt a masik jatekosra (Pass'N'Play handoff). */
    public void switchActivePlayer() {
        if ("Takarito1".equalsIgnoreCase(activePlayerName)) {
            activePlayerName = "Buszos1";
        } else {
            activePlayerName = "Takarito1";
        }
    }

    /**
     * Eldonti, hogy a megadott jarmu az aktualis jatekos iranyithatja-e.
     * NPC autokat (owner == null) senki nem iranyithatja kozvetlenul.
     */
    public boolean canControl(jarmuvek.Jarmu vehicle) {
        if (vehicle == null) return false;
        if (vehicle.owner == null) return false;
        return vehicle.owner.equalsIgnoreCase(activePlayerName);
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
            gameOverReason = "Minden busz mozgaskepetlen lett.";
            enqueueEvent("JATEK VEGE: minden busz mozgaskepetlen.");
            running = false;
            return;
        }
        if (accidents >= 5) {
            gameOverReason = "Kijárási tilalom — túl sok baleset történt (" + accidents + ").";
            enqueueEvent("JATEK VEGE: kijarasi tilalom, kritikus balesetszam.");
            running = false;
        }
    }
}
