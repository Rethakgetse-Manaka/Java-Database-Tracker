import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class AWT extends Frame {
  public AWT(){
    
    Connection conn = this.getConnection();

    JTabbedPane tabbedPane = new JTabbedPane();
        
    // Add the first tab
    JPanel tab1 = new JPanel();
    tab1.add(new JLabel("This is the first tab"));
    tabbedPane.addTab("Staff Tab", tab1);
    
    // Add the second tab
    JPanel tab2 = new JPanel();
    tab2.add(new JLabel("This is the second tab"));
    tabbedPane.addTab("Report Tab", tab2);
    
    // Add the third tab
    JPanel tab3 = new JPanel();
    tab3.add(new JLabel("This is the third tab"));
    tabbedPane.addTab("Notifications Tab", tab3);

    JPanel tab4 = new JPanel();
    tab4.add(new JLabel("This is the fourth tab"));
    tabbedPane.addTab("Films Tab", tab4);
    
    add(tabbedPane);
    setSize(700, 600);
    setTitle("Database");
    setVisible(true);
    setLayout(new BorderLayout());


    
    
    
    
    
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

  public static void main(String args[]){
    new AWT();
  }
}