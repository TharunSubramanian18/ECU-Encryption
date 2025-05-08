package Module;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ABEEncryptionButtonAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Open file dialog to select Excel file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Encrypt");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToEncrypt = fileChooser.getSelectedFile();
            String outputPath = "path/to/your/encrypted/folder/encrypted_file.abe"; // Define your output path

            try {
                // Read the file content
                byte[] fileContent = Files.readAllBytes(fileToEncrypt.toPath());

                // Perform attribute-based encryption
                String ECU_id = "ECU123"; // Example attribute
                String timestamp = String.valueOf(System.currentTimeMillis()); // Current timestamp
                String throttleSpeed = "100"; // Example throttle speed

                byte[] encryptedContent = attributeBasedEncrypt(fileContent, ECU_id, timestamp, throttleSpeed);

                // Write the encrypted content to the output file
                try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                    fos.write(encryptedContent);
                }

                JOptionPane.showMessageDialog(null, "File encrypted successfully and saved to: " + outputPath);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error encrypting file: " + ex.getMessage());
            }
        }
    }

    private byte[] attributeBasedEncrypt(byte[] data, String ECU_id, String timestamp, String throttleSpeed) {
        // Simple encryption logic (not secure, for demonstration purposes)
        String attributes = ECU_id + "|" + timestamp + "|" + throttleSpeed;
        String combinedData = Base64.getEncoder().encodeToString(data) + "|" + attributes;

        // Simulate encryption by encoding the combined data
        return Base64.getEncoder().encode(combinedData.getBytes());
    }

    public static void main(String[] args) {
        // Example usage
        JFrame frame = new JFrame("ABE Encryption Example");
        JButton encryptButton = new JButton("Encrypt File");
        encryptButton.addActionListener(new ABEEncryptionButtonAction());
        frame.add(encryptButton);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
