import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class JUnitTestGetAllTypes{
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

    //Testing if all the types is in the ArrayList
    //This test depends on on which types that is regitered in the database.
    @Test
    public void testGetAllTypes(){
        ArrayList<String> allTypes = instance.getAllTypes();
        assertEquals("Mid drive", allTypes.get(0));
        assertEquals("Off-road", allTypes.get(1));
        assertEquals("Tandem cycle", allTypes.get(2));
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetTypeDesc(){
        String typeDesc = instance.getTypeDesc("Mid drive");
        assertEquals("Electrical components are on the frame. Gives advantages like can use whatever gear is on the bike, good weight distribution and it is easy to repair", typeDesc);
    }

    //Testing a type that does not exist
    @Test
    public void testGetTypeDesc2(){
        String typeDesc = instance.getTypeDesc("A type that does not exist");
        assertEquals("-1", typeDesc);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetPrice(){
        double price = instance.getPrice("Mid drive");
        assertEquals(50, price, 0);
    }

    //Testing a type that does not exist
    @Test
    public void testGetPrice2(){
        double price = instance.getPrice("A type that does not exist");
        assertEquals(-1, price, 0);
    }

    //This test depends on what is registered in the database.
    @Test
    public void testGetTypeAmount(){
        int amount = instance.getTypeAmount("Mid drive");
        assertEquals(2, amount);
    }

    //Testing a type that does not exist
    @Test
    public void testGetTypeAmount2(){
        int amount = instance.getTypeAmount("A type that does not exist");
        assertEquals(-1, amount);
    }
}