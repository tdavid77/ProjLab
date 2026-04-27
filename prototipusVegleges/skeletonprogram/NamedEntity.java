package skeletonprogram;

/**
 * Kozos nevvel azonosithato objektumok interfesze a parancskezeleshez.
 */
public interface NamedEntity {
    String name();

    void renameTo(String newName);

    String type();

    String statusLine(GameState state);
}
