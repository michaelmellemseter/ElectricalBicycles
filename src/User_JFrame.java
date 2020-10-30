/*
A class made to maintain the  application that will run on the
machines available at the different dockingstations in the city. Consists of two
buttons that will redirect the user to other pages based on choice.

@Author Team 09
created on 2018-04-22
 */

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static javax.swing.JOptionPane.*;

public class User_JFrame extends JFrame{
    //Top panel and the components
    private JPanel header_panel = new JPanel();

    //Bottom panel and the components
    private JPanel bottomPanel = new JPanel();
    private JButton bottomInfoBtn = new JButton("i");
    private JButton bottomHomeBtn = new JButton("Home");
    private JButton bottomBackBtn = new JButton("Back");

    //Global font variable that is used for several components
    private Font mainFont = new Font("Helvetica", Font.BOLD, 22);

    //Global variable to store the ID of the rentmachine running the software
    private int rentMachineId;

    //All the different dynamic panels the user can view
    private JPanel homePanel;
    private JPanel rentPanel;
    private JPanel receiptPanel;
    private JPanel termsPanel;
    private JPanel paymentPanel;
    private JPanel successPanel = new JPanel();
    private JPanel failurePanel;

    //All the names of the pagepanels
    private final String HOME = "Home";
    private final String RENT = "Rent";
    private final String RECEIPT = "Receipt";
    private final String TERMS = "Terms";
    private final String PAYMENT = "Payment";
    private final String SUCCESS = "Success";

    //Global label that changes depending on which page you are on
    private JLabel headerText = new JLabel(HOME);

    //Global values that need to be resetted when rollbacking rent
    private JTextArea rentAddedToCart = new JTextArea("");
    private JLabel rentPrice = new JLabel("");

    //Global JComboboxes that need to be updated when entering rent
    private JComboBox rentTypes = new JComboBox();
    private JComboBox rentAmount = new JComboBox();


    //Global label for successpanel information
    private JTextArea successInfo = new JTextArea("");
    private JLabel termsLabel = new JLabel("");

    //Global values used for successpanel
    private ArrayList<Integer> allBicycleIds = new ArrayList<>();
    private String bicycleIds = "";
    private int rentId;

    //Global components from terms panel
    private JCheckBox termsAccept = new JCheckBox("I accept these terms.");

    //Global components from rent panel
    private JButton rentAddBtn = new JButton("Add");

    //scheduler
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture threadantiIdle = null;



    //The CardLayout used for all panels inside the centralContainer-panel
    private JPanel centralContainer = new JPanel(new CardLayout());
    private CardLayout cl = (CardLayout) centralContainer.getLayout();

    private User_database db;//class with all the functions

    public User_JFrame() throws Exception{
        setRentMachineId();
        this.db = new User_database(3,"com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ", rentMachineId);
        setTitle("Bicycle rental");
        BorderLayout main_layout = new BorderLayout();
        setLayout(main_layout);
        threadantiIdle = executor.scheduleAtFixedRate(antiIdle, 0, 60, TimeUnit.SECONDS);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                System.out.println("Closed");
                System.out.println("threadantiIdle canceled");
                db.disconnect();
                e.getWindow().dispose();
            }
        });
        setSize(800, 600);
        setResizable(false);

        //Adding to the different panels by using methods
        createHeaderPanel();
        createBottomPanel();
        createHomePanel();
        createRentPanel();
        createReceiptPanel();
        createTermsPanel();
        createPaymentPanel();
        createsuccessPanel();

        //Adding the components
        add(header_panel, BorderLayout.NORTH);
        add(centralContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
        setLocationRelativeTo(null); //Used to center the application. NB! Must be added in the end to function
    }

    private void createHeaderPanel(){
        //Adding to header
        header_panel.setLayout(new MigLayout());
        header_panel.setBackground(new Color(75, 127, 44));
        try {
            JLabel bicycleIcon = new JLabel(new ImageIcon("if_vehicles-16_809728.png"));
            header_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            header_panel.add(bicycleIcon);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        headerText.setForeground(new Color(239, 255, 229));
        Font font = new Font("Helvetica", Font.BOLD, 24);
        headerText.setBorder(new EmptyBorder(0, 0, 0, 450));
        headerText.setFont(font);
        header_panel.add(headerText);
    }

    private void createBottomPanel(){
        //Adding to bottom
        bottomPanel.setLayout(new MigLayout());
        bottomPanel.setBackground(new Color(75, 127, 44));
        //Font font = new Font("Helvetica", Font.BOLD + Font.ITALIC, 18);
        bottomInfoBtn.setFont(mainFont);
        bottomHomeBtn.setFont(mainFont);
        bottomBackBtn.setFont(mainFont);
        bottomPanel.add(bottomBackBtn, "gapleft 8");
        bottomPanel.add(bottomHomeBtn, "gapleft 541");
        bottomPanel.add(bottomInfoBtn, "gapleft 1");
        btnDisable(bottomHomeBtn);
        btnDisable(bottomBackBtn);

        bottomBackBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
                if(getCurrentCard() == homePanel){
                    btnDisable(bottomHomeBtn);
                    btnDisable(bottomBackBtn);
                }
            }
        });

        bottomHomeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                termsAccept.setSelected(false);
                btnDisable(bottomHomeBtn);
                btnDisable(bottomBackBtn);
                showCard(HOME);
                db.rerollRent(rentId, allBicycleIds);
                rentId = -1;
                allBicycleIds.clear();
                rentAddedToCart.setText("");
                bicycleIds = "";
                rentPrice.setText("");
            }
        });

        bottomInfoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog(centralContainer, "Press the rent-button if you wish to rent a bicycle.\n" +
                        "If you have docked a rented bicycle, you can receive a receipt for your trip, \nby pressing the " +
                "get-button and inserting the card you used to rent the bicycle.");
            }
        });

    }

    private void createHomePanel(){
        //Making the homePanel and adding it to centralContainer
        homePanel = new JPanel();
        //homePanel.setFont(mainFont);
        setCentralColor(homePanel);
        JLabel homeInfoLabel = new JLabel("What do you want to do?");
        JLabel homeRentText = new JLabel("Rent a bicycle");
        JLabel homeReceiptText = new JLabel("Get receipt");
        JButton homeRentBtn = new JButton("Rent");
        JButton homeReceiptBtn = new JButton("Get");

        homeInfoLabel.setFont(mainFont);
        homeRentText.setFont(mainFont);
        homeReceiptText.setFont(mainFont);
        homeRentBtn.setFont(mainFont);
        homeReceiptBtn.setFont(mainFont);



        homePanel.setLayout(new MigLayout("align 50%"));
        homePanel.add(homeInfoLabel, "gapleft 120, wrap");
        homePanel.add(homeRentText, "gaptop 50");
        homePanel.add(homeReceiptText, "wrap");
        homePanel.add(homeRentBtn, "gapleft 15");
        homePanel.add(homeReceiptBtn, "gapleft 15");


        homeRentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEnable(bottomHomeBtn);
                btnEnable(bottomBackBtn);
                btnEnable(rentAddBtn);
                showCard(RENT);
                rentId = db.updateRentId();
                updateCombobox(db.getAllAvailableTypesBicycles(), rentTypes);
                updateCombobox(db.getAmountBicyclesAvailable(rentTypes.getSelectedObjects()[0].toString()), rentAmount);
            }
        });

        homeReceiptBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEnable(bottomHomeBtn);
                btnEnable(bottomBackBtn);
                showCard(RECEIPT);
                String tempCardToken = showInputDialog("Write in the number of the card you are using(temp)");
                if(db.getReceipt(tempCardToken) == false){
                    btnDisable(bottomBackBtn);
                    btnDisable(bottomHomeBtn);
                    showCard(HOME);
                }
            }
        });
        centralContainer.add(homePanel, HOME);
    }

    private void createRentPanel(){
        //Making the rentPanel
        ArrayList<String> allTypes = new ArrayList<String>();
        ArrayList<Integer> allAmounts = new ArrayList<>();

        rentPanel = new JPanel();
        setCentralColor(rentPanel);
        JLabel rentInfoLabel = new JLabel("What do you want to rent?");
        JLabel rentTypeLabel = new JLabel("Types: ");
        JButton rentCheckoutBtn = new JButton("Checkout");

        JPanel left = new JPanel(new MigLayout());
        JPanel right = new JPanel(new MigLayout());
        setCentralColor(left);
        setCentralColor(right);

        rentInfoLabel.setFont(mainFont);
        rentTypeLabel.setFont(mainFont);
        rentCheckoutBtn.setFont(mainFont);
        rentAddBtn.setFont(mainFont);
        rentTypes.setFont(mainFont);
        rentAmount.setFont(mainFont);

        updateCombobox(db.getAllAvailableTypesBicycles(), rentTypes);
        updateCombobox(db.getAmountBicyclesAvailable(rentTypes.getSelectedObjects()[0].toString()), rentAmount);

        rentPanel.setLayout(new MigLayout());

        left.add(rentInfoLabel, "wrap");
        left.add(rentTypeLabel, "wrap");
        left.add(rentTypes, "growx");
        left.add(rentAmount, "wrap");
        left.add(rentAddBtn, "wrap");
        left.add(rentCheckoutBtn, "gaptop 250, wrap");

        rentPanel.add(left, "w 400, h 500");
        rentPanel.add(right, "w 400, h 500");
        rentAddedToCart.setEditable(false);
        rentAddedToCart.setBackground(new Color(239, 255, 229));
        rentAddedToCart.setSize(400, 400);
        right.add(rentPrice, "wrap");
        right.add(rentAddedToCart);


        rentTypes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    String i = e.getItemSelectable().getSelectedObjects()[0].toString();
                    updateCombobox(db.getAmountBicyclesAvailable(i), rentAmount);

                    //Disables the Add-btn if there aren't more bicycles available
                    if(i.equals("N/A - empty")){
                        btnDisable(rentAddBtn);
                    }
                }
            }
        });

        rentAddBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int newCurrentAmount = amountUpdate(rentAmount);
                String newCurrentType = typeUpdate(rentTypes);
                String type = newCurrentType;
                allTypes.add(type);
                int amount1 = newCurrentAmount;
                allAmounts.add(amount1);

                //adding a bike to the cart so many times the user wants of a type
                for (int i = 0; i < newCurrentAmount; i++) {
                    int bicycleId = db.addToCart(newCurrentType, rentId);
                    allBicycleIds.add(bicycleId);
                }

                double price = db.getPrice(allBicycleIds);
                rentPrice.setText(Double.toString(price) + " NOK pr. hour");

                String oldInfo = rentAddedToCart.getText();
                rentAddedToCart.setText(oldInfo + " " + newCurrentAmount + " " + newCurrentType + "\n ");

                //updates the comboboxes so they are up to date on which bikes and how many that are available
                updateCombobox(db.getAllAvailableTypesBicycles(), rentTypes);
                updateCombobox(db.getAmountBicyclesAvailable(typeUpdate(rentTypes)), rentAmount);
            }

        });

        rentCheckoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allBicycleIds.size() != 0) {
                    String cardToken = showInputDialog("Card token: ");
                    if(!cardToken.equals("")) {
                        db.rent(rentId, cardToken);
                        showCard(TERMS);

                        //Updating the next panel so that the price is up to date
                        double price = db.getPrice(allBicycleIds);
                        Font termsFont = new Font("Helvetica", Font.BOLD, 18);
                        termsLabel = new JLabel("You will be charged " + price + " NOK/hour, and " + db.getDeposit() + " NOK if the " +
                                "bicycle is not returned.");
                        JLabel cardAcceptedLabel = new JLabel("Card accepted.");
                        JButton termsRentBtn = new JButton("Rent");
                        termsLabel.setFont(termsFont);
                        cardAcceptedLabel.setFont(termsFont);
                        termsRentBtn.setFont(termsFont);
                        termsAccept.setFont(termsFont);
                        termsPanel.removeAll();
                        termsPanel.revalidate();
                        termsPanel.repaint();
                        termsPanel.add(cardAcceptedLabel, "wrap");
                        termsPanel.add(termsLabel, "wrap");
                        termsPanel.add(termsAccept, "wrap");
                        termsPanel.add(termsRentBtn);
                        termsRentBtn.setEnabled(false);


                        termsAccept.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if (termsAccept.isSelected()) {
                                    termsRentBtn.setEnabled(true);
                                } else {
                                    termsRentBtn.setEnabled(false);
                                }
                            }
                        });

                        termsRentBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                showCard(PAYMENT);
                            }
                        });
                    }else{
                        showMessageDialog(centralContainer, "Input card token and retry checkout.");
                    }
                }else {
                    showMessageDialog(centralContainer, "You have to add at least one bicycle before checking out.");
                }
            }
        });
        centralContainer.add(rentPanel, RENT);
    }

    private void createReceiptPanel(){
        //Making the receipt panel
        receiptPanel = new JPanel();
        receiptPanel.setLayout(new MigLayout());
        JLabel receiptInfoLabel = new JLabel("Swipe your card to get a receipt.");
        receiptInfoLabel.setFont(mainFont);
        receiptPanel.add(receiptInfoLabel);
        centralContainer.add(receiptPanel, RECEIPT);
    }

    private void createTermsPanel(){
        //Making the terms panel
        termsPanel = new JPanel();
        setCentralColor(termsPanel);
        termsPanel.setLayout(new MigLayout());
        centralContainer.add(termsPanel, TERMS);
    }

    private void createPaymentPanel() {
        paymentPanel = new JPanel();
        paymentPanel.setLayout(new MigLayout("align 50%"));
        JLabel paymentInfoLabel = new JLabel("Insert your card and enter your code.");

        //Input-panel for the code

        JPanel codeInput = new JPanel();
        setCentralColor(paymentPanel);
        codeInput.setLayout(new GridLayout(4, 3));
        JButton btn1 = new JButton("1");
        JButton btn2 = new JButton("2");
        JButton btn3 = new JButton("3");
        JButton btn4 = new JButton("4");
        JButton btn5 = new JButton("5");
        JButton btn6 = new JButton("6");
        JButton btn7 = new JButton("7");
        JButton btn8 = new JButton("8");
        JButton btn9 = new JButton("9");
        JButton btnClear = new JButton("Clear");
        JButton btn0 = new JButton("0");
        JButton btnOk = new JButton("OK");

        paymentInfoLabel.setFont(mainFont);
        btn1.setFont(mainFont);
        btn2.setFont(mainFont);
        btn3.setFont(mainFont);
        btn4.setFont(mainFont);
        btn5.setFont(mainFont);
        btn6.setFont(mainFont);
        btn7.setFont(mainFont);
        btn8.setFont(mainFont);
        btn9.setFont(mainFont);
        btnClear.setFont(mainFont);
        btn0.setFont(mainFont);
        btnOk.setFont(mainFont);

        codeInput.add(btn1);
        codeInput.add(btn2);
        codeInput.add(btn3);
        codeInput.add(btn4);
        codeInput.add(btn5);
        codeInput.add(btn6);
        codeInput.add(btn7);
        codeInput.add(btn8);
        codeInput.add(btn9);
        codeInput.add(btnClear);
        codeInput.add(btn0);
        codeInput.add(btnOk);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < allBicycleIds.size(); i++){
                    bicycleIds += allBicycleIds.get(i) + ", ";
                }
                double price = db.getPrice(allBicycleIds);
                successInfo = new JTextArea("Bicycles you have rented: " + bicycleIds + "\nand price will be " + price + " NOK pr. hour");
                successInfo.setPreferredSize(new Dimension(600, 300));
                successInfo.setBackground(new Color(239, 255, 229));
                successPanel.removeAll();
                successPanel.revalidate();
                successPanel.repaint();
                successPanel.add(successInfo);
                successInfo.setFont(mainFont);
                showCard(SUCCESS);
                btnDisable(bottomBackBtn);
                rentId = -1;
            }
        });
        //Adding the panels
        paymentPanel.add(codeInput);
        centralContainer.add(paymentPanel, PAYMENT);
    }

    //The panel that shows up if the payment was succesful
    private void createsuccessPanel(){
        setCentralColor(successPanel);
        successPanel.setLayout(new MigLayout());
        successInfo.setFont(mainFont);
        successPanel.add(successInfo);
        centralContainer.add(successPanel, SUCCESS);
    }

    //The panel that shows up if the payment failed, but it won't be used because a third party would be responsible for handling payment
    private void createFailurePanel(){
        failurePanel = new JPanel();
        setCentralColor(failurePanel);
        JLabel cardDeniedLabel = new JLabel("Card denied!");
        JLabel failureLabel = new JLabel("Try again.\nMake sure you are using a supported card, and that you are entering the correct code.");
        failurePanel.add(cardDeniedLabel, "wrap");
        failurePanel.add(failureLabel, "wrap");
    }


    //Methods to change the enabled/visibility value of the back and home button
    private void btnDisable(JButton btn) {
        if(btn.isEnabled()){
            btn.setEnabled(false);
            btn.setVisible(false);
        }
    }

    private void btnEnable(JButton btn){
        if(btn.isEnabled() == false){
            btn.setEnabled(true);
            btn.setVisible(true);
        }
    }

    //Method to get the current page you are on
    private JPanel getCurrentCard(){
        JPanel card = null;

        for(Component comp : centralContainer.getComponents()){
            if(comp.isVisible() == true){
                card = (JPanel)comp;
            }
        }
        return card;
    }

    //Method used when changing page
    private void showCard(String name){
        headerText.setText(name);
        cl.show(centralContainer, name);
    }

    //Method used when utilizing the back-button
    private void goBack(){
        if(getCurrentCard() != homePanel && getCurrentCard() != successPanel){
            if(getCurrentCard() == termsPanel){
                termsAccept.setSelected(false);
                showCard(RENT);
            }
            else if(getCurrentCard() == rentPanel){
                showCard(HOME);
                db.rerollRent(rentId, allBicycleIds);
                rentId = -1;
                rentAddedToCart.setText("");
                rentPrice.setText("");
                allBicycleIds.clear();

            }
            else if(getCurrentCard() == receiptPanel){
                showCard(HOME);
            }
            else if(getCurrentCard() == paymentPanel){
                showCard(TERMS);
            }
            else if(getCurrentCard() == termsPanel){
                showCard(RENT);
            }
        }
    }
    private int amountUpdate(JComboBox box){
        return Integer.parseInt(box.getSelectedObjects()[0].toString());
    }

    private String typeUpdate(JComboBox box){
        return box.getSelectedObjects()[0].toString();
    }

    private void updateCombobox(ArrayList<String> list, JComboBox box){
        box.removeAllItems();
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                box.addItem(list.get(i));
            }
        } else {
            box.addItem("N/A - empty");
        }
    }

    //Sets the ID for the rent machine on first-time run
    private void setRentMachineId() throws Exception{
        RentMachines rb = new RentMachines("com.mysql.jdbc.Driver", "jdbc:mysql://mysql.stud.iie.ntnu.no:3306/michame?user=michame&password=Sah9BibQ");
        boolean finished = false;
        while(!finished) {
            try {
                int potentialId = Integer.parseInt(showInputDialog("Enter the ID for this rentmachine."));
                String text = rb.toString(potentialId);
                if(text != "-1"){
                    if (showConfirmDialog(this, rb.toString(potentialId), "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        rentMachineId = potentialId;
                        finished = true;
                    }
                }else{
                    showMessageDialog(this,"this rent machine does not exist");
                }
            }catch(Exception e){
                e.printStackTrace();
                showMessageDialog(this, "Something went wrong, you might have to reboot the software!");
            }
        }
        rb.disconnect();
    }

    private void setCentralColor(JPanel panel){
        panel.setBackground(new Color(239, 255, 229));
    }

    private Runnable antiIdle = new Runnable() {
        public void run() {
            db.getDeposit();// This is to stop the database idle login system to log us out so fast
            System.out.println("antiIdle");
        }
    };
}