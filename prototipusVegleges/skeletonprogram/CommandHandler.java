package skeletonprogram;

import java.util.List;

public interface CommandHandler {
    void execute(CommandContext context, List<String> args);
}
