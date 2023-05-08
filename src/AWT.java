import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AWT extends Frame {
    private JTable StaffTable;
  public AWT(){
    
    Connection conn = this.getConnection();

    JTabbedPane tabbedPane = new JTabbedPane();
        
    // Add the first tab
    JPanel StaffTab = createStaffTab(conn);
    tabbedPane.addTab("Staff Tab", StaffTab);
    
    // Add the second tab
    JPanel tab2 = new JPanel();
    tab2.add(new JLabel("This is the second tab"));
    tabbedPane.addTab("Report Tab", tab2);
    
    // Add the third tab
    JPanel tab3 = new JPanel();
    tab3.add(new JLabel("This is the third tab"));
    tabbedPane.addTab("Notifications Tab", tab3);

    JPanel FilmTab = createFilmTab(conn);
    tabbedPane.addTab("Films Tab", FilmTab);
    
    setSize(700, 600);
    setTitle("Database");
    setVisible(true);
    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);


    
    
    
    
    
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


private JPanel createFilmTab(Connection conn){
    JPanel tab1 = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel();
    StaffTable = new JTable(model);
    tab1.add(new JScrollPane(StaffTable), BorderLayout.CENTER);
    
    //Retrieve data from the database, Creating a statement and executing it
    try{
        String sql = "Select * from film limit 10";
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
            AddFilmData(conn);
                    }
    });
    tab1.add(addButton, BorderLayout.SOUTH);
    return tab1;
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

    return tab1;
}
private void AddFilmData(Connection conn){
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
                                          
    JPanel Last_Update = new JPanel();
    DateFormat updateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Define the format for the timestamp
    JFormattedTextField updateField = new JFormattedTextField(updateFormat); // Create a JFormattedTextField with the input format
    updateField.setColumns(20); // Set the number of columns for the input field
    updateField.setValue(new Date()); // Set the default value to the current date and time

    Last_Update.add(new JLabel("Last Update:"));
    Last_Update.add(updateField);
    panel.add(Last_Update);
                                          
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Create a panel with FlowLayout

    JButton cancelButton = new JButton("Cancel"); // Create a "Cancel" button
    cancelButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Code to execute when the "Cancel" button is clicked
            System.out.println("Cancel button clicked!");
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
                             "VALUES ('Dry', 'Words', 2022, 1, null, 6, 0.99, 86, 190.99, 'PG', 'Trailers')";
                Statement stmt = conn.createStatement();
                stmt.executeQuery(sql);
            }catch(Exception d){
                System.out.println("Error");
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