import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class JUnitTestGetAddDeleteRentMachine {
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

    //This test depends on what is registered in the database
    @Test
    public void testGetRentmachines(){
        ArrayList<String> rentmachines = instance.getRentmachines(1);
        assertEquals("1", rentmachines.get(0));
    }

    //Testing a rentmachine that does not exist
    @Test
    public void testGetRentmachines2(){
        ArrayList<String> rentmachines = instance.getRentmachines(190);
        assertEquals("-1", rentmachines.get(0));
    }

    //This test depends on how many rentmachines there is registered in the database
    @Test
    public void testAddRentmachines(){
        ArrayList<String> rentmachines = instance.addRentmachines(1, 3);
        assertEquals("7", rentmachines.get(0));
        assertEquals("9", rentmachines.get(2));
        instance.deleteRentmachine(7);
        instance.deleteRentmachine(8);
        instance.deleteRentmachine(9);
    }

    @Test
    public void testDeleteRentmachine(){
        ArrayList<String> rentmachines = instance.addRentmachines(1, 1);
        boolean answer = instance.deleteRentmachine(7);
        assertEquals(true, answer);
    }

    //Testing a rentmachine that does not exist
    @Test
    public void testDeleteRentmachine2(){
        boolean answer = instance.deleteRentmachine(190);
        assertEquals(false, answer);
    }
}
