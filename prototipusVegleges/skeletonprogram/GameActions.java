package skeletonprogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GameActions {
    private final GameState state;

    public GameActions(GameState state) {
        this.state = state;
    }

    void ensureArgCount(List<String> args, int min, int max, String usage) {
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

    void ok(String message) {
        System.out.println("OK: " + message);
    }

    void error(String message) {
        System.out.println("ERROR: " + message);
    }

    void handleHelp(CommandContext context, List<String> args) {
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

    void handleExit(CommandContext context, List<String> args) {
        ensureArgCount(args, 0, 0, "kilepes");
        state.running = false;
        ok("A jatek leall.");
    }

    void handleDifficulty(CommandContext context, List<String> args) {
        ensureArgCount(args, 1, 1, "nehezseg [easy|medium|hard]");
        Difficulty difficulty = Difficulty.fromInput(args.get(0));
        if (difficulty == null) {
            throw new IllegalArgumentException("Ervenytelen nehezseg: " + args.get(0));
        }
        state.difficulty = difficulty;
        ok("Nehezseg beallitva: " + difficulty.name().toLowerCase(Locale.ROOT));
    }

    void handleCreate(CommandContext context, List<String> args) {
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

    void handleRename(CommandContext context, List<String> args) {
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

    void handleSelect(CommandContext context, List<String> args) {
        ensureArgCount(args, 1, 1, "kivalaszt [nev]");
        NamedEntity entity = state.getEntity(args.get(0));
        if (entity == null) {
            throw new IllegalArgumentException("Nem talalhato ilyen objektum: " + args.get(0));
        }
        state.selectedName = entity.name();
        ok("Kivalasztva: " + entity.type() + " " + entity.name());
    }

    void handleCreateUt(CommandContext context, List<String> args) {
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

    void handleDeposit(CommandContext context, List<String> args) {
        ensureArgCount(args, 4, 4, "lerakodas [utnev] [sav] [tipus] [ertek]");
        Ut ut = requireUt(args.get(0));
        int savIndex = parseSavIndex(ut, args.get(1));
        DepositType depositType = DepositType.fromInput(args.get(2));
        if (depositType == null) {
            throw new IllegalArgumentException("Ervenytelen lerakodas tipus: " + args.get(2));
        }
        int value = parseNonNegativeInt(args.get(3), "ertek");
        ut.sav(savIndex).applyDeposit(depositType, value);
        ok("Lerakodas rogzitve: " + ut.name() + " sav " + savIndex + " -> " + depositType.name());
    }

    void handleMove(CommandContext context, List<String> args) {
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

        ok("'" + vehicle.name + "' a(z) '" + target.name() + "' " + targetSav + ". savjaba lepett.");
    }

    void handleWait(CommandContext context, List<String> args) {
        ensureArgCount(args, 0, 1, "wait [nev-opcionalis]");
        String who = args.isEmpty() ? "jelenlegi jatekos" : args.get(0);
        state.tickTime();
        ok("Varakozas vegrehajtva: " + who);
    }

    void handleBuyHokotro(CommandContext context, List<String> args) {
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

    void handleStatus(CommandContext context, List<String> args) {
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

    void handleMap(CommandContext context, List<String> args) {
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
                System.out.println("  -> " + opposite + " : " + ut.name() + " (" + ut.savSzam() + " sav)");
            }
        }
        ok("Terkep listazva.");
    }

    void handleFejSwap(CommandContext context, List<String> args) {
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

    void handleFejBuy(CommandContext context, List<String> args) {
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

    void handleSaltRefill(CommandContext context, List<String> args) {
        ensureArgCount(args, 1, 1, "sotoltes [hokotro]");
        TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
        if (!state.jatekosAtTelephely(jatekos)) {
            throw new IllegalArgumentException("Sotoltes csak telephelyen engedelyezett.");
        }
        Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
        h.sotoltes(jatekos);
        ok("'" + h.name + "' sokeszlete feltoltve. Penz levonva: 50");
    }

    void handleFuelRefill(CommandContext context, List<String> args) {
        ensureArgCount(args, 1, 1, "kerozintoltes [hokotro]");
        TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
        if (!state.jatekosAtTelephely(jatekos)) {
            throw new IllegalArgumentException("Kerozintoltes csak telephelyen engedelyezett.");
        }
        Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
        h.kerozintoltes(jatekos);
        ok("'" + h.name + "' kerozinkeszlete feltoltve. Penz levonva: 60");
    }

    void handleGritRefill(CommandContext context, List<String> args) {
        ensureArgCount(args, 1, 1, "zuzalektoltes [hokotro]");
        TakaritoJatekos jatekos = resolveCleanerForHokotroAction(args.get(0));
        if (!state.jatekosAtTelephely(jatekos)) {
            throw new IllegalArgumentException("Zuzalektoltes csak telephelyen engedelyezett.");
        }
        Hokotro h = requireOwnedHokotro(jatekos, args.get(0));
        h.zuzalektoltes(jatekos);
        ok("'" + h.name + "' zuzalekkeszlete feltoltve. Penz levonva: 40");
    }

    void handleCleanSav(CommandContext context, List<String> args) {
        ensureArgCount(args, 2, 2, "takarit [hokotro] [sav]");
        Hokotro h = requireHokotro(args.get(0));
        Ut ut = h.aktualisUt(state);
        int savIndex = parseSavIndex(ut, args.get(1));
        h.takaritSav(ut, savIndex, state);
        ok("'" + h.name + "' takaritasa lefutott a(z) " + ut.name() + " " + savIndex + ". savon.");
    }

    void handleHoeses(CommandContext context, List<String> args) {
        ensureArgCount(args, 0, 0, "havazas");
        if (state.utak.isEmpty()) {
            throw new IllegalArgumentException("Nincs ut a halozatban.");
        }
        ok("Havazas szimulacio elinditva.");
        for (Ut ut : state.utak.values()) {
            int delta = ut.alkalmazHoEses();
            if (delta != 0) {
                state.enqueueEvent(ut.name() + " savjain a ho +" + delta + " egyseggel nott.");
            } else {
                state.enqueueEvent(ut.name() + " savjain nem valtozott a horetag.");
            }
        }
        state.tickTime();
        state.evaluateGameOver();
    }

    void handleListByType(CommandContext context, List<String> args) {
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

    void handleSetPenz(CommandContext context, List<String> args) {
        ensureArgCount(args, 2, 2, "penz [nev] [osszeg]");
        String name = args.get(0);
        int amount = parseNonNegativeInt(args.get(1), "osszeg");

        Jatekos jatekos = state.getTypedEntity(name, Jatekos.class);
        if (jatekos == null) {
            throw new IllegalArgumentException("Nincs ilyen jatekos: " + name);
        }

        jatekos.setPenz(amount);
        ok("'" + name + "' egyenlege beallitva: " + amount);
    }
}
