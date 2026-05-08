package motor;

/**
 * Push-tipusu ertesites interfesz: a GameState ezzel jelzi a feliratkozott
 * objektumoknak (jellemzoen view-knak), hogy az allapota valtozott es
 * frissiteni kell a megjelenitest.
 *
 * A kevert MVC modellben ez a "push" oldal: a model akkor szol, ha
 * valami tortent (kivalasztas, lepes, kor vege). A view ezutan a
 * paintComponent()-ben "pull" modon olvassa a model aktualis allapotat.
 */
public interface GameStateListener {
    /** A model allapota valtozott; a hallgato itt frissitse magat. */
    void onStateChanged(GameState state);
}
