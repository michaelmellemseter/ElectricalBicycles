import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class JUnitTestGetAllRepairIds {
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

    //This test depends on how many repairs there is registered in the database
    @Test
    public void testGetAllRepairIds(){
        ArrayList<Integer> repairIds = instance.getAllRepairIds();
        int number = repairIds.get(0);
        int number2 = repairIds.get(1);
        int number3 = repairIds.get(6);
        assertEquals(1, number);
        assertEquals(2, number2);
        assertEquals(7, number3);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetSentDate()throws java.text.ParseException{
        Date date = instance.getSentDate(1);
        Date expected = new SimpleDateFormat("yyyy-MM-dd").parse("2018-04-04");
        assertEquals(expected, date);
    }

    //Testing an id that does not exist
    @Test
    public void testGetSentDate2(){
        Date date = instance.getSentDate(7000);
        assertEquals(null, date);
    }

    //This test depends on what is registered in the database
    @Test
    public void testGetRepairCost(){
        double cost = instance.getRepairCost(2018);
        assertEquals(1500 ,cost, 0);
    }
}
