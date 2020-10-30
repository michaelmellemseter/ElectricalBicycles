/**
 *
 * Opprydder.java  - "Programmering i Java", 4.utgave - 2009-07-01
 * Metoder for � rydde opp etter databasebruk
 */
package mittBibliotek;
import java.sql.*;
public class Opprydder {
  public static void lukkResSet(ResultSet res) {
    try {
      if (res != null) {
        res.close();
      }
    } catch (SQLException e) {
      skrivMelding(e, "lukkResSet()");
    }
  }

  public static void lukkSetning(Statement stm) {
    try {
      if (stm != null) {
        stm.close();
      }
    } catch (SQLException e) {
      skrivMelding(e, "lukkSetning()");
    }
  }

  public static void lukkForbindelse(Connection forbindelse) {
    try {
      if (forbindelse != null) {
        forbindelse.close();
        System.out.println("Forbindelse lukket");
      }
    } catch (SQLException e) {
      skrivMelding(e, "lukkForbindelse()");
    }
  }

  public static void rullTilbake(Connection forbindelse) {
    try {
      if (forbindelse != null && !forbindelse.getAutoCommit()) {
        forbindelse.rollback();
      }
    } catch (SQLException e) {
      skrivMelding(e, "rollback()");
    }
  }

  public static void settAutoCommit(Connection forbindelse) {
    try {
      if (forbindelse != null && !forbindelse.getAutoCommit()) {
        forbindelse.setAutoCommit(true);
      }
    } catch (SQLException e) {
      skrivMelding(e, "settAutoCommit()");
    }
  }

  public static void skrivMelding(Exception e, String melding) {
    System.err.println("*** Feil oppst�tt: " + melding + ". ***");
    e.printStackTrace(System.err);
  }
}