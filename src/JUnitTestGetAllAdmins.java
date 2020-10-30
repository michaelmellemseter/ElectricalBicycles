import org.junit.*;
import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;

public class JUnitTestGetAllAdmins {
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
    public void testGetAllAdmins(){
        ArrayList<String> admins = instance.getAllAdmins();
        assertEquals("magomed.k98@gmail.com", admins.get(0));
    }

    @Test
    public void testDelteAdmin() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        instance.register("mikl", "cow");
        boolean answer = instance.deleteAdmin("mikl");
        assertEquals(true, answer);
    }

    @Test
    public void testDelteAdmin2() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        boolean answer = instance.deleteAdmin("mikl");
        assertEquals(false, answer);
    }
}
