/**
 * Só szórásával kémiai úton olvasztja el az akadályokat egy adott sávon.
 */
public class SoszoroFej extends Fej {
    private boolean fejAllapota;
    private int sokeszlet;

    @Override
    public void takaritHatas(Sav s, Hokotro h) {
        System.out.println("SoszoroFej.takaritHatas() meghivva.");
    }
}
