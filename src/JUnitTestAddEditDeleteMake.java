import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

//This test is expecting that Nakamura is registered in producers in the database
public class JUnitTestAddEditDeleteMake{
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
        ArrayList<String> producer = instance.getAllMakes();
        for(int i = 0; i < producer.size(); i++){
            if(producer.get(i).equals("A new make")){
                instance.deleteType("A new make");
            }
            if(producer.get(i).equals("Aaaaaaa")){
                instance.deleteType("Aaaaaaa");
            }
        }
        if(instance.getMakeDate("Nakamura").equals("1987-11-12")){
            instance.editMakes("1976-09-11", "Nakamura");
        }
        instance.disconnect();
        instance = null;
    }

    @Test
    public void testAddMake(){
        String make = instance.addMake("A new make", "1994-02-09");
        assertEquals("A new make", make);
    }

    //Testing if the make actually is beeing registered
    //It is called Aaaaaa because then we know it ends at the top of the table and first in the ArrayList
    @Test
    public void testAddMake4(){
        String make = instance.addMake("Aaaaaaa", "1994-02-09");
        ArrayList<String> producer = instance.getAllMakes();
        assertEquals("Aaaaaaa", producer.get(0));
    }

    //Testing a already activated producer
    @Test
    public void testAddMake3(){
        String make = instance.addMake("Nakamura", "1994-02-09");
        assertEquals("-1", make);
    }

    @Test
    public void testEditMakes(){
        boolean answer = instance.editMakes("1987-11-12", "Nakamura");
        assertEquals(true, answer);
    }

    //Testing if the the makes has been edited in the database
    @Test
    public void testEditMakes3(){
        boolean answer = instance.editMakes("1987-11-12", "Nakamura");
        String date = instance.getMakeDate("Nakamura");
        assertEquals("1987-11-12", date);
    }

    //Testing a make that does not exist
    @Test
    public void testEditMakes2(){
        boolean answer = instance.editMakes("1987-11-12", "Does not exist");
        assertEquals(false, answer);
    }

    @Test
    public void testDeleteMake(){
        String make = instance.addMake("A new make", "1994-02-09");
        boolean answer = instance.deleteMake("A new make");
        assertEquals(true, answer);
    }

    //Testing a make that does not exist
    @Test
    public void testDeleteMake2(){
        boolean answer = instance.deleteMake("A make that does not exist");
        assertEquals(false, answer);
    }
}
