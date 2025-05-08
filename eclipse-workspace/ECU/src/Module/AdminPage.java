package Module;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.stream.Stream;

public class AdminPage extends JFrame {
    private JTextField userIdField;
    private String adminId;
    private JTextArea requestMessageArea;
    private JTextArea feedbackArea;
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private JButton acceptButton, rejectButton, encryptButton, decryptButton, checkStatusButton, generateReportButton;
    private JButton analyzeButton;
    private static final String SECRET_KEY = "1918180306190201";
    private JLabel lblNewLabel;

    public AdminPage(String adminId) {
        this.adminId = adminId;
        setTitle("Admin Encryption Request Page");
        setSize(916, 714);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel userLabel = new JLabel("USER ID");
        userLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(152, 299, 100, 30);
        getContentPane().add(userLabel);

        userIdField = new JTextField();
        userIdField.setBounds(333, 300, 150, 30);
        getContentPane().add(userIdField);

        requestMessageArea = new JTextArea();
        requestMessageArea.setLineWrap(true);
        requestMessageArea.setWrapStyleWord(true);
        JScrollPane requestScrollPane = new JScrollPane(requestMessageArea);
        requestScrollPane.setBounds(50, 350, 800, 100);
        getContentPane().add(requestScrollPane);

        String[] columnNames = {"User ID", "Request Message", "Status", "Created At", "Admin ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        requestTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBounds(50, 50, 800, 200);
        getContentPane().add(scrollPane);

        JButton submitButton = new JButton("Submit Request");
        submitButton.addActionListener(new SubmitRequestAction());
        submitButton.setBounds(50, 470, 150, 30);
        getContentPane().add(submitButton);

        checkStatusButton = new JButton("Check Request Status");
        checkStatusButton.addActionListener(new CheckRequestStatusAction());
        checkStatusButton.setBounds(220, 470, 200, 30);
        getContentPane().add(checkStatusButton);

        acceptButton = new JButton("Accept Request");
        acceptButton.addActionListener(new AcceptRequestAction());
        acceptButton.setEnabled(true);
        acceptButton.setBounds(430, 470, 150, 30);
        getContentPane().add(acceptButton);

        rejectButton = new JButton("Reject Request");
        rejectButton.addActionListener(new RejectRequestAction());
        rejectButton.setEnabled(true);
        rejectButton.setBounds(590, 470, 150, 30);
        getContentPane().add(rejectButton);

        encryptButton = new JButton("Encrypt");
        encryptButton.setEnabled(false);
        encryptButton.setBounds(430, 510, 150, 30);
        encryptButton.addActionListener(new EncryptAction());
        getContentPane().add(encryptButton);

        decryptButton = new JButton("Decrypt");
        decryptButton.setEnabled(false);
        decryptButton.setBounds(590, 510, 150, 30);
        decryptButton.addActionListener(new DecryptAction());
        getContentPane().add(decryptButton);
        
        analyzeButton = new JButton("Analyze");
        analyzeButton.setEnabled(false);
        analyzeButton.setBounds(220, 510, 150, 30);
        analyzeButton.addActionListener(new AnalyzeAction());
        getContentPane().add(analyzeButton);
      


        generateReportButton = new JButton("Generate Report");
        generateReportButton.setBounds(50, 510, 150, 30);
        generateReportButton.addActionListener(new GenerateReportAction());
        getContentPane().add(generateReportButton);

        feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setBounds(50, 550, 800, 100);
        getContentPane().add(feedbackArea);

        lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\THARUN\\OneDrive\\画像\\vecteezy_abstract-black-friday-wallpaper-luxury-vector-background_6789424.jpg"));
        lblNewLabel.setBounds(0, 0, 902, 677);
        getContentPane().add(lblNewLabel);

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        tableModel.setRowCount(0);
        String query = "SELECT user_id, request_message, status, created_at, admin FROM encryption_requests WHERE status = 'pending'";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getString("user_id"),
                        resultSet.getString("request_message"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at"),
                        resultSet.getString("admin")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            feedbackArea.setText("Database error: " + ex.getMessage());
        }
    }
    private class SubmitRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = userIdField.getText();
            String requestMessage = requestMessageArea.getText();

            if (userId.isEmpty() || requestMessage.isEmpty()) {
                feedbackArea.setText("All fields are required.");
                return;
            }

            String query = "INSERT INTO encryption_requests (user_id, request_message, status, created_at, admin) VALUES (?, ?, 'pending', ?, ?)";

            try (Connection connection = DataBaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, requestMessage);
                preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                preparedStatement.setString(4, adminId); 

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    feedbackArea.setText("Request submitted successfully!");
                    loadPendingRequests();
                } else {
                    feedbackArea.setText("Failed to submit request.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                feedbackArea.setText("Database error: " + ex.getMessage());
            }
        }
    }

    private class CheckRequestStatusAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = userIdField.getText();
            if (userId.isEmpty()) {
                feedbackArea.setText("Please enter a user ID.");
                return;
            }

            tableModel.setRowCount(0);
            String query = "SELECT user_id, request_message, status, created_at, admin FROM encryption_requests WHERE user_id = ?";

            try (Connection connection = DataBaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String requestMessage = resultSet.getString("request_message");
                    String status = resultSet.getString("status");
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    String adminId = resultSet.getString("admin");
                    tableModel.addRow(new Object[]{userId, requestMessage, status, createdAt, adminId});
                }

                if (tableModel.getRowCount() == 0) {
                    feedbackArea.setText("No requests found for user ID: " + userId);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                feedbackArea.setText("Database error: " + ex.getMessage());
            }
        }
    }

    private class AcceptRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) tableModel.getValueAt(selectedRow, 0);
                String requestMessage = (String) tableModel.getValueAt(selectedRow, 1);
                String currentStatus = (String) tableModel.getValueAt(selectedRow, 2);

                if ("accepted".equalsIgnoreCase(currentStatus)) {
                    feedbackArea.setText("Request is already accepted. Encrypt and Decrypt buttons enabled.");
                    encryptButton.setEnabled(true);
                    decryptButton.setEnabled(true);
                    return;
                }

                if ("granted".equalsIgnoreCase(currentStatus)) {
                    String query = "UPDATE encryption_requests SET status = 'accepted' WHERE user_id = ? AND request_message = ? AND status = 'granted'";
                    try (Connection connection = DataBaseConnection.getConnection();
                         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                        preparedStatement.setString(1, userId);
                        preparedStatement.setString(2, requestMessage);
                        int result = preparedStatement.executeUpdate();
                        if (result > 0) {
                            tableModel.setValueAt("accepted", selectedRow, 2);
                            feedbackArea.setText("Status changed from 'granted' to 'accepted'. Encrypt and Decrypt enabled.");
                            encryptButton.setEnabled(true);
                            decryptButton.setEnabled(true);
                        } else {
                            feedbackArea.setText("Failed to update status from 'granted' to 'accepted'.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        feedbackArea.setText("Database error: " + ex.getMessage());
                    }
                    return;
                }

                String query = "UPDATE encryption_requests SET status = 'accepted' WHERE user_id = ? AND request_message = ? AND status = 'pending'";
                try (Connection connection = DataBaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, requestMessage);
                    int result = preparedStatement.executeUpdate();
                    if (result > 0) {
                        tableModel.setValueAt("accepted", selectedRow, 2);
                        feedbackArea.setText("Request accepted successfully. Encrypt and Decrypt enabled.");
                        encryptButton.setEnabled(true);
                        decryptButton.setEnabled(true);
                    } else {
                        feedbackArea.setText("Failed to accept request.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    feedbackArea.setText("Database error: " + ex.getMessage());
                }

            } else {
                feedbackArea.setText("Please select a request to accept.");
            }
        }
    }


    private class RejectRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) tableModel.getValueAt(selectedRow, 0);
                String requestMessage = (String) tableModel.getValueAt(selectedRow, 1);
                String query = "UPDATE encryption_requests SET status = 'rejected' WHERE user_id = ? AND request_message = ?";

                try (Connection connection = DataBaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, userId);
                    preparedStatement.setString(2, requestMessage);
                    int result = preparedStatement.executeUpdate();
                    if (result > 0) {
                        tableModel.setValueAt("rejected", selectedRow, 2);
                        feedbackArea.setText("Request rejected successfully!");
                    } else {
                        feedbackArea.setText("Failed to reject request.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    feedbackArea.setText("Database error: " + ex.getMessage());
                }
            } else {
                feedbackArea.setText("Please select a request to reject.");
            }
        }
    }

    public class EncryptAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String inputFilePath = selectedFile.getAbsolutePath();
                String outputFilePath = inputFilePath.replace(".xlsx",".bin");

                try {
                    FileEncryptionUtil.encryptFile(inputFilePath, outputFilePath, SECRET_KEY);
                    JOptionPane.showMessageDialog(null, "File encrypted successfully: " + outputFilePath);
                    encryptButton.setVisible(false);

                    String insertQuery = "INSERT INTO encrypted_files (user_id, admin, encrypted_file, encrypted_to, timestamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
                    try (Connection conn = DataBaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                        stmt.setString(1, userIdField.getText().trim());
                        stmt.setString(2, adminId);
                        stmt.setString(3, inputFilePath);
                        stmt.setString(4, outputFilePath);

                        stmt.executeUpdate();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to log encryption: " + sqlEx.getMessage());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error encrypting file: " + ex.getMessage());
                }
            }
        }
    }


    public class DecryptAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String inputFilePath = selectedFile.getAbsolutePath();
                String outputFilePath = inputFilePath.replace(".bin", "_decrypted.xlsx");

                try {
                    FileEncryptionUtil.decryptFile(inputFilePath, outputFilePath, SECRET_KEY);
                    JOptionPane.showMessageDialog(null, "File decrypted successfully: " + outputFilePath);
                    analyzeButton.setEnabled(true); // Enable analyze button
                    // Insert decryption log into database
                    String insertQuery = "INSERT INTO decrypted_files (user_id, admin, decrypted_file, decrypted_to, timestamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
                    try (Connection conn = DataBaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                        stmt.setString(1, userIdField.getText().trim());
                        stmt.setString(2, adminId);
                        stmt.setString(3, inputFilePath);
                        stmt.setString(4, outputFilePath);

                        stmt.executeUpdate();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to log decryption: " + sqlEx.getMessage());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error decrypting file: " + ex.getMessage());
                }
            }
        }
    }




    private class GenerateReportAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Document document = new Document();
            try {
                File reportsDir = new File("C:\\Users\\THARUN\\Downloads\\Admin_Reports");
                if (!reportsDir.exists()) {
                    reportsDir.mkdirs(); 
                }

                String outputPath = reportsDir + "\\AdminReport_" + adminId + ".pdf";

                PdfWriter.getInstance(document, new FileOutputStream(outputPath));
                document.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                Paragraph title = new Paragraph("Accepted Requests Report - Admin: " + adminId, titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 4, 4, 4, 4});
                addTableHeader(table);

                String sql = """
                    SELECT r.user_id, r.request_message, f.encrypted_file, d.decrypted_to, f.timestamp
                    FROM encryption_requests r
                    LEFT JOIN encrypted_files f ON r.user_id = f.user_id AND r.admin = f.admin
                    LEFT JOIN decrypted_files d ON r.user_id = d.user_id AND r.admin = d.admin
                    WHERE r.status = 'accepted' AND r.admin = ?
                    """;

                try (Connection conn = DataBaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, adminId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        table.addCell(rs.getString("user_id"));
                        table.addCell(rs.getString("request_message"));
                        table.addCell(rs.getString("encrypted_file") == null ? "N/A" : rs.getString("encrypted_file"));
                        table.addCell(rs.getString("decrypted_to") == null ? "N/A" : rs.getString("decrypted_to"));
                        table.addCell(rs.getTimestamp("timestamp") == null ? "N/A" : rs.getTimestamp("timestamp").toString());
                    }
                }

                document.add(table);
                document.close();
                JOptionPane.showMessageDialog(null, "PDF Report generated successfully:\n" + outputPath);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating PDF: " + ex.getMessage());
            }
        }

        private void addTableHeader(PdfPTable table) {
            Stream.of("User ID", "Request Message", "Encrypted File", "Decrypted File", "Timestamp")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminPage adminPage = new AdminPage("5542");
            adminPage.setVisible(true);
        });
    }
}
