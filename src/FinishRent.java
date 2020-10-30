import javax.swing.*;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/*READ TO UNDERSTAND SOFTWARE
When a bicycle is docked a method should run that would set it to that docking station and end the trip for that with all the methods to insert payment and such.
ecause we dont have the acctual docking stations running we made this software to simulate that  you dock a bike manualy.
It uses docking station id and an input for when you delivered the bicycle.
Normaly it would just take the time and date that is right now, but when using this to test you might have just checked out bikes,
so then we think it is better to just put in a time you choose.
 */

public class FinishRent {
    public static void main(String[] args) throws  Exception {
        int rentMachineId = 1;
        Admin_database db = new Admin_database(2, "com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ");
        boolean finished = false;
        while(!finished) {
            try {
                int stationId = Integer.parseInt(showInputDialog("Enter the ID for docking station"));
                int bikeId = Integer.parseInt(showInputDialog("Enter the ID for the bike you want to end your trip with. (have been returned when your rented)"));
                String date = showInputDialog("Enter the the date and for when you docked the bike in a format like: yyyy-MM-dd hh:mm:ss \n Example: 2018-04-14 18:56:10");
                boolean t = db.endTrip(bikeId, date, stationId);
                if (showConfirmDialog(null, "Do you want to end another trip?", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                }else {
                    finished = true;
                }
            }catch(Exception e){
                e.printStackTrace();
                showMessageDialog(null, "Something went wrong, reboot the software!");
            }
        }
        db.disconnect();
    }
}
