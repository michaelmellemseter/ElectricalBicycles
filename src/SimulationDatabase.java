import mittBibliotek.Opprydder;
import mittBibliotek.database.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/*This class is mostly using methods from user-_database just that here they are costomized a bit. This class could be simplified, but I want
 to go trough the same steps as an actual rent with the renting software to give an accurate simulation*/


public class SimulationDatabase {
    private DatabasePool dbPool;

    public SimulationDatabase(int poolCapacity, String dbDriver, String dbName) throws Exception {
        try {
            dbPool = new DatabasePool(poolCapacity, dbDriver, dbName);
        } catch (Exception e) {
            Opprydder.skrivMelding(e, "constructor");
            throw e;
        }
    }

    public void disconnect() {
        dbPool.lukkAlleForbindelser();
    }

    public boolean positionUpdater(int id, double lat, double lon){
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                DecimalFormat coordFormatter = new DecimalFormat("#.######");
                DecimalFormatSymbols custom = new DecimalFormatSymbols();
                custom.setDecimalSeparator('.');
                coordFormatter.setDecimalFormatSymbols(custom);

                lat = Double.parseDouble(coordFormatter.format(lat));
                lon = Double.parseDouble(coordFormatter.format(lon));
                con.getForbindelse().setAutoCommit(false);
                String sqlQuery = "SELECT * FROM bicycle WHERE bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, id);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE bicycle SET bicycle_lat = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, lat);
                    query.setInt(2, id);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET bicycle_lon = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, lon);
                    query.setInt(2, id);
                    query.executeUpdate();
                    r = true;
                }else {
                    System.out.println("Something went wrong, bicycle might no exist.");
                    r = false;
                }
                ok = true;
                con.getForbindelse().commit();
            } catch (SQLException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return false;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.settAutoCommit(con.getForbindelse());
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r;
    }

    public double[] getBikePosition(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double[] position = new double[2];
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT bicycle_lat,bicycle_lon FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                position[0] = res.getDouble("bicycle_lat");
                position[1] = res.getDouble("bicycle_lon");
            }else{
                System.out.println("Bike does not exist");
                double[] fail = {-1,-1};
                return fail;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            double[] fail = {-1,-1};
            return fail;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return position;
    }

    public double[] getStationPosition(int dsId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double[] position = new double[2];
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT ds_lat, ds_lon FROM dockingstation WHERE ds_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, dsId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                position[0] = res.getDouble("ds_lat");
                position[1] = res.getDouble("ds_lon");
            }else{
                System.out.println("Bike does not exist");
                double[] fail = {-1,-1};
                return fail;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            double[] fail = {-1,-1};
            return fail;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return position;
    }

    public ArrayList<String> getAllAvailableTypesBicycles(int dsId) { //returns a list of all the different types that is regitered at a dockingstation
        ArrayList<String> allAvailableTypesBicycles = new ArrayList<String>();
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT * FROM bicycle b WHERE b.ds_id = ? AND b.battery_lvl > 50";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, dsId);
            res = query.executeQuery();
            while (res.next()) {
                String newType = res.getString("type_name");
                boolean alreadyThere = false;
                String type = "";
                for (int i = 0; i < allAvailableTypesBicycles.size(); i++) {
                    type = allAvailableTypesBicycles.get(i);
                    if (type.equals(newType)) {
                        alreadyThere = true;
                    }
                }
                if (!alreadyThere) {
                    allAvailableTypesBicycles.add(newType);
                }
            }
            res.close();
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return allAvailableTypesBicycles;
    }

    public int getAmountBicyclesAvailable(String typeName, int dsId){ //returns a list with numbers that count all the bicycles of a specific type at a dockingstation
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        int a = 0;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT COUNT(bicycle_id) AS amount FROM bicycle b WHERE b.ds_id = ? AND b.battery_lvl > 50 AND b.type_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, dsId);
            query.setString(2, typeName);
            res = query.executeQuery();
            while(res.next()){
                int amount = res.getInt("amount");
                a = amount;
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return a;
    }

    public ArrayList<Integer> getAllDockingstationsWithBikes(){
        ArrayList<Integer> stations = new ArrayList<Integer>();

        String sqlQuery = "SELECT DISTINCT ds_id FROM bicycle WHERE ds_id IS NOT NULL AND battery_lvl > 50";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            while (res.next()) {
                int id = res.getInt("ds_id");
                stations.add(id);
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ArrayList<Integer> fail = new ArrayList<Integer>();
            fail.add(-1);
            return fail;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return stations;
    }

    public int getBicycleRentId(){ //returns a number that is 1 number higher than the highest bicycle rent id that is already registered
        int bicycleRentId = 0;
        String sqlQuery = "SELECT MAX(bicycle_rent_id) AS bicycle_rent_id FROM bicycle_rent";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            res.next();
            int bicycle_rent_id = res.getInt("bicycle_rent_id");
            bicycleRentId = bicycle_rent_id + 1;
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return bicycleRentId;
    }

    public int getRentId(){//returns a number that is 1 number higher than the highest rent id that is already registered
        int RentId = 0;
        String sqlQuery = "SELECT MAX(rent_id) AS rent_id FROM rent";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            res.next();
            int rent_id = res.getInt("rent_id");
            RentId = rent_id + 1;
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return RentId;
    }

    public int updateRentId(){ //takes a new rent id and create a line in the rent table that inserts only a rent id
        int rentId = getRentId();
        PreparedStatement query = null;
        Forbindelse con = null;
        boolean ok = false;
        int tries = 0;
        do {
            try {
                con = dbPool.reserverForbindelse();
                String sqlQuery = "INSERT INTO rent VALUES (" + rentId + ",  NULL, NULL, NULL, NULL)";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.executeUpdate();
                ok = true;
            } catch (SQLException e) {
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return -1;
                }
            } finally {
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        } while (!ok);
        return rentId;
    }

    public int getBicycleId(String typeName, int dsId){ //returns a random bicycle id from a bicycle that is at a specific dockingstation and is a specific type
        int bicycleId = 0;
        //the reason we want to have a random bicycle id is so that it's not always the same bikes that is being rented
        PreparedStatement query = null;
        PreparedStatement query2 = null;
        ResultSet res = null;
        ResultSet res2 = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT * FROM bicycle b, dockingstation d, rentmachine r WHERE b.ds_id = d.ds_id AND d.ds_id = r.ds_id AND d.ds_id = ? AND type_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, dsId);
            query.setString(2, typeName);
            res = query.executeQuery();

            //counting how many bikes there is so that the random number can't get higher than the number og bikes available
            String sqlQuery2 = "SELECT COUNT(bicycle_id) AS amount FROM bicycle b" +
                    " WHERE b.ds_id = ? AND type_name = ?";
            query2 = con.getForbindelse().prepareStatement(sqlQuery2);
            query2.setInt(1, dsId);
            query2.setString(2, typeName);
            res2 = query2.executeQuery();

            if(res.isBeforeFirst()){
                res2.next();
                int upperLimit = res2.getInt("amount");
                //picks a random number so we get a random number from the table
                Random randomGen = new Random();
                int amount = randomGen.nextInt(upperLimit) + 1;
                for(int i = 0; i < amount; i++){
                    res.next();
                    bicycleId = res.getInt("bicycle_id");
                }
            }else{
                System.out.println("No more bicycles available");
                return -1;
            }
            res.close();
            res2.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkResSet(res2);
            Opprydder.lukkSetning(query);
            Opprydder.lukkSetning(query2);
            dbPool.frigiForbindelse(con.getNr());
        }
        return bicycleId;
    }

    public int addToCart(String typeName, int rentId, int dsId){ //returns the bicycle id of the bicycle that is added to the cart
        int bicycleId = getBicycleId(typeName, dsId);
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                con.getForbindelse().setAutoCommit(false);
                String sqlQuery = "SELECT * FROM bicycle WHERE bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, bicycleId);
                res = query.executeQuery();
                if (res.next()) {
                    int trips = res.getInt("bicycle_trips");
                    //inserts a line in the bicycle rent table with updated values
                    String sqlUp = "INSERT INTO bicycle_rent VALUES (" + getBicycleRentId() + ", " + bicycleId + ", " + rentId +  ", NOW(), NULL )";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.executeUpdate();
                    //removes the docking station id for the biycle that will be rented
                    sqlUp = "UPDATE bicycle SET ds_id = NULL WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, bicycleId);
                    query.executeUpdate();
                    //updates the trips with one more to the bicycle that will be rented
                    sqlUp = "UPDATE bicycle SET bicycle_trips = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1,trips + 1);
                    query.setInt(2, bicycleId);
                    query.executeUpdate();
                } else {
                    System.out.println("Something went wrong, bike might not exist");
                    return -1;
                }
                ok = true;
                con.getForbindelse().commit();
            } catch (SQLException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return -1;
                }
            } catch (IllegalArgumentException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong ");
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.settAutoCommit(con.getForbindelse());
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        } while (!ok);
        return bicycleId;
    }

    public boolean rent(int rentId, String cardToken, int dsId){ //a method that updates the rest values of to the rent id we already made
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                con.getForbindelse().setAutoCommit(false);
                String sqlQuery = "SELECT * FROM rent WHERE rent_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, rentId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE rent SET rent_price = 0 WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, rentId);
                    query.executeUpdate();
                    sqlQuery = "SELECT r.rentmachine_id FROM dockingstation d LEFT JOIN rentmachine r ON d.ds_id = r.ds_id WHERE d.ds_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlQuery);
                    query.setInt(1, dsId);
                    res = query.executeQuery();
                    res.next();
                    int rentmachineId = res.getInt("rentmachine_id");// doesnt matter which rentmachine it is as long as it is one in the right docking station so ill just take the first one
                    sqlUp = "UPDATE rent SET rentmachine_id = ? WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, rentmachineId);
                    query.setInt(2, rentId);
                    query.executeUpdate();
                    sqlUp = "UPDATE rent SET rent_date = NOW() WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, rentId);
                    query.executeUpdate();
                    sqlUp = "UPDATE rent SET card_token = ? WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, cardToken);
                    query.setInt(2, rentId);
                    query.executeUpdate();
                    r = true;
                } else {
                    System.out.println("Something went wrong, bike might not exist");
                    r = false;
                }
                ok = true;
                con.getForbindelse().commit();
            } catch (SQLException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return false;
                }
            } catch (IllegalArgumentException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong ");
                    return false;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.settAutoCommit(con.getForbindelse());
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        } while (!ok);
        return r;
    }

    public boolean endTrip(int bikeId, int stationId){ //missing testing
        boolean r = false;
        boolean ok = false;
        int tries = 0;

        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);

        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                con.getForbindelse().setAutoCommit(false);
                String sqlQuery = "SELECT * FROM bicycle_rent WHERE bicycle_id = ? AND end_time IS NULL";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, bikeId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    res.next();
                    int rentId = res.getInt("rent_id");
                    String sqlUp = "UPDATE bicycle_rent SET end_time = NOW() WHERE bicycle_id = ? AND end_time IS NULL";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, bikeId);
                    query.executeUpdate();
                    sqlQuery = "SELECT ((TIME_TO_SEC(TIMEDIFF(br.end_time, br.start_time))/3600)*t.type_rentprice) AS price\n" +
                            "FROM bicycle_rent br\n" +
                            "LEFT JOIN bicycle b\n" +
                            "ON br.bicycle_id = b.bicycle_id\n" +
                            "LEFT JOIN type t\n" +
                            "ON b.type_name = t.type_name\n" +
                            "WHERE b.bicycle_id = ?" +
                            " ORDER BY ABS(TIMEDIFF(NOW(), br.end_time)) LIMIT 1";
                    query = con.getForbindelse().prepareStatement(sqlQuery);
                    query.setInt(1, bikeId);
                    res = query.executeQuery();
                    res.next();

                    double price = res.getDouble("price");
                    System.out.println("price: " + price);
                    sqlUp = "UPDATE rent SET rent_price = rent_price + ? WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, Double.parseDouble(df.format(price)));
                    query.setInt(2, rentId);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET ds_id = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, stationId);
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    r = true;
                } else {
                    System.out.println("Something went wrong, bicycle might not be on a trip");
                    r = false;
                }
                ok = true;
                con.getForbindelse().commit();
            } catch (SQLException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return false;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.settAutoCommit(con.getForbindelse());
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        } while (!ok);
        return r;
    }

    public ArrayList<Integer> getAllDockingstations(){
        ArrayList<Integer> stations = new ArrayList<Integer>();

        String sqlQuery = "SELECT ds_id FROM dockingstation";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            while (res.next()) {
                int id = res.getInt("ds_id");
                stations.add(id);
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ArrayList<Integer> fail = new ArrayList<Integer>();
            fail.add(-1);
            return fail;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return stations;
    }

    public double getBatteryLvl(int bikeId){
        double lvl = 0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;

        try{
            String sqlQuery = "SELECT battery_lvl FROM bicycle WHERE bicycle_id = ?";
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1,bikeId);
            res = query.executeQuery();
            if(res.isBeforeFirst()){
                res.next();
                lvl = res.getDouble("battery_lvl");
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } catch (Exception e) {
            return -1;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return lvl;
    }

    public boolean updateBatteryLvl(int bikeId, double increasement){
        boolean r = false;
        boolean ok = false;
        int tries = 0;

        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);

        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                double curBat = getBatteryLvl(bikeId);
                double newBat = 0;
                if((curBat + increasement) >= 0){// if battery lvl is null you should be still able to use it, but wont go below 0
                    newBat = curBat + increasement;
                }
                con = dbPool.reserverForbindelse();
                String sqlUp = "UPDATE bicycle SET battery_lvl = ? WHERE bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlUp);
                query.setDouble(1, Double.parseDouble(df.format(newBat)));
                query.setInt(2,bikeId);
                query.executeUpdate();
                r = true;
                ok = true;
            } catch (SQLException e) {
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return false;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r;
    }

    public boolean updateKm(int bikeId, double increasement){
        boolean r = false;
        boolean ok = false;
        int tries = 0;

        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);

        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                double curKm = 0d;
                String sqlQuery = "SELECT bicycle_km FROM bicycle WHERE bicycle_id = ?";
                con = dbPool.reserverForbindelse();
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, bikeId);
                res = query.executeQuery();
                if(res.isBeforeFirst()){
                    res.next();
                    curKm = res.getDouble("bicycle_km");
                }
                double newKm = (curKm + increasement);
                String sqlUp = "UPDATE bicycle SET bicycle_km = ? WHERE bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlUp);
                query.setDouble(1, Double.parseDouble(df.format(newKm)));
                query.setInt(2, bikeId);
                query.executeUpdate();
                r = true;
                ok = true;
            } catch (SQLException e) {
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return false;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r;
    }

    public ArrayList<Integer> getAllBikesAtStation(int id){
        ArrayList<Integer> list = new ArrayList<Integer>();
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            String sqlQuery = "SELECT bicycle_id FROM bicycle WHERE ds_id=?";
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, id);
            res = query.executeQuery();
            if(res.isBeforeFirst()){
                while(res.next()) {
                    list.add(res.getInt("bicycle_id"));
                }
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ArrayList<Integer> fail = new ArrayList<Integer>();
            fail.add(-1);
            return fail;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return list;
    }

    public boolean updateDockingstationAndBikes(int dsId){
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        Random rand = new Random();
        DecimalFormat df = new DecimalFormat("#.###");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);

        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                ArrayList<Integer> bikeList = new ArrayList<Integer>();
                String sqlQuery = "SELECT bicycle_id FROM bicycle WHERE ds_id=?";
                con = dbPool.reserverForbindelse();
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, dsId);
                res = query.executeQuery();
                if(res.isBeforeFirst()){
                    while(res.next()) {
                        bikeList.add(res.getInt("bicycle_id"));
                    }
                }
                for(int i = 0; i < bikeList.size(); i++) {
                    int bikeId = bikeList.get(i);
                    double curBat = getBatteryLvl(bikeId);
                    double newBat = 100;
                    double increasement = 25 + (rand.nextInt(21)/5);
                    if ((curBat + increasement) <= 100) {// if battery lvl is null you should be still able to use it, but wont go below 0
                        newBat = curBat + increasement;
                    }
                    String sqlUp = "UPDATE bicycle SET battery_lvl = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, Double.parseDouble(df.format(newBat)));
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                }
                double extraKwh = (0.013 + rand.nextInt(5)/1000)*bikeList.size();
                extraKwh = Double.parseDouble(df.format(extraKwh));
                String sqlUp = "UPDATE dockingstation SET kwh = kwh + ? WHERE ds_id = ?";
                query = con.getForbindelse().prepareStatement(sqlUp);
                query.setDouble(1, extraKwh);
                query.setInt(2, dsId);
                query.executeUpdate();
                r = true;
                ok = true;
                res.close();
            } catch (SQLException e) {
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return false;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r;
    }

}
