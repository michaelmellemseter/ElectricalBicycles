import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class JUnitTestAddToCartAndRent {
    User_database instance;

    @BeforeClass
    public static void setUpClass() throws Exception{
    }

    @AfterClass
    public static void tearDownClass() throws Exception{
    }

    @Before
    public void setUp() throws Exception{
        instance = new User_database(2,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ",1);
    }

    @After
    public void tearDown(){
        instance.disconnect();
        instance = null;
    }

    //This test debends on how many Mid drive that is registered at a dockingstation and how many of them that are active(that will say those with NULL at end_time)
    //Since this returns a random number we tests if it is inside a specific interval
    @Test
    public void testGetBicycleId(){
        int bicycleId = instance.getBicycleId("Mid drive");
        boolean answer = false;
        if(bicycleId <= 21 && bicycleId >= 1){
            answer = true;
        }
        assertEquals(true, answer);
    }

    //TEsting a type that does not exist
    @Test
    public void testGetBicycleId2(){
        int bicycleId = instance.getBicycleId("A type that does not exist");
        assertEquals(-1, bicycleId);
    }

    //This test depends on how many bicycle rent ids that is registered
    @Test
    public void testGetBicycleRentId(){
        int bicycleRentId = instance.getBicycleRentId();
        assertEquals(3, bicycleRentId);
    }

    //This test depends on how many rent ids that is registered
    @Test
    public void testGetRentId(){
        int rentId = instance.getRentId();
        assertEquals(15, rentId);
    }

    //This test depends on how many rent ids that is registered
    @Test
    public void testUpdateRentId(){
        int rentId = instance.updateRentId();
        assertEquals(15, rentId);
        ArrayList<Integer> list = new ArrayList<>();
        instance.rerollRent(rentId, list);
    }

    //This test expect it to be possible to rent a Mid drive that had bicycle id 1 at this dockingstation
    @Test
    public void testAddToCart(){
        int rentId = instance.updateRentId();
        int bicycleId = instance.addToCart("Mid drive", rentId);
        assertEquals(1, bicycleId);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(bicycleId);
        instance.rerollRent(rentId, list);
    }

    //Testing a type that does not exist
    @Test
    public void testAddToCart2(){
        int bicycleId = instance.addToCart("A bicycle that does not exist", 1);
        assertEquals(-1, bicycleId);
    }

    //This test depends on that the bicycles with ids 1, 2 and 3 is two Mid drives (costs 50) and one Off-road (costs 150)
    @Test
    public void testGetPrice(){
        ArrayList<Integer> bicycleIds = new ArrayList<>();
        bicycleIds.add(1);
        bicycleIds.add(2);
        bicycleIds.add(3);
        double price = instance.getPrice(bicycleIds);
        assertEquals(250, price, 0);
    }

    //Testing with a bicycle that does not exist
    @Test
    public void testGetPrice2(){
        ArrayList<Integer> bicycleIds = new ArrayList<>();
        bicycleIds.add(1);
        bicycleIds.add(2);
        bicycleIds.add(7000);
        double price = instance.getPrice(bicycleIds);
        assertEquals(-1, price, 0);
    }

    @Test
    public void testRent(){
        int rentId = instance.updateRentId();
        boolean answer = instance.rent(rentId, "123");
        assertEquals(true, answer);
        ArrayList<Integer> list = new ArrayList<>();
        instance.rerollRent(rentId, list);
    }

    //Testing a rent id that does not exist
    @Test
    public void testRent2(){
        boolean answer = instance.rent(7000, "123");
        assertEquals(false, answer);
    }

    @Test
    public void testRerollRent(){
        int rentId = instance.updateRentId();
        int bicycleId = instance.addToCart("Mid drive", rentId);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(bicycleId);
        boolean answer = instance.rerollRent(rentId, list);
        assertEquals(true, answer);
    }

    //Testing a rent id that does not exist
    @Test
    public void testRerollRent2(){
        ArrayList<Integer> list = new ArrayList<>();
        boolean answer = instance.rerollRent(-1, list);
        assertEquals(false, answer);
    }
}
