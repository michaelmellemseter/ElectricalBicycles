/*
A class made to be used in the Admin_software, to let admins make changes to
the bicycles, makes, types, repairs etc.

@Author Team 09
created on 2018-04-22
 */
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.*;
import com.esri.core.geometry.Envelope;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.map.*;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

import static javax.swing.JOptionPane.*;

public class Admin_JFrame extends JFrame {

    //Creating object of Admin_database for functions
    private Admin_database db;

    //Creating header panel (always the same)
    private JPanel panelTop = new JPanel();

    //Creating central panel ver. login
    private JPanel panelLogin = new JPanel();
    //Creating central panel ver. main menu
    private JPanel panelMenu = new JPanel();
    //Central panel ver. administer bicycle types
    private JPanel panelAdmType = new JPanel();
    //Central panel ver. register new admin
    private JPanel panelRegAdmin = new JPanel();
    //Central panel ver. edit/delete admin
    private JPanel panelEditAdmin = new JPanel();
    //Central panel ver. register bicycle
    private JPanel panelRegBike = new JPanel();
    //Central panel ver. edit/delete bicycle
    private JPanel panelEditDelBike = new JPanel();
    //Central panel ver. register repair
    private JPanel panelRegRep = new JPanel();
    //Central panel ver. show bicycle status
    private JPanel panelBikeStatus = new JPanel();
    //Central panel ver. add/edit/del docking station
    private  JPanel panelAdmDs = new JPanel();
    //Central panel ver. show docking station status
    private JPanel panelShowDockStat = new JPanel();
    //Central panel ver. show map
    private JPanel panelShowMap = new JPanel();
    //Central panel ver. statistical info
    private JPanel panelStats = new JPanel();
    //Central panel ver. stats: power usage
    private JPanel panelPowerUsage = new JPanel();
    //Central JPanel ver. stats: bike usage
    private JPanel panelBikeUsage = new JPanel();
    //Central panel ver. stats: bike purchases
    private JPanel panelBikePurchase = new JPanel();
    //Central panel ver. stats: repair stats
    private JPanel panelRepairsStats = new JPanel();
    //Central panel ver. stats: type stats
    private JPanel panelTypeStats = new JPanel();
    //Central panel ver. deposit value
    private JPanel panelPropVal = new JPanel();
    //Central panel ver. administer makes
    private JPanel panelAdmMakes = new JPanel();
    //Central panel for ver. end trip
    private JPanel panelEndTrip = new JPanel();

    //Creating bottom ver. main menu
    private JPanel panelBottom1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    //Creating bottom ver. logout
    private JPanel panelBottom2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    //Creating bottom ver. login
    private JPanel panelBottom3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    //Creating bottom ver. go back
    private JPanel panelBottomGoBack = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    //Creating two containers. One for the central panel, one for the bottom.
    private CardLayout cl = new CardLayout();
    private JPanel centralContainer = new JPanel(cl);
    private CardLayout cl2 = new CardLayout();
    private JPanel bottomContainer = new JPanel(cl2);

    //global comboxes so we can update them
    private JComboBox editBikeIds = new JComboBox();
    private JComboBox deleteBikeIds = new JComboBox();
    private JComboBox bicNumber1 = new JComboBox();
    private JComboBox bicNumber2 = new JComboBox();
    private JComboBox bikeNrsStat = new JComboBox();
    private JComboBox addBikeMake = new JComboBox();
    private JComboBox addBikeType = new JComboBox();
    private JComboBox editTypes = new JComboBox();
    private JComboBox deleteTypes = new JComboBox();
    private JComboBox deleteMakes = new JComboBox();
    private JComboBox editMakes = new JComboBox();
    private JComboBox regBikeStationIds = new JComboBox();
    private JComboBox dockingStationIds = new JComboBox();
    private JComboBox statusDockingStationIds = new JComboBox();
    private JComboBox adminEmails = new JComboBox();
    private JComboBox dsIdsForMachine = new JComboBox();
    private JComboBox rentmachineIds = new JComboBox();
    private JComboBox bikesBikeUsage = new JComboBox();
    //ComboBoxes for end trip
    private JComboBox endTripBikeIds = new JComboBox();
    private JComboBox endTripStationIds = new JComboBox();

    //Global username for the current user
    private String userName;

    //Global JLabel so we can update them
    private JLabel currentDep = new JLabel();
    private JLabel currentTime = new JLabel();
    //Labels for show bicycle status:
    private JLabel batteryLevel = new JLabel();
    private JLabel dockingStation = new JLabel();
    private JLabel totalKm = new JLabel();
    private JLabel totalTrips = new JLabel();
    private JLabel repairs = new JLabel();
    private JLabel id = new JLabel();
    private JLabel price = new JLabel();
    private JLabel type = new JLabel();
    private JLabel make = new JLabel();
    private JLabel date = new JLabel();
    //Label for the header - showing where the user currently is
    private String constTxt = "You are currently viewing ";
    private JLabel currentPage = new JLabel(constTxt + "Login menu");

    //Global JTextfields and JTextAreas
    private JTextField bikePriceField = new JTextField();
    private JTextField bikeAmountField = new JTextField();
    private JTextField bikeLatField = new JTextField();
    private JTextField bikeLonField = new JTextField();
    private JTextField name = new JTextField();
    private JTextField date1 = new JTextField();
    private JTextField typeName = new JTextField();
    private JTextArea typeDesc = new JTextArea();
    private JTextField priceAdder = new JTextField();
    private JTextField adressInput = new JTextField();
    private JTextField rentMachinesInput = new JTextField();
    private JTextField machineAmount = new JTextField();
    private JTextField unitsInput = new JTextField();
    private JTextField newDep = new JTextField();
    private JTextField newTime = new JTextField();
    private JTextField emailInput = new JTextField();
    private JPasswordField adminPasswordInput = new JPasswordField();
    private JPasswordField adminConfirmInput = new JPasswordField();
    private JTextArea reqDesc = new JTextArea();
    private JTextField repPrice = new JTextField();
    private JTextArea regDesc = new JTextArea();
    //Date inputfield for end trip
    private JTextField endTripDateTime = new JTextField(10);


    //Global graphicslayer for showMap
    private GraphicsLayer graphicsL = new GraphicsLayer();
    private SimpleMarkerSymbol redCircle = new SimpleMarkerSymbol(Color.RED, 16, SimpleMarkerSymbol.Style.CIRCLE);

    //Fonts
    //Main title
    private Font main = new Font("Helvetica", Font.PLAIN,  30);

    //Under-title
    private Font under = new Font("Helvetica", Font.PLAIN,  20);

    //Buttons
    private Font buttonFont = new Font("Helvetica", Font.BOLD, 15);

    //Global attributes for map so it can be changed
    private JMap map = new JMap();
    private JPanel contentPane = new JPanel(new BorderLayout());
    private JMap bikeAndDsMap = new JMap();
    private JMap admDsMap = new JMap();
    private JPanel dsMapPanel = new JPanel(new BorderLayout());

    //scheduler
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture threadUpdate = null;
    private ScheduledFuture threadantiIdle = null;


    public Admin_JFrame() throws Exception {
        this.db = new Admin_database(3,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ");
        threadantiIdle = executor.scheduleAtFixedRate(antiIdle, 0, 60, TimeUnit.SECONDS);
        //Stop the connection when window is closed.
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e){
                System.out.println("Closed");
                userName = "";
                map.dispose();
                bikeAndDsMap.dispose();
                admDsMap.dispose();
                System.out.println("threadantiIdle canceled");
                threadantiIdle.cancel(true);
                db.disconnect();
                e.getWindow().dispose();
            }
        });

        //Adding to the different panels by using methods
        createHeaderPanel();
        createLoginPanel();
        createMenuPanel();
        createAdmTypePanel();
        createRegAdminPanel();
        createEditAdminPanel();
        createRegBikePanel();
        createEditBikePanel();
        createRegRepPanel();
        createBikeStatusPanel();
        createPanelAdmDs();
        createPanelShowDockStatus();
        createPanelShowMap();
        createPanelStats();
        createPanelBikePurchase();
        createPanelTypeStats();
        createPanelPowerUsage();
        createPanelBikeUsage();
        createPanelPropVal();
        createPanelAdmMakes();
        createPanelEndTrip();
        createBottom1Panel();
        createBottom2Panel();
        createBottom3Panel();
        createGoBackBottom();

        //Choosing startup panels for the panels with multiple versions
        cl.show(centralContainer, "panelLogin");
        cl2.show(bottomContainer, "panelBottomLogin");

        //Adding all the panels to the JFrame
        add(panelTop, BorderLayout.NORTH);
        add(centralContainer, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);

        this.setTitle("Bicycle rental system administration");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1080, 720);
        this.setVisible(true);
        setLocationRelativeTo(null);
    }

    private void createHeaderPanel(){
        currentPage.setForeground(new Color(239, 255, 229));
        panelTop.setLayout(new MigLayout());
        panelTop.setBackground(new Color(75, 127, 44));
        try {
            JLabel bicycleIcon = new JLabel(new ImageIcon("if_vehicles-16_809728.png"));
            panelTop.add(bicycleIcon);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Font font = new Font("Helvetica", Font.PLAIN,  32);
        JLabel headerText = new JLabel("Bicycle system administration");
        headerText.setFont(font);
        headerText.setForeground(new Color(239, 255, 229));
        panelTop.add(headerText);
        panelTop.add(currentPage);

    }

    private void createLoginPanel(){
        panelLogin.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelLogin);
        JLabel information = new JLabel("To administer the system, you must log in.");
        information.setFont(under);
        JTextField textfieldEmail = new JTextField(20);
        JPasswordField passwordFieldPass = new JPasswordField(20);

        JButton buttonLogin = new JButton("Log in");
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Checks that user has entered values into both fields
                if (textfieldEmail.getText().equals("") || passwordFieldPass.getPassword().length == 0) {
                    showMessageDialog(centralContainer, "You haven't entered both the username and password.");
                    passwordFieldPass.setText("");
                } else {
                    //Setting new current page info:
                    currentPage.setText(constTxt + "Main menu");
                    //Checks if the user is in the database
                    String email = textfieldEmail.getText();
                    char[] password = passwordFieldPass.getPassword();
                    try {
                        if (db.login(email, new String(password)) == true) {
                            cl.show(centralContainer, "panelMenu");
                            cl2.show(bottomContainer, "panelBottomLogout");
                            userName = email;
                            textfieldEmail.setText("");
                            passwordFieldPass.setText("");
                            email = "";
                            password = null; //To clear the sensitive information
                        } else {
                            showMessageDialog(centralContainer, "Invalid username or password!");
                            passwordFieldPass.setText("");
                            email = ""; //To clear the sensitive information
                            password = null;
                        }
                    } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                        ex.printStackTrace();
                        showMessageDialog(centralContainer, "Unexpected error, please retry!");
                    }
                }
            }
        });

        //Adding to the login panel
        panelLogin.add(information, "span, center, wrap 15");
        panelLogin.add(new JLabel("Username/e-mail: "));
        panelLogin.add(textfieldEmail, "center, wrap");
        panelLogin.add(new JLabel("Password: "));
        panelLogin.add(passwordFieldPass, "center, wrap");
        panelLogin.add(buttonLogin, "span, center");

        //Sets a nice border
        panelLogin.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        //Adding the login panel to the central panel that is used to display different pages
        centralContainer.add(panelLogin, "panelLogin");
    }

    private void createMenuPanel(){
        JLabel infoMenu = new JLabel("What do you want to do?");
        infoMenu.setFont(under);

        //Creating Administer bicycle types button
        JButton buttonAdmType = new JButton("Administer bicycle types");
        buttonAdmType.setFont(buttonFont);
        buttonAdmType.setPreferredSize(new Dimension(300, 35));
        buttonAdmType.addActionListener(createActionListener("panelAdmType"));
        buttonAdmType.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonAdmType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Administer bicycle types");
                typeName.setText("");
                typeDesc.setText("");
                priceAdder.setText("");
                ArrayList<String> allTypes = db.getAllTypes();
                updateCombobox(allTypes, editTypes);
                updateCombobox(allTypes, deleteTypes);
            }
        });

        //Creating Register new admin button
        JButton buttonRegAdmin = new JButton("Register new admin");
        buttonRegAdmin.setFont(buttonFont);
        //Change button size? buttonRegAdmin.setPreferredSize(new Dimension(35, 35));
        buttonRegAdmin.setPreferredSize(new Dimension(300, 35));
        buttonRegAdmin.addActionListener(createActionListener("panelRegAdmin"));
        buttonRegAdmin.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonRegAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailInput.setText("");
                currentPage.setText(constTxt + "Main menu → Register new admin");
            }
        });

        //Creating Edit/delete admin
        JButton buttonEditAdminAdm = new JButton("Edit/delete admin");
        buttonEditAdminAdm.setFont(buttonFont);
        buttonEditAdminAdm.setPreferredSize(new Dimension(300, 35));
        buttonEditAdminAdm.addActionListener(createActionListener("panelEditAdmin"));
        buttonEditAdminAdm.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonEditAdminAdm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminPasswordInput.setText("");
                adminConfirmInput.setText("");
                updateCombobox(db.getAllAdmins(), adminEmails);
                currentPage.setText(constTxt + "Main menu → Edit/delete admin");
            }
        });

        //Creating administer makes
        JButton buttonAdmMakes = new JButton("Admister bicycle makes");
        buttonAdmMakes.setFont(buttonFont);
        buttonAdmMakes.setPreferredSize(new Dimension(300, 35));
        buttonAdmMakes.addActionListener(createActionListener("panelAdmMakes"));
        buttonAdmMakes.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonAdmMakes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Administer bicycle makes");
                name.setText("Make name");
                date1.setText("YYYY-MM-DD");
                updateCombobox(db.getAllMakes(), editMakes);
                updateCombobox(db.getAllMakes(), deleteMakes);
            }
        });

        //Creating Register a new bicycle
        JButton buttonRegBike = new JButton("Register a new bicycle");
        buttonRegBike.setFont(buttonFont);
        buttonRegBike.setPreferredSize(new Dimension(300, 35));
        buttonRegBike.addActionListener(createActionListener("panelRegBike"));
        buttonRegBike.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonRegBike.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Register new bicycle");
                bikePriceField.setText("");
                bikeAmountField.setText("");
                updateCombobox(db.getAllMakes(), addBikeMake);
                updateCombobox(db.getAllTypes(), addBikeType);
                updateCombobox(db.getAllDockingStations(), regBikeStationIds);
            }
        });

        //Creating Edit/delete bicycle
        JButton buttonEditBike = new JButton("Edit/delete a bicycle");
        buttonEditBike.setFont(buttonFont);
        buttonEditBike.setPreferredSize(new Dimension(300, 35));
        buttonEditBike.addActionListener(createActionListener("panelEditDelBike"));
        buttonEditBike.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonEditBike.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Edit/delete a bicycle");
                ArrayList<String> allBikes = db.getAllBikes();
                updateCombobox(allBikes, editBikeIds);
                updateCombobox(allBikes, deleteBikeIds);
            }
        });

        //Creating Register repair
        JButton buttonRegRep = new JButton("Administer repairs");
        buttonRegRep.setFont(buttonFont);
        buttonRegRep.setPreferredSize(new Dimension(300, 35));
        buttonRegRep.addActionListener(createActionListener("panelRegRep"));
        buttonRegRep.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonRegRep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reqDesc.setText("");
                repPrice.setText("");
                regDesc.setText("");
                currentPage.setText(constTxt + "Main menu → Administer repairs");
                ArrayList<String> allBikes = db.getAllBikes();
                updateCombobox(allBikes, bicNumber1);
                updateCombobox(allBikes, bicNumber2);
            }
        });

        //Creating Show bicycle status
        JButton bikeStatusBtn = new JButton("Show bicycle status");
        bikeStatusBtn.setFont(buttonFont);
        bikeStatusBtn.setPreferredSize(new Dimension(300, 35));
        bikeStatusBtn.addActionListener(createActionListener("panelBikeStatus"));
        bikeStatusBtn.addActionListener(createActionListenerb("panelBottomMainMenu"));
        bikeStatusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadUpdate = executor.scheduleAtFixedRate(updateStatusMapRunnable, 0, db.getMapUpdateTime(), TimeUnit.SECONDS);
                currentPage.setText(constTxt + "Main menu → Show bicycle status");
                ArrayList<String> allBikes = db.getAllBikes();
                updateCombobox(allBikes, bikeNrsStat);
            }
        });

        //Show docking status
        JButton buttonDockingStationStatus = new JButton("Show docking station status");
        buttonDockingStationStatus.setFont(buttonFont);
        buttonDockingStationStatus.setPreferredSize(new Dimension(300, 35));
        buttonDockingStationStatus.addActionListener(createActionListener("panelShowDockStatus"));
        buttonDockingStationStatus.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonDockingStationStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Show docking station status");
                updateCombobox(db.getAllDockingStations(), statusDockingStationIds);
            }
        });

        //Edit/delete docking station
        JButton buttonEditStationDs = new JButton("Administer docking stations");
        buttonEditStationDs.setFont(buttonFont);
        buttonEditStationDs.setPreferredSize(new Dimension(300, 35));
        buttonEditStationDs.addActionListener(createActionListener("panelAdmDs"));
        buttonEditStationDs.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonEditStationDs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Edit/delete/add a docking station");
                adressInput.setText("");
                rentMachinesInput.setText("");
                unitsInput.setText("");
                machineAmount.setText("");
                updateCombobox(db.getAllDockingStations(), dockingStationIds);
                updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                updateDsMap();
            }
        });

        //Show map
        JButton buttonShowMap = new JButton("Show map");
        buttonShowMap.setFont(buttonFont);
        buttonShowMap.setPreferredSize(new Dimension(300, 35));
        buttonShowMap.addActionListener(createActionListener("panelShowMap"));
        buttonShowMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadUpdate = executor.scheduleAtFixedRate(updateMapRunnable, 0, db.getMapUpdateTime(), TimeUnit.SECONDS);
            }
        });
        buttonShowMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBikeAndDsMap();
            }
        });
        buttonShowMap.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonShowMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Show map");
            }
        });

        //Statistical info
        JButton buttonStats = new JButton("Show statistical information");
        buttonStats.setFont(buttonFont);
        buttonStats.setPreferredSize(new Dimension(300, 35));
        buttonStats.addActionListener(createActionListener("panelStats"));
        buttonStats.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Show statistical information");
            }
        });

        //Change property values
        JButton buttonProperties = new JButton("Change properties values");
        buttonProperties.setFont(buttonFont);
        buttonProperties.setPreferredSize(new Dimension(300, 35));
        buttonProperties.addActionListener(createActionListener("panelPropVal"));
        buttonProperties.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newDep.setText("");
                newTime.setText("");
                currentPage.setText(constTxt + "Main menu → Change properties values");
                currentDep.setText("Current deposit value: " + Double.toString(db.getDeposit()) + " NOK");
                currentTime.setText("Current map update time value: " + Integer.toString(db.getMapUpdateTime()) + " seconds");
            }
        });

        //End trip
        JButton buttonEndTrip = new JButton("End a bicycle trip");
        buttonEndTrip.setFont(buttonFont);
        buttonEndTrip.setPreferredSize(new Dimension(300, 35));
        buttonEndTrip.addActionListener(createActionListener("panelEndTrip"));
        buttonEndTrip.addActionListener(createActionListenerb("panelBottomMainMenu"));
        buttonEndTrip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCombobox(db.getAllBikesOnTrip(), endTripBikeIds);
                updateCombobox(db.getAllDockingStations(), endTripStationIds);
                endTripDateTime.setText("");
                currentPage.setText(constTxt + "Main menu → End a bicycle trip");
            }
        });

        panelMenu.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelMenu);
        panelMenu.add(infoMenu, "span, center, wrap");

        panelMenu.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelMenu);
        panelMenu.add(infoMenu, "span, center, wrap");


        //Panel for the left buttons.
        JPanel leftButtons = new JPanel(new MigLayout("gapy 15, wrap"));
        setCentralColor(leftButtons);
        leftButtons.add(buttonRegBike);
        leftButtons.add(buttonEditBike);
        leftButtons.add(buttonAdmMakes);
        leftButtons.add(buttonAdmType);
        leftButtons.add(buttonEditStationDs);
        leftButtons.add(buttonRegAdmin);
        leftButtons.add(buttonEditAdminAdm);


        //Panel for the right buttons
        JPanel rightButtons = new JPanel(new MigLayout("gapy 15, wrap"));
        setCentralColor(rightButtons);
        rightButtons.add(buttonRegRep);
        rightButtons.add(bikeStatusBtn);
        rightButtons.add(buttonDockingStationStatus);
        rightButtons.add(buttonShowMap);
        rightButtons.add(buttonStats);
        rightButtons.add(buttonProperties);
        rightButtons.add(buttonEndTrip);

        panelMenu.add(leftButtons, "top");
        panelMenu.add(rightButtons, "top");

        //Sets a nice border
        panelMenu.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        centralContainer.add(panelMenu, "panelMenu");
    }

    private void createAdmTypePanel(){
        //Importing all the needed arrays
        //All types:
        ArrayList<String> allTypes = db.getAllTypes();
        updateCombobox(allTypes, editTypes);
        updateCombobox(allTypes, deleteTypes);

        panelAdmType.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelAdmType);
        JLabel typesHeader = new JLabel("Administer bicycle types");
        typesHeader.setFont(main);
        panelAdmType.add(typesHeader, "span, center, wrap ");

        //Left column (adding)
        JPanel addPanel = new JPanel(new MigLayout());
        setCentralColor(addPanel);
        JLabel addHeader = new JLabel("Add a type");
        addHeader.setFont(under);
        addPanel.add(addHeader, "span, center, wrap");
        addPanel.add(new JLabel("Type name:"), "w 75");
        typeName = new JTextField();
        addPanel.add(typeName, "w 250!, wrap");
        addPanel.add(new JLabel("Description:"));
        typeDesc = new JTextArea(5, 20);
        typeDesc.setLineWrap(true);
        typeDesc.setWrapStyleWord(true);
        JScrollPane scrollPaneDesc = new JScrollPane(typeDesc);
        addPanel.add(scrollPaneDesc, "growx, wrap");
        addPanel.add(new JLabel("Price:"));
        priceAdder = new JTextField();
        addPanel.add(priceAdder, "growx, wrap");


        //Centre column (deleting)
        JPanel deletePanel = new JPanel(new MigLayout());
        setCentralColor(deletePanel);
        JLabel deleteHeader = new JLabel("Delete a type");
        deleteHeader.setFont(under);
        deletePanel.add(deleteHeader, "span, center, wrap");
        deletePanel.add(new JLabel("Type name:"), "w 75");
        deletePanel.add(deleteTypes, "w 250, wrap");


        //Right column (editing)
        JPanel editPanel = new JPanel(new MigLayout());
        setCentralColor(editPanel);
        JLabel editHeader = new JLabel("Edit a type");
        editHeader.setFont(under);
        editPanel.add(editHeader, "span , center, wrap");
        editPanel.add(new JLabel("Select type:"), "w 75");
        //Getting the current type
        String type = editTypes.getSelectedItem().toString();

        editPanel.add(editTypes, "w 250!, wrap");
        editPanel.add(new JLabel("Description"));
        JTextArea typeDesc2 = new JTextArea(5, 20);
        typeDesc2.setLineWrap(true);
        typeDesc2.setWrapStyleWord(true);
        JScrollPane scrollPaneDesc2 = new JScrollPane(typeDesc2);
        typeDesc2.setText(db.getTypeDesc(type));
        editPanel.add(scrollPaneDesc2, "growx, wrap");
        editPanel.add(new JLabel("Edit price:"));
        JTextField priceEdit = new JTextField();
        priceEdit.setText(String.valueOf(db.getPrice(type)));
        editPanel.add(priceEdit, "growx, wrap");

        //Listener to the combobox
        editTypes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    //Needs to check everything is ok with the selected type. If -1 or "-1" is returned for one of the methods, something has gone wrong or the type was deleted. In the extremely rare case of the type being deleted AFTER this check, the old values are still stored in these variables and will be shown.
                    String typeDesc = db.getTypeDesc(type).toString();
                    Double price = db.getPrice(type);

                    if(!typeDesc.equals("-1") && price!=-1){
                        String type = (e.getItemSelectable().getSelectedObjects()[0]).toString();
                        typeDesc2.setText(typeDesc);
                        priceEdit.setText(String.valueOf(price));
                    }
                    else{
                        showMessageDialog(null, "Something went wrong.");
                        updateCombobox(db.getAllTypes(), editTypes);
                    }
                }
            }
        });

        //Adding all the buttons with action listeners
        //Add button
        JButton buttonAdd = new JButton("Add");
        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String newName = typeName.getText();
                    String newTypeDesc = typeDesc.getText();
                    double price = Double.parseDouble(priceAdder.getText());
                    if(price>=0 && !newName.equals("") && !newTypeDesc.equals("")){
                        if ((db.addType(newName, newTypeDesc , price)).equals(typeName.getText())) {
                            typeName.setText("");
                            typeDesc.setText("");
                            priceAdder.setText("");
                            showMessageDialog(null, "You successfully added " + newName + " to the types.");
                        } else {
                            showMessageDialog(null, "Something went wrong, bicycle not added");
                        }
                    }
                    else{
                        showMessageDialog(null, "You must enter a name and a description, and you can't a negative price.");
                    }


                }catch(NumberFormatException ex){
                    showMessageDialog(null, "Something went wrong, illegal input");
                }
                ArrayList<String> allTypes = db.getAllTypes();
                updateCombobox(allTypes, editTypes);
                updateCombobox(allTypes, deleteTypes);
            }
        });
        addPanel.add(buttonAdd, "growx");

        //Delete button
        JButton buttonDel = new JButton("Delete");
        buttonDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deleteType = deleteTypes.getSelectedItem().toString();
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete type : " + deleteType + "?", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    boolean ed = db.deleteType(deleteType);
                    if (ed) {
                        typeDesc2.setText("");
                        showMessageDialog(null,"You have successfully deleted " + deleteType + ".");
                    } else {
                        showMessageDialog(null, "Type did not get deleted");
                    }
                } else {
                    showMessageDialog(null, "Type not deleted");
                }
                /*We need to think of use by multiple users, an error can come from someone trying to do something to an type_name that
                         does no longer exist so if that happens we update the comboboxes to make sure it doesnt happen again*/
                ArrayList<String> allTypes = db.getAllTypes();
                updateCombobox(allTypes, editTypes);
                updateCombobox(allTypes, deleteTypes);
            }
        });
        deletePanel.add(buttonDel, "growx");

        //Edit button
        JButton buttonEdit = new JButton("Edit");
        buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double price = Double.parseDouble(priceEdit.getText());

                    if(price>=0){
                        boolean edited = db.editType(editTypes.getSelectedItem().toString(), typeDesc2.getText().toString(), price);
                        if (edited) {
                            showMessageDialog(null, "Type edited");
                        } else {
                            showMessageDialog(null, "Something went wrong, type did not get edited");
                        }

                    }
                    else{
                        showMessageDialog(null, "You can't enter a negative price.");
                    }


                }catch(NumberFormatException ex){
                    showMessageDialog(null, "Something went wrong, illegal input");
                }
                ArrayList<String> allTypes = db.getAllTypes();
                updateCombobox(allTypes, editTypes);
                updateCombobox(allTypes, deleteTypes);
            }
        });
        editPanel.add(buttonEdit, "growx");

        panelAdmType.add(addPanel, "top, w 300");
        panelAdmType.add(deletePanel, "top, w 300");
        panelAdmType.add(editPanel, "top, w 300");

        panelAdmType.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        centralContainer.add(panelAdmType, "panelAdmType");
    }

    private void createRegAdminPanel(){

        //Setting up the panel
        panelRegAdmin.setLayout(new MigLayout());
        setCentralColor(panelRegAdmin);
        JLabel infoText = new JLabel("Register a new administrator");
        infoText.setFont(under);
        panelRegAdmin.add(infoText, "wrap");
        emailInput = new JTextField();
        panelRegAdmin.add(new JLabel("Enter e-mail address:"), "wrap");
        panelRegAdmin.add(emailInput, "growx, wrap");
        JButton regAdmBtn = new JButton("Register!");
        panelRegAdmin.add(regAdmBtn);
        panelRegAdmin.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        //Action if register button is pressed
        regAdmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!emailInput.getText().equals("")) {
                    if(isValidEmailAddress(emailInput.getText())){
                        try{
                            char[] pass = db.genPass().toCharArray();
                            if(db.register(emailInput.getText(), new String(pass))) {
                                db.epost(emailInput.getText(), new String(pass));
                                showMessageDialog(centralContainer, "User created. Generated password sent to: " + emailInput.getText());
                                pass = null; //In case of potential risk with saving a password as a variable
                            }
                            else{
                                pass = null; //In case of potential risk with saving a password as a variable
                                showMessageDialog(centralContainer, "Something went wrong, e-mail may already be registered.");
                            }
                        }catch(Exception a){
                            showMessageDialog(centralContainer, "Something went wrong. User not registered.");
                        }
                        emailInput.setText("");
                    } else{
                        showMessageDialog(centralContainer, "The entered value is not a valid e-mail. Please retry!");
                        emailInput.setText("");
                    }
                } else {
                    showMessageDialog(centralContainer, "You haven't entered anything. Please add an e-mail for the admin user.");
                    emailInput.setText("");
                }
            }
        });

        centralContainer.add(panelRegAdmin, "panelRegAdmin");
    }

    private void createEditAdminPanel(){
        //Admins
        updateCombobox(db.getAllAdmins(), adminEmails);

        panelEditAdmin.setLayout(new MigLayout());
        setCentralColor(panelEditAdmin);

        JPanel deletePanel = new JPanel(new MigLayout());
        setCentralColor(deletePanel);
        JLabel deleteTitle = new JLabel("Delete admin");
        deleteTitle.setFont(under);
        deletePanel.add(deleteTitle, "wrap");
        deletePanel.add(adminEmails, "growx");
        JButton deleteAdminBtn = new JButton("Delete");
        deletePanel.add(deleteAdminBtn, "left");

        JPanel editPanel = new JPanel(new MigLayout());
        setCentralColor(editPanel);
        JLabel editTitle = new JLabel("Edit account: ");
        editTitle.setFont(under);
        editPanel.add(editTitle, "wrap");
        editPanel.add(new JLabel("Password: "));
        adminPasswordInput = new JPasswordField(20);
        editPanel.add(adminPasswordInput, "growx, wrap");
        editPanel.add(new JLabel("Confirm password: "));
        adminConfirmInput = new JPasswordField(20);
        editPanel.add(adminConfirmInput, "growx, wrap");
        JButton editAdminBtn = new JButton("Save changes");
        editPanel.add(editAdminBtn,"growx");

        panelEditAdmin.add(editPanel, "top");
        panelEditAdmin.add(deletePanel, "top");

        editAdminBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(((new String(adminPasswordInput.getPassword()) != "") && new String(adminPasswordInput.getPassword()).equals(new String(adminConfirmInput.getPassword())))){
                    try{
                        db.change(userName, new String(adminConfirmInput.getPassword()));
                        adminPasswordInput.setText("");
                        adminConfirmInput.setText("");
                        showMessageDialog(centralContainer, "Password succesfully edited!");

                    }catch(Exception a){
                        showMessageDialog(centralContainer, "Edit failed! Please try again.");
                        adminPasswordInput.setText("");
                        adminConfirmInput.setText("");
                    }
                }
                else{
                    showMessageDialog(centralContainer, "Please check that you have filled all fields with valid data.");
                    adminPasswordInput.setText("");
                    adminConfirmInput.setText("");
                }
            }
        });

        deleteAdminBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = adminEmails.getSelectedObjects()[0].toString();
                if(!user.equals(userName)) {
                    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete admin: " + (adminEmails.getSelectedObjects()[0]).toString() + "?", "WARNING",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (db.deleteAdmin(user)) {
                            showMessageDialog(centralContainer, "Admin: " + user + " was succesfully deleted.");
                            updateCombobox(db.getAllAdmins(), adminEmails);
                        } else{
                            updateCombobox(db.getAllAdmins(), adminEmails);
                            showMessageDialog(centralContainer, "Admin wasn't deleted. List refreshed!");
                        }
                    }else{
                        updateCombobox(db.getAllAdmins(), adminEmails);
                        showMessageDialog(centralContainer, "Deletion aborted.");
                    }
                }else{
                    updateCombobox(db.getAllAdmins(), adminEmails); //Not needed, but added just to give an updated list
                    showMessageDialog(centralContainer, "You can not delete your own user.");
                }
            }
        });

        panelEditAdmin.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        centralContainer.add(panelEditAdmin, "panelEditAdmin");
    }

    private void createRegBikePanel(){
        updateCombobox(db.getAllMakes(), addBikeMake);
        updateCombobox(db.getAllTypes(), addBikeType);
        updateCombobox(db.getAllDockingStations(), regBikeStationIds);
        panelRegBike.setLayout(new MigLayout());
        setCentralColor(panelRegBike);
        JLabel regBikeText = new JLabel("Register a new bicycle");
        regBikeText.setFont(under);
        panelRegBike.add(regBikeText, "wrap");
        bikePriceField = new JTextField(4);
        bikeAmountField = new JTextField(4);
        bikeLatField.setEditable(false);
        bikeLonField.setEditable(false);
        JButton regBikeBtn = new JButton("Register bicycle");
        panelRegBike.add(new JLabel("Make: "));
        panelRegBike.add(addBikeMake, "growx, wrap");
        panelRegBike.add(new JLabel("Type: "));
        panelRegBike.add(addBikeType, "growx, wrap");
        panelRegBike.add(new JLabel("Price: "));
        panelRegBike.add(bikePriceField, "growx, wrap");
        panelRegBike.add(new JLabel("Amount: "));
        panelRegBike.add(bikeAmountField, "growx, wrap");
        panelRegBike.add(new JLabel("Docked at station (ID): "));
        panelRegBike.add(regBikeStationIds, "growx, wrap");
        panelRegBike.add(bikeLatField, "growx, wrap");
        panelRegBike.add(bikeLonField, "growx, wrap");
        panelRegBike.add(regBikeBtn, "growx, wrap");

        panelRegBike.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        regBikeStationIds.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    double[] coords = db.getStationPosition(Integer.parseInt(regBikeStationIds.getSelectedObjects()[0].toString()));
                    bikeLatField.setText("" + coords[0]);
                    bikeLonField.setText("" + coords[1]);
                }
            }
        });

        regBikeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String make = addBikeMake.getSelectedObjects()[0].toString();
                    String type = addBikeType.getSelectedObjects()[0].toString();
                    double price = Double.parseDouble(bikePriceField.getText());
                    int amount = Integer.parseInt(bikeAmountField.getText());
                    if(amount > 0 && price >= 0) {
                        ArrayList<String> addedBikes = db.addBike(price, make, type, amount, Integer.parseInt(regBikeStationIds.getSelectedObjects()[0].toString()), Double.parseDouble(bikeLatField.getText()), Double.parseDouble(bikeLonField.getText()));
                        if (!addedBikes.get(0).equals("-1")) {
                            String result = "";
                            for (int i = 0; i < addedBikes.size() - 1; i++) {
                                result += addedBikes.get(i) + ", ";
                            }
                            result += addedBikes.get(addedBikes.size() - 1);
                            showMessageDialog(panelRegBike, "Bicycles with id: " + result + " successfully registered!");
                            updateCombobox(db.getAllMakes(), addBikeMake);
                            updateCombobox(db.getAllTypes(), addBikeType);
                            updateCombobox(db.getAllDockingStations(), regBikeStationIds);
                            bikeAmountField.setText("");
                            bikePriceField.setText("");
                        } else {
                            showMessageDialog(panelRegBike, "Something went wrong, bicycle did not get added");
                            updateCombobox(db.getAllMakes(), addBikeMake);
                            updateCombobox(db.getAllTypes(), addBikeType);
                            updateCombobox(db.getAllDockingStations(), regBikeStationIds);
                            bikeAmountField.setText("");
                            bikePriceField.setText("");
                        }
                    }else {
                        showMessageDialog(panelRegBike, "There is something wrong in the inputs");
                        updateCombobox(db.getAllMakes(), addBikeMake);
                        updateCombobox(db.getAllTypes(), addBikeType);
                        updateCombobox(db.getAllDockingStations(), regBikeStationIds);
                        bikeAmountField.setText("");
                        bikePriceField.setText("");
                    }
                }catch(IllegalArgumentException ie){
                    showMessageDialog(panelRegBike, "Illegal arguments entered. Please check entered values, and retry.");
                    bikeAmountField.setText("");
                    bikePriceField.setText("");
                }
            }
        });

        centralContainer.add(panelRegBike, "panelRegBike");
    }

    //method to update all the input fields for editBicycle
    private void updateEditBikeInputs(JTextField date, JTextField price, JTextField km, JTextField trips, JComboBox types, JComboBox makes){
        date.setText(""+db.getBikeRegdate(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString())));
        price.setText(""+db.getBikePrice(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString())));
        km.setText(""+db.getBikeKm(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString())));
        trips.setText(""+db.getBikeTrips(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString())));
        ArrayList<String> typeList = db.getAllTypes();
        updateCombobox(typeList, types);
        String rType = db.getBikeType(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString()));
        types.setSelectedItem(rType);
        ArrayList<String> makeList = db.getAllMakes();
        updateCombobox(makeList, makes);
        String rMake = db.getBikeMake(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString()));
        types.setSelectedItem(rMake);
    }

    private void createEditBikePanel(){
        ArrayList<String> allBikes = db.getAllBikes();
        updateCombobox(allBikes, editBikeIds);
        updateCombobox(allBikes, deleteBikeIds);
        panelEditDelBike.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelEditDelBike);
        JLabel header = new JLabel("Edit/delete bicycle");
        header.setFont(main);
        panelEditDelBike.add(header, "span, center, wrap");
        JPanel left = new JPanel(new MigLayout());
        setCentralColor(left);
        JPanel right = new JPanel(new MigLayout("right"));
        setCentralColor(right);

        JLabel editHead = new JLabel("Edit");
        editHead.setFont(under);
        left.add(editHead, "span, center, wrap");
        left.add(new JLabel("Bike: "));
        left.add(editBikeIds, "w 150, wrap");
        left.add(new JLabel("Date: "));
        JTextField date = new JTextField(4);
        left.add(date, "growx, wrap");
        left.add(new JLabel("Type: "));
        JComboBox types = new JComboBox();
        left.add(types, "growx, wrap");
        left.add(new JLabel("Make: "));
        JComboBox makes = new JComboBox();
        left.add(makes, "growx, wrap");
        left.add(new JLabel("Price: "));
        JTextField price = new JTextField(4);
        left.add(price, "growx, wrap");
        left.add(new JLabel("Km traveled: "));
        JTextField km = new JTextField(4);
        left.add(km, "growx, wrap");
        left.add(new JLabel("Trips: "));
        JTextField trips = new JTextField(4);
        left.add(trips, "growx, wrap");
        JButton edit = new JButton("Make changes");
        left.add(edit,"growx, wrap");
        updateEditBikeInputs(date, price, km, trips, types, makes);

        editBikeIds.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    //Needs to check everything is ok with the selected bike. If -1 or "-1" is returned for one of the methods, something has gone wrong or the bike was deleted. In the extremely rare case of the bike being deleted AFTER this check, the old values are still stored in these variables and will be shown.

                    int i = Integer.parseInt((e.getItemSelectable().getSelectedObjects()[0]).toString());
                    //All the variables needed:
                    String foundDate = db.getBikeRegdate(i);
                    Double foundPrice = db.getBikePrice(i);
                    Double foundKm = db.getBikeKm(i);
                    int foundTrips = db.getBikeTrips(i);
                    int allRepairs = db.getBikeRepairAmount(i);
                    ArrayList<String> foundTypes = db.getAllTypes();
                    String foundType = db.getBikeType(i);
                    ArrayList<String>  foundMakes = db.getAllMakes();
                    String foundMake = db.getBikeMake(i);

                    if(!foundDate.equals("-1") && foundPrice!=-1 && !foundDate.equals("-1") && foundKm!=-1 && foundTrips!=-1 && allRepairs!=-1  && !foundTypes.get(0).equals("-1") && !foundType.equals("-1") && !foundMakes.get(0).equals("-1") && !foundMake.equals("-1")){
                        date.setText(""+foundDate);
                        price.setText(""+foundPrice);
                        km.setText(""+foundKm);
                        trips.setText(""+foundTrips);
                        updateCombobox(foundTypes, types);
                        types.setSelectedItem(foundType);
                        updateCombobox(foundMakes, makes);
                        makes.setSelectedItem(foundMake);
                    }
                    else{
                        showMessageDialog(null, "Something went wrong! Maybe the selected type has been deleted?");
                        updateCombobox(db.getAllTypes(), types);
                    }
                }
            }
        });

        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double newPrice = Double.parseDouble(price.getText());
                    double newKm = Double.parseDouble(km.getText());
                    int newTrips = Integer.parseInt(trips.getText());

                    //Must check if these inputs aren't negative; It does not make sense with a negative value. But they could all be 0.
                    if(newPrice>=0 && newKm>=0 && newTrips>=0){
                        boolean ed = db.editBike(Integer.parseInt((editBikeIds.getSelectedObjects()[0]).toString()), newPrice, (makes.getSelectedObjects()[0]).toString(), date.getText(), newKm, newTrips, (types.getSelectedObjects()[0]).toString());
                        if (ed) {
                            showMessageDialog(null, "Bike edited successfully");
                        } else {
                            showMessageDialog(null, "Bike did not get updated");
                            updateEditBikeInputs(date, price, km, trips, types, makes);
                        }
                    }
                    else {
                        showMessageDialog(null, "The values you have entered are not valid; Neither of date, price, km and trips should be a negative value.");
                    }

                }catch(NumberFormatException ex){
                    showMessageDialog(null, "Bike did not get updated, wrong input type");
                }
                /*We need to think of use by multiple users, an error can come from someone trying to do something to an id that does no longer exist so if that happens we update the comboboxes to make sure it doesnt happen again*/
                updateCombobox(db.getAllBikes(), editBikeIds);
                updateCombobox(db.getAllBikes(), deleteBikeIds);
            }
        });

        JLabel delHead = new JLabel("Delete");
        delHead.setFont(under);
        right.add(delHead, "span, center, wrap");
        right.add(new JLabel("Bike: "));
        right.add(deleteBikeIds, "w 150, wrap");
        JButton delete = new JButton("Delete");
        right.add(delete, "growx, wrap");

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete bike with id: " + (deleteBikeIds.getSelectedObjects()[0]).toString() + "?", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    boolean ed = db.deleteBike(Integer.parseInt((deleteBikeIds.getSelectedObjects()[0]).toString()));
                    if (ed) {
                        showMessageDialog(null, "Bike deleted successfully");
                    } else {
                        showMessageDialog(null, "Bike did not get deleted");
                    }
                } else {
                    showMessageDialog(null, "Bike not deleted");
                }
                /*We need think of use by multiple users, an error can come from someone trying to do something to an id that
                         does no longer exist so if that happens we update the comboboxes to make sure it doesnt happen again*/
                updateCombobox(db.getAllBikes(), editBikeIds);
                updateCombobox(db.getAllBikes(), deleteBikeIds);
            }
        });

        panelEditDelBike.add(left, "top, w 300");
        panelEditDelBike.add(right,"top, w 300");

        panelEditDelBike.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        centralContainer.add(panelEditDelBike, "panelEditDelBike");
    }

    private void createRegRepPanel(){

        panelRegRep.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelRegRep);
        JLabel header = new JLabel("Administer repairs");
        header.setFont(main);
        panelRegRep.add(header, "span, center, wrap");

        //Request
        JPanel requestPanel = new JPanel(new MigLayout("align 50%"));
        setCentralColor(requestPanel);

        JLabel requestLbl = new JLabel("Request repair");
        requestLbl.setFont(under);
        requestPanel.add(requestLbl, "span, center, wrap");
        requestPanel.add(new JLabel("Bicycle number:"), "w 75");

        requestPanel.add(bicNumber1, "w 150, wrap");
        requestPanel.add(new JLabel("Repair request description:"), "growx");
        reqDesc = new JTextArea(5, 20);
        reqDesc.setLineWrap(true);
        reqDesc.setWrapStyleWord(true);
        JScrollPane scrollPaneReq = new JScrollPane(reqDesc);
        scrollPaneReq.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        requestPanel.add(scrollPaneReq, "growx, wrap");
        JButton requestBtn = new JButton("Request repair");
        requestPanel.add(requestBtn, "growx");

        //Register
        JPanel registerPanel = new JPanel(new MigLayout("align 50%"));
        setCentralColor(registerPanel);

        JLabel registerLbl = new JLabel("Register repair:");
        registerLbl.setFont(under);
        registerPanel.add(registerLbl, "span, center, wrap");
        registerPanel.add(new JLabel("Bicycle number:"), "w 75");
        registerPanel.add(bicNumber2, "w 150, wrap");
        registerPanel.add(new JLabel("Price:"), "growx");
        repPrice = new JTextField();
        registerPanel.add(repPrice, "growx, wrap");
        registerPanel.add(new JLabel("Repairs description:"), "growx");
        regDesc = new JTextArea(5, 20);
        regDesc.setLineWrap(true);
        regDesc.setWrapStyleWord(true);
        JScrollPane scrollPaneReg = new JScrollPane(regDesc);
        scrollPaneReg.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        registerPanel.add(scrollPaneReg, "growx, wrap");
        JButton registerBtn = new JButton("Register repair");
        registerPanel.add(registerBtn, "growx");

        //Adding action listeners to buttons
        requestBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int bicNr = Integer.parseInt(bicNumber1.getSelectedItem().toString());
                if(db.requestRepair(bicNr, reqDesc.getText().toString())){
                    showMessageDialog(null, "You successfully sent your request for bicycle number " + bicNr);
                }
                else{
                    showMessageDialog(null, "Something went wrong. Request not sent");
                }

            }
        });

        //Adding action listeners to buttons
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try{
                    double cost = Double.parseDouble(repPrice.getText());

                    if(cost>=0){
                        int bicNr = Integer.parseInt(bicNumber2.getSelectedItem().toString());
                        if(db.registerRepair(bicNr, cost, regDesc.getText().toString())){
                            showMessageDialog(null, "You successfully registered the repair for bicycle number " + bicNr);
                        }
                        else{
                            showMessageDialog(null, "Something went wrong, repair not registred.");
                            updateCombobox(db.getAllBikes(), bicNumber1);
                            updateCombobox(db.getAllBikes(), bicNumber2);
                        }
                        repPrice.setText("");
                        regDesc.setText("");
                    }
                    else {
                        showMessageDialog(null, "You can't enter a negative price.");
                    }
                }
                catch(IllegalArgumentException ex){
                    showMessageDialog(null, "Please enter valid values. Repair not registred.");
                }
            }
        });

        panelRegRep.add(requestPanel, "top, w 500");
        panelRegRep.add(registerPanel, "top, w 500");

        panelRegRep.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        centralContainer.add(panelRegRep, "panelRegRep");
    }

    private void createBikeStatusPanel(){
        //Must fill combobox before this panel is created:
        updateCombobox(db.getAllBikes(), bikeNrsStat);

        panelBikeStatus.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelBikeStatus);
        JLabel header = new JLabel("Bicycle status");
        header.setFont(main);
        panelBikeStatus.add(header, "span, center, wrap");
        panelBikeStatus.add(new JLabel("Choose bicycle number: "), "right");
        panelBikeStatus.add(bikeNrsStat, "w 100, wrap");

        //Creating the map:
        int bikenr = Integer.parseInt(bikeNrsStat.getSelectedItem().toString());
        double[] coords = db.getBikePosition(bikenr);

        //Displaying a bike when displaying map

        contentPane.setSize(300, 300);
        MapOptions mapOptions = new MapOptions(MapOptions.MapType.TOPO, coords[0], coords[1], 15);
        map.setMapOptions(mapOptions);
        contentPane.add(map);
        map.addMarkerGraphic(coords[0],coords[1], "Bicycle", "Bicycle description");

        //Stats panel
        JPanel stats = new JPanel(new MigLayout("wrap"));
        stats.add(batteryLevel);
        stats.add(dockingStation);
        stats.add(totalKm);
        stats.add(totalTrips);
        stats.add(repairs);
        stats.add(new JLabel(""), "wrap");
        stats.add(new JLabel("Bike information:"));
        stats.add(id);
        stats.add(price);
        stats.add(type);
        stats.add(make);
        stats.add(date);

        bikeNrsStat.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    //Needs to check everything is ok with the selected bike. If -1 or "-1" is returned for one of the methods, something has gone wrong or the bike was deleted. In the extremely rare case of the bike being deleted AFTER this check, the old values are still stored in these variables and will be shown.

                        int bikenr = Integer.parseInt(e.getItemSelectable().getSelectedObjects()[0].toString());

                        Double km = db.getBikeKm(bikenr);
                        int trips = db.getBikeTrips(bikenr);
                        int allRepairs = db.getBikeRepairAmount(bikenr);
                        Double foundPrice = db.getBikePrice(bikenr);
                        String foundType = db.getBikeType(bikenr);
                        String foundMake = db.getBikeMake(bikenr);
                        String foundDate = db.getBikeRegdate(bikenr);

                        if(km!=-1 && trips!=-1 && allRepairs!=-1 && foundPrice!=-1 && !foundType.equals("-1") && !foundMake.equals("-1") && !foundDate.equals("-1")){
                            //Updating map
                            double[] coords = db.getBikePosition(bikenr);
                            map.removeAllMarkerGraphics();
                            map.addMarkerGraphic(coords[0], coords[1], "Bicycle", "Bicycle description");

                            //Updating info
                            batteryLevel.setText("Battery level: " + db.getBatteryLvl(bikenr));
                            //Checking if the bike is not at any docking station.
                            int dockingStationId = db.getBikeDockingStation(bikenr);
                            if (dockingStationId==0) {
                                dockingStation.setText("Docking station: None");
                            }
                            else{
                                dockingStation.setText("Docking station: " + dockingStationId);
                            }

                            totalKm.setText("Total km: " + km);
                            totalTrips.setText("Total trips: " + trips);
                            repairs.setText("Repairs: " + allRepairs);
                            id.setText(" - ID: " + bikenr);
                            price.setText(" - Price: " + foundPrice + " NOK");
                            type.setText(" - Type: " + foundType);
                            make.setText(" - Make: " + foundMake);
                            date.setText(" - Date purchased: " + foundDate);
                        }
                        else{
                            showMessageDialog(null, "Something unfortunately went wrong. Bicycle list will be refreshed.");
                            updateCombobox(db.getAllBikes(), bikeNrsStat);
                        }
                }
            }
        });

        panelBikeStatus.add(contentPane, "top, growy, w 500, h 300");
        panelBikeStatus.add(stats, "top, growy, w 500, h 300");

        stats.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.gray));
        panelBikeStatus.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        contentPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        centralContainer.add(panelBikeStatus, "panelBikeStatus");
    }

    private void createPanelAdmDs (){
        panelAdmDs.setLayout(new MigLayout());
        setCentralColor(panelAdmDs);

        //One panel for add docking station, another one for edit dockingstation and one more for add/delete rentmachine
        JPanel left = new JPanel(new MigLayout());
        JPanel right = new JPanel(new MigLayout());
        JPanel machinePanel = new JPanel(new MigLayout());
        setCentralColor(left);
        setCentralColor(right);
        setCentralColor(machinePanel);
        //Adding the code for add docking station panels

        JPanel inputPanel = new JPanel(new MigLayout());
        setCentralColor(inputPanel);
        setCentralColor(dsMapPanel);

        JButton addBtn = new JButton("Add");
        adressInput = new JTextField(8);
        rentMachinesInput = new JTextField(4);
        unitsInput = new JTextField(4);

        //Used to save values
        JLabel latLabel = new JLabel();
        JLabel lonLabel = new JLabel();

        DecimalFormat coordFormatter = new DecimalFormat("#.######");
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        coordFormatter.setDecimalFormatSymbols(custom);

        admDsMap.addMapOverlay(new MapOverlay() {
            @Override
            public void onMouseClicked(MouseEvent event) {
                updateDsMap(); //To stop several markers showing up when clicking around on map
                Point screenPoint = event.getPoint();
                com.esri.core.geometry.Point mapPoint = admDsMap.toMapPoint(screenPoint.x,  screenPoint.y);

                latLabel.setText(coordFormatter.format(mapPoint.getY()));
                lonLabel.setText(coordFormatter.format(mapPoint.getX()));
                admDsMap.addMarkerGraphic(mapPoint.getY(), mapPoint.getX(), "", "");
                super.onMouseClicked(event);
            }
        });
        ArcGISTiledMapServiceLayer worldLayer = new ArcGISTiledMapServiceLayer(
                "http://services.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer");
        admDsMap.getLayers().add(worldLayer);

        admDsMap.setExtent(new Envelope(10.360, 63.400, 10.420, 63.460)); //To make it open on Trondheim

        dsMapPanel.add(admDsMap, BorderLayout.CENTER);
        updateDsMap(); //Updating markers in map
        dsMapPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        //Adding components to panels
        JLabel addText = new JLabel("Add docking station");
        addText.setFont(under);
        left.add(addText, "wrap");
        left.add(new JLabel("Mark where you want your dockingstation"), "wrap");
        left.add(dsMapPanel, "top, growy, w 300, h 300");
        inputPanel.add(new JLabel("Adress: "));
        inputPanel.add(adressInput, "wrap");
        inputPanel.add(new JLabel("Charging units: "));
        inputPanel.add(unitsInput, "wrap");
        inputPanel.add(new JLabel("Rentmachines: "));
        inputPanel.add(rentMachinesInput, "wrap");
        inputPanel.add(addBtn, "wrap");
        left.add(inputPanel, "top, growy, w 300, h 100");

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String adress = adressInput.getText();
                    int units = Integer.parseInt(unitsInput.getText());
                    double lat = Double.parseDouble(latLabel.getText());
                    double lon = Double.parseDouble(lonLabel.getText());
                    int machines = Integer.parseInt(rentMachinesInput.getText());
                    if ((units >= 1) && (machines >= 1)) {
                        if (db.addStation(adress, units, lat, lon, machines) != -1) {
                            showMessageDialog(centralContainer, "Dockingstation succesfully added!");
                            updateCombobox(db.getAllDockingStations(), dockingStationIds);
                            updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                            updateDsMap();
                            adressInput.setText("");
                            rentMachinesInput.setText("");
                            unitsInput.setText("");
                        } else{
                            showMessageDialog(centralContainer, "Something went wrong!");
                        }
                    }else if((units < 1) || (machines < 1)){
                        showMessageDialog(centralContainer, "The values you have entered are not valid. Each station should have minimum 1 charging unit and 1 rent machine.");
                    }
                }catch(IllegalArgumentException i){
                    showMessageDialog(centralContainer, "Check your entered values, and retry!");
                }
            }
        });

        //Adding to the panel for edit dockingstation

        updateCombobox(db.getAllDockingStations(), dockingStationIds);
        JButton saveChangesBtn = new JButton("Save changes");
        JButton dsDeleteBtn = new JButton("Delete");
        JTextField bikeCapField = new JTextField(4);
        JLabel editText = new JLabel("Edit docking station");
        editText.setFont(under);
        right.add(editText, "wrap");
        right.add(new JLabel("Choose docking station ID"), "wrap");
        right.add(dockingStationIds, "wrap");
        right.add(new JLabel("Bicycle capacity: "));
        right.add(bikeCapField, "wrap");
        right.add(saveChangesBtn);
        right.add(dsDeleteBtn, "wrap");

        bikeCapField.setText(""+db.showDsBikeCap(Integer.parseInt(dockingStationIds.getSelectedObjects()[0].toString())));

        dockingStationIds.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    int i = Integer.parseInt(e.getItemSelectable().getSelectedObjects()[0].toString());

                    bikeCapField.setText("" + db.showDsBikeCap(i));
                    if(Integer.parseInt(bikeCapField.getText()) == -1){
                        showMessageDialog(centralContainer, "Someone has deleted this station, list refreshed!");
                        updateCombobox(db.getAllDockingStations(), dockingStationIds);
                        updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                        updateDsMap();
                    }
                }
            }
        });

        saveChangesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(dockingStationIds.getSelectedObjects()[0].toString());
                try{
                    int bikes = Integer.parseInt(bikeCapField.getText());
                    if(bikes >= 1) {
                        if (db.editDockingStation(id, bikes)) {
                            showMessageDialog(centralContainer, "Succesfully edited dockingstation " + id + ".");
                            updateCombobox(db.getAllDockingStations(), dockingStationIds);
                            updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                            updateDsMap();
                        } else{
                            showMessageDialog(centralContainer, "Something went wrong. List refreshed!");
                            updateCombobox(db.getAllDockingStations(), dockingStationIds);
                            updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                            updateDsMap();
                        }
                    }else if(bikes < 1){
                        showMessageDialog(centralContainer, "The amount of charging units cannot be fewer than 1.");
                    }
                }catch (IllegalArgumentException ia){
                    showMessageDialog(centralContainer, "Illegal arguments entered. Please enter integer values.");
                }
            }
        });

        dsDeleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(dockingStationIds.getSelectedObjects()[0].toString());
                if(showConfirmDialog(centralContainer, "Are you sure you want to delete?", "WARNING", YES_NO_OPTION) == YES_OPTION) {
                    if (db.deleteDockingStation(id)) {
                        showMessageDialog(centralContainer, "Dockingstation " + id + " was succesfully deleted!");
                        updateCombobox(db.getAllDockingStations(), dockingStationIds);
                        updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                        updateDsMap();
                    }
                    else{
                        showMessageDialog(centralContainer, "Something went wrong, list refreshed!");
                        updateCombobox(db.getAllDockingStations(), dockingStationIds);
                        updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                        updateDsMap();
                    }
                }
                else{
                    showMessageDialog(centralContainer, "Deletion aborted!");
                }
            }
        });

        //Adding to rentmachine panel
        JLabel machineTitle = new JLabel("Add/delete rentmachine");
        machineTitle.setFont(under);
        JButton addMachineBtn = new JButton("Add");
        JButton deleteMachineBtn = new JButton("Delete");
        machineAmount = new JTextField(4);

        machinePanel.add(machineTitle, "wrap");
        machinePanel.add(new JLabel("Docking station ID: "));
        machinePanel.add(dsIdsForMachine, "wrap");
        machinePanel.add(new JLabel("Rentmachine ID:"));
        machinePanel.add(rentmachineIds, "wrap");
        machinePanel.add(new JLabel("Rentmachine amount: "));
        machinePanel.add(machineAmount, "wrap");
        machinePanel.add(addMachineBtn);
        machinePanel.add(deleteMachineBtn);

        dsIdsForMachine.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    int i = Integer.parseInt(dsIdsForMachine.getSelectedObjects()[0].toString());
                    updateCombobox(db.getRentmachines(i), rentmachineIds);
                }
            }
        });

        left.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        right.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        right.add(machinePanel, "bottom, growy, w 300, h 100");
        //Adding the panels to the main panel
        panelAdmDs.add(left);
        panelAdmDs.add(right, "top");
        centralContainer.add(panelAdmDs, "panelAdmDs");

        addMachineBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int amount = Integer.parseInt(machineAmount.getText());
                    int currentDs = Integer.parseInt(dsIdsForMachine.getSelectedObjects()[0].toString());
                    if(amount >= 1) {
                        ArrayList<String> values = db.addRentmachines(currentDs, amount);
                        String result = "";
                        for (int i = 0; i < values.size(); i++) {
                            result += values.get(i) + ", ";
                        }
                        showMessageDialog(centralContainer, "Rentmachine " + result + "have now been added to dockingstation " + currentDs + ".");
                        updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                        updateCombobox(db.getAllDockingStations(), dockingStationIds);
                        updateDsMap();
                    }else if( amount < 1){
                        showMessageDialog(centralContainer, "Please enter a positive value for the amount of machines you want to add.");
                    }
                }catch(Exception a){
                    updateCombobox(db.getAllDockingStations(),dsIdsForMachine);
                    updateCombobox(db.getAllDockingStations(), dockingStationIds);
                    updateDsMap();
                    showMessageDialog(centralContainer, "Something went wrong, lists refreshed. Please try again!");
                }
            }
        });

        deleteMachineBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(rentmachineIds.getSelectedObjects()[0].toString());
                if(showConfirmDialog(centralContainer, "Are you sure you want to delete?", "WARNING", YES_NO_OPTION) == YES_OPTION) {
                    if (db.deleteRentmachine(id)) {
                        showMessageDialog(centralContainer, "Rentmachine " + id + " was succesfully deleted!");
                        updateCombobox(db.getAllDockingStations(), dockingStationIds);
                        updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                        updateDsMap();
                    }
                    else{
                        showMessageDialog(centralContainer, "Something went wrong, list has been refreshed!");
                        updateCombobox(db.getAllDockingStations(), dockingStationIds);
                        updateCombobox(db.getAllDockingStations(), dsIdsForMachine);
                        updateDsMap();
                    }
                }
                else{
                    showMessageDialog(centralContainer, "Deletion aborted!");
                }
            }
        });

    }

    private void createPanelShowDockStatus(){
        DecimalFormat df = new DecimalFormat("#.##");

        panelShowDockStat.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelShowDockStat);
        JLabel header = new JLabel("Show docking station status");
        header.setFont(main);
        panelShowDockStat.add(header, "span, center, wrap");
        JLabel header2 = new JLabel("Select docking station number");
        header2.setFont(under);
        panelShowDockStat.add(header2, "span, center, wrap");
        panelShowDockStat.add(statusDockingStationIds, "growx, wrap");
        panelShowDockStat.add(new JLabel("Name: "),"growx");
        JLabel adress = new JLabel();
        panelShowDockStat.add(adress, "right, wrap");
        panelShowDockStat.add(new JLabel("Bicycle charging capacity: "), "growx");
        JLabel cap = new JLabel();
        panelShowDockStat.add(cap, "right, wrap");
        panelShowDockStat.add(new JLabel("Amount of bicycles currently charging: "), "growx");
        JLabel currentlyIn = new JLabel();
        panelShowDockStat.add(currentlyIn, "right, wrap");
        panelShowDockStat.add(new JLabel("Bicycle id of those at the station: "), "growx");
        JLabel bikeIds = new JLabel();
        panelShowDockStat.add(bikeIds, "right, wrap");
        panelShowDockStat.add(new JLabel("Power usage for charging:  "), "growx");
        JLabel powerUsage = new JLabel();
        panelShowDockStat.add(powerUsage, "right, wrap");
        centralContainer.add(panelShowDockStat, "panelShowDockStatus");
        panelShowDockStat.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        statusDockingStationIds.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    //Needs to check everything is ok with the selected docking station. If -1 or "-1" is returned for one of the methods, something has gone wrong or the docking station was deleted. In the extremely rare case of the docking station being deleted AFTER this check, the old values are still stored in these variables and will be shown.
                    int i = Integer.parseInt((e.getItemSelectable().getSelectedObjects()[0]).toString());
                    String foundAdress = db.getDockingStationAdress(i);
                    int bCap = db.showDsBikeCap(i);
                    int in = db.getAmountBikesAtStation(i);
                    ArrayList<Integer> bikes = db.getBikesIdsAtStation(i);
                    double pCap = db.showDsPowerCap(i);
                    if(!foundAdress.equals("-1") && bCap!=-1 && in!=-1 && bikes.get(0)!=-1 && pCap!=-1){
                        adress.setText("" + foundAdress);
                        cap.setText("" + bCap + " units");
                        currentlyIn.setText("" + in + " bicyles");
                        String idList = "";
                        if(!(bikes.get(0)==0)){
                            for(int k=0; k<bikes.size(); k++){
                                if(k<(bikes.size())-1){
                                    idList+= bikes.get(k) + ", ";
                                }
                                else{
                                    idList+=bikes.get(k);
                                }
                            }
                        }
                        else{
                            idList = "Currently no bicycles at this station.";
                        }

                        bikeIds.setText(idList);
                        powerUsage.setText("" + df.format(pCap) + " kWh");
                    }
                    else {
                        showMessageDialog(null, "Something went wrong. Maybe the docking station was deleted? List is now refreshed.");
                        updateCombobox(db.getAllDockingStations(), statusDockingStationIds);
                    }
                }
            }
        });
    }

    private void createPanelShowMap (){
        panelShowMap.setLayout(new MigLayout());
        setCentralColor(panelShowMap);

        ArcGISTiledMapServiceLayer tiledLayer = new ArcGISTiledMapServiceLayer(
                "http://services.arcgisonline.com/ArcGIS/rest/services/ESRI_StreetMap_World_2D/MapServer");
        LayerList layers = bikeAndDsMap.getLayers();
        layers.add(tiledLayer);
        bikeAndDsMap.setPreferredSize(new Dimension(1080, 680));
        bikeAndDsMap.setExtent(new Envelope(10.37980, 63.42995, 10.41080, 63.43010)); //To make it open on Trondheim
        panelShowMap.add(bikeAndDsMap);
        layers.add(graphicsL);
        updateBikeAndDsMap(); //Updating markers in map


        panelShowMap.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        centralContainer.add(panelShowMap, "panelShowMap");
    }

    private void createPanelStats (){
        panelStats.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelStats);
        panelStats.add(new JLabel("Statistics"), "span, center, wrap");
        panelStats.add(new JLabel("Choose the statistics you want to view: "), "wrap");
        JButton bikePurchase = new JButton("Bike purchase");
        bikePurchase.addActionListener(createActionListener("panelStatsBikePurchase"));
        bikePurchase.addActionListener(createActionListenerb("panelBottomGoBack"));
        bikePurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanelBikePurchase();
                currentPage.setText(constTxt + "Main menu → Show statistical information → Bicycle purchases");
            }
        });
        JButton repairs = new JButton("Repairs");
        repairs.addActionListener(createActionListener("panelStatsRepair"));
        repairs.addActionListener(createActionListenerb("panelBottomGoBack"));
        repairs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanelRepairsStats();
                currentPage.setText(constTxt + "Main menu → Show statistical information → Repair costs");
            }
        });
        JButton powerUsage = new JButton("Power usage");
        powerUsage.addActionListener(createActionListener("panelStatsPowerUsage"));
        powerUsage.addActionListener(createActionListenerb("panelBottomGoBack"));
        powerUsage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanelPowerUsage();
                currentPage.setText(constTxt + "Main menu → Show statistical information → Power usage");
            }
        });
        JButton bicycleUsage = new JButton("Bicycle usage");
        bicycleUsage.addActionListener(createActionListener("panelStatsBikeUsage"));
        bicycleUsage.addActionListener(createActionListenerb("panelBottomGoBack"));
        bicycleUsage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanelBikeUsage();
                currentPage.setText(constTxt + "Main menu → Show statistical information → Bike usage");
            }
        });
        JButton typeStats = new JButton("Type statistics");
        typeStats.addActionListener(createActionListener("panelTypeStats"));
        typeStats.addActionListener(createActionListenerb("panelBottomGoBack"));
        typeStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanelTypeStats();
                currentPage.setText(constTxt + "Main menu → Show statistical information → Type statistics");
            }
        });
        panelStats.add(bikePurchase, "growx, wrap");
        panelStats.add(repairs, "growx, wrap");
        panelStats.add(powerUsage, "growx, wrap");
        panelStats.add(bicycleUsage, "growx, wrap");
        panelStats.add(typeStats, "growx, wrap");
        panelStats.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        centralContainer.add(panelStats, "panelStats");
    }

    private void createPanelEndTrip(){
        panelEndTrip.setLayout(new MigLayout());
        setCentralColor(panelEndTrip);
        JLabel endTripTitle = new JLabel("End a bicycle trip");
        JButton endTripOkBtn = new JButton("OK");
        endTripTitle.setFont(under);
        panelEndTrip.add(endTripTitle, "wrap");
        panelEndTrip.add(new JLabel("Choose bike: "));
        panelEndTrip.add(endTripBikeIds, "wrap");
        panelEndTrip.add(new JLabel("Choose station: "));
        panelEndTrip.add(endTripStationIds, "wrap");
        panelEndTrip.add(new JLabel("Datetime: "));
        panelEndTrip.add(endTripDateTime);
        panelEndTrip.add(new JLabel("(example: 2018-06-21 18:23:10)"), "wrap");
        panelEndTrip.add(endTripOkBtn);

        endTripOkBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int bikeId = Integer.parseInt(endTripBikeIds.getSelectedObjects()[0].toString());
                    int dsId = Integer.parseInt(endTripStationIds.getSelectedObjects()[0].toString());
                    String date = endTripDateTime.getText();
                    boolean added = db.endTrip(bikeId, date, dsId);
                    if (added) {
                        double[] stationPos = db.getStationPosition(dsId);
                        db.positionUpdater(bikeId, stationPos[0], stationPos[1]);
                        showMessageDialog(panelRegBike, "Trip ended successfully");
                    } else {
                        showMessageDialog(panelRegBike, "Something went wrong, trip not ended");
                    }
                }catch(IllegalArgumentException ie){
                    showMessageDialog(panelRegBike, "Illegal arguments entered. Please check entered values, and retry.");
                }
                updateCombobox(db.getAllBikesOnTrip(), endTripBikeIds);
                updateCombobox(db.getAllDockingStations(), endTripStationIds);
                endTripDateTime.setText("");
            }
        });
        centralContainer.add(panelEndTrip, "panelEndTrip");
    }

    private void createPanelBikePurchase(){
        //Makes sure old version is always updated.
        panelBikePurchase = new JPanel();
        centralContainer.remove(panelBikePurchase);

        setCentralColor(panelBikePurchase);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] data = getDatasetsBikePurchase();

        if (data==null){
            showMessageDialog(null, "Something went wrong when loading the chart.");
        }
        else {
            //Must find the span of years
            //First year
            String firstYear = data[0];
            int firstYearInt = Integer.parseInt(firstYear);
            //Last year
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);

            //Two dimensional array with data
            String[][] completeData = new String[(year-firstYearInt)+1][2];
            int amount = 0;
            for(int i=0; i<=year-firstYearInt; i++){
                completeData[i][0] = Integer.toString(Integer.parseInt(firstYear)+i);
                for(int h=0; h<data.length; h++){
                    if(completeData[i][0].equals(data[h])){
                        amount++;
                    }
                }
                completeData[i][1] = Integer.toString(amount);
                amount=0;
            }

            for (int i=0; i <= year-firstYearInt; i++){
                dataset.addValue(Integer.parseInt(completeData[i][1]), "Bike purchases", completeData[i][0]);
            }
            //Creating the line chart
            JFreeChart bpChart = ChartFactory.createLineChart(
                    "Bicycle purchases per year", // Chart title
                    "Date", // X-Axis Label
                    "Number of purchases", // Y-Axis Label
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            bpChart.setBackgroundPaint(new Color(239, 255, 229));
            CategoryPlot plot = (CategoryPlot) bpChart.getPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setDomainGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(239, 255, 229));

            ChartPanel chartPanel = new ChartPanel(bpChart, false);
            chartPanel.setPreferredSize(new Dimension(1000, 500));
            panelBikePurchase.add(chartPanel);
            centralContainer.add(panelBikePurchase, "panelStatsBikePurchase");
        }
    }

    private String[] getDatasetsBikePurchase(){
        String[] dateStringYear = null;
        String[] datesString;
        try{
            ArrayList<String> allBikes = db.getAllBikes();
            Date[] datesDate = new Date[allBikes.size()];
            datesString = new String[allBikes.size()];
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //Putting all dates into an Date array to sort more easily. If getting a date were to fail, "-1" will be returned. This will give a ParseExeption, which will return null as a signal that something went wrong.
            for (int i=0; i<allBikes.size(); i++){
                datesDate[i]=format.parse(db.getBikeRegdate(Integer.parseInt(allBikes.get(i))));
            }
            //Sorting the dates, and putting them back into a String array
            Arrays.sort(datesDate);
            for(int i=0; i<datesDate.length; i++){
                datesString[i]=format.format(datesDate[i]);
            }
            dateStringYear = new String[datesString.length];
            for(int i=0; i<datesString.length; i++){
                String[] fullDate = datesString[i].split("-");
                String year = fullDate[0];
                dateStringYear[i]=year;
            }
            return dateStringYear;
        }
        catch(ParseException pe){
            pe.printStackTrace();
            return null;
        }
    }

    private void createPanelPowerUsage(){
        panelPowerUsage = new JPanel();
        centralContainer.remove(panelPowerUsage);

        setCentralColor(panelPowerUsage);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[][] data = getDatasetsPowerUsage();

        if (data==null){
            showMessageDialog(null, "Something went wrong when loading the chart.");
        }
        else{
            for(int i=0; i < data.length; i++){
                dataset.addValue(Double.parseDouble(data[i][0]), data[i][1], data[i][2]);
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Power usage",
                    "Docking stations",
                    "",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            chart.setBackgroundPaint(new Color(239, 255, 229));
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setDomainGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(239, 255, 229));

            ChartPanel chartPanel = new ChartPanel(chart, false);
            chartPanel.setPreferredSize(new Dimension(1000, 500));
            panelPowerUsage.add(chartPanel);
        }

        panelPowerUsage.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        centralContainer.add(panelPowerUsage, "panelStatsPowerUsage");

    }

    private String[][] getDatasetsPowerUsage(){
        try{
            ArrayList<String> allDS = db.getAllDockingStations();
            //Checking if the docking stations were loaded successfully. If not, null is returned.
            if(!allDS.get(0).equals("-1")){
                String[][] dsDataset = new String[allDS.size()][3];
                for (int i = 0; i < db.getAllDockingStations().size(); i++){
                    double kwh = db.showDsPowerCap(Integer.parseInt(allDS.get(i)));
                    //Checking if the kWh value is gotten successfully. If not, null is returned.
                    if(kwh!=-1){
                        dsDataset[i][0] = Double.toString(kwh);
                        dsDataset[i][1] = "kWh";
                        dsDataset[i][2] = "Docking station " + allDS.get(i);
                    }
                    else{
                        return null;
                    }
                }
                return dsDataset;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
        //Null is returned here if getting the docking stations failed.
        return null;
    }

    private void createPanelRepairsStats(){
        //Makes sure panel is always updated.
        panelRepairsStats = new JPanel();
        centralContainer.remove(panelRepairsStats);
        setCentralColor(panelRepairsStats);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int[] years = getRepairDataset();

        if(years==null){
            showMessageDialog(null, "Something went wrong when loading the chart.");
        }
        else {

            double[] cost = new double[years.length];

            //Now we add all the values to price...
            for(int i=0; i<cost.length; i++){
                cost[i]=db.getRepairCost(years[i]);
            }

            for (int i=0; i<years.length; i++){
                dataset.addValue(cost[i], "Cost", Integer.toString(years[i]));
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Repair costs",
                    "Year",
                    "",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            chart.setBackgroundPaint(new Color(239, 255, 229));
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setDomainGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(239, 255, 229));

            ChartPanel chartPanel = new ChartPanel(chart, false);
            chartPanel.setPreferredSize(new Dimension(1000, 500));
            panelRepairsStats.add(chartPanel);

        }


        centralContainer.add(panelRepairsStats, "panelStatsRepair");

    }

    private int[] getRepairDataset(){
        int[] dateSorter = null;
        try{
            //Getting all repair IDs
            ArrayList<Integer> allRepairs = db.getAllRepairIds();

            //Creating an ArrayList of Date, as length of different years are not yet known
            ArrayList<Integer> sentDates = new ArrayList<>();

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            //We need to add all but null values. We also only need the year, so day and month is removed.
            for (int i=0; i<allRepairs.size(); i++){
                Date date = db.getSentDate(allRepairs.get(i));
                if(date!=null) {
                    String[] splitted = format.format(date).split("-");
                    sentDates.add(Integer.parseInt(splitted[0]));
                }
            }

            //Now we need to put these values into an array to easily sort them.
            dateSorter = new int[sentDates.size()];
            for(int i=0; i<dateSorter.length; i++){
                dateSorter[i] = sentDates.get(i);
            }

            //...But first, we remove the duplicates by using set. This may cause disorder, but we are sorting anyways...
            //Duplicates are removed when put into a set.
            Set<Integer> set = new HashSet<Integer>();
            for (int i=0; i<dateSorter.length; i++){
                set.add(dateSorter[i]);
            }
            //Now adding them back to the array
            Iterator it = set.iterator();
            dateSorter = new int[set.size()];
            for(int i=0; i< dateSorter.length; i++){
                dateSorter[i]=(Integer)it.next();
            }

            //Sorting array...
            Arrays.sort(dateSorter);

            return dateSorter;
        }
        catch(Exception e){
            e.printStackTrace();
            return dateSorter;
        }
    }

    private void createPanelBikeUsage(){
        //Makes sure panel is always updated
        panelBikeUsage = new JPanel();
        centralContainer.remove(panelBikeUsage);

        ChartPanel chartPanel;
        JFreeChart chart;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        //Sets color of panel
        setCentralColor(panelBikeUsage);

        //Updates combobox first time panel is created.
        updateCombobox(db.getAllBikes(), bikesBikeUsage);

        int bike = Integer.parseInt(bikesBikeUsage.getItemAt(0).toString());

        //Adds the combobox and label
        JLabel currentBike = new JLabel("Select what bike to show statistics about: ");
        panelBikeUsage.add(currentBike);
        panelBikeUsage.add(bikesBikeUsage);

        int foundTrips = db.getBikeTrips(bike);
        double foundKms = db.getBikeKm(bike);

        //Needs to check everything is ok with the selected bike. If -1 or "-1" is returned for one of the methods, something has gone wrong or the bike was deleted. In the extremely rare case of the bike being deleted AFTER this check, the old values are still stored in these variables and will be shown.
        if(foundTrips!=-1 && foundKms!=-1){
            dataset.addValue(foundTrips, "Trips", "Bike " + bike);
            dataset.addValue(foundKms, "Km", "Bike " + bike);
        }
        else {
            showMessageDialog(null,"Something went wrong when loading the statistics.");
        }

        //Chart is made. If something went wrong, an empty chart is viewed.
        chart = ChartFactory.createBarChart(
                "Bicycle usage",
                "Bikes",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        //More chart settings.
        chart.setBackgroundPaint(new Color(239, 255, 229));

        //Plot settings (chart)
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(239, 255, 229));

        //Chart panel settings
        chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 550));

        //Adding the chart panel to main panel
        panelBikeUsage.add(chartPanel);
        panelBikeUsage.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        //Adding to container
        centralContainer.add(panelBikeUsage, "panelStatsBikeUsage");

        //Listener to the combobox.
        bikesBikeUsage.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){

                    //Needs to check everything is ok with the selected bike. If -1 or "-1" is returned for one of the methods, something has gone wrong or the bike was deleted. In the extremely rare case of the bike being deleted AFTER this check, the old values are still stored in these variables and will be used.
                    int bikeId = Integer.parseInt(e.getItemSelectable().getSelectedObjects()[0].toString());

                    int trips = db.getBikeTrips(bikeId);
                    double km = db.getBikeKm(bikeId);
                    double kmPerTrip  = km/trips;

                    if(trips!=-1 && km!=-1){
                        //Updates the dataset
                        dataset.clear();
                        dataset.addValue(trips, "Trips", "Bike " + bikeId);
                        dataset.addValue(km, "Km", "Bike " + bikeId);
                        dataset.addValue(kmPerTrip, "Km per trip", "Bike " + bikeId);
                    }
                    else{
                        showMessageDialog(null, "Something went wrong when loading the data.");
                    }
                }
            }
        });
    }

    private void createPanelTypeStats(){
        //Makes sure old panel is kept updated.
        panelTypeStats = new JPanel();
        centralContainer.remove(panelTypeStats);

        setCentralColor(panelTypeStats);
        DefaultPieDataset dataset = new DefaultPieDataset();

        ArrayList<String> allTypes = db.getAllTypes();

        if(allTypes.get(0).equals("-1")){
            showMessageDialog(null, "Something went wrong when loading the data.");
        }
        else{
            for(int i=0; i< allTypes.size(); i++){
                dataset.setValue(allTypes.get(i), db.getTypeAmount(allTypes.get(i)));
            }
        }

        //If dataset is unsuccessful, there will just be empty in the panel
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Types", // chart title
                dataset, // data
                true, // include legend
                true,
                false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);

        chart.setBackgroundPaint(new Color(239, 255, 229));
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(700, 470));
        setCentralColor(chartPanel);

        panelTypeStats.add(chartPanel);
        centralContainer.add(panelTypeStats, "panelTypeStats");
    }

    private void createPanelPropVal(){
        panelPropVal.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelPropVal);

        JLabel header = new JLabel("Properties");
        header.setFont(main);
        panelPropVal.add(header, "span, center, wrap");

        JPanel left = new JPanel(new MigLayout());
        setCentralColor(left);
        JLabel depositHead = new JLabel("Enter the new deposit value: ");
        depositHead.setFont(under);
        left.add(depositHead, "w 150, wrap");
        left.add(currentDep, "wrap");
        newDep = new JTextField();
        left.add(newDep, "growx");
        left.add(new JLabel("NOK"), "wrap");
        JButton enterDep = new JButton("Enter");
        left.add(enterDep);

        JPanel right = new JPanel(new MigLayout("right"));
        setCentralColor(right);
        JLabel timeHead = new JLabel("Enter the new time value: ");
        timeHead.setFont(under);
        right.add(timeHead, "w 150, wrap");
        right.add(currentTime, "wrap");
        newTime = new JTextField();
        right.add(newTime, "growx");
        right.add(new JLabel("Seconds"), "wrap");
        JButton enterTime = new JButton("Enter");
        right.add(enterTime);

        enterDep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    double depValue = Double.parseDouble(newDep.getText());
                    if (depValue>=0){
                        if(db.changeDeposit(depValue)){
                            newDep.setText("");
                            showMessageDialog(null, "You sucessfully changed the deposit value.");
                            currentDep.setText("Current deposit value: " + Double.toString(db.getDeposit()) + " NOK");
                        }
                        else {
                            showMessageDialog(null, "Something went wrong, value not changed.");
                        }
                    }
                    else{
                        showMessageDialog(null, "You can't enter a negative deposit value.");
                    }
                }
                catch(IllegalArgumentException ie){
                    ie.printStackTrace();
                    showMessageDialog(null,"Please enter a valid value.");
                }

            }
        });
        enterTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int timeValue =Integer.parseInt(newTime.getText());
                    if(timeValue>0){
                        if(db.changeMapUpdateTime(timeValue)){
                            newTime.setText("");
                            showMessageDialog(null, "You sucessfully changed the map update time value.");
                            currentTime.setText("Current map update time value: " + Integer.toString(db.getMapUpdateTime()) + " seconds");
                        }
                        else {
                            showMessageDialog(null, "Something went wrong, value not changed.");
                        }
                    }
                    else{
                        showMessageDialog(null,"You can't enter a value less or equal to zero. ");
                    }

                }
                catch(IllegalArgumentException ie){
                    ie.printStackTrace();
                    showMessageDialog(null,"Please enter a valid value.");
                }
            }
        });

        panelPropVal.add(left, "w 300, left");
        panelPropVal.add(right, "w 300, right");
        panelPropVal.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));
        centralContainer.add(panelPropVal, "panelPropVal");
    }

    private void createPanelAdmMakes(){
        //Importing needed arrays
        ArrayList<String> allMakes = db.getAllMakes();
        updateCombobox(allMakes, deleteMakes);
        updateCombobox(allMakes, editMakes);

        setCentralColor(panelAdmMakes);
        panelAdmMakes.setLayout(new MigLayout("align 50%"));
        setCentralColor(panelPropVal);
        JLabel makesHead = new JLabel("Administer makes:");
        makesHead.setFont(main);
        panelAdmMakes.add(makesHead, "span, center, wrap");

        //Add
        JPanel left = new JPanel(new MigLayout("wrap"));
        setCentralColor(left);
        JLabel addHead = new JLabel("Add make");
        addHead.setFont(under);
        left.add(addHead, "w 150!");
        name = new JTextField();
        left.add(name, "growx");
        date1 = new JTextField();
        left.add(date1, "growx");

        //Delete
        JPanel central = new JPanel(new MigLayout("wrap"));
        setCentralColor(central);
        JLabel deleteHead = new JLabel("Delete make");
        deleteHead.setFont(under);
        central.add(deleteHead, "w 150!");
        central.add(deleteMakes, "growx");

        //Edit
        JPanel right = new JPanel(new MigLayout("wrap"));
        setCentralColor(right);
        JLabel editHead = new JLabel("Edit make");
        editHead.setFont(under);
        right.add(editHead, "w 150!");
        right.add(editMakes, "growx");
        JTextField date2 = new JTextField(db.getMakeDate(editMakes.getSelectedItem().toString()));
        right.add(date2, "growx");

        //Listener to the combobox
        editMakes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    //Needs to check everything is ok with the selected make. If "-1" is returned the method, something has gone wrong or the make. In the extremely rare case of the make being deleted AFTER this initiation, the old values are still stored in these variables and will be shown. The user will them get an error when he clicks "save".
                    String make = (e.getItemSelectable().getSelectedObjects()[0]).toString();
                    String makeDate = db.getMakeDate(make);

                    if(!makeDate.equals("-1")){
                        date2.setText(makeDate);
                    }
                    else{
                        showMessageDialog(null, "Something went wrong.");
                        updateCombobox(db.getAllMakes(), editMakes);
                    }
                }
            }
        });

        //Adding all the actionlisteners
        //Adder
        JButton adder = new JButton("Add");
        left.add(adder, "w 100");
        adder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String newName = name.getText().toString();
                    if (db.addMake(newName, date1.getText().toString()).equals(newName)) {
                        showMessageDialog(null, "You have successfully added " + newName + ".");
                    } else {
                        showMessageDialog(null, "Something went wrong, make not added.");
                    }
                }catch (IllegalArgumentException ia){
                    showMessageDialog(null, "Illegal arguments entered. Please legal date");
                }
                ArrayList<String> allMakes = db.getAllMakes();
                updateCombobox(allMakes, deleteMakes);
                updateCombobox(allMakes, editMakes);
            }
        });

        //Delete
        JButton delete = new JButton("Delete");
        central.add(delete, "w 100");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deleteName = deleteMakes.getSelectedItem().toString();
                if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete make : " + deleteName + "?", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    boolean ed = db.deleteMake(deleteName);
                    if (ed) {
                        showMessageDialog(null,"You have successfully deleted " + deleteName + ".");
                    } else {
                        showMessageDialog(null, "Make did not get deleted");
                    }
                } else {
                    showMessageDialog(null, "Make not deleted");
                }
                /*We need to think of use by multiple users, an error can come from someone trying to do something to a make name that
                         does no longer exist so if that happens we update the comboboxes to make sure it doesnt happen again*/
                ArrayList<String> allMakes = db.getAllMakes();
                updateCombobox(allMakes, deleteMakes);
                updateCombobox(allMakes, editMakes);
            }
        });

        //Saver
        JButton saver = new JButton("Save changes");
        right.add(saver, "w 100");
        saver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//when you change this add illegal argument exception!!!!
                try {
                    String id = editMakes.getSelectedItem().toString();
                    String newDate = date2.getText().toString();
                    if (db.editMakes(newDate, id)) {
                        showMessageDialog(null, "You successfully changed " + id + "'s establish date to " + newDate + ".");
                    }
                    else {
                        showMessageDialog(null, "Something went wrong, changes not saved.");
                    }
                } catch(IllegalArgumentException ife){
                    showMessageDialog(null, "Could not be saved, illegal date input.");
                }
                ArrayList<String> allMakes = db.getAllMakes();
                updateCombobox(allMakes, deleteMakes);
                updateCombobox(allMakes, editMakes);
            }
        });
        panelAdmMakes.add(left, "top");
        panelAdmMakes.add(central, "top");
        panelAdmMakes.add(right, "top");

        centralContainer.add(panelAdmMakes, "panelAdmMakes");
    }

    private void createBottom1Panel(){
        //Adding to bottom ver. 1
        setBottomColor(panelBottom1);
        JButton buttonMenu = new JButton("Back to main menu");
        buttonMenu.addActionListener(createActionListener("panelMenu"));
        buttonMenu.addActionListener(createActionListenerb("panelBottomLogout"));
        buttonMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu");
                if(threadUpdate != null) {
                    System.out.println("threadUpdate canceled");
                    threadUpdate.cancel(true);//can cancel this even though we havent started thread, easier then checking which panel we are on every time
                }
            }
        });
        panelBottom1.add(buttonMenu);
        buttonMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomContainer.add(panelBottom1, "panelBottomMainMenu");
    }

    private void createBottom2Panel(){
        setBottomColor(panelBottom2);
        JButton buttonLogout = new JButton("Log out");
        buttonLogout.addActionListener(createActionListener("panelLogin"));
        buttonLogout.addActionListener(createActionListenerb("panelBottomLogin"));
        buttonLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Login menu");
                userName = ""; //Clear the username when logging out
            }
        });
        panelBottom2.add(buttonLogout);
        bottomContainer.add(panelBottom2, "panelBottomLogout");
    }

    private void createBottom3Panel(){
        setBottomColor(panelBottom3);
        bottomContainer.add(panelBottom3, "panelBottomLogin");
    }

    private void createGoBackBottom(){
        setBottomColor(panelBottomGoBack);
        JButton goBack = new JButton("Go back");
        goBack.addActionListener(createActionListener("panelStats"));
        goBack.addActionListener(createActionListenerb("panelBottomMainMenu"));
        goBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage.setText(constTxt + "Main menu → Show statistical information");
            }
        });
        panelBottomGoBack.add(goBack);
        bottomContainer.add(panelBottomGoBack, "panelBottomGoBack");
    }

    private ActionListener createActionListener(String constraint){
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centralContainer, constraint);
            }
        };
        return al;
    }

    private ActionListener createActionListenerb(String constraint){
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl2.show(bottomContainer, constraint);
            }
        };
        return al;
    }

    private void updateCombobox(ArrayList<String> list, JComboBox box){
        box.removeAllItems();
        for (int i = 0; i < list.size(); i++){
            box.addItem(list.get(i));
        }
    }

    private void updateBikeAndDsMap(){
        bikeAndDsMap.removeAllMarkerGraphics();
        graphicsL.removeAll();

        for(int i = 0; i < db.getAllBikes().size(); i++){
            int bikeId = Integer.parseInt(db.getAllBikes().get(i));
            try {
                double[] bikeCoords = db.getBikePosition(bikeId);
                if(bikeCoords[0] != 0 && bikeCoords[1] != 0) {
                    com.esri.core.geometry.Point bikePoint = new com.esri.core.geometry.Point(bikeCoords[1], bikeCoords[0]);
                    TextSymbol description = new TextSymbol(7,Integer.toString(bikeId), Color.WHITE);
                    graphicsL.addGraphic(new Graphic(bikePoint, redCircle));
                    graphicsL.addGraphic(new Graphic(bikePoint, description));

                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        for(int j = 0; j < db.getAllDockingStations().size(); j++){
            int dockingId = Integer.parseInt(db.getAllDockingStations().get(j));
            try {
                double[] dsCoords = db.getStationPosition(dockingId);
                if(dsCoords[0] != 0 && dsCoords[1] != 0) {
                    bikeAndDsMap.addMarkerGraphic(dsCoords[0], dsCoords[1], "Dockingstation " + dockingId, "Docked bikes: " + db.getAmountBikesAtStation(dockingId));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateDsMap(){
        admDsMap.removeAllMarkerGraphics();
        for(int j = 0; j < db.getAllDockingStations().size(); j++){
            int dockingId = Integer.parseInt(db.getAllDockingStations().get(j));
            try {
                double[] dsCoords = db.getStationPosition(dockingId);
                if(dsCoords[0] != 0 && dsCoords[1] != 0) {
                    admDsMap.addMarkerGraphic(dsCoords[0], dsCoords[1], "Dockingstation " + dockingId, "Docked bikes: " + db.getAmountBikesAtStation(dockingId));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateStatusMap(){
        map.removeAllMarkerGraphics();
        int bikeId = Integer.parseInt(bikeNrsStat.getSelectedItem().toString());
        try {
            double[] bikeCoords = db.getBikePosition(bikeId);
            if(bikeCoords[0] != 0 && bikeCoords[1] != 0) {
                map.addMarkerGraphic(bikeCoords[0], bikeCoords[1], "Bicycle " + bikeId, "" );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setCentralColor(JPanel panel){
        panel.setBackground(new Color(239, 255, 229));
    }

    private void setBottomColor(JPanel panel){
        panel.setBackground(new Color(75, 127, 44));
    }

    private Runnable updateMapRunnable = new Runnable() {
        public void run() {
            updateBikeAndDsMap();
            System.out.println("Main map updated!");
        }
    };

    private Runnable updateStatusMapRunnable = new Runnable() {
        @Override
        public void run() {
            updateStatusMap();
            System.out.println("Bicycle map updated!");
        }
    };

    private static boolean isValidEmailAddress(String email) { //missing testing
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private Runnable antiIdle = new Runnable() {
        public void run() {
            db.getMapUpdateTime();// This is to stop the database idle login system to log us out so fast
            System.out.println("antiIdle");
        }
    };
}
