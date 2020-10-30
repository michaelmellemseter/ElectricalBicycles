import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class JUnitTestGetAllAvailableTypesBicycles {
    User_database instance;

    @BeforeClass
    public static void setUpClass() throws Exception{
    }

    @AfterClass
    public static void tearDownClass() throws Exception{
    }

    @Before
    public void setUp() throws Exception{
        instance = new User_database(2,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ", 1);
    }

    @After
    public void tearDown(){
        instance.disconnect();
        instance = null;
    }

    //Testing all the bikes that is at dockingstation 1
    //This test depends on which bicycles there is at dockingstation 1 in the database.
    @Test
    public void testGetAllAvailableTypesBicycles(){
        ArrayList<String> allBicycles = instance.getAllAvailableTypesBicycles();
        assertEquals("Mid drive", allBicycles.get(0));
        assertEquals("Off-road", allBicycles.get(1));
        assertEquals("Tandem cycle", allBicycles.get(2));
    }

    //Testing the mid drives at dockingstation 1
    //This test depends on how many bicycles there is of Mid drive at dockingstation 1 in the database.
    @Test
    public void testGetAmountBicyclesAvailable(){
        ArrayList<String> amount = instance.getAmountBicyclesAvailable("Mid drive");
        assertEquals("1", amount.get(0));
        assertEquals("2", amount.get(1));
    }
}
