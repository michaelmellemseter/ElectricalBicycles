import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.Random;
class bikeSimulation implements Runnable {

    private SimulationDatabase db;
    int speed;

    public bikeSimulation(SimulationDatabase db, int speed) throws Exception{
        this.db = db;
        this.speed = speed;
    }

    @Override
    public void run() {
        try {
            Random rand = new Random();
            ArrayList<Integer> dock = db.getAllDockingstationsWithBikes();
            int n = rand.nextInt(dock.size());
            int dsId = dock.get(n);
            System.out.println("dsId: " + dsId);
            ArrayList<String> types = db.getAllAvailableTypesBicycles(dsId);
            n = rand.nextInt(types.size());
            String type = types.get(n);
            int amount = db.getAmountBicyclesAvailable(type, dsId);
            System.out.println("amountbikes: "+ amount + "typessize: "+types.size() + ", at dsId: " + dsId);
            if(amount >= 2){// it must be at least on bike available, but if there is more we can try to sometimes rent two
                n = rand.nextInt(2)+1;
            }else{
                n = 1;
            }
            int rentId = db.updateRentId();
            ArrayList<Integer> bikeIds = new ArrayList<Integer>();
            for(int i = 0; i < n; i++){
                bikeIds.add(db.addToCart(type, rentId, dsId));
            }
            n = rand.nextInt(35)+500;// just a random card token
            db.rent(rentId, ""+n, dsId);
            double[] stationP = db.getStationPosition(dsId);
            for(int i = 0; i < bikeIds.size(); i++) {
                db.positionUpdater(bikeIds.get(i), stationP[0], stationP[1]);//The real in life verstion would have been already updated, but here I make sure the position is correct
                System.out.println("rented bike: " + bikeIds.get(i) + "position : " + stationP[0] + ", " + stationP[1]);
                System.out.println("bikeId: " + ": " + bikeIds.get(i));
            }
            Thread.sleep((int)((60/this.speed) * 1000));
            int runs = rand.nextInt(20) + 5;
            int x = 0;

            while(x < runs) {
                for (int i = 0; i < bikeIds.size(); i++) {
                    double lat = rand.nextInt(8) / 1000d;// want to keep 0 as a possiblity for this one
                    double lon = rand.nextInt(8) / 1000d;
                    double[] bikePos = db.getBikePosition(bikeIds.get(i));
                    n = rand.nextInt(4);
                    System.out.println("n: " + n +"\n" + "Posadded: lat " + lat + "lon " + lon);
                    if(n == 0) {//just to make it able to go all directions
                        db.positionUpdater(bikeIds.get(i), (bikePos[0] + lat), bikePos[1] + lon);
                    }else if(n == 1){
                        db.positionUpdater(bikeIds.get(i), bikePos[0] - lat, bikePos[1] - lon);
                    }else if(n == 2){
                        db.positionUpdater(bikeIds.get(i), bikePos[0] + lat, bikePos[1] - lon);
                    }else if(n == 3){
                        db.positionUpdater(bikeIds.get(i), bikePos[0] - lat, bikePos[1] + lon);
                    }
                    double km = ((lat + lon) * 1000) / 10d;//cirka value for what is kinda likley for ekstra km
                    db.updateKm(bikeIds.get(i), km);
                    double bat = -1d * (rand.nextInt(3) + (rand.nextInt(10) / 10d));
                    db.updateBatteryLvl(bikeIds.get(i), bat);
                }
                Thread.sleep((int) ((60 / this.speed) * 1000));
                x++;
            }
            /*63.4264427, 10.38972
                    63.4289248, 10.3957139*/
            for (int i = 0; i < bikeIds.size(); i++) {
                ArrayList<Integer> ds = db.getAllDockingstations();
                n = rand.nextInt(ds.size());
                int setToDs = ds.get(n);
                double[] setToDsPos = db.getStationPosition(setToDs);
                double[] newBikePos = db.getBikePosition(bikeIds.get(i));
                double latDif = setToDsPos[0] - newBikePos[0];
                double lonDif = setToDsPos[1] - newBikePos[1];
                n = rand.nextInt(6) + 4;
                int h = 0;
                while(h < n){
                    newBikePos = db.getBikePosition(bikeIds.get(i));
                    db.positionUpdater(bikeIds.get(i), newBikePos[0] + (latDif/n), newBikePos[1] + (lonDif/n));
                    h++;
                    double km = ((Math.abs(latDif + lonDif)/n) * 1000) / 10d;//cirka value for what is kinda likley for ekstra km
                    db.updateKm(bikeIds.get(i), km);
                    double bat = -1d * (rand.nextInt(3) + (rand.nextInt(10) / 10d));
                    db.updateBatteryLvl(bikeIds.get(i), bat);
                    Thread.sleep((int) ((60 / this.speed) * 1000));
                }
                db.endTrip(bikeIds.get(i), setToDs);
            }
        }catch(InterruptedException e){
            e.getStackTrace();
        }
    }
}

class stationSimulation implements Runnable {

    private SimulationDatabase db;
    int speed;
    int dsId;

    public stationSimulation(int dsId, SimulationDatabase db, int speed) throws Exception{
        this.db = db;
        this.speed = speed;
        this.dsId = dsId;
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < 15; i++) {
                db.updateDockingstationAndBikes(dsId);
                Thread.sleep((60 / this.speed) * 1000);
            }

        }catch(InterruptedException e){
            e.getStackTrace();
        }
    }
}

public class Simulation {
    public static void main(String[] args) throws Exception {
        int speed = 1;
        SimulationDatabase db = new SimulationDatabase(4,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ");
        ExecutorService executor = Executors.newCachedThreadPool();
        ArrayList<Integer> allDockingstations = db.getAllDockingstations();
        for(int x = 0; x < allDockingstations.size(); x++){
            Thread.sleep(4*1000);
            executor.execute(new stationSimulation(allDockingstations.get(x), db, speed));
        }
        for(int i = 0; i < 15; i++){
            Random rand = new Random();
            int n = rand.nextInt(10) + 1;
            int s = n*1000;
            executor.execute(new bikeSimulation(db, speed));
            Thread.sleep(s);
        }
        Thread.sleep((int)(38*(60/speed) * 1000));// Makes sure all bike simulations are done.
        db.disconnect();
    }
}
