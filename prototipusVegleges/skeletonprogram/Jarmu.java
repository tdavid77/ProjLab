package skeletonprogram;

public class Jarmu implements NamedEntity {
    protected String name;
    protected String owner;
    protected String currentUt;
    protected int savIndex;
    protected int disabledTime;

    protected Jarmu(String name) {
        this.name = name;
        this.currentUt = null;
        this.savIndex = 0;
        this.disabledTime = 0;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void renameTo(String newName) {
        this.name = newName;
    }

    @Override
    public String type() {
        return "Jarmu";
    }

    @Override
    public String statusLine(GameState state) {
        String pos;
        if (currentUt == null) {
            pos = "Telephely";
        } else {
            pos = currentUt + ", " + savIndex;
        }
        String allapot = disabledTime > 0 ? "Baleset(" + disabledTime + " kor)" : "Aktiv";
        return type() + " " + name + " | Poz:" + pos + " | Allapot:" + allapot;
    }

    public boolean canMove() {
        return disabledTime <= 0;
    }

    public void tickTime() {
        if (disabledTime > 0) {
            disabledTime -= 1;
        }
    }

    public void vegrehajtLepes(Ut target, int targetSav, GameState state) {
        if (currentUt != null) {
            Ut current = state.getUt(currentUt);
            if (current == null) {
                throw new IllegalArgumentException("Nincs ilyen ut: " + currentUt);
            }
            boolean sharesNode = current.hasNode(target.nodeA) || current.hasNode(target.nodeB);
            if (!sharesNode && !current.name().equalsIgnoreCase(target.name())) {
                throw new IllegalArgumentException("A celut nem szomszedos a jelenlegi uttal.");
            }
        }

        currentUt = target.name();
        savIndex = targetSav;

        maybeCrash(target.sav(targetSav), state);
        onCelUtElerve(target, state);
    }

    protected void onCelUtElerve(Ut target, GameState state) {
        // alapertelmezetten nincs extra logika
    }

    private void maybeCrash(Sav sav, GameState state) {
        if (this instanceof Hokotro) {
            return;
        }
        if (sav.ice <= 0) {
            return;
        }
        double modifier = sav.zuzalekHatralevoIdeje > 0 ? -0.12 : 0.0;
        double chance = Math.max(0.0, Math.min(0.95, state.difficulty.baseCrashChance() + (sav.ice * 0.03) + modifier));
        if (state.random.nextDouble() < chance) {
            disabledTime = 2;
            state.accidents += 1;
            state.enqueueEvent(name + " megcsuszott jeges savon, 2 korre kiesett.");
        }
    }
}
