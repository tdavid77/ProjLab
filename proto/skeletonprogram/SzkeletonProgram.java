package skeletonprogram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SzkeletonProgram {
    private static int depth = 0;

    public static void printIndent() {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }

    public static void logCall(String callerId, String className, String methodName, String params) {
        printIndent();
        System.out.println("-> " + callerId + ": " + className + "." + methodName + "(" + params + ")");
        depth++;
    }

    public static void logReturn(String returnDesc) {
        depth--;
        printIndent();
        System.out.println("<- " + returnDesc);
    }

    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.run(args);
    }

    private enum Difficulty {
        EASY(0.08),
        MEDIUM(0.18),
        HARD(0.32);

        private final double baseCrashChance;

        Difficulty(double baseCrashChance) {
            this.baseCrashChance = baseCrashChance;
        }

        public double baseCrashChance() {
            return baseCrashChance;
        }

        public static Difficulty fromInput(String value) {
            for (Difficulty d : values()) {
                if (d.name().equalsIgnoreCase(value)) {
                    return d;
                }
            }
            return null;
        }
    }

    private enum UtTipus {
        NORMAL,
        HID,
        ALAGUT;

        public static UtTipus fromInput(String value) {
            for (UtTipus t : values()) {
                if (t.name().equalsIgnoreCase(value)) {
                    return t;
                }
            }
            return null;
        }
    }

    private enum EntityType {
        TAKARITOJATEKOS,
        BUSZOSJATEKOS,
        HOKOTRO,
        BUSZ,
        AUTO
    }

    private enum FejTipus {
        SOPROFEJ,
        HANYOFEJ,
        JEGTOROFEJ,
        SARKANYFEJ,
        SOSZOROFEJ,
        ZUZOTTKOSZOROFEJ;

        public static FejTipus fromInput(String value) {
            if (value == null) {
                return null;
            }
            String normalized = value.trim().toUpperCase(Locale.ROOT);
            for (FejTipus type : values()) {
                if (type.name().equals(normalized)) {
                    return type;
                }
            }
            return null;
        }
    }

    private enum DepositType {
        HO,
        JEG,
        SO,
        FELTORTJEG,
        ZUZALEK;

        public static DepositType fromInput(String value) {
            for (DepositType t : values()) {
                if (t.name().equalsIgnoreCase(value)) {
                    return t;
                }
            }
            return null;
        }
    }

    private interface NamedEntity {
        String name();
        void renameTo(String newName);
        String type();
        String statusLine(GameState state);
    }

    private interface CommandHandler {
        void execute(CommandContext context, List<String> args);
    }

    private static final class CommandContext {
        private final GameState state;
        private final String rawLine;

        private CommandContext(GameState state, String rawLine) {
            this.state = state;
            this.rawLine = rawLine;
        }

        public GameState state() {
            return state;
        }

        public String rawLine() {
            return rawLine;
        }
    }

    private static final class Ut {
        private String name;
        private final String nodeA;
        private final String nodeB;
        private final int length;
        private final UtTipus type;
        private final List<Sav> savok;

        private Ut(String name, String nodeA, String nodeB, int length, UtTipus type, int savSzam) {
            this.name = name;
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.length = length;
            this.type = type;
            this.savok = new ArrayList<>();
            for (int i = 0; i < savSzam; i++) {
                savok.add(new Sav(i));
            }
        }

        public String name() {
            return name;
        }

        public void renameTo(String newName) {
            this.name = newName;
        }

        public boolean hasNode(String node) {
            return nodeA.equalsIgnoreCase(node) || nodeB.equalsIgnoreCase(node);
        }

        public String opposite(String node) {
            if (nodeA.equalsIgnoreCase(node)) {
                return nodeB;
            }
            if (nodeB.equalsIgnoreCase(node)) {
                return nodeA;
            }
            return null;
        }

        public int savSzam() {
            return savok.size();
        }

        public Sav sav(int idx) {
            return savok.get(idx);
        }

        public int sumHo() {
            int total = 0;
            for (Sav sav : savok) {
                total += sav.ho;
            }
            return total;
        }

        public int alkalmazHoEses() {
            int before = sumHo();
            for (Sav sav : savok) {
                sav.hoingOneUnit(type == UtTipus.ALAGUT);
            }
            int after = sumHo();
            return after - before;
        }

        public String describeCompact() {
            StringBuilder sb = new StringBuilder();
            sb.append("Ut ").append(name).append(" (").append(type.name()).append(")")
                .append(" | Szomszedok:[").append(nodeA).append(", ").append(nodeB).append("]")
                .append(" | Savok: ");
            List<String> savText = new ArrayList<>();
            for (Sav sav : savok) {
                savText.add(sav.describeCompact());
            }
            sb.append(String.join(", ", savText));
            return sb.toString();
        }
    }

    private static final class Sav {
        private final int index;
        private int ho;
        private int ice;
        private int feltortJeg;
        private int soHatralevoIdeje;
        private int zuzalekHatralevoIdeje;

        private Sav(int index) {
            this.index = index;
        }

        public void applyDeposit(DepositType type, int value) {
            if (type == DepositType.HO) {
                ho = Math.max(0, value);
            }
            if (type == DepositType.JEG) {
                ice = Math.max(0, value);
            }
            if (type == DepositType.SO) {
                soHatralevoIdeje = Math.max(0, value);
            }
            if (type == DepositType.FELTORTJEG) {
                feltortJeg = Math.max(0, value);
            }
            if (type == DepositType.ZUZALEK) {
                zuzalekHatralevoIdeje = Math.max(0, value);
            }
        }

        public void hoingOneUnit(boolean protectedByTunnel) {
            if (protectedByTunnel) {
                return;
            }
            if (soHatralevoIdeje > 0) {
                return;
            }
            ho += 1;
        }

        public void tickTime() {
            if (soHatralevoIdeje > 0) {
                soHatralevoIdeje -= 1;
            }
            if (zuzalekHatralevoIdeje > 0) {
                zuzalekHatralevoIdeje -= 1;
            }
        }

        public String describeCompact() {
            return "[Sav" + index + "(Ho:" + ho + ", Jeg:" + ice + ", Sozott:" + (soHatralevoIdeje > 0 ? "I" : "N") + ")]";
        }
    }

    private static class Jatekos implements NamedEntity {
        protected String name;
        protected int money = 1000;
        protected final List<String> vehicles = new ArrayList<>();
        protected final List<FejTipus> inventory = new ArrayList<>();

        private Jatekos(String name) {
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

    private static final class TakaritoJatekos extends Jatekos {
        private TakaritoJatekos(String name) {
            super(name);
        }

        @Override
        public String type() {
            return "TakaritoJatekos";
        }
    }

    private static final class BuszosJatekos extends Jatekos {
        private BuszosJatekos(String name) {
            super(name);
        }

        @Override
        public String type() {
            return "BuszosJatekos";
        }
    }

    private static class Jarmu implements NamedEntity {
        protected String name;
        protected String owner;
        protected String currentUt;
        protected int savIndex;
        protected int disabledTime;

        private Jarmu(String name) {
            this.name = name;
            this.currentUt = null;
            this.savIndex = 0;
            this.disabledTime = 0;
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
            return "Jarmu";
        }

        @Override
        public String statusLine(GameState state) {
            String pos;
            if (currentUt == null) {
                pos = "Telephely";
            } else {
                pos = currentUt + ", " + savIndex;
            }
            String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
            return type() + " " + name + " | Poz:" + pos + " | Allapot:" + allapot;
        }

        public boolean canMove() {
            return disabledTime <= 0;
        }

        public void tickTime() {
            if (disabledTime > 0) {
                disabledTime -= 1;
            }
        }

        public void vegrehajtLepes(Ut target, int targetSav, GameState state) {
            if (currentUt != null) {
                Ut current = state.getUt(currentUt);
                if (current == null) {
                    throw new IllegalArgumentException("Nincs ilyen ut: " + currentUt);
                }
                boolean sharesNode = current.hasNode(target.nodeA) || current.hasNode(target.nodeB);
                if (!sharesNode && !current.name.equalsIgnoreCase(target.name)) {
                    throw new IllegalArgumentException("A celut nem szomszedos a jelenlegi uttal.");
                }
            }

            currentUt = target.name;
            savIndex = targetSav;

            maybeCrash(target.sav(targetSav), state);
            onCelUtElerve(target, state);
        }

        protected void onCelUtElerve(Ut target, GameState state) {
            // alapertelmezetten nincs extra logika
        }

        private void maybeCrash(Sav sav, GameState state) {
            if (this instanceof Hokotro) {
                return;
            }
            if (sav.ice <= 0) {
                return;
            }
            double modifier = sav.zuzalekHatralevoIdeje > 0 ? -0.12 : 0.0;
            double chance = Math.max(0.0, Math.min(0.95, state.difficulty.baseCrashChance() + (sav.ice * 0.03) + modifier));
            if (state.random.nextDouble() < chance) {
                disabledTime = 2;
                state.accidents += 1;
                state.enqueueEvent(name + " megcsuszott jeges savon, 2 korre kiesett.");
            }
        }
    }

    private static final class Hokotro extends Jarmu {
        private Fej aktivFej;
        private int so;
        private int kerozin;
        private int zuzottko;

        private Hokotro(String name) {
            super(name);
            this.aktivFej = FejFactory.create(FejTipus.SOPROFEJ);
            this.so = 0;
            this.kerozin = 0;
            this.zuzottko = 0;
        }

        @Override
        public String type() {
            return "Hokotro";
        }

        @Override
        public String statusLine(GameState state) {
            String pos;
            if (currentUt == null) {
                pos = "Telephely";
            } else {
                pos = currentUt + ", " + savIndex;
            }
            String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
            String fejNev = aktivFej == null ? FejTipus.SOPROFEJ.name() : aktivFej.tipus().name();
            return "Hokotro " + name
                + " | Poz:" + pos
                + " | Fej:" + fejNev
                + " | Keszletek:[So:" + so + ", Kerozin:" + kerozin + ", Zuzottko:" + zuzottko + "]"
                + " | Allapot:" + allapot;
        }

        public Ut aktualisUt(GameState state) {
            if (currentUt == null) {
                throw new IllegalArgumentException("A hokotro nincs uton, nincs mit takaritani.");
            }
            Ut ut = state.getUt(currentUt);
            if (ut == null) {
                throw new IllegalArgumentException("Nincs ilyen ut: " + currentUt);
            }
            return ut;
        }

        public void takaritSav(Ut ut, int savIndex, GameState state) {
            Sav sav = ut.sav(savIndex);
            if (aktivFej == null) {
                aktivFej = FejFactory.create(FejTipus.SOPROFEJ);
            }
            aktivFej.takaritHatas(this, sav, ut, savIndex, state);
        }

        public void fejCsere(Jatekos jatekos, FejTipus ujFej) {
            if (!jatekos.removeFejFromInventory(ujFej)) {
                throw new IllegalArgumentException("A kivant fej nincs a raktarban: " + ujFej.name());
            }
            if (aktivFej != null) {
                jatekos.addFejToInventory(aktivFej.tipus());
            }
            aktivFej = FejFactory.create(ujFej);
        }

        public void sotoltes(Jatekos jatekos) {
            int price = 50;
            if (!jatekos.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz sotolteshez.");
            }
            jatekos.charge(price);
            so = 100;
        }

        public void kerozintoltes(Jatekos jatekos) {
            int price = 60;
            if (!jatekos.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz kerozintolteshez.");
            }
            jatekos.charge(price);
            kerozin = 100;
        }

        public void zuzalektoltes(Jatekos jatekos) {
            int price = 40;
            if (!jatekos.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz zuzalektolteshez.");
            }
            jatekos.charge(price);
            zuzottko = 100;
        }
    }

    private static final class Busz extends Jarmu {
        private int completedTrips;

        private Busz(String name) {
            super(name);
            this.completedTrips = 0;
        }

        @Override
        public String type() {
            return "Busz";
        }

        @Override
        protected void onCelUtElerve(Ut target, GameState state) {
            if (target.hasNode("Vegallomas") || target.hasNode("Vegallomas_1") || target.hasNode("Vegallomas_2")) {
                completedTrips += 1;
                Jatekos ownerEntity = state.getTypedEntity(owner, Jatekos.class);
                if (ownerEntity != null) {
                    ownerEntity.money += 40;
                    state.enqueueEvent("Busz kor teljesitve: " + name + ", jovairas +40.");
                }
            }
        }
    }

    private static final class Auto extends Jarmu {
        private Auto(String name) {
            super(name);
        }

        @Override
        public String type() {
            return "Auto";
        }
    }

    private abstract static class Fej {
        private final FejTipus tipus;

        protected Fej(FejTipus tipus) {
            this.tipus = tipus;
        }

        public FejTipus tipus() {
            return tipus;
        }

        public abstract void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state);
    }

    private static final class SoproFej extends Fej {
        private SoproFej() {
            super(FejTipus.SOPROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
            int movedHo = sav.ho + sav.feltortJeg;
            sav.ho = 0;
            sav.feltortJeg = 0;
            int rightIndex = savIndex + 1;
            if (movedHo > 0 && rightIndex < ut.savSzam()) {
                Sav rightSav = ut.sav(rightIndex);
                rightSav.ho += movedHo;
            }
            state.enqueueEvent("Soprofej atterelte a csapadekot.");
        }
    }

    private static final class HanyoFej extends Fej {
        private HanyoFej() {
            super(FejTipus.HANYOFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
            sav.ho = 0;
            sav.feltortJeg = 0;
            state.enqueueEvent("Hanyofej az ut melle tavolitotta a csapadekot.");
        }
    }

    private static final class JegtoroFej extends Fej {
        private JegtoroFej() {
            super(FejTipus.JEGTOROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
            sav.feltortJeg += sav.ice;
            sav.ice = 0;
            sav.ho += sav.feltortJeg;
            sav.feltortJeg = 0;
            state.enqueueEvent("Jegtorofej feltorte a jeget, ho jellegu retegre valtott.");
        }
    }

    private static final class SoszoroFej extends Fej {
        private SoszoroFej() {
            super(FejTipus.SOSZOROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
            if (h.so < 10) {
                throw new IllegalArgumentException("Nincs eleg so a soszorofejhez.");
            }
            h.so -= 10;
            sav.ho = 0;
            sav.ice = 0;
            sav.soHatralevoIdeje = 3;
            state.enqueueEvent("Soszoro fej aktiv: ho es jeg eltunt, 3 korig vedett a sav.");
        }
    }

    private static final class SarkanyFej extends Fej {
        private SarkanyFej() {
            super(FejTipus.SARKANYFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
            if (h.kerozin < 10) {
                throw new IllegalArgumentException("Nincs eleg kerozin a sarkanyfejhez.");
            }
            h.kerozin -= 10;
            sav.ho = 0;
            sav.ice = 0;
            sav.feltortJeg = 0;
            state.enqueueEvent("Sarkanyfej aktiv: azonnali olvasztas vegrehajtva.");
        }
    }

    private static final class ZuzottkoSzoroFej extends Fej {
        private ZuzottkoSzoroFej() {
            super(FejTipus.ZUZOTTKOSZOROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Sav sav, Ut ut, int savIndex, GameState state) {
            if (h.zuzottko < 10) {
                throw new IllegalArgumentException("Nincs eleg zuzottko a zuzottkoszorofejhez.");
            }
            h.zuzottko -= 10;
            sav.zuzalekHatralevoIdeje = 3;
            state.enqueueEvent("Zuzottko kiszorva: csuszasveszely csokkent 3 korre.");
        }
    }

    private static final class FejFactory {
        private FejFactory() {
        }

        public static Fej create(FejTipus type) {
            if (type == null) {
                return new SoproFej();
            }
            if (type == FejTipus.HANYOFEJ) {
                return new HanyoFej();
            }
            if (type == FejTipus.JEGTOROFEJ) {
                return new JegtoroFej();
            }
            if (type == FejTipus.SARKANYFEJ) {
                return new SarkanyFej();
            }
            if (type == FejTipus.SOSZOROFEJ) {
                return new SoszoroFej();
            }
            if (type == FejTipus.ZUZOTTKOSZOROFEJ) {
                return new ZuzottkoSzoroFej();
            }
            return new SoproFej();
        }
    }

    private static final class GameState {
        private final Map<String, NamedEntity> entities = new LinkedHashMap<>();
        private final Map<String, Ut> utak = new LinkedHashMap<>();
        private final Map<String, List<String>> graph = new HashMap<>();
        private final Random random = new Random(42);
        private final Set<String> fileLoadStack = new HashSet<>();
        private final Deque<String> eventQueue = new ArrayDeque<>();

        private Difficulty difficulty = Difficulty.MEDIUM;
        private String selectedName;
        private boolean running = true;
        private int accidents;
        private String depotNode = "Telephely_1";

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
                if (vehicle == null) {
                    continue;
                }
                if (vehicle.currentUt == null) {
                    return true;
                }
                Ut ut = getUt(vehicle.currentUt);
                if (ut != null && (ut.hasNode(depotNode))) {
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

    private static final class GameEngine {
        private final GameState state = new GameState();
        private final GameActions actions = new GameActions(state);
        private final CommandRouter router = new CommandRouter(state, actions);

        public void run(String[] args) {
            String autoInput = null;
            for (String arg : args) {
                if (arg.startsWith("--input=")) {
                    autoInput = arg.substring("--input=".length());
                }
            }

            System.out.println("Parancssori prototipus indult. A parancsok listajahoz: help");
            if (autoInput != null && !autoInput.trim().isEmpty()) {
                router.executeLine("betolt " + autoInput);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                while (state.running) {
                    System.out.print("> ");
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    router.executeLine(line);
                }
            } catch (IOException ex) {
                actions.error("I/O hiba: " + ex.getMessage());
            }
        }
    }

    private static final class CommandRouter {
        private final GameState state;
        private final GameActions actions;
        private final Map<String, CommandHandler> commands = new HashMap<>();

        private CommandRouter(GameState state, GameActions actions) {
            this.state = state;
            this.actions = actions;
            registerCommands();
        }

        private void registerCommands() {
            commands.put("help", actions::handleHelp);
            commands.put("kilepes", actions::handleExit);
            commands.put("nehezseg", actions::handleDifficulty);
            commands.put("letrehoz", actions::handleCreate);
            commands.put("szerkeszt", actions::handleRename);
            commands.put("kivalaszt", actions::handleSelect);
            commands.put("betolt", this::handleLoadFromFile);
            commands.put("utletrehoz", actions::handleCreateUt);
            commands.put("lerakodas", actions::handleDeposit);
            commands.put("lepes", actions::handleMove);
            commands.put("wait", actions::handleWait);
            commands.put("hokotrovasarlas", actions::handleBuyHokotro);
            commands.put("allapot", actions::handleStatus);
            commands.put("terkep", actions::handleMap);
            commands.put("fejcsere", actions::handleFejSwap);
            commands.put("fejvasarlas", actions::handleFejBuy);
            commands.put("sotoltes", actions::handleSaltRefill);
            commands.put("kerozintoltes", actions::handleFuelRefill);
            commands.put("zuzalektoltes", actions::handleGritRefill);
            commands.put("takarit", actions::handleCleanSav);
            commands.put("havazas", actions::handleHoeses);
            commands.put("lista", actions::handleListByType);
            commands.put("penz", actions::handleSetPenz);
        }

        private void executeLine(String line) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) {
                return;
            }

            List<String> parts = tokenize(trimmed);
            if (parts.isEmpty()) {
                return;
            }

            String commandName = parts.get(0).toLowerCase(Locale.ROOT);
            List<String> args = parts.subList(1, parts.size());
            CommandHandler handler = commands.get(commandName);
            if (handler == null) {
                actions.error("Ismeretlen parancs: " + commandName + ". Segitseg: help");
                return;
            }

            try {
                handler.execute(new CommandContext(state, trimmed), args);
                state.flushEvents();
            } catch (IllegalArgumentException ex) {
                actions.error(ex.getMessage());
            } catch (Exception ex) {
                actions.error("Varatlan hiba: " + ex.getMessage());
            }
        }

        private List<String> tokenize(String line) {
            List<String> tokens = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean inQuote = false;
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch == '"') {
                    inQuote = !inQuote;
                    continue;
                }
                if (!inQuote && Character.isWhitespace(ch)) {
                    if (current.length() > 0) {
                        tokens.add(current.toString());
                        current.setLength(0);
                    }
                } else {
                    current.append(ch);
                }
            }
            if (current.length() > 0) {
                tokens.add(current.toString());
            }
            if (inQuote) {
                throw new IllegalArgumentException("Hibas bemenet: idezojel nincs lezarva.");
            }
            return tokens;
        }

        private void handleLoadFromFile(CommandContext context, List<String> args) {
            actions.ensureArgCount(args, 1, 1, "betolt [fajlnev]");
            String file = args.get(0);
            String normalized = file.toLowerCase(Locale.ROOT);
            if (!state.fileLoadStack.add(normalized)) {
                throw new IllegalArgumentException("Rekurziv betoltes tiltva: " + file);
            }
            int count = 0;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) {
                        continue;
                    }
                    count++;
                    executeLine(trimmed);
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException("Nem sikerult megnyitni a fajlt: " + file + " (" + ex.getMessage() + ")");
            } finally {
                state.fileLoadStack.remove(normalized);
            }
            actions.ok("Betoltes kesz. Lefuttatott parancsok szama: " + count);
        }
    }

    private static final class GameActions { //Ez hivatalosan nem létezik
        private final GameState state;

        private GameActions(GameState state) {
            this.state = state;
        }

        private void ensureArgCount(List<String> args, int min, int max, String usage) {
            if (args.size() < min || args.size() > max) {
                throw new IllegalArgumentException("Hibas parameterlista. Hasznalat: " + usage);
            }
        }

        private void ensureUniqueName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Ures nev nem megengedett.");
            }
            if (state.existsName(name)) {
                throw new IllegalArgumentException("Mar letezik ilyen azonosito: " + name);
            }
        }

        private void ok(String message) {
            System.out.println("OK: " + message);
        }

        private void error(String message) {
            System.out.println("ERROR: " + message);
        }

        private void handleHelp(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 0, "help");
            List<String> lines = Arrays.asList(
                "nehezseg [easy|medium|hard]",
                "letrehoz [TakaritoJatekos|BuszosJatekos|Hokotro|Busz|Auto] [nev]",
                "szerkeszt [nev] [ujnev]",
                "kivalaszt [nev]",
                "betolt [fajlnev]",
                "utletrehoz [nev] [cs1] [cs2] [hossz] [normal|hid|alagut] [savszam]",
                "lerakodas [utnev] [sav] [Ho|Jeg|So|Feltortjeg|Zuzalek] [ertek]",
                "lepes [ut] [sav-opcionalis]",
                "wait [nev-opcionalis]",
                "hokotrovasarlas [nev]",
                "fejvasarlas [hokotro] [fejtipus]",
                "fejcsere [hokotro] [ujfej]",
                "sotoltes [hokotro]",
                "kerozintoltes [hokotro]",
                "zuzalektoltes [hokotro]",
                "takarit [hokotro] [sav]",
                "allapot [nev|Mind]",
                "terkep",
                "havazas",
                "lista [tipus]",
                "penz [nev] [osszeg]",
                "kilepes"
            );
            ok("Elerheto parancsok:");
            for (String line : lines) {
                System.out.println("  " + line);
            }
        }

        private void handleExit(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 0, "kilepes");
            state.running = false;
            ok("A jatek leall.");
        }

        private void handleDifficulty(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "nehezseg [easy|medium|hard]");
            Difficulty difficulty = Difficulty.fromInput(args.get(0));
            if (difficulty == null) {
                throw new IllegalArgumentException("Ervenytelen nehezseg: " + args.get(0));
            }
            state.difficulty = difficulty;
            ok("Nehezseg beallitva: " + difficulty.name().toLowerCase(Locale.ROOT));
        }

        private void handleCreate(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "letrehoz [tipus] [nev]");
            String typeText = args.get(0).toUpperCase(Locale.ROOT);
            String name = args.get(1);
            ensureUniqueName(name);

            EntityType entityType;
            try {
                entityType = EntityType.valueOf(typeText);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Ismeretlen tipus: " + args.get(0));
            }

            NamedEntity created;
            if (entityType == EntityType.TAKARITOJATEKOS) {
                created = new TakaritoJatekos(name);
            } else if (entityType == EntityType.BUSZOSJATEKOS) {
                created = new BuszosJatekos(name);
            } else if (entityType == EntityType.HOKOTRO) {
                created = new Hokotro(name);
                attachVehicleToSelectedJatekos((Jarmu) created);
            } else if (entityType == EntityType.BUSZ) {
                created = new Busz(name);
                attachVehicleToSelectedJatekos((Jarmu) created);
            } else {
                created = new Auto(name);
                attachVehicleToSelectedJatekos((Jarmu) created);
            }

            state.putEntity(created);
            ok(created.type() + " '" + created.name() + "' sikeresen letrehozva.");
        }

        private void attachVehicleToSelectedJatekos(Jarmu vehicle) {
            NamedEntity selected = state.selected();
            if (selected instanceof Jatekos) {
                Jatekos jatekos = (Jatekos) selected;
                jatekos.addVehicle(vehicle.name);
                vehicle.owner = jatekos.name;
            }
        }

        private void handleRename(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "szerkeszt [nev] [ujnev]");
            String oldName = args.get(0);
            String newName = args.get(1);
            ensureUniqueName(newName);

            NamedEntity entity = state.getEntity(oldName);
            if (entity != null) {
                state.removeEntity(oldName);
                entity.renameTo(newName);
                state.putEntity(entity);
                if (state.selectedName != null && state.selectedName.equalsIgnoreCase(oldName)) {
                    state.selectedName = newName;
                }
                relinkReferences(oldName, newName);
                ok("Atnevezve: '" + oldName + "' -> '" + newName + "'.");
                return;
            }

            Ut ut = state.getUt(oldName);
            if (ut != null) {
                state.removeUt(oldName);
                ut.renameTo(newName);
                state.putUt(ut);
                for (NamedEntity e : state.entities.values()) {
                    if (e instanceof Jarmu) {
                        Jarmu v = (Jarmu) e;
                        if (oldName.equalsIgnoreCase(v.currentUt)) {
                            v.currentUt = newName;
                        }
                    }
                }
                ok("Atnevezve: '" + oldName + "' -> '" + newName + "'.");
                return;
            }

            throw new IllegalArgumentException("Nincs ilyen objektum vagy ut: " + oldName);
        }

        private void relinkReferences(String oldName, String newName) {
            for (NamedEntity e : state.entities.values()) {
                if (e instanceof Jatekos) {
                    Jatekos p = (Jatekos) e;
                    for (int i = 0; i < p.vehicles.size(); i++) {
                        if (p.vehicles.get(i).equalsIgnoreCase(oldName)) {
                            p.vehicles.set(i, newName);
                        }
                    }
                }
                if (e instanceof Jarmu) {
                    Jarmu v = (Jarmu) e;
                    if (oldName.equalsIgnoreCase(v.owner)) {
                        v.owner = newName;
                    }
                }
            }
        }

        private void handleSelect(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "kivalaszt [nev]");
            NamedEntity entity = state.getEntity(args.get(0));
            if (entity == null) {
                throw new IllegalArgumentException("Nem talalhato ilyen objektum: " + args.get(0));
            }
            state.selectedName = entity.name();
            ok("Kivalasztva: " + entity.type() + " " + entity.name());
        }

        private void handleCreateUt(CommandContext context, List<String> args) {
            ensureArgCount(args, 6, 6, "utletrehoz [nev] [cs1] [cs2] [hossz] [tipus] [savszam]");
            String name = args.get(0);
            String cs1 = args.get(1);
            String cs2 = args.get(2);
            int length = parsePositiveInt(args.get(3), "hossz");
            UtTipus type = UtTipus.fromInput(args.get(4));
            int savSzam = parsePositiveInt(args.get(5), "savszam");
            if (type == null) {
                throw new IllegalArgumentException("Ervenytelen uttipus: " + args.get(4));
            }
            ensureUniqueName(name);
            if (cs1.equalsIgnoreCase(cs2)) {
                throw new IllegalArgumentException("Az ut ket vegpontja nem lehet azonos.");
            }
            if (savSzam > 8) {
                throw new IllegalArgumentException("Tul sok sav. Maximum 8 sav engedelyezett.");
            }
            Ut ut = new Ut(name, cs1, cs2, length, type, savSzam);
            state.putUt(ut);
            ok("Ut letrehozva: " + name + " (" + savSzam + " sav).");
        }

        private void handleDeposit(CommandContext context, List<String> args) {
            ensureArgCount(args, 4, 4, "lerakodas [utnev] [sav] [tipus] [ertek]");
            Ut ut = requireUt(args.get(0));
            int savIndex = parseSavIndex(ut, args.get(1));
            DepositType depositType = DepositType.fromInput(args.get(2));
            if (depositType == null) {
                throw new IllegalArgumentException("Ervenytelen lerakodas tipus: " + args.get(2));
            }
            int value = parseNonNegativeInt(args.get(3), "ertek");
            ut.sav(savIndex).applyDeposit(depositType, value);
            ok("Lerakodas rogzitve: " + ut.name + " sav " + savIndex + " -> " + depositType.name());
        }

        private void handleMove(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 2, "lepes [ut] [sav-opcionalis]");
            Jarmu vehicle = requireSelectedVehicle();
            if (!vehicle.canMove()) {
                throw new IllegalArgumentException("A jarmu mozgaskepetlen meg " + vehicle.disabledTime + " korig.");
            }

            Ut target = requireUt(args.get(0));
            int targetSav = vehicle.savIndex;
            if (args.size() == 2) {
                targetSav = parseSavIndex(target, args.get(1));
            }

            vehicle.vegrehajtLepes(target, targetSav, state);

            ok("'" + vehicle.name + "' a(z) '" + target.name + "' " + targetSav + ". savjaba lepett.");
        }

        private void handleWait(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 1, "wait [nev-opcionalis]");
            String who = args.isEmpty() ? "jelenlegi jatekos" : args.get(0);
            state.tickTime();
            ok("Varakozas vegrehajtva: " + who);
        }

        private void handleBuyHokotro(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "hokotrovasarlas [nev]");
            ensureUniqueName(args.get(0));
            TakaritoJatekos jatekos = requireSelectedCleanerJatekos();
            if (!state.jatekosAtTelephely(jatekos)) {
                throw new IllegalArgumentException("A jatekos nem telephelyen van.");
            }

            Hokotro h = jatekos.vasarolHokotro(args.get(0));
            state.putEntity(h);
            ok("Hokotro vasarlas sikeres: " + h.name + ". Penz levonva: 300");
        }

        private void handleStatus(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "allapot [nev|Mind]");
            String name = args.get(0);
            if ("Mind".equalsIgnoreCase(name)) {
                for (NamedEntity entity : state.entities.values()) {
                    System.out.println(entity.statusLine(state));
                }
                for (Ut ut : state.utak.values()) {
                    System.out.println(ut.describeCompact());
                }
                ok("Allapot lekerdezes kesz: Mind.");
                return;
            }

            NamedEntity entity = state.getEntity(name);
            if (entity != null) {
                System.out.println(entity.statusLine(state));
                ok("Allapot lekerdezes kesz: " + name);
                return;
            }

            Ut ut = state.getUt(name);
            if (ut != null) {
                System.out.println(ut.describeCompact());
                ok("Allapot lekerdezes kesz: " + name);
                return;
            }

            throw new IllegalArgumentException("Nincs ilyen objektum: " + name);
        }

        private void handleMap(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 0, "terkep");
            if (state.utak.isEmpty()) {
                throw new IllegalArgumentException("Nincs letrehozott ut a terkepen.");
            }

            Map<String, List<Ut>> byNode = new LinkedHashMap<>();
            for (Ut ut : state.utak.values()) {
                byNode.computeIfAbsent(ut.nodeA, key -> new ArrayList<>()).add(ut);
                byNode.computeIfAbsent(ut.nodeB, key -> new ArrayList<>()).add(ut);
            }

            for (Map.Entry<String, List<Ut>> entry : byNode.entrySet()) {
                System.out.println("Csomopont " + entry.getKey());
                for (Ut ut : entry.getValue()) {
                    String opposite = ut.opposite(entry.getKey());
                    System.out.println("  -> " + opposite + " : " + ut.name + " (" + ut.savSzam() + " sav)");
                }
            }
            ok("Terkep listazva.");
        }

        private void handleFejSwap(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "fejcsere [hokotro] [ujfej]");
            TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
            if (!state.jatekosAtTelephely(jatekos)) {
                throw new IllegalArgumentException("Fejcsere csak telephelyen engedelyezett.");
            }

            Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
            FejTipus ujFej = FejTipus.fromInput(args.get(1));
            if (ujFej == null) {
                throw new IllegalArgumentException("Ismeretlen fejtipus: " + args.get(1));
            }
            h.fejCsere(jatekos, ujFej);
            ok("Fejcsere sikeres: " + h.name + " -> " + ujFej.name());
        }

        private void handleFejBuy(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "fejvasarlas [hokotro] [fejtipus]");
            TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
            if (!state.jatekosAtTelephely(jatekos)) {
                throw new IllegalArgumentException("Fejvasarlas csak telephelyen engedelyezett.");
            }
            requireOwnedHokotro(jatekos, args.get(0));
            FejTipus type = FejTipus.fromInput(args.get(1));
            if (type == null) {
                throw new IllegalArgumentException("Ismeretlen fejtipus: " + args.get(1));
            }
            jatekos.vasarolFej(type);
            ok("Fejvasarlas sikeres: " + type.name() + ", levonas: 80");
        }

        private void handleSaltRefill(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "sotoltes [hokotro]");
            TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
            if (!state.jatekosAtTelephely(jatekos)) {
                throw new IllegalArgumentException("Sotoltes csak telephelyen engedelyezett.");
            }
            Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
            h.sotoltes(jatekos);
            ok("'" + h.name + "' sokeszlete feltoltve. Penz levonva: 50");
        }

        private void handleFuelRefill(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "kerozintoltes [hokotro]");
            TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
            if (!state.jatekosAtTelephely(jatekos)) {
                throw new IllegalArgumentException("Kerozintoltes csak telephelyen engedelyezett.");
            }
            Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
            h.kerozintoltes(jatekos);
            ok("'" + h.name + "' kerozinkeszlete feltoltve. Penz levonva: 60");
        }

        private void handleGritRefill(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "zuzalektoltes [hokotro]");
            TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
            if (!state.jatekosAtTelephely(jatekos)) {
                throw new IllegalArgumentException("Zuzalektoltes csak telephelyen engedelyezett.");
            }
            Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
            h.zuzalektoltes(jatekos);
            ok("'" + h.name + "' zuzalekkeszlete feltoltve. Penz levonva: 40");
        }

        private void handleCleanSav(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "takarit [hokotro] [sav]");
            Hokotro h = requireHokotro(args.get(0));
            Ut ut = h.aktualisUt(state);
            int savIndex = parseSavIndex(ut, args.get(1));
            h.takaritSav(ut, savIndex, state);
            ok("'" + h.name + "' takaritasa lefutott a(z) " + ut.name + " " + savIndex + ". savon.");
        }

        private void handleHoeses(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 0, "havazas");
            if (state.utak.isEmpty()) {
                throw new IllegalArgumentException("Nincs ut a halozatban.");
            }
            ok("Havazas szimulacio elinditva.");
            for (Ut ut : state.utak.values()) {
                int delta = ut.alkalmazHoEses();
                if (delta != 0) {
                    state.enqueueEvent(ut.name + " savjain a ho +" + delta + " egyseggel nott.");
                } else {
                    state.enqueueEvent(ut.name + " savjain nem valtozott a horetag.");
                }
            }
            state.tickTime();
            state.evaluateGameOver();
        }

        private void handleListByType(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "lista [tipus]");
            String type = args.get(0).toLowerCase(Locale.ROOT);
            int count = 0;
            for (NamedEntity entity : state.entities.values()) {
                if (entity.type().toLowerCase(Locale.ROOT).equals(type)) {
                    System.out.println(entity.statusLine(state));
                    count++;
                }
            }
            if (count == 0) {
                throw new IllegalArgumentException("Nincs ilyen tipusu objektum: " + args.get(0));
            }
            ok("Listazas kesz. Talalat: " + count);
        }

        private int parsePositiveInt(String raw, String fieldName) {
            int value;
            try {
                value = Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("A(z) " + fieldName + " nem szam: " + raw);
            }
            if (value <= 0) {
                throw new IllegalArgumentException("A(z) " + fieldName + " pozitiv kell legyen.");
            }
            return value;
        }

        private int parseNonNegativeInt(String raw, String fieldName) {
            int value;
            try {
                value = Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("A(z) " + fieldName + " nem szam: " + raw);
            }
            if (value < 0) {
                throw new IllegalArgumentException("A(z) " + fieldName + " nem lehet negativ.");
            }
            return value;
        }

        private int parseSavIndex(Ut ut, String raw) {
            int idx = parseNonNegativeInt(raw, "sav");
            if (idx >= ut.savSzam()) {
                throw new IllegalArgumentException("Ervenytelen sav index: " + idx + ", max: " + (ut.savSzam() - 1));
            }
            return idx;
        }

        private Ut requireUt(String name) {
            Ut ut = state.getUt(name);
            if (ut == null) {
                throw new IllegalArgumentException("Nincs ilyen ut: " + name);
            }
            return ut;
        }

        private Jarmu requireSelectedVehicle() {
            NamedEntity selected = state.selected();
            if (!(selected instanceof Jarmu)) {
                throw new IllegalArgumentException("Nincs kivalasztott jarmu. Hasznald: kivalaszt [jarmuNev]");
            }
            return (Jarmu) selected;
        }

        private TakaritoJatekos requireSelectedCleanerJatekos() {
            NamedEntity selected = state.selected();
            if (!(selected instanceof TakaritoJatekos)) {
                throw new IllegalArgumentException("A muvelethez TakaritoJatekos kivalasztasa szukseges.");
            }
            return (TakaritoJatekos) selected;
        }

        private TakaritoJatekos resolveCleanerForHokotroAction(String hokotroName) {
            NamedEntity selected = state.selected();
            if (selected instanceof TakaritoJatekos) {
                return (TakaritoJatekos) selected;
            }

            Hokotro h = state.getTypedEntity(hokotroName, Hokotro.class);
            if (h != null && h.owner != null) {
                TakaritoJatekos owner = state.getTypedEntity(h.owner, TakaritoJatekos.class);
                if (owner != null) {
                    return owner;
                }
            }

            throw new IllegalArgumentException("A muvelethez TakaritoJatekos kivalasztasa szukseges.");
        }

        private Hokotro requireOwnedHokotro(TakaritoJatekos jatekos, String name) {
            Hokotro h = state.getTypedEntity(name, Hokotro.class);
            if (h == null) {
                throw new IllegalArgumentException("Nincs ilyen hokotro: " + name);
            }
            if (!jatekos.vehicles.contains(h.name)) {
                throw new IllegalArgumentException("A hokotro nem a kivalasztott jatekoshoz tartozik: " + name);
            }
            return h;
        }

        private Hokotro requireHokotro(String name) {
            Hokotro h = state.getTypedEntity(name, Hokotro.class);
            if (h == null) {
                throw new IllegalArgumentException("Nincs ilyen hokotro: " + name);
            }
            return h;
        }

        private void handleSetPenz(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "penz [nev] [osszeg]");
            String name = args.get(0);
            
            // A te beépített parseNonNegativeInt metódusodat használjuk!
            int amount = parseNonNegativeInt(args.get(1), "osszeg"); 

            Jatekos jatekos = state.getTypedEntity(name, Jatekos.class);
            if (jatekos == null) {
                throw new IllegalArgumentException("Nincs ilyen jatekos: " + name);
            }
            
            jatekos.setPenz(amount);
            ok("'" + name + "' egyenlege beallitva: " + amount);
        }
    }
}
