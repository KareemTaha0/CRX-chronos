import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.HashMap;

class CRXWapp {
    private Timer loadingTimer;
    private Timer stopwatchTimer;
    private int elapsedSeconds = 0;
    private JLabel timeLabel;

    public static void main(String[] args) {
        new CRXWapp().start();
    }

    private void start() {
        JFrame frame = new JFrame("CRX Wapp - Ultimate Time & Weather");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 600);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 16));
        frame.add(tabbedPane);

        // Add tabs
        tabbedPane.addTab("Weather", createWeatherPanel());
        tabbedPane.addTab("Stopwatch", createStopwatchPanel());
        tabbedPane.addTab("World Clock", createWorldClockPanel());

        frame.setVisible(true);
    }

    private JPanel createWeatherPanel() {
        HashMap<String, String> weatherData = new HashMap<>();
        weatherData.put("Cairo", "☀️ Sunny, 30°C");
        weatherData.put("London", "☁️ Cloudy, 18°C");
        weatherData.put("New York", "🌧️ Rainy, 22°C");
        weatherData.put("Tokyo", "🌬️ Windy, 25°C");
        weatherData.put("Paris", "🌫️ Foggy, 20°C");

        JPanel panel = createBasePanel();
        JLabel titleLabel = createLabel("Weather Checker", 28, Font.BOLD, "#343a40", SwingConstants.CENTER);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        String[] cities = {"Select City", "Cairo", "London", "New York", "Tokyo", "Paris"};
        JComboBox<String> citySelector = new JComboBox<>(cities);
        citySelector.setFont(new Font("SansSerif", Font.PLAIN, 18));
        citySelector.setMaximumSize(new Dimension(250, 40));
        citySelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(citySelector);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel spinnerLabel = new JLabel(new ImageIcon("spinner.gif"));
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinnerLabel.setVisible(false);
        panel.add(spinnerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel resultLabel = createLabel("", 20, Font.PLAIN, "#495057", SwingConstants.CENTER);
        panel.add(resultLabel);

        citySelector.addActionListener((ActionEvent e) -> {
            String selectedCity = (String) citySelector.getSelectedItem();
            if (selectedCity != null && !selectedCity.equals("Select City")) {
                spinnerLabel.setVisible(true);
                resultLabel.setText("");

                if (loadingTimer != null && loadingTimer.isRunning()) {
                    loadingTimer.stop();
                }

                loadingTimer = new Timer(1500, (ActionEvent evt) -> {
                    spinnerLabel.setVisible(false);
                    String weatherInfo = weatherData.getOrDefault(selectedCity, "City not found!");
                    resultLabel.setText("Weather in " + selectedCity + ": " + weatherInfo);
                    if (weatherInfo.equals("City not found!")) {
                        resultLabel.setForeground(Color.RED);
                    } else {
                        resultLabel.setForeground(new Color(40, 167, 69));
                    }
                });
                loadingTimer.setRepeats(false);
                loadingTimer.start();
            } else {
                spinnerLabel.setVisible(false);
                resultLabel.setText("");
            }
        });

        addFooter(panel);
        return panel;
    }

    private JPanel createStopwatchPanel() {
        JPanel panel = createBasePanel();
        JLabel titleLabel = createLabel("Stopwatch", 28, Font.BOLD, "#343a40", SwingConstants.CENTER);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        timeLabel = createLabel("00:00:00", 36, Font.BOLD, "#495057", SwingConstants.CENTER);
        panel.add(timeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton startButton = createButton("Start", "#28a745");
        JButton stopButton = createButton("Stop", "#dc3545");
        JButton resetButton = createButton("Reset", "#ffc107");

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        panel.add(buttonPanel);

        stopwatchTimer = new Timer(1000, (ActionEvent e) -> {
            elapsedSeconds++;
            updateTimerLabel();
        });

        startButton.addActionListener((e) -> stopwatchTimer.start());
        stopButton.addActionListener((e) -> stopwatchTimer.stop());
        resetButton.addActionListener((e) -> {
            stopwatchTimer.stop();
            elapsedSeconds = 0;
            updateTimerLabel();
        });

        addFooter(panel);
        return panel;
    }

    private JPanel createWorldClockPanel() {
        JPanel panel = createBasePanel();
        JLabel titleLabel = createLabel("World Clock 🌍", 28, Font.BOLD, "#343a40", SwingConstants.CENTER);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel estLabel = createTimeLabel("EST (New York)");
        JLabel pstLabel = createTimeLabel("PST (Los Angeles)");
        JLabel gmt3Label = createTimeLabel("GMT+3 (Riyadh)");
        JLabel gmtLabel = createTimeLabel("GMT (London)");
        JLabel gmt2Label = createTimeLabel("GMT+2 (Cairo)");

        panel.add(estLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(pstLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(gmt3Label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(gmtLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(gmt2Label);

        Timer clockTimer = new Timer(1000, (ActionEvent e) -> {
            updateTimeLabel(estLabel, "America/New_York");
            updateTimeLabel(pstLabel, "America/Los_Angeles");
            updateTimeLabel(gmt3Label, "Asia/Riyadh");
            updateTimeLabel(gmtLabel, "GMT");
            updateTimeLabel(gmt2Label, "Africa/Cairo");
        });
        clockTimer.start();

        addFooter(panel);
        return panel;
    }

    // 🔥 Helper Methods
    private JPanel createBasePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#f8f9fa"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JLabel createLabel(String text, int size, int style, String colorHex, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(Color.decode(colorHex));
        return label;
    }

    private JButton createButton(String text, String colorHex) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(Color.decode(colorHex));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JLabel createTimeLabel(String zoneName) {
        JLabel label = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        label.setForeground(Color.decode("#495057"));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setText(zoneName + ": Loading...");
        return label;
    }

    private void updateTimeLabel(JLabel label, String zoneId) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(zoneId));
        String timeText = String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond());
        label.setText(label.getText().split(":")[0] + ": " + timeText);
    }

    private void updateTimerLabel() {
        int hours = elapsedSeconds / 3600;
        int minutes = (elapsedSeconds % 3600) / 60;
        int seconds = elapsedSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void addFooter(JPanel panel) {
        panel.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("Made by CRX", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(Color.GRAY);
        panel.add(footer);
    }
}
