package skeletonprogram;

/**
 * Alkalmazas belepesi pontja es egyszeru hivasnaplozo segedmetodusok taroloja.
 */
public class SzkeletonProgram {
    private static int depth = 0;

    public static void printIndent() {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }

    public static void logCall(String callerId, String className, String methodName, String params) {
        printIndent();
        System.out.println("-> " + callerId + ": " + className + "." + methodName + "(" + params + ")");
        depth++;
    }

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
