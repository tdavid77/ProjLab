package motor;

/**
 * A 'letrehoz' parancs altal tamogatott entitas-tipusokat sorolja fel.
 * Az ertek alapjan a GameActions.handleCreate() donti el, melyik konkret
 * osztalyt kell peldanyositani (TakaritoJatekos, BuszosJatekos, Hokotro, Busz, Auto).
 */
public enum EntityType {
    /** Takarito jatekos szerepkor (hokotrokat iranyit). */
    TAKARITOJATEKOS,
    /** Buszsofőr jatekos szerepkor (buszokat iranyit). */
    BUSZOSJATEKOS,
    /** Takarito jarmu, keszletekkel es fejjel. */
    HOKOTRO,
    /** Menetrendszeru utazasi jarmu. */
    BUSZ,
    /** Autonoman kozlekedo NPC-jarmu. */
    AUTO
}
