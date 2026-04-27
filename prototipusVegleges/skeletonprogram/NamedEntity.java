package skeletonprogram;

public interface NamedEntity {
    String name();

    void renameTo(String newName);

    String type();

    String statusLine(GameState state);
}
