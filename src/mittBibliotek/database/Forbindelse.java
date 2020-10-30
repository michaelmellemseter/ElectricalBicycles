/**
 * Forbindelse.java  - "Programmering i Java", 4.utgave - 2009-07-01
 *
 * Et objekt av denne klassen inneholder nødvendige data for en
 * databaseforbindelse.
 * Konstruktøren og noen av metodene har pakketilgang,
 * det er meningen at de kun skal kalles fra klassen DatabasePool.
 *
 * Feilhåndtering: Konstruktøren og lukkForbindelse() kan kaste SQLException,
 * ellers er det ikke aktuelt å kaste unntak.
 */

package mittBibliotek.database;
import java.sql.*;
public class Forbindelse {
  private int nr;  // identifiserer en forbindelse
  private Connection dbForb;
  private boolean ledig;

  /* Pakketilgang */
  Forbindelse(int nr, String dbNavn) throws SQLException {
    this.nr = nr;
    dbForb = DriverManager.getConnection(dbNavn);
    ledig = true;
  }

  void lukkForbindelse() throws SQLException {
    if (dbForb != null) {
      dbForb.close();
    }
  }

  void setLedig(boolean ledig) {
    this.ledig = ledig;
  }

  /* Offentlig tilgang */
  public int getNr() {
    return nr;
  }

  public Connection getForbindelse() {
    return dbForb;
  }

  public boolean isLedig() {
    return ledig;
  }

  public String toString() {
    String resultat = "Forbindelse nr " + nr;
    if (ledig) {
      resultat +=  " er ledig. ";
    } else {
      resultat += " er ikke ledig. ";
    }
    return resultat;
  }
}
