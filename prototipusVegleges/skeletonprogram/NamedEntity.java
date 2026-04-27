package skeletonprogram;

/**
 * Kozos interfesz minden nevvel azonosithato jatekobj ektu mhoz.
 * Biztositja a nev, atnevezes, tipusnev, allapotsor es polimorf cast-
 * metódusokat. Az as*() metodusok type cast helyett polimorf dispatch-et
 * valositanak meg instanceof nelkul. Az onRegistered(), tickTime(),
 * relinkEntityName(), relinkUtName() es attachVehicle() default
 * implementaciot kapnak, hogy csak az erdekeltek irjak felul.
 */
public interface NamedEntity {
    /** Visszaadja az entitas azonositoneveit. */
    String name();

    /** Megvaltoztatja az entitas azonositoneveit (atnevezes muvelethezhasznalt). */
    void renameTo(String newName);

    /**
     * Szoveges tipusnevet ad vissza a konzolos UI szamara.
     * Nem viselkedesi elagazas alapja - csak megjeleniteshez es a 'lista' szuröhöz.
     */
    String type();

    /** Konzolos allapotsort ad vissza az entitasrol. */
    String statusLine(GameState state);

    /** Polimorf cast: Jarmu alosztaly eseten visszaadja onmagat, egyebkent null. */
    default Jarmu asJarmu() { return null; }
    /** Polimorf cast: Jatekos alosztaly eseten visszaadja onmagat, egyebkent null. */
    default Jatekos asJatekos() { return null; }
    /** Polimorf cast: TakaritoJatekos eseten visszaadja onmagat, egyebkent null. */
    default TakaritoJatekos asTakaritoJatekos() { return null; }
    /** Polimorf cast: Hokotro eseten visszaadja onmagat, egyebkent null. */
    default Hokotro asHokotro() { return null; }

    /** Hook: putEntity() hivja meg, lehetove teve specializalt regisztraciot (pl. Busz). */
    default void onRegistered(GameState state) {}
    /** Koridö-leptetés: csokkenti az időfuggő szamlalokat. */
    default void tickTime() {}
    /** Entitas-atnevezes eseten frissiti az entitasra hivatkozo belso referenciakat. */
    default void relinkEntityName(String oldName, String newName) {}
    /** Ut-atnevezes eseten frissiti az utra hivatkozo belso referenciakat. */
    default void relinkUtName(String oldName, String newName) {}
    /** Hozzarendel egy jarmut az entitashoz (Jatekos implementalja, masoknal no-op). */
    default void attachVehicle(Jarmu vehicle) {}
}
