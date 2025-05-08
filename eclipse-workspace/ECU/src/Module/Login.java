package Module;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class Login extends JFrame {
    private JTextField userText;
    private JPasswordField passwordField;
    private JTextArea messageArea;

    public Login() {
        setTitle("Login Page");
        setSize(916, 714);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        ImageIcon backgroundImage = new ImageIcon("C:\\Users\\THARUN\\OneDrive\\画像\\vecteezy_abstract-black-friday-wallpaper-luxury-vector-background_6789424.jpg");

    
        userText = new JTextField();
        getContentPane().add(userText);
        userText.setBounds(360, 282, 150, 30);

    
        passwordField = new JPasswordField();
        getContentPane().add(passwordField);
        passwordField.setBounds(360, 370, 150, 30);

        
        messageArea = new JTextArea();
        messageArea.setBounds(96, 443, 250, 50);
        messageArea.setEditable(false);
        getContentPane().add(messageArea);

        
        JButton loginButton = new JButton("Login");
        getContentPane().add(loginButton);
        loginButton.setBounds(418, 463, 100, 30);

        
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        passwordLabel.setForeground(new Color(255, 255, 255));
        getContentPane().add(passwordLabel);
        passwordLabel.setBounds(188, 369, 100, 30);

        JLabel userLabel = new JLabel("USER ID");
        userLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        userLabel.setForeground(new Color(255, 255, 255));
        getContentPane().add(userLabel);
        userLabel.setBounds(188, 281, 100, 30);

        
        JButton register = new JButton("Register");
        register.setForeground(new Color(0, 0, 0));
        getContentPane().add(register);
        register.setBounds(418, 540, 100, 30);
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\THARUN\\OneDrive\\画像\\vecteezy_abstract-black-friday-wallpaper-luxury-vector-background_6789424.jpg"));
        lblNewLabel.setBounds(0, 0, 902, 677);
        getContentPane().add(lblNewLabel);
        register.addActionListener(e -> new Register().setVisible(true));

        
        loginButton.addActionListener(new LoginAction());
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = userText.getText();
            String password = new String(passwordField.getPassword());

            if (userId.isEmpty() || password.isEmpty()) {
                messageArea.setText("Please enter both User ID and Password.");
                return;
            }

            String query = "SELECT role FROM user WHERE userid = ? AND password = ?";
            try (Connection connection = DataBaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, Integer.parseInt(userId));
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    messageArea.setText("Login successful! Redirecting to " + role + " dashboard...");

                    if ("admin".equals(role)) {
                        SwingUtilities.invokeLater(() -> {
                            AdminPage adminPage = new AdminPage(userId);
                            adminPage.setVisible(true);
                            dispose(); 
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            UserPage userPage = new UserPage(userId);
                            userPage.setVisible(true);
                            dispose(); 
                        });
                    }
                } else {
                    messageArea.setText("User  ID not found. Please register.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                messageArea.setText("Database error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                messageArea.setText("User  ID must be a number.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login loginPage = new Login();
            loginPage.setVisible(true);
        });
    }
}