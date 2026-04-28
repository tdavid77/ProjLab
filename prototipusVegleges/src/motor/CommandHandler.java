package motor;

import java.util.List;

/**
 * Funkcionalis interfesz egy parancs kezelesi logikajahoz.
 */
public interface CommandHandler {
    void execute(CommandContext context, List<String> args);
}
