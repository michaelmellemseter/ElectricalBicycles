/*
A class that contains all the database-related methods used inside Admin_JFrame.
@Author Team 09
created on 2018-04-22
 */

import mittBibliotek.Opprydder;
import javax.swing.*;
import java.sql.*;
import java.text.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import mittBibliotek.database.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Admin_database {
    private DatabasePool dbPool;

    public Admin_database(int poolCapacity, String dbDriver, String dbName) throws Exception {
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

    public String addType(String name, String desc, double price){//making function to return, a boolean. Does not need to use the return value if not needed.
        boolean ok = false;
        int tries = 0;
        String r = "";
        do {
            ResultSet res = null;
            PreparedStatement query = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                r="";
                String sqlQuery = "SELECT * FROM type WHERE type_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, name);
                res = query.executeQuery();
                if (!res.isBeforeFirst()) {
                    String sqlIns = "insert into type values(?, ?, ?)";
                    query = con.getForbindelse().prepareStatement(sqlIns);
                    query.setString(1, name);
                    query.setString(2, desc);
                    query.setDouble(3, price);
                    query.executeUpdate();
                    r = name;
                } else {
                    r = "-1";
                }
                ok = true;
                res.close();
            } catch (SQLException e) {
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return "-1";
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r; // false if already registered
    }

    public boolean editType(String name, String desc, double price){//making function to return, a boolean. Does not need to use the return value if not needed.
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//In this software the adminJframe will handel wherever type_name exists, but I will check anyway so it can be expanded to be used in different places, plus if something goes wrong in the jframe handling this class wont not commit to database and return true
                con = dbPool.reserverForbindelse();
                con.getForbindelse().setAutoCommit(false);
                String sqlQuery = "SELECT * FROM type WHERE type_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, name);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE type SET type_desc = ? WHERE type_name = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, desc);
                    query.setString(2, name);
                    query.executeUpdate();
                    sqlUp = "UPDATE type SET type_rentprice= ? WHERE type_name = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, price);
                    query.setString(2, name);
                    query.executeUpdate();
                    r = true;
                }else {
                    System.out.println("Something went wrong, name input might not exist");
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

    public boolean deleteType(String name){//making function to return, a boolean. Does not need to use the return value if not needed.
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//wherever the type_name actually exist do we not need to worry about becuase the user will have to chose from a list of types that exist
                con = dbPool.reserverForbindelse();
                String sqlQuery = "SELECT bicycle_id FROM bicycle WHERE type_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, name);
                res = query.executeQuery();
                String bikes = "";
                if(res.isBeforeFirst()){
                    while(res.next()){
                        bikes += ", " + res.getInt("bicycle_id");
                    }
                }
                if (JOptionPane.showConfirmDialog(null, "If you delete " + name + " you will alos delete these bicycles: " + bikes + ". Press yes you are sure.", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {


                    sqlQuery = "SELECT * FROM type WHERE type_name = ?";
                    query = con.getForbindelse().prepareStatement(sqlQuery);
                    query.setString(1, name);
                    res = query.executeQuery();
                    if (res.isBeforeFirst()) {
                        String sqlUp = "DELETE FROM type WHERE type_name = ?";
                        query = con.getForbindelse().prepareStatement(sqlUp);
                        query.setString(1, name);
                        query.executeUpdate();
                        r = true;
                    } else {
                        System.out.println("Something went wrong, name input might not exist");
                        r = false;
                    }
                }else{
                    r = false;
                }
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

    public int getTypeAmount(String type){
        int amount=0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            String sqlQuery = "SELECT COUNT(*) AS counted FROM bicycle WHERE type_name=?";
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, type);
            res = query.executeQuery();
            if(res.isBeforeFirst()){
                res.next();
                amount = res.getInt("counted");
                if(amount == 0){
                    return -1;
                }
                System.out.println("Found value: "+ amount);
            }
            res.close();
            return amount;
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
        return amount;
    }

    public ArrayList<String> addBike(double price, String make, String typeName, int amount, int dsId, double lat, double lon){//making function to return, a boolean. Does not need to use the return value if not needed.
        boolean ok = false;
        int tries = 0;
        ArrayList<String> list = new ArrayList<String>();
        do {
            ResultSet res = null;
            Statement query2 = null;
            PreparedStatement prepQuery = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                list.clear();
                con.getForbindelse().setAutoCommit(false);
                for(int i = 0; i<amount; i++) {//Lets the user add multiple of the same bikes at once
                    query2 = con.getForbindelse().createStatement();
                    String sqlQuery = "SELECT * FROM bicycle";
                    res = query2.executeQuery(sqlQuery);
                    int nextNr = 0;
                    if (res.isBeforeFirst()) {
                        sqlQuery = "SELECT MAX(bicycle_id) AS max FROM bicycle";
                        res = query2.executeQuery(sqlQuery);
                        res.next();
                        nextNr = res.getInt("max") + 1;
                        list.add(""+nextNr);
                    }
                    String sqlIns = "insert into bicycle values(?, 100, ?, ?, CURRENT_DATE, 0, 0, ?, 0, ?, ?, ?)";
                    prepQuery = con.getForbindelse().prepareStatement(sqlIns);
                    prepQuery.setInt(1, nextNr);
                    prepQuery.setDouble(2, price);
                    prepQuery.setString(3, make);
                    prepQuery.setString(4, typeName);
                    prepQuery.setInt(5, dsId);
                    prepQuery.setDouble(6, lat);
                    prepQuery.setDouble(7, lon);
                    prepQuery.executeUpdate();
                }
                ok = true;
                con.getForbindelse().commit();
            } catch (SQLException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    ArrayList<String> fail = new ArrayList<String>();
                    fail.add("-1");
                    return fail;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query2);
                Opprydder.lukkSetning(prepQuery);
                Opprydder.settAutoCommit(con.getForbindelse());
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return list;
    }

    public boolean editBike(int bikeId, double price, String make, String date, double km, int trips, String typeName){//making function to return, a boolean. Does not need to use the return value if not needed.
        //the rest of the Bike variabels that is not in the input should never be needed to be edited
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//In this software the adminJframe will handel wherever type_name exists, but I will check anyway so it can be expanded to be used in different places, plus if something goes wrong in the jframe handling this class wont not commit to database and return true
                con = dbPool.reserverForbindelse();
                con.getForbindelse().setAutoCommit(false);
                String sqlQuery = "SELECT * FROM bicycle WHERE bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, bikeId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE bicycle SET bicycle_price = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, price);
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET producer_name = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, make);
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET bicycle_regdate = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDate(1, java.sql.Date.valueOf(date));
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET bicycle_km = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, km);
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET bicycle_trips = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, trips);
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    sqlUp = "UPDATE bicycle SET type_name = ? WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, typeName);
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    r = true;
                }else {
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
            }catch (IllegalArgumentException e){
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong ");
                    return false;
                }
            }

            finally {
                Opprydder.settAutoCommit(con.getForbindelse());
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r;
    }

    public boolean deleteBike(int bikeId){//making function to return, a boolean. Does not need to use the return value if not needed.
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//wherever the type_name actually exist do we not need to worry about becuase the user will have to chose from a list of types that exist
                con = dbPool.reserverForbindelse();
                String sqlQuery = "SELECT * FROM bicycle WHERE bicycle_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, bikeId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "DELETE FROM bicycle WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, bikeId);
                    query.executeUpdate();
                    r = true;
                }else {
                    System.out.println("Something went wrong, input might not exist");
                    r = false;
                }
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

    public int getRepairId(){ //returns the highest repair id + 1 that will be used when requesting a new repair
        int repairId = 0;
        String sqlQuery = "SELECT MAX(repair_id) AS repair_id FROM repair";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                res.next();
                int repair_id = res.getInt("repair_id");
                repairId = repair_id + 1;
            }else{
                repairId = 1;
            }
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
        return repairId;
    }

    public ArrayList<Integer> getAllRepairIds(){
        ArrayList<Integer> repairs = new ArrayList<Integer>();

        String sqlQuery = "SELECT * FROM repair";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            while (res.next()) {
                repairs.add(res.getInt("repair_id"));
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
        return repairs;
    }

    public Date getSentDate(int id){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        Date date = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT sent_date FROM repair WHERE repair_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, id);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                date = res.getDate("sent_date");
            }else{
                System.out.println("");
                return date;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return date;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return date;
    }

    public double getRepairCost(int year){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double price;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT SUM(repair_price) AS countedPrice FROM repair WHERE YEAR(sent_date) = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, year);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                price = res.getDouble("countedPrice");
                return price;
            }else{
                return 0.0;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
    }

    public boolean requestRepair(int bicycleId, String text){ //a method to request a repair. Updates the bicycle to under reapir and makes a repair with repair id and other values that is necessary
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        //current date:
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
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
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE bicycle SET under_repair = TRUE WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, bicycleId);
                    query.executeUpdate();
                    sqlUp = "INSERT INTO repair VALUES(?, ?,  NULL, ?, NULL, ?, NULL)";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, getRepairId());
                    query.setInt(2, bicycleId);
                    query.setDate(3, date);
                    query.setString(4, text);
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

    public int getRepairId2(int bicycleId){ //returns the repair id to a specific bike that is to repair
        int repairId = 0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            //checking if the sent_date is null so that the repair id is from the repair that is not finished yet
            String sqlQuery = "SELECT repair_id FROM repair WHERE bicycle_id = ? AND sent_date IS NULL";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bicycleId);
            res = query.executeQuery();
            if(res.isBeforeFirst()){
                res.next();
                repairId = res.getInt("repair_id");
            }else{
                System.out.println("Bicycle is not requested for repair or the repair might already be registered");
                return -1;
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
        return repairId;
    }

    public boolean registerRepair(int bicycleId, double price, String text){ //a method to register a repair that is finished. Updates the repair that is already registered in requestRepair()
        if(getRepairId2(bicycleId) == -1){
            return false;
        }
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        //current date:
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
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
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE bicycle SET under_repair = FALSE WHERE bicycle_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, bicycleId);
                    query.executeUpdate();
                    sqlUp = "UPDATE repair SET sent_date = ? WHERE repair_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDate(1, date);
                    query.setInt(2, getRepairId2(bicycleId));
                    query.executeUpdate();
                    sqlUp = "UPDATE repair SET repair_price = ? WHERE repair_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setDouble(1, price);
                    query.setInt(2, getRepairId2(bicycleId));
                    query.executeUpdate();
                    sqlUp = "UPDATE repair SET desc_after = ? WHERE repair_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, text);
                    query.setInt(2, getRepairId2(bicycleId));
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

    public ArrayList<String> getAllTypes(){
        ArrayList<String> types = new ArrayList<String>();

        String sqlQuery = "SELECT * FROM type";
        ResultSet res = null;
        Statement query = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().createStatement();
            res = query.executeQuery(sqlQuery);
            while (res.next()) {
                String type_name = res.getString("type_name");
                types.add(type_name);
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ArrayList<String> fail = new ArrayList<String>();
            fail.add("-1");
            return fail;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return types;
    }

    public String getTypeDesc(String type){
        String desc = "";

        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT type_desc FROM type WHERE type_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, type);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                desc = res.getString("type_desc");
            }else{
                System.out.println("Type does not exist");
                return "-1";
            }
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return "-1";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return desc;
    }

    public double getPrice(String type){
        double price = 0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT type_rentprice FROM type WHERE type_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, type);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                price = res.getDouble("type_rentprice");
            }else{
                System.out.println("Type does not exist");
                return -1;
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

    public ArrayList<String> getAllBikes(){
        ArrayList<String> list = new ArrayList<String>();
        Statement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            list.clear();
            query = con.getForbindelse().createStatement();
            String sqlQuery = "SELECT bicycle_id FROM bicycle ORDER BY bicycle_id";
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                while (res.next()) {
                    list.add(""+res.getInt("bicycle_id"));
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
            dbPool.frigiForbindelse(con.getNr());
        }
        return list;
    }

    public double getBikePrice(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double price = 0;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT bicycle_price FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                price = res.getDouble("bicycle_price");
            }else{
                System.out.println("Bike does not exist");
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
        return price;
    }

    public String getBikeRegdate(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        String regdate = "";
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT bicycle_regdate FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                regdate = (res.getDate("bicycle_regdate")).toString();
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
            dbPool.frigiForbindelse(con.getNr());
        }
        return regdate;
    }

    public double getBikeKm(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double km = 0;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT bicycle_km FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                km = res.getDouble("bicycle_km");
            }else{
                System.out.println("Bike does not exist");
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
        return km;
    }

    public int getBikeTrips(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        int trips = 0;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT bicycle_trips FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                trips = res.getInt("bicycle_trips");
            }else{
                System.out.println("Bike does not exist");
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
        return trips;
    }

    public String getBikeType(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        String trips = "";
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT type_name FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                trips = res.getString("type_name");
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
            dbPool.frigiForbindelse(con.getNr());
        }
        return trips;
    }

    public String getBikeMake(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        String make = "";
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT producer_name FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                make = res.getString("producer_name");
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
            dbPool.frigiForbindelse(con.getNr());
        }
        return make;
    }

    public int getBikeDockingStation(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        int station;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT ds_id FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                station = res.getInt("ds_id");
                return station;
            }else{
                System.out.println("Bike has no docking station.");
                return -1;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong.");
            return -1;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
    }

    public int getBikeRepairAmount(int bikeId){
        int amount=0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            String sqlQuery = "SELECT COUNT(*) AS counted FROM repair WHERE bicycle_id=?";
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if(res.isBeforeFirst()){
                res.next();
                amount=res.getInt("counted");
            }
            res.close();
            return amount;
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            amount=-1;
            return amount;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return amount;
    }

    public double getBatteryLvl(int bikeId){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        double batteryLvl;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT battery_lvl FROM bicycle WHERE bicycle_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, bikeId);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                batteryLvl = res.getDouble("battery_lvl");
            }else{
                System.out.println("Bike does not exist");
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
        return batteryLvl;
    }

    public ArrayList<String> getAllMakes(){
        ArrayList<String> list = new ArrayList<String>();
        Statement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            list.clear();
            query = con.getForbindelse().createStatement();
            String sqlQuery = "SELECT producer_name FROM producer";
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                while (res.next()) {
                    list.add(res.getString("producer_name"));
                }
            }
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ArrayList<String> fail = new ArrayList<String>();
            fail.add("-1");
            return fail;
        }
        finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return list;
    }

    public String getMakeDate(String producer_name){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        String date = "";
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT established_date FROM producer WHERE producer_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, producer_name);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                date = (res.getDate("established_date")).toString();
            }else{
                System.out.println("Make does not exist");
                return "-1";
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return "-1";
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return date;
    }

    public ArrayList<String> getAllDockingStations(){ //Method used to show all the dockingstation ID's. Used to show all ID's inside a JComboBox on Admin_JFrame
        ArrayList<String> allDockingStations = new ArrayList<String>();
        Statement stmtQuery = null;
        ResultSet res = null;
        Forbindelse con = null;
        String sqlQuery = "select ds_id from dockingstation where ds_id IS NOT NULL";
        try{
            con = dbPool.reserverForbindelse();
            stmtQuery = con.getForbindelse().createStatement();
            res = stmtQuery.executeQuery(sqlQuery);
            while(res.next()){
                int stationId = res.getInt("ds_id");
                allDockingStations.add(Integer.toString(stationId));
            }
        }catch(SQLException e){
            Opprydder.skrivMelding(e, "Something went wrong");
            allDockingStations.add("-1");
        }finally{
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(stmtQuery);
            dbPool.frigiForbindelse(con.getNr());
        }
        return allDockingStations;
    }

    public int showDsBikeCap(int id){
        String sqlQuery = "select charging_units from dockingstation where ds_id = ?";
        ResultSet res = null;
        PreparedStatement query = null;
        Forbindelse con = null;
        int cap = 0;
        try{
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, id);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                cap = res.getInt("charging_units");
            }else{
                System.out.println("Station does not exist");
                return -1;
            }
        }catch(SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return cap;
    }

    public double showDsPowerCap(int id){
        String sqlQuery = "select kwh from dockingstation ds where ds_id = ?";
        ResultSet res = null;
        PreparedStatement query = null;
        Forbindelse con = null;
        double cap = 0;
        try{
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, id);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                cap = res.getDouble("kwh");
            }else{
                System.out.println("Station does not exist");
                return -1;
            }
        }catch(SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return -1;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return cap;
    }

    public int addStation(String adress, int chargingUnits, double lat, double lon, int amountRentmachine){//making function to return, a boolean. Does not need to use the return value if not needed.
        boolean ok = false;
        int tries = 0;
        int r = -1;
        do {
            ResultSet res = null;
            Statement query = null;
            PreparedStatement prepQuery = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                con.getForbindelse().setAutoCommit(false);
                query = con.getForbindelse().createStatement();
                String sqlQuery = "SELECT * FROM dockingstation";
                res = query.executeQuery(sqlQuery);
                int nextNr = 0;
                if (res.isBeforeFirst()) {
                    sqlQuery = "SELECT MAX(ds_id) AS max FROM dockingstation";
                    res = query.executeQuery(sqlQuery);
                    res.next();
                    nextNr = res.getInt("max") + 1;
                    r = nextNr;
                }
                String sqlIns = "insert into dockingstation values(?, ?, ?, ?, ?, ?)";
                prepQuery = con.getForbindelse().prepareStatement(sqlIns);
                prepQuery.setInt(1, nextNr);
                prepQuery.setString(2, adress);
                prepQuery.setInt(3, chargingUnits);
                prepQuery.setDouble(4, 0.0);
                prepQuery.setDouble(5, lat);
                prepQuery.setDouble(6, lon);
                prepQuery.executeUpdate();

                for(int a = 0; a < amountRentmachine; a++) {
                    sqlQuery = "select * from rentmachine";
                    res = query.executeQuery(sqlQuery);
                    int nextNr2 = 0;
                    if (res.isBeforeFirst()) {
                        sqlQuery = "SELECT max(rentmachine_id) as max from rentmachine";
                        res = query.executeQuery(sqlQuery);
                        res.next();
                        nextNr2 = res.getInt("max") + 1;
                    }
                    sqlIns = "insert into rentmachine values(?, ?)";
                    prepQuery = con.getForbindelse().prepareStatement(sqlIns);
                    prepQuery.setInt(1, nextNr2);
                    prepQuery.setInt(2, nextNr);
                    prepQuery.executeUpdate();
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
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                Opprydder.settAutoCommit(con.getForbindelse());
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r;
    }

    public boolean editDockingStation(int stationId, int bCap){
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
                String sqlQuery = "SELECT * FROM dockingstation WHERE ds_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, stationId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE dockingstation SET charging_units = ? WHERE ds_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, bCap);
                    query.setInt(2, stationId);
                    query.executeUpdate();
                    r = true;
                } else {
                    System.out.println("Something went wrong");
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

    public boolean deleteDockingStation(int id){
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                String sqlQuery = "SELECT * FROM dockingstation WHERE ds_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, id);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "DELETE FROM dockingstation WHERE ds_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, id);
                    query.executeUpdate();
                    r = true;
                }else {
                    System.out.println("Something went wrong, dockingstation-id might not exist");
                    r = false;
                }
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

    public String getDockingStationAdress(int id){
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        String adress;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT adress FROM dockingstation WHERE ds_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, id);
            res = query.executeQuery();
            if (res.isBeforeFirst()) {
                res.next();
                adress = (res.getString("adress"));
            }else{
                System.out.println("Docking station does not exist");
                return "-1";
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            return "-1";
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return adress;
    }

    public int getAmountBikesAtStation(int id){
        int amount=0;
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            String sqlQuery = "SELECT COUNT(*) AS counted FROM bicycle WHERE ds_id=?";
            con = dbPool.reserverForbindelse();
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, id);
            res = query.executeQuery();
            if(res.isBeforeFirst()){
                res.next();
                amount = res.getInt("counted");
            }
            res.close();
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
        return amount;
    }

    public ArrayList<Integer> getBikesIdsAtStation(int id){
        ArrayList<Integer> ids = new ArrayList<>();
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
                while(res.next()){
                    ids.add(res.getInt("bicycle_id"));
                }
            }
            else{
                ids.add(0);
            }
            res.close();
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            ids.add(-1);
            return ids;
        }
        catch (Exception e) {
            e.printStackTrace();
            ids.add(-1);
            return ids;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return ids;
    }

    public ArrayList<String> getRentmachines(int stationId){
        ArrayList<String> rentmachineIds = new ArrayList<String>();
        PreparedStatement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            String sqlQuery = "SELECT rentmachine_id FROM rentmachine WHERE ds_id = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setInt(1, stationId);
            res = query.executeQuery();
            if(res.isBeforeFirst()) {
                while (res.next()) {
                    int machine = res.getInt("rentmachine_id");
                    rentmachineIds.add(Integer.toString(machine));
                }
            }else{
                ArrayList<String> fail = new ArrayList<>();
                fail.add("-1");
                return fail;
            }
        } catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong.");
            ArrayList<String> fail = new ArrayList<>();
            fail.add("-1");
            return fail;
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(query);
            dbPool.frigiForbindelse(con.getNr());
        }
        return rentmachineIds;
    }

    public ArrayList<String> addRentmachines(int stationId, int amount){
        boolean ok = false;
        int tries = 0;
        ArrayList<String> list = new ArrayList<String>();
        do {
            ResultSet res = null;
            Statement query = null;
            PreparedStatement prepQuery = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                list.clear();
                con.getForbindelse().setAutoCommit(false);
                for(int i = 0; i<amount; i++) {
                    query = con.getForbindelse().createStatement();
                    String sqlQuery = "SELECT * FROM rentmachine";
                    res = query.executeQuery(sqlQuery);
                    int nextNr = 0;
                    if (res.isBeforeFirst()) {
                        sqlQuery = "SELECT MAX(rentmachine_id) AS max FROM rentmachine";
                        res = query.executeQuery(sqlQuery);
                        res.next();
                        nextNr = res.getInt("max") + 1;
                        list.add(""+nextNr);
                    }
                    String sqlIns = "insert into rentmachine values(?, ?)";
                    prepQuery = con.getForbindelse().prepareStatement(sqlIns);
                    prepQuery.setInt(1, nextNr);
                    prepQuery.setDouble(2, stationId);
                    prepQuery.executeUpdate();
                }
                ok = true;
                con.getForbindelse().commit();
            } catch (SQLException e) {
                Opprydder.rullTilbake(con.getForbindelse());
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    ArrayList<String> fail = new ArrayList<String>();
                    fail.add("-1");
                    return fail;
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                Opprydder.lukkSetning(prepQuery);
                Opprydder.settAutoCommit(con.getForbindelse());
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return list;
    }

    public boolean deleteRentmachine(int rentmachineId){
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                String sqlQuery = "SELECT * FROM rentmachine WHERE rentmachine_id = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setInt(1, rentmachineId);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "DELETE FROM rentmachine WHERE rentmachine_id = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setInt(1, rentmachineId);
                    query.executeUpdate();
                    r = true;
                }else {
                    System.out.println("Something went wrong, rentmachine-id might not exist");
                    r = false;
                }
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

    public String addMake(String name, String date){
        if(!checkDate(date, "yyyy-MM-dd")){
            return "-1";
        }
        boolean ok = false;
        int tries = 0;
        String r = "";
        do {
            ResultSet res = null;
            PreparedStatement query = null;
            Forbindelse con = null;
            try {

                con = dbPool.reserverForbindelse();
                r="";
                String sqlQuery = "SELECT * FROM producer WHERE producer_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, name);
                res = query.executeQuery();
                if (!res.isBeforeFirst()) {
                    java.sql.Date javaSqlDate = java.sql.Date.valueOf(date);
                    String sqlIns = "insert into producer values(?, ?)";
                    query = con.getForbindelse().prepareStatement(sqlIns);
                    query.setString(1, name);
                    query.setDate(2, javaSqlDate);
                    query.executeUpdate();
                    r = name;
                } else {
                    System.out.println("Something went wrong, name might already exist");
                    r = "-1";
                }
                ok = true;
                res.close();
            } catch (SQLException e) {
                if (tries < 4) {
                    tries++;
                } else {
                    Opprydder.skrivMelding(e, "Something went wrong");
                    return "-1";
                }
            } finally {
                Opprydder.lukkResSet(res);
                Opprydder.lukkSetning(query);
                dbPool.frigiForbindelse(con.getNr());
            }
        }while (!ok);
        return r; // false if already registered
    }

    public boolean editMakes(String date, String name){
        if(!checkDate(date, "yyyy-MM-dd")){
            return false;
        }
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
                String sqlQuery = "SELECT * FROM producer WHERE producer_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, name);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "UPDATE producer SET established_date = ? WHERE producer_name = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, date);
                    query.setString(2, name);
                    query.executeUpdate();
                    r = true;
                } else {
                    System.out.println("Something went wrong");
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

    public boolean deleteMake(String name){
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//wherever the type_name actually exist do we not need to worry about becuase the user will have to chose from a list of types that exist
                con = dbPool.reserverForbindelse();
                String sqlQuery = "SELECT bicycle_id FROM bicycle WHERE producer_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, name);
                res = query.executeQuery();
                String bikes = "";
                if(res.isBeforeFirst()){
                    while(res.next()){
                        bikes += ", " + res.getInt("bicycle_id");
                    }
                }
                if (JOptionPane.showConfirmDialog(null, "If you delete " + name + " you will also delete these bicycles: " + bikes + ". Press yes you are sure.", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    sqlQuery = "SELECT * FROM producer WHERE producer_name = ?";
                    query = con.getForbindelse().prepareStatement(sqlQuery);
                    query.setString(1, name);
                    res = query.executeQuery();
                    if (res.isBeforeFirst()) {
                        String sqlUp = "DELETE FROM producer WHERE producer_name = ?";
                        query = con.getForbindelse().prepareStatement(sqlUp);
                        query.setString(1, name);
                        query.executeUpdate();
                        r = true;
                    } else {
                        System.out.println("Something went wrong, input might not exist");
                        r = false;
                    }
                }else{
                    r = false;
                }
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
    
    public boolean register(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        Hashing hash = new Hashing();
        String s = hash.Hash(password);
        String[] ts = s.split(":");
        ResultSet res = null;
        PreparedStatement query = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            //Checking to see if there's already a user with these values
            String sqlQuery = "select count(*) as total from admin_user where user_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, username);
            res = query.executeQuery();
            res.next();
            if(res.getInt("total") > 0){
                System.out.println("Username Already Taken");
                return false;
            } else {
                System.out.println("Registering new user");
                sqlQuery =
                        "insert into admin_user(user_name, gen_hash, finished_hash) values(?, ?, ?)";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, username);
                query.setString(2, ts[0]);
                query.setString(3, ts[1]);
                query.executeUpdate();
                return true;
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        } finally {
            if(query != null && !query.isClosed()){
                Opprydder.lukkSetning(query);
            }
            Opprydder.lukkResSet(res);
            dbPool.frigiForbindelse(con.getNr());
        }
    }

    public boolean login(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        ResultSet res = null;
        PreparedStatement query = null;
        Forbindelse con = null;
        try{
            con = dbPool.reserverForbindelse();
            String sqlQuery = "select gen_hash as salt, finished_hash as password from admin_user where user_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, username);
            res = query.executeQuery();
            res.next();
            if(!res.getString("salt").isEmpty() && !res.getString("password").isEmpty()){
                String salt = res.getString("salt");
                String pass = res.getString("password");
                Hashing hash = new Hashing();
                //If the check test passes it allows the login, meaning the user entered a valid username and password combination
                boolean check = hash.check(salt, password, pass);
                if(check){
                    System.out.println("Login Approved");
                    return true;
                } else {
                    System.out.println("No user data found");
                    return false;
                }
            } else {
                System.out.println("No user data found");
                return false;
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        } finally {
            if(query != null && !query.isClosed()){
                query.close();
            }
            Opprydder.lukkResSet(res);
            dbPool.frigiForbindelse(con.getNr());
        }
    }

    //Generates a randomized password
    public String genPass(){ 
        Random rand = new Random();

        int[] bigLetter = {90, 65};
        int[] smallLetter = {122, 97};
        int[] tall = {57, 48};

        //Decide the passwords length, this makes it so the password is at least 7 characters, and at most 12.
        int size = rand.nextInt((12 - 7) + 1) + 7;

        //Creating arrays to contain the valus of different datatypes
        //Generated passwords only contain numbers, lowercase characters or uppercase characters
        char[] charsBig = new char[size];
        char[] charsSmall = new char[size];
        int[] tallene = new int[size];

        for(int i = 0; i < size; i++){
            charsBig[i] = (char) (rand.nextInt((bigLetter[0] - bigLetter[1]) + 1) + bigLetter[1]);
            charsSmall[i] = (char) (rand.nextInt((smallLetter[0] - smallLetter[1]) + 1) + smallLetter[1]);
            tallene[i] = rand.nextInt((tall[0] - tall[1]) + 1) + tall[1];
        }

        String genPass = "";

        for(int i = 0; i < size; i++){
            int n = rand.nextInt(((size - 1) + 1) + 1);
            if(n < 4){
                genPass += Character.toString(charsBig[i]);
            } else if(n >= 4 && n <= 8){
                genPass += Character.toString(charsSmall[i]);
            } else {
                genPass += Integer.toString(tallene[i]);
            }
        }
        return genPass;
    }

    /*
    epost code is based upon the code created by Laabidi Raissi, with some minor changes. Original code
    which can be found here:
    https://stackoverflow.com/questions/15597616/sending-email-via-gmail-smtp-server-in-java
    Date: 16.April.2018
    */

    public void epost(String mottaker, String password){ //This didn't have password as argument
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", "DatabaseProsjektTeam09");
        props.put("mail.smtp.password", "Sah9BibQ");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");


        Session session = Session.getInstance(props,null);
        MimeMessage message = new MimeMessage(session);

        System.out.println("Port: "+session.getProperty("mail.smtp.port"));

        try {
            InternetAddress from = new InternetAddress("DatabaseProsjektTeam09@gmail.com");
            message.setSubject("Password for admin user");
            message.setFrom(from);
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mottaker));

            Multipart multipart = new MimeMultipart("alternative");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Your new password!");

            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            String htmlMessage = password;  //this was just a random string. I first changed it to genPass(), but I need to save the value
            messageBodyPart.setContent(htmlMessage, "text/html");


            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "DatabaseProsjektTeam09", "Sah9BibQ");
            System.out.println("Transport: "+transport.toString());
            transport.sendMessage(message, message.getAllRecipients());

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean change(String username, String newpassword) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException{
        PreparedStatement query = null;
        Forbindelse con = null;
        try{
            Hashing hash = new Hashing();
            String s = hash.Hash(newpassword);
            String[] ord = s.split(":");
            con = dbPool.reserverForbindelse();
            String sqlQuery = "update admin_user set gen_hash = ?, finished_hash = ? where user_name = ?";
            query = con.getForbindelse().prepareStatement(sqlQuery);
            query.setString(1, ord[0]);
            query.setString(2,ord[1]);
            query.setString(3, username);

            if(query.executeUpdate() > 0){
                return true;
            } else {
                System.out.println("Something went wrong");
                return false;
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        } finally {
            if(query != null && !query.isClosed()){
                query.close();
            }
        }
    }

    public ArrayList<String> getAllAdmins(){
        ArrayList<String> allAdmins = new ArrayList<String>();
        Statement stmtQuery = null;
        ResultSet res = null;
        Forbindelse con = null;
        String sqlQuery = "select user_name from admin_user where user_name IS NOT NULL";
        try{
            con = dbPool.reserverForbindelse();
            stmtQuery = con.getForbindelse().createStatement();
            res = stmtQuery.executeQuery(sqlQuery);
            while(res.next()){
                String mail = res.getString("user_name");
                allAdmins.add(mail);
            }
        }catch(SQLException e){
            Opprydder.skrivMelding(e, "Something went wrong");
        }finally{
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(stmtQuery);
            dbPool.frigiForbindelse(con.getNr());
        }
        return allAdmins;
    }

    public boolean deleteAdmin(String username){
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {
                con = dbPool.reserverForbindelse();
                String sqlQuery = "SELECT * FROM admin_user WHERE user_name = ?";
                query = con.getForbindelse().prepareStatement(sqlQuery);
                query.setString(1, username);
                res = query.executeQuery();
                if (res.isBeforeFirst()) {
                    String sqlUp = "DELETE FROM admin_user WHERE user_name = ?";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setString(1, username);
                    query.executeUpdate();
                    r = true;
                }else {
                    System.out.println("Something went wrong, admin user might not exist");
                    r = false;
                }
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

    public boolean checkDate(String dateToCheck, String dateFormat){
        if(dateToCheck == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {

            //ParseException is thrown if format is wrong
            Date date = sdf.parse(dateToCheck);
            System.out.println(date);

        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public double getDeposit(){ //returns the current deposit value
        double deposit=0;
        Statement stmt = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            String sqlQuery = "SELECT deposit_value FROM properties";
            con = dbPool.reserverForbindelse();
            stmt = con.getForbindelse().createStatement();
            res = stmt.executeQuery(sqlQuery);
            if(res.isBeforeFirst()){
                res.next();
                deposit=res.getDouble("deposit_value");
            }
            res.close();
            return deposit;
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            deposit=-1;
            return deposit;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(stmt);
            dbPool.frigiForbindelse(con.getNr());
        }
        return deposit;
    }

    public boolean changeDeposit(double deposit){ //making function to return, a boolean. Does not need to use the return value if not needed.
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//In this software the adminJframe will handel wherever type_name exists, but I will check anyway so it can be expanded to be used in different places, plus if something goes wrong in the jframe handling this class wont not commit to database and return true
                con = dbPool.reserverForbindelse();
                String sqlUp = "UPDATE properties SET deposit_value = ? WHERE properties_set = 1";
                query = con.getForbindelse().prepareStatement(sqlUp);
                query.setDouble(1, deposit);
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

    public int getMapUpdateTime(){
        int time = 0;
        Statement stmt = null;
        ResultSet res = null;
        Forbindelse con = null;
        try{
            String sqlQuery = "SELECT refresh_map_every_seconds FROM properties";
            con = dbPool.reserverForbindelse();
            stmt = con.getForbindelse().createStatement();
            res = stmt.executeQuery(sqlQuery);
            if(res.isBeforeFirst()){
                res.next();
                time=res.getInt("refresh_map_every_seconds");
            }
            res.close();
            return time;
        }
        catch (SQLException e) {
            Opprydder.skrivMelding(e, "Something went wrong");
            time=-1;
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Opprydder.lukkResSet(res);
            Opprydder.lukkSetning(stmt);
            dbPool.frigiForbindelse(con.getNr());
        }
        return time;
    }

    public boolean changeMapUpdateTime(int seconds){//making function to return, a boolean. Does not need to use the return value if not needed.
        //minutes is how often map update. Example minutes = 5 is map updating every 5 minutes
        boolean r = false;
        boolean ok = false;
        int tries = 0;
        do {
            PreparedStatement query = null;
            ResultSet res = null;
            Forbindelse con = null;
            try {//In this software the adminJframe will handel wherever type_name exists, but I will check anyway so it can be expanded to be used in different places, plus if something goes wrong in the jframe handling this class wont not commit to database and return true
                con = dbPool.reserverForbindelse();
                String sqlUp = "UPDATE properties SET refresh_map_every_seconds = ? WHERE properties_set = 1";
                query = con.getForbindelse().prepareStatement(sqlUp);
                query.setInt(1, seconds);
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

    public boolean endTrip(int bikeId, String endDate, int stationId){ 
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
                    String sqlUp = "UPDATE bicycle_rent SET end_time = ? WHERE bicycle_id = ? AND end_time IS NULL";
                    query = con.getForbindelse().prepareStatement(sqlUp);
                    query.setTimestamp(1, java.sql.Timestamp.valueOf(endDate));
                    query.setInt(2, bikeId);
                    query.executeUpdate();
                    sqlQuery = "SELECT ((TIME_TO_SEC(TIMEDIFF(br.end_time, br.start_time))/3600)*t.type_rentprice) AS price\n" +
                            "FROM bicycle_rent br\n" +
                            "LEFT JOIN bicycle b\n" +
                            "ON br.bicycle_id = b.bicycle_id\n" +
                            "LEFT JOIN type t\n" +
                            "ON b.type_name = t.type_name\n" +
                            "WHERE b.bicycle_id = ? ORDER BY ABS(TIMEDIFF(NOW(), br.end_time)) LIMIT 1";
                    query = con.getForbindelse().prepareStatement(sqlQuery);
                    query.setInt(1, bikeId);
                    res = query.executeQuery();
                    res.next();

                    double price = res.getDouble("price");
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

    public ArrayList<String> getAllBikesOnTrip(){
        ArrayList<String> list = new ArrayList<String>();
        Statement query = null;
        ResultSet res = null;
        Forbindelse con = null;
        try {
            con = dbPool.reserverForbindelse();
            list.clear();
            query = con.getForbindelse().createStatement();
            String sqlQuery = "SELECT bicycle_id FROM bicycle_rent WHERE end_time IS NULL ORDER BY bicycle_id";
            res = query.executeQuery(sqlQuery);
            if (res.isBeforeFirst()) {
                while (res.next()) {
                    list.add(""+res.getInt("bicycle_id"));
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
            dbPool.frigiForbindelse(con.getNr());
        }
        return list;
    }
}
