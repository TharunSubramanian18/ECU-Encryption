package Module;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.stream.Stream;

public class UserPage extends JFrame {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private JButton checkButton;
    private JButton grantButton;
    private JButton rejectButton;
    private JButton generateReportButton;
    private String userId; 

    public UserPage(String userId) {
        this.userId = userId; 
        setTitle("User  Page");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        
        String[] columnNames = {"Request Message", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        requestTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBounds(50, 50, 500, 200);
        getContentPane().add(scrollPane);

        
        checkButton = new JButton("Check Requests");
        checkButton.setBounds(50, 270, 150, 30);
        checkButton.addActionListener(new CheckRequestAction());
        getContentPane().add(checkButton);

        grantButton = new JButton("Grant Request");
        grantButton.setBounds(220, 270, 150, 30);
        grantButton.addActionListener(new GrantRequestAction());
        grantButton.setEnabled(false);
        getContentPane().add(grantButton);

        rejectButton = new JButton("Reject Request");
        rejectButton.setBounds(390, 270, 150, 30);
        rejectButton.addActionListener(new RejectRequestAction());
        rejectButton.setEnabled(false);
        getContentPane().add(rejectButton);

        generateReportButton = new JButton("Generate Report");
        generateReportButton.setBounds(220, 310, 150, 30);
        generateReportButton.addActionListener(new GenerateReportAction());
        getContentPane().add(generateReportButton);
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\THARUN\\OneDrive\\画像\\vecteezy_abstract-black-friday-wallpaper-luxury-vector-background_6789424.jpg"));
        lblNewLabel.setBounds(0, 0, 586, 363);
        getContentPane().add(lblNewLabel);
    }

    private class CheckRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
    
            tableModel.setRowCount(0);

            
            String query = "SELECT request_message, status FROM encryption_requests WHERE user_id = ? AND status = 'pending'";

            try (Connection connection = DataBaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, userId); 

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String requestMessage = resultSet.getString("request_message");
                    String status = resultSet.getString("status");
                    tableModel.addRow(new Object[]{requestMessage, status});
                }

                if (tableModel.getRowCount() > 0) {
                    grantButton.setEnabled(true);
                    rejectButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(UserPage.this, "No pending requests found.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(UserPage.this, "Database error: " + ex.getMessage());
            }
        }
    }

    private class GrantRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                String requestMessage = (String) tableModel.getValueAt(selectedRow, 0);
                String query = "UPDATE encryption_requests SET status = 'granted' WHERE request_message = ? AND status = 'pending'";

                try (Connection connection = DataBaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, requestMessage);
                    int result = preparedStatement.executeUpdate();
                    if (result > 0) {
                        tableModel.setValueAt("granted", selectedRow, 1);
                        JOptionPane.showMessageDialog(UserPage.this, "Request granted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(UserPage.this, "Failed to grant request.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(UserPage.this, "Database error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(UserPage.this, "Please select a request to grant.");
            }
        }
    }

    private class RejectRequestAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow != -1) {
                String requestMessage = (String) tableModel.getValueAt(selectedRow, 0);
                String query = "UPDATE encryption_requests SET status = 'rejected' WHERE request_message = ? AND status = 'pending'";

                try (Connection connection = DataBaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, requestMessage);
                    int result = preparedStatement.executeUpdate();
                    if (result > 0) {
                        tableModel.setValueAt("rejected", selectedRow, 1);
                        JOptionPane.showMessageDialog(UserPage.this, "Request rejected successfully!");
                    } else {
                        JOptionPane.showMessageDialog(UserPage.this, "Failed to reject request.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(UserPage.this, "Database error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(UserPage.this, "Please select a request to reject.");
            }
        }
    }

    private class GenerateReportAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Document document = new Document();
            try {
                File reportsDir = new File("C:\\Users\\THARUN\\Downloads\\User_Reports");
                if (!reportsDir.exists()) {
                    reportsDir.mkdirs();  
                }

                String outputPath = reportsDir + "\\UserReport_" + userId + ".pdf";

                PdfWriter.getInstance(document, new FileOutputStream(outputPath));
                document.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                Paragraph title = new Paragraph("Accepted Requests Report - User: " + userId, titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 4, 4, 4, 4});
                addTableHeader(table);

                String sql = """
                    SELECT r.admin, r.request_message, f.encrypted_file, d.decrypted_to, f.timestamp
                    FROM encryption_requests r
                    LEFT JOIN encrypted_files f ON r.user_id = f.user_id AND r.admin = f.admin
                    LEFT JOIN decrypted_files d ON r.user_id = d.user_id AND r.admin = d.admin
                    WHERE r.status = 'accepted' AND r.user_id = ?
                    """;

                try (Connection conn = DataBaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        table.addCell(rs.getString("admin"));  // Admin ID
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
            Stream.of("Admin ID", "Request Message", "Encrypted File", "Decrypted File", "Timestamp")
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
            UserPage userPage = new UserPage("2234"); 
            userPage.setVisible(true);
        });
    }
}