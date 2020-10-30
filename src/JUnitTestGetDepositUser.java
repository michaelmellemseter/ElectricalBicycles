import org.junit.*;
import static org.junit.Assert.*;

public class JUnitTestGetDepositUser {
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

    //This test depends on what deposit value is registered in the database
    @Test
    public void testGetDeposit(){
        double deposit = instance.getDeposit();
        assertEquals(5000, deposit, 0);
    }

    //This test depends on what refresh rate value that is registered in the database
    @Test
    public void testGetRefreshMapRate(){
        double refreshRate = instance.getRefreshMapRate();
        assertEquals(5, refreshRate, 0);
    }
}