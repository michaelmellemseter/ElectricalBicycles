import org.junit.*;
import static org.junit.Assert.*;

public class JUnitTestGetDepositAdmin {
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
        //Reroll the deposit value back to original, so this depends on what the original value is
        if(instance.getDeposit() == 2){
            instance.changeDeposit(5000);
        }
        //Reroll the update map time value back to original, so this depends on what the original value is
        if(instance.getMapUpdateTime() == 9000){
            instance.changeMapUpdateTime(5);
        }
        instance.disconnect();
        instance = null;
    }

    //This test depends on what deposit value is registered in the database
    @Test
    public void testGetDeposit(){
        double deposit = instance.getDeposit();
        assertEquals(5000, deposit, 0);
    }

    @Test
    public void testChangeDeposit(){
        boolean answer = instance.changeDeposit(2);
        assertEquals(true, answer);
    }

    //Testing if the new deposit is beeing registered
    @Test
    public void testChangeDeposit2(){
        instance.changeDeposit(2);
        double deposit = instance.getDeposit();
        assertEquals(2, deposit, 0);
    }

    //This test depends on what refresh rate value that is registered in the database
    @Test
    public void testGetMapUpdateTime(){
        int updateTime = instance.getMapUpdateTime();
        assertEquals(5, updateTime);
    }

    @Test
    public void testChangeMapUpdateTime(){
        boolean answer = instance.changeMapUpdateTime(9000);
        assertEquals(true, answer);
    }

    //Testing if the new update time is beeing registered
    @Test
    public void testChangeMapUpdateTime2(){
        instance.changeMapUpdateTime(9000);
        int updateTime = instance.getMapUpdateTime();
        assertEquals(9000, updateTime);
    }
}