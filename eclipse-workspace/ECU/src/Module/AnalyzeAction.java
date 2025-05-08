package Module;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AnalyzeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();

            if (!csvFile.getName().endsWith(".csv")) {
                JOptionPane.showMessageDialog(null, "Please select a .csv file.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String headerLine = br.readLine();
                if (headerLine == null) {
                    JOptionPane.showMessageDialog(null, "File is empty.");
                    return;
                }

                // Split header and find "speed" column
                String[] headers = headerLine.replace("\"", "").split(",");
                int speedIndex = -1;
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].toLowerCase().contains("speed")) {
                        speedIndex = i;
                        break;
                    }
                }

                if (speedIndex == -1) {
                    JOptionPane.showMessageDialog(null, "'Speed' column not found.");
                    return;
                }

                // Prepare dataset with numeric X axis
                XYSeries series = new XYSeries("Speed");
                String line;
                int record = 1;

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length > speedIndex) {
                        try {
                            double speed = Double.parseDouble(values[speedIndex].trim());
                            series.add(record, speed);
                            record++;
                        } catch (NumberFormatException ignored) {}
                    }
                }

                XYSeriesCollection dataset = new XYSeriesCollection(series);

                // Create XY chart
                JFreeChart chart = ChartFactory.createXYLineChart(
                        "Speed Analysis",
                        "Record",
                        "Speed (km/h)",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true, true, false
                );

                // Show chart in new window
                JFrame chartFrame = new JFrame("Speed Graph");
                chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                chartFrame.setSize(800, 600);
                chartFrame.add(new ChartPanel(chart));
                chartFrame.setVisible(true);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Analyze Action Test");
        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new AnalyzeAction());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.add(analyzeButton);
        frame.setVisible(true);
    }
}
