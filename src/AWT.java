import java.awt.*;
import java.awt.event.*;
import java.sql.*;
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
            JOptionPane pane = new JOptionPane();
            JPanel addPanel = new JPanel();
            // Add components to the addPanel for inputting new data
            int option = pane.showConfirmDialog(null, addPanel, "Add New Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                // Add new data to the database and reload the table
            }
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
    JPanel filterPanel = new JPanel(new BorderLayout());
    JTextField filter = new JTextField("Type here to filter results");
    filterPanel.add(filter, BorderLayout.CENTER);
    tab1.add(filterPanel, BorderLayout.SOUTH);

    //Filter implementation
    filter.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            applyFilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            applyFilter();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            applyFilter();
        }

        // Update the table based on the filter text
        private void applyFilter() {
            String filterText = filter.getText();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            StaffTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText));
        }
    });

    return tab1;
}

  public static void main(String args[]){
    new AWT();
  }
}