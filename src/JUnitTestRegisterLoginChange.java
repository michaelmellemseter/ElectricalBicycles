import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import static junit.framework.Assert.assertEquals;
import org.junit.*;

public class JUnitTestRegisterLoginChange{
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
        instance = null;
    }

    @Test
    public void testRegisterTrue(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        System.out.println("testRegisterTrue");
        boolean test = instance.register(username, password);
        assertEquals(true, test);
    }

    @Test
    public void testRegisterFalse(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        System.out.println("testRegisterFalse");
        boolean test = instance.register(username, password);
        assertEquals(false, test);
    }

    @Test
    public void testLoginTrue(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        System.out.println("testLoginTrue");
        boolean test = instance.login(username, password);
        assertEquals(true, test);
    }

    @Test
    public void testLoginFalse(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        System.out.println("testLoginFalse");
        boolean test = instance.login(username, password);
        assertEquals(false, test);
    }

    @Test
    public void testChangeTrue(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        System.out.println("testChangeTrue");
        boolean test = instance.change(username, password);
        assertEquals(true, test);
    }

    @Test
    public void testChangeFalse(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        System.out.println("testChangeFalse");
        boolean test = instance.change(username, password);
        assertEquals(false, test);
    }

    public static void main (String[] args){
        org.junit.runner.JUnitCore.main(JUnitTestRegisterLoginChange.class.getName());
    }

}