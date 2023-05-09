import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class AWT extends Frame {
    JTable StaffTable;
    JTable FilmTable;
    JTable ReportTable;
    JTable NotificationTable;
    JTable CustomerTable;
    JTable RentalTable;
    JTable PaymentTable;
    JTable InventoryTable;
    JTable StoreTable;
    JTable AddressTable;
    JTable CityTable;
    JTable CountryTable;
    JTable LanguageTable;
    JTable CategoryTable;
    JTable FilmCategoryTable;
    JTable FilmActorTable;
    JTable ActorTable;
    JTable FilmTextTable;
    JTable StaffListTable;
    JTable CustomerListTable;
    JTable FilmListTable;
    JTable SalesByFilmCategoryTable;
    JTable SalesByStoreTable;
    JTable SalesByStaffTable;
    JTable SalesByCustomerTable;
    JTable SalesByDateTable;
    JTable SalesByMonthTable;
    JTable SalesByYearTable;
    JTable Sale;
  public AWT(){
    
    Connection conn = this.getConnection();

    JTabbedPane tabbedPane = new JTabbedPane();
        
    // Add the first tab
    JPanel StaffTab = createStaffTab(conn);
    tabbedPane.addTab("Staff Tab", StaffTab);
    
    // Add the second tab
    JPanel ReportTab = createReportTab(conn);
    tabbedPane.addTab("Report Tab", ReportTab);
    
    // Add the third tab
    JPanel NotificationTab = createNotificationsTab(conn);
    tabbedPane.addTab("Notifications Tab", NotificationTab);

    JPanel FilmTab = createFilmTab(conn);
    tabbedPane.addTab("Films Tab", FilmTab);
    
    setSize(700, 600);
    setTitle("Database");
    setVisible(true);
    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);



    ReportTab.addMouseListener(new MouseAdapter() {
    @Override
        public void mouseClicked(MouseEvent e) {
            // Do something when the tab is clicked
            System.out.println("Tab clicked");
            GenerateReport(conn,(DefaultTableModel)ReportTable.getModel());
        }
    });
   
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
            dispose();
        }
    });
  }
  public Connection getConnection() {
    String url = "jdbc:mariadb://localhost:3306/u22491032_u22617541_sakila";
    String username = "root";
    String password = "Manaka4n";
    
    // Load the JDBC driver
    try {
        Class.forName("org.mariadb.jdbc.Driver");
    } catch (ClassNotFoundException ex) {
        System.err.println("Failed to load JDBC driver.");
        ex.printStackTrace();
        return null;
    }
    // Establish the database connection
    Connection conn = null;
    try {
        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Connected to database.");
    } catch (SQLException ex) {
        System.err.println("Failed to connect to database.");
        ex.printStackTrace();
    }
    
    return conn;
}
private JPanel createNotificationsTab(Connection conn){
    JPanel tab1 = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel();
    NotificationTable = new JTable(model);
    tab1.add(new JScrollPane(NotificationTable), BorderLayout.CENTER);

    try{
        String sql = "SELECT * FROM customer ORDER BY last_update DESC LIMIT 10";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        //Adding the columns to the table
        int columnNumber = rsmd.getColumnCount();
        for(int i=1;i<=columnNumber;i++){
            String columnName = rsmd.getColumnName(i);
            model.addColumn(columnName);
        }
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
    JButton DeleteButton = new JButton("Delete Customer info");
    JButton UpdateButton = new JButton("Update Customer info");
    JButton InsertButton = new JButton("Insert Customer info");
    DeleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            DeleteCustomer(conn,model,NotificationTable);
              
        }
    });
    InsertButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            InsertCustomer(conn,model,NotificationTable);
              
        }
    });
    UpdateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UpdateCustomer(conn,model,NotificationTable);
              
        }
    });
    JPanel mainPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
    mainPanel.setLayout(boxLayout);
    
    // Add the button panel to the main panel
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(DeleteButton);
    buttonPanel.add(UpdateButton);
    buttonPanel.add(InsertButton);
    mainPanel.add(buttonPanel);
    
    // Add the filter panel to the main panel
    JPanel filterPanel = new JPanel(new BorderLayout());
    JTextField filter = new JTextField("Type here to filter results");
    filterPanel.add(filter, BorderLayout.CENTER);
    mainPanel.add(filterPanel);
    
    // Add the table to the main panel
    mainPanel.add(new JScrollPane(NotificationTable));
    
    // Add the main panel to the tab
    tab1.add(mainPanel, BorderLayout.CENTER);

    // create a TableRowSorter to filter the table
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
    NotificationTable.setRowSorter(sorter);

    // set the filter when the user types in the text field
    filter.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            String text = filter.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null); // remove filter if empty
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // case-insensitive filter
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            insertUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }
    });
    return tab1; 
}
private void UpdateCustomer(Connection conn,DefaultTableModel model,JTable Table){
    int row = Table.getSelectedRow();
    if(row<0){
        JOptionPane.showMessageDialog(null, "Customer to update not selected", "Error", JOptionPane.ERROR_MESSAGE);
    }else{
        JFrame UpdateCustomer = new JFrame();
        UpdateCustomer.setTitle("Add Customer");
        UpdateCustomer.setSize(600, 600); 
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel NamePanel = new JPanel();
        JTextField Name = new JTextField(45); 
        NamePanel.add(new JLabel("First Name:"));
        NamePanel.add(Name);
        panel.add(NamePanel);
                                              
        JPanel SurnamePanel  = new JPanel();
        JTextField Surname = new JTextField(45); 
        SurnamePanel.add(new JLabel("Last Name:"));
        SurnamePanel.add(Surname);
        panel.add(SurnamePanel);
                                    
        JPanel emailPanel  = new JPanel();
        JTextField Email = new JTextField(50); 
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(Email);
        panel.add(emailPanel);
                                            
        JPanel addressPanel  = new JPanel();
        JTextField Address = new JTextField(50); 
        addressPanel.add(new JLabel("Address:"));
        addressPanel.add(Address);
        panel.add(addressPanel);
        
        JPanel activePanel = new JPanel();
        String[] activeIDs = {"True", "False"}; // Define the list of language IDs
        JComboBox<String> activeBox = new JComboBox<>(activeIDs); // Create a JComboBox with the list of language IDs
        activeBox.setSelectedIndex(0); // Set the default selected item
        activePanel.add(new JLabel("Active:"));
        activePanel.add(activeBox);
        panel.add(activePanel);
        
        String cell = model.getValueAt(row, 0).toString();
        String sql = "UPDATE customer SET first_name = ?, last_name = ?, email = ?, address_id = ?, active = ?, last_update = ? WHERE customer_id = " + cell;
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, Name.getText());
            stmt.setString(2, Surname.getText());
            stmt.setString(3, Email.getText());
            stmt.setString(4, Address.getText());
            stmt.setInt(5, activeBox.getSelectedIndex());
            LocalDateTime now = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(now);
            stmt.setTimestamp(6, timestamp);
            stmt.executeUpdate();
            RefreshNotificationTab(conn,(DefaultTableModel)NotificationTable.getModel());
        }catch(Exception f){
            System.out.println("Error: " + f.getMessage());
        }
    }

    return;
}
private void DeleteCustomer(Connection conn,DefaultTableModel model,JTable Table){
    int row = Table.getSelectedRow();
    if(row<0){
        JOptionPane.showMessageDialog(null, "Customer to delete not selected", "Error", JOptionPane.ERROR_MESSAGE);
    }else{
    String cell = model.getValueAt(row, 0).toString();
        String sql = "DELETE FROM customer WHERE customer_id = " + cell;
        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            RefreshNotificationTab(conn,(DefaultTableModel)NotificationTable.getModel());
        }catch(Exception f){
            System.out.println("Error: " + f.getMessage());
        }
    }
}
private void InsertCustomer(Connection conn,DefaultTableModel model,JTable Table){
        JFrame AddCustomer = new JFrame();
        AddCustomer.setTitle("Add Customer");
        AddCustomer.setSize(600, 600); 
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel StorePanel = new JPanel();
        String[] storeID = {"1", "2"}; // Define the list of language IDs
        JComboBox<String> Store = new JComboBox<>(storeID); // Create a JComboBox with the list of language IDs
        Store.setSelectedIndex(0); // Set the default selected item
        StorePanel.add(new JLabel("Store ID:"));
        StorePanel.add(Store);
        panel.add(StorePanel);

        JPanel NamePanel = new JPanel();
        JTextField Name = new JTextField(45); 
        NamePanel.add(new JLabel("First Name:"));
        NamePanel.add(Name);
        panel.add(NamePanel);
                                              
        JPanel SurnamePanel  = new JPanel();
        JTextField Surname = new JTextField(45); 
        SurnamePanel.add(new JLabel("Last Name:"));
        SurnamePanel.add(Surname);
        panel.add(SurnamePanel);
                                    
        JPanel emailPanel  = new JPanel();
        JTextField Email = new JTextField(50); 
        emailPanel.add(new JLabel("Email:"));
        emailPanel.add(Email);
        panel.add(emailPanel);
                                            
        JPanel addressPanel  = new JPanel();
        JTextField Address = new JTextField(50); 
        addressPanel.add(new JLabel("Address:"));
        addressPanel.add(Address);
        panel.add(addressPanel);
                                 
        JPanel postCodePanel  = new JPanel();
        JTextField PostCode = new JTextField(50); 
        postCodePanel.add(new JLabel("Post Code:"));
        postCodePanel.add(PostCode);
        panel.add(postCodePanel);
                                 
        JPanel DistrictPanel  = new JPanel();
        JTextField District = new JTextField(50); 
        DistrictPanel.add(new JLabel("District:"));
        DistrictPanel.add(District);
        panel.add(DistrictPanel);
        
        JPanel PhonePanel  = new JPanel();
        JTextField Phone = new JTextField(50); 
        PhonePanel.add(new JLabel("Phone Number:"));
        PhonePanel.add(Phone);
        panel.add(PhonePanel);
                                 

        JPanel countryPanel = new JPanel();
        String[] country = getCountry(conn); // Define the list of language IDs
        JComboBox<String> countryBox = new JComboBox<>(country); // Create a JComboBox with the list of language IDs
        countryBox.setSelectedIndex(0); // Set the default selected item
        countryPanel.add(new JLabel("Country:"));
        countryPanel.add(countryBox);
        panel.add(countryPanel);
                                            
        JPanel cityPanel = new JPanel();
        String[] city = getCity(conn); // Define the list of language IDs
        JComboBox<String> cityBox = new JComboBox<>(city); // Create a JComboBox with the list of language IDs
        cityBox.setSelectedIndex(0); // Set the default selected item
        cityPanel.add(new JLabel("City:"));
        cityPanel.add(cityBox);
        panel.add(cityPanel);
        
                                            
                                            
        JPanel activePanel = new JPanel();
        String[] activeIDs = {"True", "False"}; // Define the list of language IDs
        JComboBox<String> activeBox = new JComboBox<>(activeIDs); // Create a JComboBox with the list of language IDs
        activeBox.setSelectedIndex(0); // Set the default selected item
        activePanel.add(new JLabel("Active:"));
        activePanel.add(activeBox);
        panel.add(activePanel);                                 
        
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Current Date and Time: " + now);
        
        
                                              
        
                                        
        
                                              
        
                                              
       
                                              
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Create a panel with FlowLayout
    
        JButton cancelButton = new JButton("Cancel"); // Create a "Cancel" button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute when the "Cancel" button is clicked
                System.out.println("Cancel button clicked!");
                AddCustomer.dispose(); // Close the dialog
            }
        });
        buttonPanel.add(cancelButton); // Add the "Cancel" button to your panel
        
        JButton addButton = new JButton("Add"); // Create an "Add" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //First insert the address    
                try {
                        String sql = "INSERT INTO address (address_id,address, district, city_id, postal_code,phone)"+
                        "VALUES (null, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, Address.getText());
                        stmt.setString(2, District.getText());
                        stmt.setInt(3, cityBox.getSelectedIndex()+1);
                        stmt.setString(4, PostCode.getText());
                        stmt.setString(5, Phone.getText());
                        stmt.executeUpdate();
                    } catch (Exception f) {
                        
                        System.out.println("Error: "+f.getMessage());
                    }
                //Then get the address ID
                int addressID = 0;
                try {
                    String sql = "Select address_id from address ORDER BY address_id DESC LIMIT 1";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        addressID = rs.getInt("address_id");
                    }
                    System.out.println("Address ID: "+addressID);
                } catch (Exception x) {
                    
                    System.out.println("Error: "+x.getMessage());
                }
                //Then insert the customer
                try {
                    String sql = "INSERT INTO customer(customer_id,store_id,first_name,last_name,email,address_id,active,create_date)"+
                        "VALUES (null, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, Store.getSelectedIndex()+1);
                        stmt.setString(2, Name.getText());
                        stmt.setString(3, Surname.getText());
                        stmt.setString(4, Email.getText());
                        stmt.setInt(5, addressID);
                        stmt.setInt(6, activeBox.getSelectedIndex()+1);
                        LocalDateTime now = LocalDateTime.now();
                        Timestamp timestamp = Timestamp.valueOf(now);
                        stmt.setTimestamp(7, timestamp);
                        stmt.executeUpdate();
                } catch (Exception v) {
                    System.out.println("Error: "+v.getMessage());
                }
                AddCustomer.dispose(); // Close the dialog
            }
        });
        
        buttonPanel.add(addButton); // Add the "Add" button to your panel
        panel.add(buttonPanel); // Add the button panel to your main panel // Add the "Add" button to your panel
        
        AddCustomer.add(panel);
        AddCustomer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AddCustomer.setLocationRelativeTo(null);
        AddCustomer.setVisible(true);
    
}
private String[] getCity(Connection conn){
    try{
        Statement stmt = conn.createStatement();
        List<String> cityList = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT city FROM city");
        while (rs.next()) {
            cityList.add(rs.getString("city"));
        }
        String[] city = cityList.stream().toArray(String[]::new);
        return city;
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
    return new String[0];
}
private String[] getCountry(Connection conn){
    try{
        Statement stmt = conn.createStatement();
        List<String> countryList = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT country FROM country");
        while (rs.next()) {
            countryList.add(rs.getString("country"));
        }
        String[] country = countryList.stream().toArray(String[]::new);
        return country;
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
    return new String[0];
}
private void RefreshNotificationTab(Connection conn,DefaultTableModel model){
    model.setRowCount(0);
    try{
        String sql = "SELECT * FROM customer ORDER BY last_update DESC LIMIT 10";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        //Adding the columns to the table
        int columnNumber = rsmd.getColumnCount();
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
}
private JPanel createReportTab(Connection conn){
    JPanel tab1 = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel();
    StaffTable = new JTable(model);
    tab1.add(new JScrollPane(StaffTable), BorderLayout.CENTER);

    try{
        String sql = "SELECT s.store_id, c.name, COUNT(*) AS movie_count "+
                     "FROM inventory i "+
                     "LEFT JOIN film f ON i.film_id = f.film_id "+
                     "LEFT JOIN store s ON i.store_id = s.store_id "+
                     "LEFT JOIN film_category fc ON f.film_id = fc.film_id "+
                     "LEFT JOIN category c ON fc.category_id = c.category_id "+
                     "GROUP BY s.store_id, c.name "+
                     "ORDER BY s.store_id, c.name";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        //Adding the columns to the table
        int columnNumber = rsmd.getColumnCount();
        for(int i=1;i<=columnNumber;i++){
            String columnName = rsmd.getColumnName(i);
            model.addColumn(columnName);
        }
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }

    return tab1; 
}
private void GenerateReport(Connection conn,DefaultTableModel model){
    model.setRowCount(0);
    try{
        String sql = "SELECT s.store_name, g.genre_name, COUNT(*) AS movie_count "+
                     "FROM inventory i "+
                     "LEFT JOIN film f ON i.film_id = f.film_id "+
                     "LEFT JOIN store s ON i.store_id = s.store_id "+
                     "LEFT JOIN film_category fc ON f.film_id = fc.film_id "+
                     "LEFT JOIN category c ON fc.category_id = c.category_id "+
                     "LEFT JOIN genre g ON c.genre_id = g.genre_id "+
                     "GROUP BY s.store_name, g.genre_name "+
                     "ORDER BY s.store_name, g.genre_name ";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnNumber = rsmd.getColumnCount();
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception f){
        System.out.println("Error: " + f.getMessage());
    }

}
private JPanel createFilmTab(Connection conn){
    JPanel tab1 = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel();
    StaffTable = new JTable(model);
    tab1.add(new JScrollPane(StaffTable), BorderLayout.CENTER);
    
    //Retrieve data from the database, Creating a statement and executing it
    try{
        String sql = "SELECT * FROM film ORDER BY film_id DESC LIMIT 10";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        //Adding the columns to the table
        int columnNumber = rsmd.getColumnCount();
        for(int i=1;i<=columnNumber;i++){
            String columnName = rsmd.getColumnName(i);
            model.addColumn(columnName);
        }
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
    JButton addButton = new JButton("Add Data");
    addButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddFilmData(conn,model);
            
        }
    });
    tab1.add(addButton, BorderLayout.SOUTH);
    return tab1;
}
private void RefreshFilmTab(Connection conn,DefaultTableModel model){
    model.setRowCount(0);
    try{
        String sql = "SELECT * FROM film ORDER BY film_id DESC LIMIT 10";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        //Adding the columns to the table
        int columnNumber = rsmd.getColumnCount();
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception c){
        System.out.println("Error: " + c.getMessage());
    }
}








private JPanel createStaffTab(Connection conn){
    JPanel tab1 = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel();
    StaffTable = new JTable(model);
    tab1.add(new JScrollPane(StaffTable), BorderLayout.CENTER);
    
    //Retrieve data from the database, Creating a statement and executing it
    try{
        String sql = "SELECT s.first_name, s.last_name, a.address, a.address2,"+ 
                    "a.district, a.postal_code, a.phone, c.city, s.store_id, s.active FROM staff s"+
                    " LEFT JOIN address a ON s.address_id = a.address_id"+
                    " LEFT JOIN store st ON s.store_id = st.store_id"+
                    " LEFT JOIN city c ON a.city_id = c.city_id";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        //Adding the columns to the table
        int columnNumber = rsmd.getColumnCount();
        for(int i=1;i<=columnNumber;i++){
            String columnName = rsmd.getColumnName(i);
            model.addColumn(columnName);
        }
        while (rs.next()) {
            Object[] row = new Object[columnNumber];
            for(int i=1;i<=columnNumber;i++){
                row[i-1] = rs.getString(i);
            }
            model.addRow(row);
        }
    }catch(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
    
    //Filter implementation
    JPanel filterPanel = new JPanel(new BorderLayout());
    JTextField filter = new JTextField("Type here to filter results");
    filterPanel.add(filter, BorderLayout.CENTER);
    tab1.add(filterPanel, BorderLayout.SOUTH);

    // create a TableRowSorter to filter the table
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
    StaffTable.setRowSorter(sorter);

    // set the filter when the user types in the text field
    filter.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            String text = filter.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null); // remove filter if empty
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // case-insensitive filter
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            insertUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }
    });

    return tab1;
}
private void AddFilmData(Connection conn,DefaultTableModel model){
    JFrame AddMovie = new JFrame();
    AddMovie.setTitle("Add Movie");
    AddMovie.setSize(600, 600); 
    
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
    JPanel TitlePanel = new JPanel();
    JTextField Title = new JTextField(20); 
    TitlePanel.add(new JLabel("Title:"));
    TitlePanel.add(Title);
    panel.add(TitlePanel);
                                          
    JPanel DescriptionPanel  = new JPanel();
    JTextField Description = new JTextField(20); 
    DescriptionPanel.add(new JLabel("Description:"));
    DescriptionPanel.add(Description);
    panel.add(DescriptionPanel);
                                          
    JPanel ReleaseYearpanel = new JPanel();
    SpinnerModel yearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 1900, Calendar.getInstance().get(Calendar.YEAR), 1);
    JSpinner ReleaseYear = new JSpinner(yearModel);
    ReleaseYearpanel.add(new JLabel("Release Year:"));
    ReleaseYearpanel.add(ReleaseYear);
    panel.add(ReleaseYearpanel);
                                    
    JPanel LanguagePanel = new JPanel();
    String[] languageIDs = {"1", "2", "3", "4", "5"}; // Define the list of language IDs
    JComboBox<String> LanguageID = new JComboBox<>(languageIDs); // Create a JComboBox with the list of language IDs
    LanguageID.setSelectedIndex(0); // Set the default selected item
    LanguagePanel.add(new JLabel("LanguageID:"));
    LanguagePanel.add(LanguageID);
    panel.add(LanguagePanel);
                                          
    JPanel OrginalLangPanel = new JPanel();
    Integer[] orgLanguageNumbers = {1, 2, 3, 4, 5}; // Define the list of language numbers
    JComboBox<Integer> OrgLanguage = new JComboBox<>(orgLanguageNumbers); // Create a JComboBox with the list of language numbers
    OrgLanguage.setSelectedIndex(0); // Set the default selected item

    OrginalLangPanel.add(new JLabel("Original Language:"));
    OrginalLangPanel.add(OrgLanguage);
    panel.add(OrginalLangPanel);
                                          
    JPanel Rental_Duration = new JPanel();
    Integer[] rentalDurations = {1, 2, 3, 4, 5}; // Define the list of rental durations
    JComboBox<Integer> RentDuration = new JComboBox<>(rentalDurations); // Create a JComboBox with the list of rental durations
    RentDuration.setSelectedIndex(0); // Set the default selected item

    Rental_Duration.add(new JLabel("Rental Duration:"));
    Rental_Duration.add(RentDuration);
    panel.add(Rental_Duration);
                                          
    JPanel Length = new JPanel();
    Integer[] movieLengths = {90, 120, 150, 180}; // Define the list of movie lengths
    JComboBox<Integer> MovieLength = new JComboBox<>(movieLengths); // Create a JComboBox with the list of movie lengths
    MovieLength.setSelectedIndex(0); // Set the default selected item

    Length.add(new JLabel("Movie Length:"));
    Length.add(MovieLength);
    panel.add(Length);
                                          
    JPanel RentRate = new JPanel();
    NumberFormat Rentformat = NumberFormat.getNumberInstance(); // Define the format for the input value
    JFormattedTextField RentalRate = new JFormattedTextField(Rentformat); // Create a JFormattedTextField with the input format
    RentalRate.setColumns(10); // Set the number of columns for the input field
    RentalRate.setValue(0.0); // Set the default value to 0.0

    RentRate.add(new JLabel("Rental Rate:"));
    RentRate.add(RentalRate);
    panel.add(RentRate);
                                          
    JPanel ReplaceCost = new JPanel();
    NumberFormat format = NumberFormat.getNumberInstance(); // Define the format for the input value
    JFormattedTextField Cost = new JFormattedTextField(format); // Create a JFormattedTextField with the input format
    Cost.setColumns(10); // Set the number of columns for the input field
    Cost.setValue(0.0); // Set the default value to 0.0

    ReplaceCost.add(new JLabel("Replacement Cost:"));
    ReplaceCost.add(Cost);
    panel.add(ReplaceCost);
                                          
    JPanel RatingPanel = new JPanel();
    String[] movieRatings = {"G", "PG", "PG-13", "R", "NC-17"}; // Define the movie ratings as an array of strings
    JComboBox<String> RatingBox = new JComboBox<>(movieRatings); // Create a JComboBox with the movie ratings
    RatingPanel.add(new JLabel("Movie Rating:"));
    RatingPanel.add(RatingBox);
    panel.add(RatingPanel);
                                          
    JPanel FeaturesPanel = new JPanel();
    JCheckBox TrailersCheckBox = new JCheckBox("Trailers");
    JCheckBox CommentariesCheckBox = new JCheckBox("Commentaries");
    JCheckBox DeletedScenesCheckBox = new JCheckBox("Deleted Scenes");
    JCheckBox BehindTheScenesCheckBox = new JCheckBox("Behind the Scenes");
    FeaturesPanel.add(new JLabel("Special Features:"));
    FeaturesPanel.add(TrailersCheckBox);
    FeaturesPanel.add(CommentariesCheckBox);
    FeaturesPanel.add(DeletedScenesCheckBox);
    FeaturesPanel.add(BehindTheScenesCheckBox);
    panel.add(FeaturesPanel);
                                          
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Create a panel with FlowLayout

    JButton cancelButton = new JButton("Cancel"); // Create a "Cancel" button
    cancelButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Code to execute when the "Cancel" button is clicked
            System.out.println("Cancel button clicked!");
            AddMovie.dispose(); // Close the dialog
        }
    });
    buttonPanel.add(cancelButton); // Add the "Cancel" button to your panel
    
    JButton addButton = new JButton("Add"); // Create an "Add" button
    addButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Code to execute when the "Add" button is clicked
            try{
                String sql = "INSERT INTO film (title, description, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features)"+
                             "VALUES (?, ?, ?, ?, null, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, Title.getText());
                stmt.setString(2, Description.getText());
                stmt.setInt(3, (Integer) ReleaseYear.getValue());
                stmt.setInt(4, LanguageID.getSelectedIndex()+1);
                stmt.setInt(5, (Integer) RentDuration.getSelectedIndex()+1);
                stmt.setDouble(6, ((Double) RentalRate.getValue()).doubleValue());
                stmt.setInt(7, (Integer) MovieLength.getSelectedIndex()+1);
                stmt.setDouble(8, ((Double) Cost.getValue()).doubleValue());
                stmt.setString(9, (String) RatingBox.getSelectedItem());
                String features = "";
                if (TrailersCheckBox.isSelected()) {
                    features += "Trailers";
                }
                if (CommentariesCheckBox.isSelected()) {
                    if (!features.isEmpty()) {
                        features += ",";
                    }
                    features += "Commentaries";
                }
                if (DeletedScenesCheckBox.isSelected()) {
                    if (!features.isEmpty()) {
                        features += ",";
                    }
                    features += "Deleted Scenes";
                }
                if (BehindTheScenesCheckBox.isSelected()) {
                    if (!features.isEmpty()) {
                        features += ",";
                    }
                    features += "Behind the Scenes";
                }
                stmt.setString(10, features);
                System.out.println(features);
                stmt.executeQuery();
                RefreshFilmTab(conn, model);
                AddMovie.dispose();
            }catch(Exception d){
                System.out.println("Error:"+d);
            }
            
        }
    });
    buttonPanel.add(addButton); // Add the "Add" button to your panel
    panel.add(buttonPanel); // Add the button panel to your main panel // Add the "Add" button to your panel
    
    AddMovie.add(panel);
    AddMovie.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    AddMovie.setLocationRelativeTo(null);
    AddMovie.setVisible(true);
}

  public static void main(String args[]){
    new AWT();
  }
}