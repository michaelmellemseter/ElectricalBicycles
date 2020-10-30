import org.junit.*;
import static org.junit.Assert.*;

public class JUnitTestCheckDate {
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

    @Test
    public void testCheckDate(){
        boolean answer = instance.checkDate("12-11-98", "dd-MM-yyyy");
        assertEquals(true, answer);
    }

    @Test
    public void testCheckDate2(){
        boolean answer = instance.checkDate("12-11-98", "yyyy-MM-dd");
        assertEquals(false, answer);
    }
}
