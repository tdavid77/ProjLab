package motor;

/**
 * Az alkalmazas belepesei pontja es a prototipus-fazisban hasznalt
 * hivásnaplo-segedmetodusok taroloja.
 * A main() letrehoz egy GameEngine peldanyt es elinditja a jatekot.
 * A logCall() / logReturn() metódusok a skeleton-fazis kezi nyomkovetesehez
 * keszultek; a kesöbbi verziokban valoszinuleg eltunnek.
 */
public class SzkeletonProgram {
    /** Az aktualis hivas-melyseg belyegzeset szimbolalja (behuzasi szint). */
    private static int depth = 0;

    /** Kiir annyi tabulator karaktert, ahany szint melen vagyunk. */
    public static void printIndent() {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }

    /** Naploz egy metódushivast: kiirja a hivot, az osztályt, a metódust es a parametereket. */
    public static void logCall(String callerId, String className, String methodName, String params) {
        printIndent();
        System.out.println("-> " + callerId + ": " + className + "." + methodName + "(" + params + ")");
        depth++;
    }

    /** Naploz egy visszateresi eseményt es csokkenti a behuzasi szintet. */
    public static void logReturn(String returnDesc) {
        depth--;
        printIndent();
        System.out.println("<- " + returnDesc);
    }

    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.run(args);
    }
}
