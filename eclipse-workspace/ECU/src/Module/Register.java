package Module;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class Register extends JFrame {
    private JTextField userText, userIdField;
    private JTextField roleField;
    private JPasswordField passwordField;
    private JTextArea messageArea;

    public Register() {
        setTitle("Registration Page");
        setSize(916, 714);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel userLabel = new JLabel("USER ID");
        userLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        userLabel.setForeground(new Color(255, 255, 255));
        getContentPane().add(userLabel);
        userLabel.setBounds(152, 299, 100, 30);

        userIdField = new JTextField();
        getContentPane().add(userIdField);
        userIdField.setBounds(333, 300, 150, 30);

      
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        passwordLabel.setForeground(new Color(255, 255, 255));
        getContentPane().add(passwordLabel);
        passwordLabel.setBounds(152, 369, 100, 30);

        passwordField = new JPasswordField();
        getContentPane().add(passwordField);
        passwordField.setBounds(333, 370, 150, 30);

     
        messageArea = new JTextArea();
        messageArea.setBounds(87, 506, 250, 50);
        messageArea.setEditable(false);
        getContentPane().add(messageArea);

        
        JLabel username = new JLabel("NAME");
        username.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        username.setForeground(new Color(255, 255, 255));
        getContentPane().add(username);
        username.setBounds(152, 240, 100, 30);

        userText = new JTextField();
        getContentPane().add(userText);
        userText.setBounds(333, 241, 150, 30);

   
        JLabel role = new JLabel("ROLE");
        role.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        role.setForeground(new Color(255, 255, 255));
        getContentPane().add(role);
        role.setBounds(162, 428, 100, 30);

        roleField = new JTextField();
        getContentPane().add(roleField);
        roleField.setBounds(333, 429, 150, 30);

        JButton registerButton = new JButton("Register");
        registerButton.setForeground(new Color(0, 0, 0));
        registerButton.addActionListener(new RegisterAction());
        getContentPane().add(registerButton);
        registerButton.setBounds(389, 526, 100, 30);

     
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\THARUN\\OneDrive\\画像\\vecteezy_abstract-black-friday-wallpaper-luxury-vector-background_6789424.jpg"));
        lblNewLabel.setForeground(new Color(255, 255, 255));
        lblNewLabel.setBounds(-11, 0, 913, 677);
        getContentPane().add(lblNewLabel);
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = userIdField.getText();
            String name = userText.getText();
            String role = roleField.getText();
            String password = new String(passwordField.getPassword());

            if (userId.isEmpty() || name.isEmpty() || role.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All Fields Are Required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO user (userid, username, role, password) VALUES (?, ?, ?, ?)";

            try (Connection connection = DataBaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, Integer.parseInt(userId));
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, role);
                preparedStatement.setString(4, password);

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Registration successful!", "Information", JOptionPane.INFORMATION_MESSAGE);
                    
                    SwingUtilities.invokeLater(() -> {
                        Login loginPage = new Login();
                        loginPage.setVisible(true);
                        dispose();
                    });
                } else {
                    JOptionPane.showMessageDialog(null, "Registration Failed", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "User  ID Must Be A Number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Register registrationPage = new Register();
            registrationPage.setVisible(true);
        });
    }
}