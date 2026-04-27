package skeletonprogram;

public final class Sav {
    final int index;
    int ho;
    int ice;
    int feltortJeg;
    int soHatralevoIdeje;
    int zuzalekHatralevoIdeje;

    public Sav(int index) {
        this.index = index;
    }

    public void applyDeposit(DepositType type, int value) {
        if (type == DepositType.HO) {
            ho = Math.max(0, value);
        }
        if (type == DepositType.JEG) {
            ice = Math.max(0, value);
        }
        if (type == DepositType.SO) {
            soHatralevoIdeje = Math.max(0, value);
        }
        if (type == DepositType.FELTORTJEG) {
            feltortJeg = Math.max(0, value);
        }
        if (type == DepositType.ZUZALEK) {
            zuzalekHatralevoIdeje = Math.max(0, value);
        }
    }

    public void hoingOneUnit(boolean protectedByTunnel) {
        if (protectedByTunnel) {
            return;
        }
        if (soHatralevoIdeje > 0) {
            return;
        }
        ho += 1;
    }

    public void tickTime() {
        if (soHatralevoIdeje > 0) {
            soHatralevoIdeje -= 1;
        }
        if (zuzalekHatralevoIdeje > 0) {
            zuzalekHatralevoIdeje -= 1;
        }
    }

    public String describeCompact() {
        return "[Sav" + index + "(Ho:" + ho + ", Jeg:" + ice + ", Sozott:" + (soHatralevoIdeje > 0 ? "I" : "N") + ")]";
    }
}
