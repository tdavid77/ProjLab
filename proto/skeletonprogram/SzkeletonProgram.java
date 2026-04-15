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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

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

    private enum RoadType {
        NORMAL,
        HID,
        ALAGUT;

        public static RoadType fromInput(String value) {
            for (RoadType t : values()) {
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

    private enum HeadType {
        SOPROFEJ,
        HANYOFEJ,
        JEGTOROFEJ,
        SARKANYFEJ,
        SOSZOROFEJ,
        ZUZOTTKOSZOROFEJ;

        public static HeadType fromInput(String value) {
            if (value == null) {
                return null;
            }
            String normalized = value.trim().toUpperCase(Locale.ROOT);
            for (HeadType type : values()) {
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

    private static final class Road {
        private String name;
        private final String nodeA;
        private final String nodeB;
        private final int length;
        private final RoadType type;
        private final List<Lane> lanes;

        private Road(String name, String nodeA, String nodeB, int length, RoadType type, int laneCount) {
            this.name = name;
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.length = length;
            this.type = type;
            this.lanes = new ArrayList<>();
            for (int i = 0; i < laneCount; i++) {
                lanes.add(new Lane(i));
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

        public int laneCount() {
            return lanes.size();
        }

        public Lane lane(int idx) {
            return lanes.get(idx);
        }

        public String describeCompact() {
            StringBuilder sb = new StringBuilder();
            sb.append("Ut ").append(name).append(" (").append(type.name()).append(")")
                .append(" | Szomszedok:[").append(nodeA).append(", ").append(nodeB).append("]")
                .append(" | Savok: ");
            List<String> laneText = new ArrayList<>();
            for (Lane lane : lanes) {
                laneText.add(lane.describeCompact());
            }
            sb.append(String.join(", ", laneText));
            return sb.toString();
        }
    }

    private static final class Lane {
        private final int index;
        private int snow;
        private int ice;
        private int brokenIce;
        private int saltedRounds;
        private int grittedRounds;

        private Lane(int index) {
            this.index = index;
        }

        public void applyDeposit(DepositType type, int value) {
            if (type == DepositType.HO) {
                snow = Math.max(0, value);
            }
            if (type == DepositType.JEG) {
                ice = Math.max(0, value);
            }
            if (type == DepositType.SO) {
                saltedRounds = Math.max(0, value);
            }
            if (type == DepositType.FELTORTJEG) {
                brokenIce = Math.max(0, value);
            }
            if (type == DepositType.ZUZALEK) {
                grittedRounds = Math.max(0, value);
            }
        }

        public void snowingOneUnit(boolean protectedByTunnel) {
            if (protectedByTunnel) {
                return;
            }
            if (saltedRounds > 0) {
                return;
            }
            snow += 1;
        }

        public void tickRound() {
            if (saltedRounds > 0) {
                saltedRounds -= 1;
            }
            if (grittedRounds > 0) {
                grittedRounds -= 1;
            }
        }

        public String describeCompact() {
            return "[Sav" + index + "(Ho:" + snow + ", Jeg:" + ice + ", Sozott:" + (saltedRounds > 0 ? "I" : "N") + ")]";
        }
    }

    private static class Jatekos implements NamedEntity {
        protected String name;
        protected int money = 1000;
        protected final List<String> vehicles = new ArrayList<>();
        protected final List<HeadType> inventory = new ArrayList<>();

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

        public boolean removeHeadFromInventory(HeadType headType) {
            return inventory.remove(headType);
        }

        public void addHeadToInventory(HeadType headType) {
            inventory.add(headType);
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
        protected String currentRoad;
        protected int laneIndex;
        protected int disabledRounds;

        private Jarmu(String name) {
            this.name = name;
            this.currentRoad = null;
            this.laneIndex = 0;
            this.disabledRounds = 0;
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
            if (currentRoad == null) {
                pos = "Telephely";
            } else {
                pos = currentRoad + ", " + laneIndex;
            }
            String allapot = disabledRounds > 0 ? "Baleset(" + disabledRounds + " kor)" : "Aktiv";
            return type() + " " + name + " | Poz:" + pos + " | Allapot:" + allapot;
        }

        public boolean canMove() {
            return disabledRounds <= 0;
        }

        public void tickRound() {
            if (disabledRounds > 0) {
                disabledRounds -= 1;
            }
        }
    }

    private static final class Hokotro extends Jarmu {
        private Fej activeHead;
        private int so;
        private int kerozin;
        private int zuzottko;

        private Hokotro(String name) {
            super(name);
            this.activeHead = FejFactory.create(HeadType.SOPROFEJ);
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
            if (currentRoad == null) {
                pos = "Telephely";
            } else {
                pos = currentRoad + ", " + laneIndex;
            }
            String allapot = disabledRounds > 0 ? "Baleset(" + disabledRounds + " kor)" : "Aktiv";
            String headName = activeHead == null ? HeadType.SOPROFEJ.name() : activeHead.tipus().name();
            return "Hokotro " + name
                + " | Poz:" + pos
                + " | Fej:" + headName
                + " | Keszletek:[So:" + so + ", Kerozin:" + kerozin + ", Zuzottko:" + zuzottko + "]"
                + " | Allapot:" + allapot;
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
        private final HeadType tipus;

        protected Fej(HeadType tipus) {
            this.tipus = tipus;
        }

        public HeadType tipus() {
            return tipus;
        }

        public abstract void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state);
    }

    private static final class SoproFej extends Fej {
        private SoproFej() {
            super(HeadType.SOPROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state) {
            int movedSnow = lane.snow + lane.brokenIce;
            lane.snow = 0;
            lane.brokenIce = 0;
            int rightIndex = laneIndex + 1;
            if (movedSnow > 0 && rightIndex < road.laneCount()) {
                Lane rightLane = road.lane(rightIndex);
                rightLane.snow += movedSnow;
            }
            state.enqueueEvent("Soprofej atterelte a csapadekot.");
        }
    }

    private static final class HanyoFej extends Fej {
        private HanyoFej() {
            super(HeadType.HANYOFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state) {
            lane.snow = 0;
            lane.brokenIce = 0;
            state.enqueueEvent("Hanyofej az ut melle tavolitotta a csapadekot.");
        }
    }

    private static final class JegtoroFej extends Fej {
        private JegtoroFej() {
            super(HeadType.JEGTOROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state) {
            lane.brokenIce += lane.ice;
            lane.ice = 0;
            lane.snow += lane.brokenIce;
            lane.brokenIce = 0;
            state.enqueueEvent("Jegtorofej feltorte a jeget, ho jellegu retegre valtott.");
        }
    }

    private static final class SoszoroFej extends Fej {
        private SoszoroFej() {
            super(HeadType.SOSZOROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state) {
            if (h.so < 10) {
                throw new IllegalArgumentException("Nincs eleg so a soszorofejhez.");
            }
            h.so -= 10;
            lane.snow = 0;
            lane.ice = 0;
            lane.saltedRounds = 3;
            state.enqueueEvent("Soszoro fej aktiv: ho es jeg eltunt, 3 korig vedett a sav.");
        }
    }

    private static final class SarkanyFej extends Fej {
        private SarkanyFej() {
            super(HeadType.SARKANYFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state) {
            if (h.kerozin < 10) {
                throw new IllegalArgumentException("Nincs eleg kerozin a sarkanyfejhez.");
            }
            h.kerozin -= 10;
            lane.snow = 0;
            lane.ice = 0;
            lane.brokenIce = 0;
            state.enqueueEvent("Sarkanyfej aktiv: azonnali olvasztas vegrehajtva.");
        }
    }

    private static final class ZuzottkoSzoroFej extends Fej {
        private ZuzottkoSzoroFej() {
            super(HeadType.ZUZOTTKOSZOROFEJ);
        }

        @Override
        public void takaritHatas(Hokotro h, Lane lane, Road road, int laneIndex, GameState state) {
            if (h.zuzottko < 10) {
                throw new IllegalArgumentException("Nincs eleg zuzottko a zuzottkoszorofejhez.");
            }
            h.zuzottko -= 10;
            lane.grittedRounds = 3;
            state.enqueueEvent("Zuzottko kiszorva: csuszasveszely csokkent 3 korre.");
        }
    }

    private static final class FejFactory {
        private FejFactory() {
        }

        public static Fej create(HeadType type) {
            if (type == null) {
                return new SoproFej();
            }
            if (type == HeadType.HANYOFEJ) {
                return new HanyoFej();
            }
            if (type == HeadType.JEGTOROFEJ) {
                return new JegtoroFej();
            }
            if (type == HeadType.SARKANYFEJ) {
                return new SarkanyFej();
            }
            if (type == HeadType.SOSZOROFEJ) {
                return new SoszoroFej();
            }
            if (type == HeadType.ZUZOTTKOSZOROFEJ) {
                return new ZuzottkoSzoroFej();
            }
            return new SoproFej();
        }
    }

    private static final class GameState {
        private final Map<String, NamedEntity> entities = new LinkedHashMap<>();
        private final Map<String, Road> roads = new LinkedHashMap<>();
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
            return entities.containsKey(key) || roads.containsKey(key);
        }

        public void putEntity(NamedEntity entity) {
            entities.put(entity.name().toLowerCase(Locale.ROOT), entity);
        }

        public void removeEntity(String name) {
            entities.remove(name.toLowerCase(Locale.ROOT));
        }

        public Road getRoad(String name) {
            if (name == null) {
                return null;
            }
            return roads.get(name.toLowerCase(Locale.ROOT));
        }

        public void putRoad(Road road) {
            roads.put(road.name().toLowerCase(Locale.ROOT), road);
            graph.computeIfAbsent(road.nodeA.toLowerCase(Locale.ROOT), key -> new ArrayList<>()).add(road.name());
            graph.computeIfAbsent(road.nodeB.toLowerCase(Locale.ROOT), key -> new ArrayList<>()).add(road.name());
        }

        public void removeRoad(String name) {
            roads.remove(name.toLowerCase(Locale.ROOT));
        }

        public boolean playerAtDepot(Jatekos player) {
            for (String vehicleName : player.vehicles) {
                Jarmu vehicle = getTypedEntity(vehicleName, Jarmu.class);
                if (vehicle == null) {
                    continue;
                }
                if (vehicle.currentRoad == null) {
                    return true;
                }
                Road road = getRoad(vehicle.currentRoad);
                if (road != null && (road.hasNode(depotNode))) {
                    return true;
                }
            }
            return player.vehicles.isEmpty();
        }

        public void enqueueEvent(String line) {
            eventQueue.addLast(line);
        }

        public void flushEvents() {
            while (!eventQueue.isEmpty()) {
                System.out.println("[ESEMENY] " + eventQueue.removeFirst());
            }
        }

        public void tickRound() {
            for (Road road : roads.values()) {
                for (Lane lane : road.lanes) {
                    lane.tickRound();
                }
            }
            for (NamedEntity entity : entities.values()) {
                if (entity instanceof Jarmu) {
                    ((Jarmu) entity).tickRound();
                }
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
            commands.put("utletrehoz", actions::handleCreateRoad);
            commands.put("lerakodas", actions::handleDeposit);
            commands.put("lepes", actions::handleMove);
            commands.put("wait", actions::handleWait);
            commands.put("hokotrovasarlas", actions::handleBuySnowplow);
            commands.put("allapot", actions::handleStatus);
            commands.put("terkep", actions::handleMap);
            commands.put("fejcsere", actions::handleHeadSwap);
            commands.put("fejvasarlas", actions::handleHeadBuy);
            commands.put("sotoltes", actions::handleSaltRefill);
            commands.put("kerozintoltes", actions::handleFuelRefill);
            commands.put("zuzalektoltes", actions::handleGritRefill);
            commands.put("takarit", actions::handleCleanLane);
            commands.put("havazas", actions::handleSnowfall);
            commands.put("lista", actions::handleListByType);
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

    private static final class GameActions {
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
                attachVehicleToSelectedPlayer((Jarmu) created);
            } else if (entityType == EntityType.BUSZ) {
                created = new Busz(name);
                attachVehicleToSelectedPlayer((Jarmu) created);
            } else {
                created = new Auto(name);
                attachVehicleToSelectedPlayer((Jarmu) created);
            }

            state.putEntity(created);
            ok(created.type() + " '" + created.name() + "' sikeresen letrehozva.");
        }

        private void attachVehicleToSelectedPlayer(Jarmu vehicle) {
            NamedEntity selected = state.selected();
            if (selected instanceof Jatekos) {
                Jatekos player = (Jatekos) selected;
                player.addVehicle(vehicle.name);
                vehicle.owner = player.name;
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

            Road road = state.getRoad(oldName);
            if (road != null) {
                state.removeRoad(oldName);
                road.renameTo(newName);
                state.putRoad(road);
                for (NamedEntity e : state.entities.values()) {
                    if (e instanceof Jarmu) {
                        Jarmu v = (Jarmu) e;
                        if (oldName.equalsIgnoreCase(v.currentRoad)) {
                            v.currentRoad = newName;
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

        private void handleCreateRoad(CommandContext context, List<String> args) {
            ensureArgCount(args, 6, 6, "utletrehoz [nev] [cs1] [cs2] [hossz] [tipus] [savszam]");
            String name = args.get(0);
            String cs1 = args.get(1);
            String cs2 = args.get(2);
            int length = parsePositiveInt(args.get(3), "hossz");
            RoadType type = RoadType.fromInput(args.get(4));
            int laneCount = parsePositiveInt(args.get(5), "savszam");
            if (type == null) {
                throw new IllegalArgumentException("Ervenytelen uttipus: " + args.get(4));
            }
            ensureUniqueName(name);
            if (cs1.equalsIgnoreCase(cs2)) {
                throw new IllegalArgumentException("Az ut ket vegpontja nem lehet azonos.");
            }
            if (laneCount > 8) {
                throw new IllegalArgumentException("Tul sok sav. Maximum 8 sav engedelyezett.");
            }
            Road road = new Road(name, cs1, cs2, length, type, laneCount);
            state.putRoad(road);
            ok("Ut letrehozva: " + name + " (" + laneCount + " sav).");
        }

        private void handleDeposit(CommandContext context, List<String> args) {
            ensureArgCount(args, 4, 4, "lerakodas [utnev] [sav] [tipus] [ertek]");
            Road road = requireRoad(args.get(0));
            int laneIndex = parseLaneIndex(road, args.get(1));
            DepositType depositType = DepositType.fromInput(args.get(2));
            if (depositType == null) {
                throw new IllegalArgumentException("Ervenytelen lerakodas tipus: " + args.get(2));
            }
            int value = parseNonNegativeInt(args.get(3), "ertek");
            road.lane(laneIndex).applyDeposit(depositType, value);
            ok("Lerakodas rogzitve: " + road.name + " sav " + laneIndex + " -> " + depositType.name());
        }

        private void handleMove(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 2, "lepes [ut] [sav-opcionalis]");
            Jarmu vehicle = requireSelectedVehicle();
            if (!vehicle.canMove()) {
                throw new IllegalArgumentException("A jarmu mozgaskepetlen meg " + vehicle.disabledRounds + " korig.");
            }

            Road target = requireRoad(args.get(0));
            int targetLane = vehicle.laneIndex;
            if (args.size() == 2) {
                targetLane = parseLaneIndex(target, args.get(1));
            }

            if (vehicle.currentRoad != null) {
                Road current = requireRoad(vehicle.currentRoad);
                boolean sharesNode = current.hasNode(target.nodeA) || current.hasNode(target.nodeB);
                if (!sharesNode && !current.name.equalsIgnoreCase(target.name)) {
                    throw new IllegalArgumentException("A celut nem szomszedos a jelenlegi uttal.");
                }
            }

            vehicle.currentRoad = target.name;
            vehicle.laneIndex = targetLane;

            maybeCrash(vehicle, target.lane(targetLane));
            if (vehicle instanceof Busz) {
                maybeRegisterBusTrip((Busz) vehicle, target);
            }

            ok("'" + vehicle.name + "' a(z) '" + target.name + "' " + targetLane + ". savjaba lepett.");
        }

        private void maybeRegisterBusTrip(Busz busz, Road target) {
            if (target.hasNode("Vegallomas") || target.hasNode("Vegallomas_1") || target.hasNode("Vegallomas_2")) {
                busz.completedTrips += 1;
                Jatekos owner = state.getTypedEntity(busz.owner, Jatekos.class);
                if (owner != null) {
                    owner.money += 40;
                    state.enqueueEvent("Busz kor teljesitve: " + busz.name + ", jovairas +40.");
                }
            }
        }

        private void maybeCrash(Jarmu vehicle, Lane lane) {
            if (vehicle instanceof Hokotro) {
                return;
            }
            if (lane.ice <= 0) {
                return;
            }
            double modifier = lane.grittedRounds > 0 ? -0.12 : 0.0;
            double chance = Math.max(0.0, Math.min(0.95, state.difficulty.baseCrashChance() + (lane.ice * 0.03) + modifier));
            if (state.random.nextDouble() < chance) {
                vehicle.disabledRounds = 2;
                state.accidents += 1;
                state.enqueueEvent(vehicle.name + " megcsuszott jeges savon, 2 korre kiesett.");
            }
        }

        private void handleWait(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 1, "wait [nev-opcionalis]");
            String who = args.isEmpty() ? "jelenlegi jatekos" : args.get(0);
            state.tickRound();
            ok("Varakozas vegrehajtva: " + who);
        }

        private void handleBuySnowplow(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "hokotrovasarlas [nev]");
            ensureUniqueName(args.get(0));
            TakaritoJatekos player = requireSelectedCleanerPlayer();
            if (!state.playerAtDepot(player)) {
                throw new IllegalArgumentException("A jatekos nem telephelyen van.");
            }
            int price = 300;
            if (!player.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz. Szukseges: " + price);
            }
            player.charge(price);
            Hokotro h = new Hokotro(args.get(0));
            h.owner = player.name;
            state.putEntity(h);
            player.addVehicle(h.name);
            ok("Hokotro vasarlas sikeres: " + h.name + ". Penz levonva: " + price);
        }

        private void handleStatus(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "allapot [nev|Mind]");
            String name = args.get(0);
            if ("Mind".equalsIgnoreCase(name)) {
                for (NamedEntity entity : state.entities.values()) {
                    System.out.println(entity.statusLine(state));
                }
                for (Road road : state.roads.values()) {
                    System.out.println(road.describeCompact());
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

            Road road = state.getRoad(name);
            if (road != null) {
                System.out.println(road.describeCompact());
                ok("Allapot lekerdezes kesz: " + name);
                return;
            }

            throw new IllegalArgumentException("Nincs ilyen objektum: " + name);
        }

        private void handleMap(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 0, "terkep");
            if (state.roads.isEmpty()) {
                throw new IllegalArgumentException("Nincs letrehozott ut a terkepen.");
            }

            Map<String, List<Road>> byNode = new LinkedHashMap<>();
            for (Road road : state.roads.values()) {
                byNode.computeIfAbsent(road.nodeA, key -> new ArrayList<>()).add(road);
                byNode.computeIfAbsent(road.nodeB, key -> new ArrayList<>()).add(road);
            }

            for (Map.Entry<String, List<Road>> entry : byNode.entrySet()) {
                System.out.println("Csomopont " + entry.getKey());
                for (Road road : entry.getValue()) {
                    String opposite = road.opposite(entry.getKey());
                    System.out.println("  -> " + opposite + " : " + road.name + " (" + road.laneCount() + " sav)");
                }
            }
            ok("Terkep listazva.");
        }

        private void handleHeadSwap(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "fejcsere [hokotro] [ujfej]");
            TakaritoJatekos player = resolveCleanerForSnowplowAction(args.get(0));
            if (!state.playerAtDepot(player)) {
                throw new IllegalArgumentException("Fejcsere csak telephelyen engedelyezett.");
            }

            Hokotro h = requireOwnedSnowplow(player, args.get(0));
            HeadType newHead = HeadType.fromInput(args.get(1));
            if (newHead == null) {
                throw new IllegalArgumentException("Ismeretlen fejtipus: " + args.get(1));
            }
            if (!player.removeHeadFromInventory(newHead)) {
                throw new IllegalArgumentException("A kivant fej nincs a raktarban: " + newHead.name());
            }

            if (h.activeHead != null) {
                player.addHeadToInventory(h.activeHead.tipus());
            }
            h.activeHead = FejFactory.create(newHead);
            ok("Fejcsere sikeres: " + h.name + " -> " + newHead.name());
        }

        private void handleHeadBuy(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "fejvasarlas [hokotro] [fejtipus]");
            TakaritoJatekos player = resolveCleanerForSnowplowAction(args.get(0));
            if (!state.playerAtDepot(player)) {
                throw new IllegalArgumentException("Fejvasarlas csak telephelyen engedelyezett.");
            }
            requireOwnedSnowplow(player, args.get(0));
            HeadType type = HeadType.fromInput(args.get(1));
            if (type == null) {
                throw new IllegalArgumentException("Ismeretlen fejtipus: " + args.get(1));
            }
            int price = 80;
            if (!player.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz a fej vasarlashoz.");
            }
            player.charge(price);
            player.addHeadToInventory(type);
            ok("Fejvasarlas sikeres: " + type.name() + ", levonas: " + price);
        }

        private void handleSaltRefill(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "sotoltes [hokotro]");
            TakaritoJatekos player = resolveCleanerForSnowplowAction(args.get(0));
            if (!state.playerAtDepot(player)) {
                throw new IllegalArgumentException("Sotoltes csak telephelyen engedelyezett.");
            }
            Hokotro h = requireOwnedSnowplow(player, args.get(0));
            int price = 50;
            if (!player.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz sotolteshez.");
            }
            player.charge(price);
            h.so = 100;
            ok("'" + h.name + "' sokeszlete feltoltve. Penz levonva: " + price);
        }

        private void handleFuelRefill(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "kerozintoltes [hokotro]");
            TakaritoJatekos player = resolveCleanerForSnowplowAction(args.get(0));
            if (!state.playerAtDepot(player)) {
                throw new IllegalArgumentException("Kerozintoltes csak telephelyen engedelyezett.");
            }
            Hokotro h = requireOwnedSnowplow(player, args.get(0));
            int price = 60;
            if (!player.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz kerozintolteshez.");
            }
            player.charge(price);
            h.kerozin = 100;
            ok("'" + h.name + "' kerozinkeszlete feltoltve. Penz levonva: " + price);
        }

        private void handleGritRefill(CommandContext context, List<String> args) {
            ensureArgCount(args, 1, 1, "zuzalektoltes [hokotro]");
            TakaritoJatekos player = resolveCleanerForSnowplowAction(args.get(0));
            if (!state.playerAtDepot(player)) {
                throw new IllegalArgumentException("Zuzalektoltes csak telephelyen engedelyezett.");
            }
            Hokotro h = requireOwnedSnowplow(player, args.get(0));
            int price = 40;
            if (!player.canAfford(price)) {
                throw new IllegalArgumentException("Nincs eleg penz zuzalektolteshez.");
            }
            player.charge(price);
            h.zuzottko = 100;
            ok("'" + h.name + "' zuzalekkeszlete feltoltve. Penz levonva: " + price);
        }

        private void handleCleanLane(CommandContext context, List<String> args) {
            ensureArgCount(args, 2, 2, "takarit [hokotro] [sav]");
            Hokotro h = requireSnowplow(args.get(0));
            if (h.currentRoad == null) {
                throw new IllegalArgumentException("A hokotro nincs uton, nincs mit takaritani.");
            }
            Road road = requireRoad(h.currentRoad);
            int laneIndex = parseLaneIndex(road, args.get(1));
            Lane lane = road.lane(laneIndex);
            if (h.activeHead == null) {
                h.activeHead = FejFactory.create(HeadType.SOPROFEJ);
            }
            h.activeHead.takaritHatas(h, lane, road, laneIndex, state);
            ok("'" + h.name + "' takaritasa lefutott a(z) " + road.name + " " + laneIndex + ". savon.");
        }

        private void handleSnowfall(CommandContext context, List<String> args) {
            ensureArgCount(args, 0, 0, "havazas");
            if (state.roads.isEmpty()) {
                throw new IllegalArgumentException("Nincs ut a halozatban.");
            }
            ok("Havazas szimulacio elinditva.");
            for (Road road : state.roads.values()) {
                int before = sumSnow(road);
                for (Lane lane : road.lanes) {
                    lane.snowingOneUnit(road.type == RoadType.ALAGUT);
                }
                int after = sumSnow(road);
                if (after != before) {
                    state.enqueueEvent(road.name + " savjain a ho +" + (after - before) + " egyseggel nott.");
                } else {
                    state.enqueueEvent(road.name + " savjain nem valtozott a horetag.");
                }
            }
            state.tickRound();
            evaluateGameOver();
        }

        private int sumSnow(Road road) {
            int total = 0;
            for (Lane lane : road.lanes) {
                total += lane.snow;
            }
            return total;
        }

        private void evaluateGameOver() {
            long busCount = state.entities.values().stream().filter(e -> e instanceof Busz).count();
            long disabledBusCount = state.entities.values().stream()
                .filter(e -> e instanceof Busz)
                .map(e -> (Busz) e)
                .filter(b -> !b.canMove())
                .count();
            if (busCount > 0 && disabledBusCount == busCount) {
                state.enqueueEvent("JATEK VEGE: minden busz mozgaskepetlen.");
                state.running = false;
                return;
            }
            if (state.accidents >= 5) {
                state.enqueueEvent("JATEK VEGE: kijarasi tilalom, kritikus balesetszam.");
                state.running = false;
            }
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

        private int parseLaneIndex(Road road, String raw) {
            int idx = parseNonNegativeInt(raw, "sav");
            if (idx >= road.laneCount()) {
                throw new IllegalArgumentException("Ervenytelen sav index: " + idx + ", max: " + (road.laneCount() - 1));
            }
            return idx;
        }

        private Road requireRoad(String name) {
            Road road = state.getRoad(name);
            if (road == null) {
                throw new IllegalArgumentException("Nincs ilyen ut: " + name);
            }
            return road;
        }

        private Jarmu requireSelectedVehicle() {
            NamedEntity selected = state.selected();
            if (!(selected instanceof Jarmu)) {
                throw new IllegalArgumentException("Nincs kivalasztott jarmu. Hasznald: kivalaszt [jarmuNev]");
            }
            return (Jarmu) selected;
        }

        private TakaritoJatekos requireSelectedCleanerPlayer() {
            NamedEntity selected = state.selected();
            if (!(selected instanceof TakaritoJatekos)) {
                throw new IllegalArgumentException("A muvelethez TakaritoJatekos kivalasztasa szukseges.");
            }
            return (TakaritoJatekos) selected;
        }

        private TakaritoJatekos resolveCleanerForSnowplowAction(String snowplowName) {
            NamedEntity selected = state.selected();
            if (selected instanceof TakaritoJatekos) {
                return (TakaritoJatekos) selected;
            }

            Hokotro h = state.getTypedEntity(snowplowName, Hokotro.class);
            if (h != null && h.owner != null) {
                TakaritoJatekos owner = state.getTypedEntity(h.owner, TakaritoJatekos.class);
                if (owner != null) {
                    return owner;
                }
            }

            throw new IllegalArgumentException("A muvelethez TakaritoJatekos kivalasztasa szukseges.");
        }

        private Hokotro requireOwnedSnowplow(TakaritoJatekos player, String name) {
            Hokotro h = state.getTypedEntity(name, Hokotro.class);
            if (h == null) {
                throw new IllegalArgumentException("Nincs ilyen hokotro: " + name);
            }
            if (!player.vehicles.contains(h.name)) {
                throw new IllegalArgumentException("A hokotro nem a kivalasztott jatekoshoz tartozik: " + name);
            }
            return h;
        }

        private Hokotro requireSnowplow(String name) {
            Hokotro h = state.getTypedEntity(name, Hokotro.class);
            if (h == null) {
                throw new IllegalArgumentException("Nincs ilyen hokotro: " + name);
            }
            return h;
        }
    }
}
