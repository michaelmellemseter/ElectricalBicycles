/*
A class with methods that will be utilized inside the User_JFrame.
@Author Team 09
created on 2018-04-22
 */

import mittBibliotek.Opprydder;
import mittBibliotek.database.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.io.*;
import java.util.Calendar;
import java.util.Random;

import static javax.swing.JOptionPane.*;

public class User_database {
    private DatabasePool dbPool;
    private final int RENT_MACHINE_NR;

    public User_database(int poolCapacity, String dbDriver, String dbName, int mNr) throws Exception {
        try {
            dbPool = new DatabasePool(poolCapacity, dbDriver, dbName);
            this.RENT_MACHINE_NR = mNr;
        } catch (Exception e) {
            Opprydder.skrivMelding(e, "constructor");
            throw e;
        }
    }

    public void disconnect() {
        dbPool.lukkAlleForbindelser();
    }

    public boolean writeToFile(String text, String filename) throws Exception{//making all functions to return, a boolean. Does need to use the return value if not neeeded.
        boolean r = true;
        try{

            FileWriter writeTo = new FileWriter(filename, true);
            PrintWriter writer = new PrintWriter(new BufferedWriter(writeTo));
            System.out.println(text);
            writer.println(text);
            writer.close();
        }catch(Exception e){
            r = false;
            showMessageDialog(null, "Something went wrong. Error: " + e.toString());
        }
        return r;
    }

    public boolean clearFile(String filename){
        boolean r = true;
        try{

            FileWriter writeTo = new FileWriter(filename);
            PrintWriter writer = new PrintWriter(new BufferedWriter(writeTo));
            writer.println("");
            writer.close();
        }catch(Exception e){
            r = false;
            showMessageDialog(null, "Something went wrong. Error: " + e.toString());
        }
        return r;
    }

    public boolean getReceipt(String cardtoken) {//missing testing
        boolean r = false;
        ResultSet res = null;
        PreparedStatement query = null;
        Forbindelse con = null;

        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);

        try {
            con = dbPool.reserverForbindelse();
            boolean clear = clearFile("receipt.txt");
            String sqlQuery = "SELECT rent_id, rent_date, rent_price FROM rent WHERE card_token = ? ORDER BY ABS(TIMEDIFF(rent_date, NOW())) LIMIT 1";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, cardtoken);
            res = query.executeQuery();
            double finalPrice = 0;
            boolean continu = false;
            int rentId = -1;
            if (res.isBeforeFirst()) {
                continu = true;
                while (res.next()) {
                    String writeTo = "This is your receipt for purchase made: " + res.getDate("rent_date") + " " + res.getTime("rent_date");
                    boolean s = writeToFile(writeTo, "receipt.txt");
                    java.util.Date date = Calendar.getInstance().getTime();
                    s = writeToFile("Receipt printed: " + date, "receipt.txt");
                    s = writeToFile("------------------------------------------------------------" ,"receipt.txt");
                    finalPrice = res.getDouble("rent_price");
                    rentId = res.getInt("rent_id");
                }
            }else{
                showMessageDialog(null,"There is no information connected to this card");
            }
            if (continu == true) {
                sqlQuery = "SELECT t.type_name AS bike_type, (TIME_TO_SEC(TIMEDIFF(br.end_time, br.start_time))/3600) AS hours, t.type_rentprice AS price_hour, ((TIME_TO_SEC(TIMEDIFF(br.end_time, br.start_time))/3600)*t.type_rentprice) AS price\n" +
                        "FROM rent r\n" +
                        "LEFT JOIN bicycle_rent br\n" +
                        "ON r.rent_id = br.rent_id\n" +
                        "LEFT JOIN bicycle b\n" +
                        "ON br.bicycle_id = b.bicycle_id\n" +
                        "LEFT JOIN type t\n" +
                        "ON b.type_name = t.type_name\n" +
                        "WHERE r.card_token = ? AND r.rent_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, cardtoken);
                query.setInt(2, rentId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    while (res.next()) {
                        String writeTo = "El-Bicycle type: " + res.getString("bike_type");
                        boolean s = writeToFile(writeTo, "receipt.txt");
                        writeTo = "Price pr hour: " + res.getDouble("price_hour");
                        s = writeToFile(writeTo, "receipt.txt");
                        writeTo = "Hours rented " + res.getDouble("hours");
                        s = writeToFile(writeTo, "receipt.txt");
                        writeTo = "Price: " + res.getDouble("price");
                        s = writeToFile(writeTo, "receipt.txt");
                        s = writeToFile("------------------------------------------------------------", "receipt.txt");

                    }
                }
                boolean s = writeToFile("Before MVA: " + Double.parseDouble(df.format(finalPrice * 0.75)) + "   MVA-%: 25%   MVA: " + Double.parseDouble((df.format(finalPrice * 0.25))) + "   Total price: " + Double.parseDouble(df.format(finalPrice)), "receipt.txt");
            }
            res.close();
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return r; // false if already registered
    }

    public ArrayList<String> getAllAvailableTypesBicycles(){ //returns a list of all the different types that is registered at a dockingstation and has a battery level over 50
        ArrayList<String> allAvailableTypesBicycles = new ArrayList<String>();
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT * FROM bicycle b, dockingstation d, rentmachine r WHERE b.ds_id = d.ds_id AND d.ds_id = r.ds_id AND rentmachine_id = ? AND battery_lvl > 50";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, RENT_MACHINE_NR);
            res = query.executeQuery();
            while (res.next()){
                String newType = res.getString("type_name");
                boolean alreadyThere = false;
                String type = "";
                //checking if the type already is registered in the arraylist
                for(int i = 0; i < allAvailableTypesBicycles.size(); i++){
                    type = allAvailableTypesBicycles.get(i);
                    if (type.equals(newType)){
                        alreadyThere = true;
                    }
                }
                if(!alreadyThere){
                    allAvailableTypesBicycles.add(newType);
                }
            }
            res.close();
        }
        catch(SQLException e){
            Opprydder.skrivMelding(e, "Something went wrong");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return allAvailableTypesBicycles;
    }

    public ArrayList<String> getAmountBicyclesAvailable(String typeName){ //returns a list with numbers that count all the bicycles of a specific type at a dockingstation and with a battery level over 50
        ArrayList<String> amountBicyclesAvailable = new ArrayList<>();
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT COUNT(bicycle_id) AS amount FROM bicycle b, dockingstation d, rentmachine r WHERE b.ds_id = d.ds_id AND d.ds_id = r.ds_id AND rentmachine_id = ? AND type_name = ? AND battery_lvl > 50";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, RENT_MACHINE_NR);
            query.setString(2, typeName);
            res = query.executeQuery();
            while(res.next()){
                int amount = res.getInt("amount");
                for (int i = 0; i < amount; i++) {
                    amountBicyclesAvailable.add(Integer.toString(i + 1));
                }
            }
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
        return amountBicyclesAvailable;
    }

    public int getBicycleId(String typeName){ //returns a random bicycle id from a bicycle that is at a specific dockingstation and is a specific type
        int bicycleId = 0;
        //the reason we want to have a random bicycle id is so that it's not always the same bikes that is being rented
        PreparedStatement query = null;
        PreparedStatement query2 = null;
        ResultSet res = null;
        ResultSet res2 = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT * FROM bicycle b, dockingstation d, rentmachine r WHERE b.ds_id = d.ds_id AND d.ds_id = r.ds_id AND rentmachine_id = ? AND type_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, RENT_MACHINE_NR);
            query.setString(2, typeName);
            res = query.executeQuery();

            //counting how many bikes there is so that the random number can't get higher than the number og bikes available
            String sqlQuery2 = "SELECT COUNT(bicycle_id) AS amount FROM bicycle b, dockingstation d, rentmachine r WHERE b.ds_id = d.ds_id AND d.ds_id = r.ds_id AND rentmachine_id = ? AND type_name = ?";
            query2 = con.getForbindelse().prepareStatement(sqlQuery2);
            query2.setInt(1, RENT_MACHINE_NR);
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
        //the reason we do this is because we need to insert lines into the bicycle_rent before we add one into rent but the bicycle_rent needs a rent id
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

    public int addToCart(String typeName, int rentId){ //returns the bicycle id of the bicycle that is added to the cart
        int bicycleId = getBicycleId(typeName);
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

    public double getPrice(ArrayList<Integer> bicycleIds){ //returns the combined price of all the bicycles that is in the ArrayList
        double price = 0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            for(int i = 0; i < bicycleIds.size(); i++){
                int bicycleId = bicycleIds.get(i);
                String sqlQuery = "SELECT type_rentprice AS price FROM type t, bicycle b WHERE t.type_name = b.type_name AND bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, bicycleId);
                res = query.executeQuery();
                if(res.isBeforeFirst()) {
                    res.next();
                    price += res.getDouble("price");
                }else{
                    return -1;
                }
            }
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return price;
    }

    public boolean rent(int rentId, String cardToken){ //a method that updates the rest values of to the rent id we already made
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
                    //updates the rent price to 0 because this will be added when the trip is ended
                    String sqlUp = "UPDATE rent SET rent_price = 0 WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, rentId);
                    query.executeUpdate();
                    sqlUp = "UPDATE rent SET rentmachine_id = ? WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, RENT_MACHINE_NR);
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

    public double getDeposit(){ //returns the current deposit value
        Statement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double deposit = 0;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT deposit_value FROM properties WHERE properties_set = 1";
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                res.next();
                deposit = res.getDouble("deposit_value");
            }else{
                System.out.println("Something went wrong, properties_set might not exist");
                return -1;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return deposit;
    }

    public double getRefreshMapRate(){ //returns the refresh map every seconds current value from properties
        Statement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double minutes = 0;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT refresh_map_every_minutes FROM properties WHERE properties_set = 1";
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                res.next();
                minutes = res.getDouble("refresh_map_every_minutes");
            }else{
                System.out.println("Something went wrong, properties_set might not exist");
                return -1;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return minutes;
    }

    public boolean rerollRent(int rentId, ArrayList<Integer> bikeList){ //a method that reroll addToCart and rent if someone decides not to rent the bicycles anyway
        boolean ok = false;
        int tries = 0;
        do {
            ResultSet res = null;
            PreparedStatement query = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                if(rentId != -1) {
                    con.getForbindelse().setAutoCommit(false);
                    String sqlUp = "DELETE FROM rent WHERE rent_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, rentId);
                    query.executeUpdate();
                /*now we have deleted the rent and all bicycle in bicycle_rent with the same rentId have been deleted
                we need to put them back on the dockingstation*/
                    String sqlQuery = "SELECT ds_id FROM rentmachine WHERE rentmachine_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlQuery);
                    query.setInt(1, RENT_MACHINE_NR);
                    res = query.executeQuery();
                    res.next();
                    int dsId = res.getInt("ds_id");
                    int amount = bikeList.size();
                    for (int i = 0; i < amount; i++) {//Lets the user add multiple of the same bikes at once
                        sqlUp = "UPDATE bicycle SET ds_id = ? WHERE bicycle_id = ?";
                        query = con.getForbindelse().prepareStatement(sqlUp);
                        query.setInt(1, dsId);
                        query.setInt(2, bikeList.get(i));
                        query.executeUpdate();
                        sqlUp = "UPDATE bicycle SET bicycle_trips = bicycle_trips-1 WHERE bicycle_id = ?";
                        query = con.getForbindelse().prepareStatement(sqlUp);
                        query.setInt(1, bikeList.get(i));
                        query.executeUpdate();

                    }
                }else {
                    System.out.println("Not rerolled, correct outprint");
                    return false;
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
                Opprydder.lukkSetning(query);
                Opprydder.settAutoCommit(con.getForbindelse());
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return true;
    }
}
