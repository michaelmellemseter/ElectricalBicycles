import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

//This test is expecting that Merida is registered in producer in the database and has a specific value
public class JUnitTestGetAllMakes{
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

    //Testing if all the producers is in the ArrayList
    //This test depends on which producers that is regitered in the database.
    @Test
    public void testGetAllMakes(){
        ArrayList<String> producer = instance.getAllMakes();
        assertEquals("Merida", producer.get(0));
        assertEquals("Nakamura", producer.get(1));
        assertEquals("Scott", producer.get(2));
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetMakeDate(){
        String date = instance.getMakeDate("Merida");
        assertEquals("1972-03-04", date);
    }

    //Testing a make that does not exist
    @Test
    public void testGetMakeDate2(){
        String date = instance.getMakeDate("A make that does not exist");
        assertEquals("-1", date);
    }
}