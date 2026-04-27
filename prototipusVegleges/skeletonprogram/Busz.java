package skeletonprogram;

public final class Busz extends Jarmu {
    private int completedTrips;

    public Busz(String name) {
        super(name);
        this.completedTrips = 0;
    }

    @Override
    public String type() {
        return "Busz";
    }

    @Override
    protected void onCelUtElerve(Ut target, GameState state) {
        if (target.hasNode("Vegallomas") || target.hasNode("Vegallomas_1") || target.hasNode("Vegallomas_2")) {
            completedTrips += 1;
            Jatekos ownerEntity = state.getTypedEntity(owner, Jatekos.class);
            if (ownerEntity != null) {
                ownerEntity.money += 40;
                state.enqueueEvent("Busz kor teljesitve: " + name + ", jovairas +40.");
            }
        }
    }
}
