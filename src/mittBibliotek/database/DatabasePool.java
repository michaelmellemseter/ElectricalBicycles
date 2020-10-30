/**
 * DatabasePool.java  - "Programmering i Java", 4.utgave - 2009-07-01
 *
 * En databasepool best�r av en mengde forbindelser. Databaseparametere
 * (driver, navn osv.) er argumenter til konstrukt�ren. Poolen opprettes
 * med en bestemt kapasitet. Dersom en metode ettersp�r en forbindelse,
 * og det ikke er noen ledig i poolen, blir det laget en ny forbindelse.
 * Denne blir lukket n�r den aktuelle metoden leverer forbindelsen tilbake.
 * Ellers m� metoden lukkAlleForbindelser() kalles for � stenge poolen.
 *
 * Feilh�ndtering: Konstrukt�ren kaster unntak dersom poolen ikke kan �pnes.
 * Ellers h�ndteres unntak ved utskrift til konsollet.
 * Slike unntak skyldes programmeringsfeil og sendes ikke til klienten.
 */

package mittBibliotek.database;
import java.util.ArrayList;
import java.sql.*;

public class DatabasePool {
  private ArrayList<Forbindelse> pool = new ArrayList<Forbindelse>();
  private int kapasitet;  // forbindelsene nummereres 0, 1, 2, .. ., (kapasitet - 1)
  private String dbNavn;

  public DatabasePool(int kapasitet, String dbDriver, String dbNavn)
                                                                  throws Exception {
    this.kapasitet = kapasitet;
    this.dbNavn = dbNavn;
    try {
      Class.forName(dbDriver);  // laster driverklassene
      for (int nr = 0; nr < kapasitet; nr++) {
        pool.add(new Forbindelse(nr, dbNavn));
      }
    } catch (Exception e) {
      skrivMelding(e, "konstrukt�r");
      lukkAlleForbindelser(); // lukker alle som hittil er �pnet
      throw e;
    }
  }

  /**
   * Reserverer f�rste ledige forbindelse. Hvis ingen ledige, opprettes en ny.
   * Returnerer null hvis problemer.
   */
  public synchronized Forbindelse reserverForbindelse() {
    for (Forbindelse forb : pool) {
      if (forb.isLedig()) {
        System.out.println("Reserverer forbindelse " + forb.getNr());
        forb.setLedig(false);
        return forb;
      }
    }
    Forbindelse ny = null;  // ingen ledige, lager ny forbindelse
    try {
      Forbindelse forb = pool.get(pool.size() - 1);
      ny = new Forbindelse(forb.getNr() + 1, dbNavn);
      ny.setLedig(false);
      pool.add(ny);
      System.out.println("Reserverer forbindelse " + ny.getNr());
    } catch (SQLException e) {
      skrivMelding(e, "reserverForbindelse()");
    }
    return ny;
  }

  /**
   * Frigir forbindelse. Hvis forbindelsen er opprettet i metoden over, blir den lukket,
   * og det tilh�rende Forbindelse-objektet blir fjernet.
   * Metoden returner false dersom ugyldig nr.
   */
  public synchronized boolean frigiForbindelse(int nr) {
    System.out.println("Frigir forbindelse " + nr + ".");
    if (nr < kapasitet) {
      Forbindelse forb = pool.get(nr);
      forb.setLedig(true);
      return true;
    } else {
      for (int i = kapasitet; i < pool.size(); i++) {
        Forbindelse forb = pool.get(i);
        if (pool.get(i).getNr() == nr) {
          try {
            forb.lukkForbindelse();
            pool.remove(i);
            System.out.println("Lukker forbindelse nr. " + nr + ".");
            return true;
          } catch (SQLException e) {
            skrivMelding(e, "frigiForbindelse()");
          }
        }
      }
      return false; // ugyldig nr
    }
  }

  public synchronized String lagUtskrift() {
    String resultat = "";
    for (Forbindelse forb : pool) {
      resultat += forb.toString() + "\n";
    }
    return resultat;
  }

  public synchronized void lukkAlleForbindelser() {
    for (Forbindelse forb : pool) {
      try {
        forb.lukkForbindelse();
        System.out.println("Lukker forbindelse nr. " + forb.getNr() + ".");
      } catch (Exception e) {
        skrivMelding(e, "lukkAlleForbindelser()");
      }
    }
    pool.clear();
  }

  private void skrivMelding(Exception e, String metode) {
    System.err.println("*** Feil i klassen DatabasePool, metode " + metode + " ***");
    e.printStackTrace(System.err);
  }
}