import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

//This test is expecting that bicycle 1 is registered in bicycles in the database and has specific values
//And that there is registered at least 11 bicycles
public class JUnitTestGetAllBikes{
    Admin_database instance;

    @BeforeClass
    public static void setUpClass() throws Exception{
    }

    @AfterClass
    public static void tearDownClass() throws Exception{
    }

    @Before
    public void setUp() throws Exception{
        instance = new Admin_database(2,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ");
    }

    @After
    public void tearDown(){
        instance.disconnect();
        instance = null;
    }

    //Testing if all the bikes is in the ArrayList
    //This test depends on how many bicycles that is registered in the database
    @Test
    public void testGetAllBikes(){
        ArrayList<String> allBikes = instance.getAllBikes();
        assertEquals("1", allBikes.get(0));
        assertEquals("2", allBikes.get(1));
        assertEquals("3", allBikes.get(2));
        assertEquals("11", allBikes.get(10));
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikePrice(){
        double bikePrice = instance.getBikePrice(1);
        assertEquals(29700 ,bikePrice, 0);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikePrice2(){
        double bikePrice = instance.getBikePrice(7000);
        assertEquals(-1 ,bikePrice, 0);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikeRegdate(){
        String regdate = instance.getBikeRegdate(1);
        assertEquals("2018-03-15", regdate);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikeRegdate2(){
        String regdate = instance.getBikeRegdate(7000);
        assertEquals("-1", regdate);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikeKm(){
        double km = instance.getBikeKm(1);
        assertEquals(0, km, 0);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikeKm2(){
        double km = instance.getBikeKm(7000);
        assertEquals(-1, km, 0);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikeTrips(){
        int trips = instance.getBikeTrips(1);
        assertEquals(14, trips);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikeTrips2(){
        int trips = instance.getBikeTrips(7000);
        assertEquals(-1, trips);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikeType(){
        String type = instance.getBikeType(1);
        assertEquals("Mid drive", type);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikeType2(){
        String type = instance.getBikeType(7000);
        assertEquals("-1", type);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikeMake(){
        String make = instance.getBikeMake(1);
        assertEquals("Skepphult", make);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikeMake2(){
        String make = instance.getBikeMake(7000);
        assertEquals("-1", make);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetBikeDockingstation(){
        int dsId = instance.getBikeDockingStation(1);
        assertEquals(1, dsId);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikeDockingstation2(){
        int dsId = instance.getBikeDockingStation(7000);
        assertEquals(-1, dsId);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetBikeRepairAmount(){
        int amount = instance.getBikeRepairAmount(1);
        assertEquals(0, amount);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetBattryLvl(){
        double batteryLvl = instance.getBatteryLvl(1);
        assertEquals(100, batteryLvl, 0);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBattryLvl2(){
        double batteryLvl = instance.getBatteryLvl(7000);
        assertEquals(-1, batteryLvl, 0);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetBikePosition(){
        double[] position = instance.getBikePosition(1);
        assertEquals(63.4319864, position[0], 0);
        assertEquals(10.3949004, position[1], 0);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetBikePosition2(){
        double[] position = instance.getBikePosition(190);
        assertEquals(-1, position[0], 0);
        assertEquals(-1, position[1], 0);
    }
}