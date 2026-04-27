package skeletonprogram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CommandRouter {
    private final GameState state;
    private final GameActions actions;
    private final Map<String, CommandHandler> commands = new HashMap<>();

    public CommandRouter(GameState state, GameActions actions) {
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

    public void executeLine(String line) {
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
