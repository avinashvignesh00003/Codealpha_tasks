import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections; // For min/max operations

public class Main extends JFrame { // Changed class name to Main

    // --- Data Structures ---
    // Inner class to represent a Student
    private static class Student {
        private String name;
        private double score;

        public Student(String name, double score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public double getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "Name: " + name + ", Score: " + String.format("%.2f", score);
        }
    }

    // ArrayList to store all student objects
    private ArrayList<Student> students;

    // --- GUI Components ---
    private JTextField studentNameField;
    private JTextField studentScoreField;
    private JTextArea studentListArea;
    private JTextArea summaryReportArea;
    private JButton addStudentButton;
    private JButton calculateSummaryButton;
    private JButton clearButton;

    // --- Constructor ---
    public Main() { // Constructor name also changed to Main
        // Initialize the JFrame
        super("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500); // Set initial size of the window
        setLocationRelativeTo(null); // Center the window on the screen

        // Initialize data storage
        students = new ArrayList<>();

        // Set up the main layout
        setLayout(new BorderLayout(10, 10)); // Add some padding between components

        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Student"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components

        // Student Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Student Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow field to expand horizontally
        studentNameField = new JTextField(20);
        inputPanel.add(studentNameField, gbc);

        // Student Score Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Score (0-100):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        studentScoreField = new JTextField(20);
        inputPanel.add(studentScoreField, gbc);

        // Add Student Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        addStudentButton = new JButton("Add Student");
        inputPanel.add(addStudentButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- Display Panel (Center) ---
        JPanel displayPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Two columns for list and summary
        displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add some horizontal padding

        // Student List Area
        studentListArea = new JTextArea();
        studentListArea.setEditable(false);
        studentListArea.setLineWrap(true);
        studentListArea.setWrapStyleWord(true);
        JScrollPane studentListScrollPane = new JScrollPane(studentListArea);
        studentListScrollPane.setBorder(BorderFactory.createTitledBorder("Student List"));
        displayPanel.add(studentListScrollPane);

        // Summary Report Area
        summaryReportArea = new JTextArea();
        summaryReportArea.setEditable(false);
        summaryReportArea.setLineWrap(true);
        summaryReportArea.setWrapStyleWord(true);
        JScrollPane summaryReportScrollPane = new JScrollPane(summaryReportArea);
        summaryReportScrollPane.setBorder(BorderFactory.createTitledBorder("Summary Report"));
        displayPanel.add(summaryReportScrollPane);

        add(displayPanel, BorderLayout.CENTER);

        // --- Control Panel (South) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center buttons
        calculateSummaryButton = new JButton("Calculate Summary");
        clearButton = new JButton("Clear All");

        controlPanel.add(calculateSummaryButton);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.SOUTH);

        // --- Event Handling ---
        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        calculateSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateSummary();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }

    // --- Core Logic Methods ---

    private void addStudent() {
        String name = studentNameField.getText().trim();
        String scoreText = studentScoreField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double score;
        try {
            score = Double.parseDouble(scoreText);
            if (score < 0 || score > 100) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid score. Please enter a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student newStudent = new Student(name, score);
        students.add(newStudent);
        updateStudentListDisplay();

        // Clear input fields after adding
        studentNameField.setText("");
        studentScoreField.setText("");
        studentNameField.requestFocusInWindow(); // Set focus back to name field
    }

    private void updateStudentListDisplay() {
        StringBuilder sb = new StringBuilder();
        if (students.isEmpty()) {
            sb.append("No students added yet.");
        } else {
            for (int i = 0; i < students.size(); i++) {
                sb.append((i + 1)).append(". ").append(students.get(i)).append("\n");
            }
        }
        studentListArea.setText(sb.toString());
    }

    private void calculateSummary() {
        if (students.isEmpty()) {
            summaryReportArea.setText("No students to generate a summary for.");
            return;
        }

        double totalScore = 0;
        double highestScore = Double.MIN_VALUE;
        double lowestScore = Double.MAX_VALUE;
        String highestScorer = "";
        String lowestScorer = "";

        for (Student student : students) {
            double currentScore = student.getScore();
            totalScore += currentScore;

            if (currentScore > highestScore) {
                highestScore = currentScore;
                highestScorer = student.getName();
            }
            if (currentScore < lowestScore) {
                lowestScore = currentScore;
                lowestScorer = student.getName();
            }
        }

        double averageScore = totalScore / students.size();

        StringBuilder summary = new StringBuilder();
        summary.append("--- Grade Summary ---\n");
        summary.append("Total Students: ").append(students.size()).append("\n");
        summary.append("Average Score: ").append(String.format("%.2f", averageScore)).append("\n");
        summary.append("Highest Score: ").append(String.format("%.2f", highestScore)).append(" (").append(highestScorer).append(")\n");
        summary.append("Lowest Score: ").append(String.format("%.2f", lowestScore)).append(" (").append(lowestScorer).append(")\n");
        summary.append("---------------------\n");

        summaryReportArea.setText(summary.toString());
    }

    private void clearAll() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all student data?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            students.clear();
            studentNameField.setText("");
            studentScoreField.setText("");
            studentListArea.setText("");
            summaryReportArea.setText("");
            studentNameField.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "All data has been cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- Main Method ---
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true); // Changed constructor call to Main
            }
        });
    }
}
