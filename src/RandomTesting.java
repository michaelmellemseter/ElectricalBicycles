import java.util.*;
public class RandomTesting {
    public static void main(String[] args) throws  Exception{
        Admin_database instance = new Admin_database(2,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ");
        User_database instance2 = new User_database(2,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ", 1);
        ArrayList<String> list = instance.getAllBikes();
        int i = 0;
        while(i<list.size()){
            System.out.println(list.get(i));
            System.out.println("Price: " + instance.getBikePrice(Integer.parseInt(list.get(i))));
            System.out.println("Regdate: " + instance.getBikeRegdate(Integer.parseInt(list.get(i))));
            System.out.println("Km: " + instance.getBikeKm(Integer.parseInt(list.get(i))));
            System.out.println("Trips: " + instance.getBikeTrips(Integer.parseInt(list.get(i))));
            double[] test = instance.getBikePosition(i);
            System.out.println("lat: " + test[0]);
            System.out.println("lon: " + test[1]);
            System.out.println("");
            i++;
        }

        double[] test = instance.getStationPosition(1);
        System.out.println("ds: 1");
        System.out.println("lat: " + test[0]);
        System.out.println("lon: " + test[1]);

       /* instance.addType("SuperTest", "just a test", 70);
        instance.addBike(300, "Scott","SuperTest",5);*/
       /* boolean tri = instance2.endTrip(1, "2018-04-14 17:35:10");
        tri = instance2.endTrip(10, "2018-04-14 18:55:10");
        tri = instance2.endTrip(8, "2018-04-14 18:56:10");*/
        instance.disconnect();
        instance2.disconnect();
    }
}
