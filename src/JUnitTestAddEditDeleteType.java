import org.junit.*;
import static org.junit.Assert.*;

public class JUnitTestAddEditDeleteType {
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
        //first tries to add if already exists then edit the existing version back to the original.
        boolean r = false;
        String re = instance.addType("Geared hub motors", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheel", 80);
        if(re.equalsIgnoreCase("Geared hub motors")) {
            r = instance.editType("Geared hub motors", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheel", 80);
        }
        instance.disconnect();
        instance = null;
    }

    @Test
    public void testRegisterType(){
        System.out.println("testRegistrerType");
        String answer = instance.addType("Mid drive", "test. Should not go trough", 100);
        String expResualt = "-1";
        assertEquals(expResualt, answer);
    }

    @Test
    public void testRegisterType2(){
        System.out.println("testRegistrerType2");
        boolean test = instance.deleteType("Geared hub motors"); //deleting the type if it exists
        String answer = instance.addType("Geared hub motors", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheel", 80);
        String expResualt = "Geared hub motors";
        assertEquals(expResualt, answer);
    }

    @Test
    public void testEditType(){
        System.out.println("testEditType");
        //add type if it does not exist
        String test = instance.addType("Geared hub motors", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheel", 80);
        boolean answer = instance.editType("Geared hub motors", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheels", 90.4);
        boolean expResualt = true;
        assertEquals(expResualt, answer);
    }

    @Test
    public void testEditType2(){
        System.out.println("testEditType2");
        boolean answer = instance.editType("huba", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheels", 90.4);
        boolean expResualt = false;
        assertEquals(expResualt, answer);
    }

    //missing testing of rollback

    @Test
    public void testDeleteType(){
        System.out.println("testDeleteType");
        //add type if it does not exist
        String test = instance.addType("Geared hub motors", "Small and ligth electrical bicyle with great torque, but have a lesser top speed. With free-wheel", 80);
        boolean answer = instance.deleteType("Geared hub motors");
        boolean expResualt = true;
        assertEquals(expResualt, answer);
    }

    @Test
    public void testDeleteType2(){
        System.out.println("testDeleteType2");
        boolean answer = instance.deleteType("Dimsam bike");
        boolean expResualt = false;
        assertEquals(expResualt, answer);
    }

    public static void main (String[] args){//also try to limit tests, reset in end of junit and check database when testing. NOTE: dont push limited test version to gitlab
        org.junit.runner.JUnitCore.main(JUnitTestAddEditDeleteType.class.getName());
    }

}