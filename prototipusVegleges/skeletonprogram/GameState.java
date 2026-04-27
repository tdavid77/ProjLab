package skeletonprogram;

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

/**
 * A teljes jatekallapot taroloja: entitasok, uthalozat, esemenyek es korallapot.
 */
public final class GameState {
    final Map<String, NamedEntity> entities = new LinkedHashMap<>();
    final Map<String, Ut> utak = new LinkedHashMap<>();
    final Map<String, List<String>> graph = new HashMap<>();
    final Random random = new Random(42);
    final Set<String> fileLoadStack = new HashSet<>();
    final Deque<String> eventQueue = new ArrayDeque<>();

    Difficulty difficulty = Difficulty.MEDIUM;
    String selectedName;
    boolean running = true;
    int accidents;
    String depotNode = "Telephely_1";

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

    public <T> T getTypedEntity(String name, Class<T> type) {
        NamedEntity entity = getEntity(name);
        if (entity == null || !type.isInstance(entity)) {
            return null;
        }
        return type.cast(entity);
    }

    public boolean existsName(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        return entities.containsKey(key) || utak.containsKey(key);
    }

    public void putEntity(NamedEntity entity) {
        entities.put(entity.name().toLowerCase(Locale.ROOT), entity);
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

    public boolean jatekosAtTelephely(Jatekos jatekos) {
        for (String vehicleName : jatekos.vehicles) {
            Jarmu vehicle = getTypedEntity(vehicleName, Jarmu.class);
            if (vehicle != null && vehicle.currentUt == null) {
                return true;
            }
        }
        return jatekos.vehicles.isEmpty();
    }

    public void enqueueEvent(String line) {
        eventQueue.addLast(line);
    }

    public void flushEvents() {
        while (!eventQueue.isEmpty()) {
            System.out.println("[ESEMENY] " + eventQueue.removeFirst());
        }
    }

    public void tickTime() {
        for (Ut ut : utak.values()) {
            for (Sav sav : ut.savok) {
                sav.tickTime();
            }
        }
        for (NamedEntity entity : entities.values()) {
            if (entity instanceof Jarmu) {
                ((Jarmu) entity).tickTime();
            }
        }
    }

    public void evaluateGameOver() {
        long busCount = entities.values().stream().filter(e -> e instanceof Busz).count();
        long disabledBusCount = entities.values().stream()
            .filter(e -> e instanceof Busz)
            .map(e -> (Busz) e)
            .filter(b -> !b.canMove())
            .count();
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
