import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

//This test is expecting that dockingstation 1 is registered in dockingstations in the database and has specific values
//And that there is registered at least 4 dockingstations
public class JUnitTestGetAllDockingstations{
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

    //Testing if all the stations is in the ArrayList
    //This test depends on how many dockingstations that is registered in the database.
    @Test
    public void testGetAllDockingstations(){
        ArrayList<String> allDockingstations = instance.getAllDockingStations();
        assertEquals("1", allDockingstations.get(0));
        assertEquals("2", allDockingstations.get(1));
        assertEquals("4", allDockingstations.get(3));
    }

    //This test depends on what is registered in the database.
    @Test
    public void testShowDsBikeCap(){
        int chargingUnits = instance.showDsBikeCap(1);
        assertEquals(20, chargingUnits);
    }

    //Testing a dockingstation that does not exist
    @Test
    public void testshowDsBikeCap2(){
        int chargingUnits = instance.showDsBikeCap(190);
        assertEquals(-1, chargingUnits);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testShowDsPowerCap(){
        double kwh = instance.showDsPowerCap(1);
        assertEquals(2.58, kwh, 0);
    }

    //Testing a dockingstation that does not exist
    @Test
    public void testShowDsPowerCap2(){
        double kwh = instance.showDsPowerCap(190);
        assertEquals(-1, kwh, 0);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetDockingStationAdress(){
        String adress = instance.getDockingStationAdress(1);
        assertEquals("Prinsens Gate", adress);
    }

    //Testing a dockingstation that does not exist
    @Test
    public void testGetDockingStationAdress2(){
        String adress = instance.getDockingStationAdress(190);
        assertEquals("-1", adress);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetStationPosition(){
        double[] position = instance.getStationPosition(1);
        assertEquals(63.4291285, position[0], 0);
        assertEquals(10.392392, position[1], 0);
    }

    //Testing a dockingstation that does not exist
    @Test
    public void testGetStationPosition2(){
        double[] position = instance.getStationPosition(190);
        assertEquals(-1, position[0], 0);
        assertEquals(-1, position[1], 0);
    }
}