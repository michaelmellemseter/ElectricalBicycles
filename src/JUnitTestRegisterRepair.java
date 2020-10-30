import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class JUnitTestRegisterRepair{
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

    //This test depends on what is in the database. It is supposed to return one number higher than the highest repair id
    @Test
    public void testGetRepairId(){
        int repariId = instance.getRepairId();
        assertEquals(3, repariId);
    }

    //Testing a bike that is to repair
    //This test depends on what is in the database.
    @Test
    public void testGetRepairId2(){
        int repariId = instance.getRepairId2(6);
        assertEquals(2, repariId);
    }

    //Testing a bike that does not exist
    @Test
    public void testGetRepairId2vol2(){
        int repariId = instance.getRepairId2(7000);
        assertEquals(-1, repariId);
    }

    //Testing a bike that already is connected to a repair id but have been finished
    //This test depends on what is in the database.
    @Test
    public void testGetRepairId2vol3(){
        int repariId = instance.getRepairId2(3);
        assertEquals(-1, repariId);
    }

    //Warning!!
    //This test register a new repair, but with no bicycle attached since we don't have any rollback method to delete repair
    //This test is depending on how many bicycles that is registered in the database
    @Test
    public void testRequestRepair(){
        instance.addBike(0, "Scott", "Mid drive", 1, 1, 63.455443, 10.242455);
        boolean answer = instance.requestRepair(23, "JUnitTest");
        assertEquals(true, answer);
        instance.deleteBike(23);
    }


    //Testing a bike that does not exist
    @Test
    public void testRequestRepair2(){
        boolean answer = instance.requestRepair(7000, "JUnitTest");
        assertEquals(false, answer);
    }

    //Warning!!
    //This test register a new repair, but with no bicycle attached since we don't have any rollback method to delete repair
    //This test is depending on how many bicycles that is registered in the database
    @Test
    public void testRegisterRepair(){
        instance.addBike(0, "Scott", "Mid drive", 1, 1, 60.100232, 10.546643);
        instance.requestRepair(23, "JUnitTest");
        boolean answer = instance.registerRepair(23, 0,"JUnitTest");
        assertEquals(true, answer);
        instance.deleteBike(23);
    }

    //Testing a bike that does not exist
    @Test
    public void testRegisterRepair2(){
        boolean answer = instance.registerRepair(7000, 0,"JUnitTest");
        assertEquals(false, answer);
    }

    //Testing a bike that is not requested for repair
    //This test expect bicycle 1 to not be requested for repair
    @Test
    public void testRegisterRepair3(){
        boolean answer = instance.registerRepair(1, 0,"JUnitTest");
        assertEquals(false, answer);
    }
}
