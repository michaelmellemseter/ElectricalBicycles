import org.junit.*;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

public class JUnitTestAddEditDeleteBike {
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
        instance.disconnect();
        instance = null;
    }

    @Test
    public void testRegisterBikeAndDelte(){
        System.out.println("testRegistrerBikeAndDelte");
        ArrayList<String> answer = instance.addBike(15000, "Skepphult", "Mid drive", 1, 1, 63.555453, 10.242455);
        boolean answer2 = instance.deleteBike(Integer.parseInt(answer.get(0)));
        boolean a = false;
        if(!answer.get(0).equalsIgnoreCase("-1") && answer2){
            a = true;
        }
        boolean expResualt = true;
        assertEquals(expResualt, a);
    }

    @Test
    public void testRegisterBikeAndDelte2(){
        System.out.println("testRegistrerBikeAndDelte");
        ArrayList<String> answer = instance.addBike(15000, "Skepphult", "Mid drive", 3, 1,61.555453, 9.242455);
        boolean answer2 = false;
        for(int i = 0; i < answer.size(); i++){
            boolean answer3 = instance.deleteBike(Integer.parseInt(answer.get(i)));
            if(answer3){
                answer2 = true;
            }else{
                answer2 = false;
            }
        }
        boolean a = false;
        if(!answer.get(0).equalsIgnoreCase("-1") && answer2){
            a = true;
        }
        boolean expResualt = true;
        assertEquals(expResualt, a);
    }

    @Test
    public void testRegisterBike(){//test add non existing forgein key
        System.out.println("testEditBike");
        ArrayList<String> answer = instance.addBike(15000, "Skepult", "Mid drive", 2, 1, 61.535343, 8.242455);
        ArrayList<String> expResualt =  new ArrayList<String>();
        expResualt.add("-1");;
        assertEquals(expResualt, answer);
    }

    @Test
    public void testEditBike(){
        System.out.println("testEditBike");
        boolean answer = instance.editBike(2, 28900, "Skepphult", "2018-03-17", 10, 1, "Mid drive");
        boolean expResualt = true;
        assertEquals(expResualt, answer);
    }

    @Test
    public void testEditBike2(){// test reroll check database
        System.out.println("testEditBike2");
        boolean answer = instance.editBike(1, 22300, "Skepphult", "2018.037", 11, 1, "Mid drive");
        boolean expResualt = false;
        assertEquals(expResualt, answer);
    }

    public static void main (String[] args){//also try to limited tests, reset in end of junit and check database when testing. NOTE: dont push limited test version to gitlab
        org.junit.runner.JUnitCore.main(JUnitTestAddEditDeleteBike.class.getName());
    }

}