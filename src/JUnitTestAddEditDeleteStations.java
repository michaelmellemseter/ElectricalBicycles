import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class JUnitTestAddEditDeleteStations{
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
        ArrayList<String> stations = instance.getAllDockingStations();
        for(int i = 0; i < stations.size(); i++){
            if(instance.getDockingStationAdress(Integer.parseInt(stations.get(i))).equals("A new adress")){
                int dsId = Integer.parseInt(stations.get(i));
                instance.deleteDockingStation(dsId);
            }
        }
        //This depends on the original values for dockingstation 1
        if(instance.showDsBikeCap(1) == 30){
            instance.editDockingStation(1, 20);
        }
        instance.disconnect();
        instance = null;
    }

    //This test depends on how many dockingstations that is registered in the database
    @Test
    public void testAddStation(){
        int station = instance.addStation("A new adress", 20, 5.6, 8.9 ,1);
        assertEquals(4, station);
    }

    //Testing if the dockingstation actually has been registered
    //This test depends on how many dockingstations that is registered in the database
    @Test
    public void testAddStation2(){
        int station = instance.addStation("A new adress", 20,5.6, 8.9, 1);
        ArrayList<String> stations = instance.getAllDockingStations();
        assertEquals("4", stations.get(3));
    }

    @Test
    public void testEditDockingstaion(){
        boolean answer = instance.editDockingStation(1,30);
        assertEquals(true, answer);
    }

    //Testing if the the dockingstation has been edited in the database
    @Test
    public void testEditDockingstation2(){
        boolean answer = instance.editDockingStation(1,30);
        int chargingUnits = instance.showDsBikeCap(1);
        assertEquals(30, chargingUnits);
    }

    //Testing a dockingstation that does not exist
    @Test
    public void testEditDockingstation(){
        boolean answer = instance.editDockingStation(90,30);
        assertEquals(false, answer);
    }

    @Test
    public void testDeleteDockingstation(){
        int station = instance.addStation("A new adress", 20,5.6, 8.9, 1);
        boolean answer = instance.deleteDockingStation(2);
        assertEquals(true, answer);
    }

    //Testing a dockingstation that does not exist
    @Test
    public void testDeleteDockingstation2(){
        boolean answer = instance.deleteDockingStation(90);
        assertEquals(false, answer);
    }
}