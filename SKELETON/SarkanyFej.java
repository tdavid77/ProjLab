/**
 * Prémium takarítóeszköz, amely gázturbinájával azonnal elolvasztja a havat és a jeget.
 */
public class SarkanyFej extends Fej {
    private boolean fejAllapota;
    private int biokerozinKeszlet;

    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SarkanyFej.takaritHatas() meghivva.");
    }
}
