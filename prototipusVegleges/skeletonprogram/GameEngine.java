package skeletonprogram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class GameEngine {
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
