import java.sql.*;
import java.util.ArrayList;
import mittBibliotek.Opprydder;
/*we need this class to let the user see information and which rent machines
they can setup so they dont chose one that does not exist*/
public class RentMachines {
    Connection con;
    /*don't feel like we need a database pool on this small class
    that is going to be existing in the user software for max 2-3 minutes*/

    public RentMachines(String dbDriver, String dbNavn) throws Exception {
        try {
            Class.forName(dbDriver);
            con = DriverManager.getConnection(dbNavn);
        } catch (Exception e) {
            Opprydder.skrivMelding(e, "constructor");
            throw e;
        }
    }

    public void disconnect(){
        Opprydder.lukkForbindelse(con);
    }

    public ArrayList<String> getAllRentMachines(){
        ArrayList<String> list = new ArrayList<String>();
        Statement query = null;
        ResultSet res = null;
        try {
            list.clear();
            query = con.createStatement();
            String sqlQuery = "SELECT rentmachine_id FROM rentmachine ORDER BY rentmachine_id";
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                while (res.next()) {
                    list.add(""+res.getInt("rentmachine_id"));
                }
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ArrayList<String> fail = new ArrayList<String>();
            fail.add("-1");
            return fail;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
        }
        return list;
    }

    public String toString(int rentMachineId){
        /*this method is to give the user information about where the rentmachine is so they can chose the correct one*/
        PreparedStatement query = null;
        ResultSet res = null;
        String info = "Information about rent machine with id: " + rentMachineId + "\n";
        try {
            String sqlQuery = "SELECT * FROM rentmachine rm LEFT JOIN dockingstation ds ON rm.ds_id = ds.ds_id WHERE rm.rentmachine_id = ?";
            query = con.prepareStatement(sqlQuery);
            query.setInt(1, rentMachineId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                info +=  "This rent machine is at docking station with ID: " + res.getInt("ds_id") + "\n";
                info += "The address of the docking station is: " + res.getString("adress") + "\n";
                info += "The docking station has " + res.getInt("charging_units") + " charging units" + "\n";
                info += "The coordinates of the docking station are: latitude " + res.getDouble("ds_lat") + " and longitude " + res.getDouble("ds_lon");
            }else{
                System.out.println("Bike does not exist");
                return "-1";
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return "-1";
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
        }
        return info;
    }
}