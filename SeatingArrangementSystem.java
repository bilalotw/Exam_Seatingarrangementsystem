import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ClassInputFrame extends JFrame {
    private int classCount;
    private JTextField[] seatFields;
    private JButton arrangeButton;
    private int[] rollStart;
    private int[] rollEnd;

    public ClassInputFrame(int examCount, int classCount, int[] rollStart, int[] rollEnd) {
        this.classCount = classCount;
        this.rollStart = rollStart;
        this.rollEnd = rollEnd;
        setTitle("Class Seat Input");
        setSize(400, classCount * 50 + 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(classCount + 1, 2, 10, 10));
        seatFields = new JTextField[classCount];

        for (int i = 0; i < classCount; i++) {
            panel.add(new JLabel("Seats in Class " + (i + 1) + ":"));
            seatFields[i] = new JTextField();
            panel.add(seatFields[i]);
        }

        arrangeButton = new JButton("Arrange Seats");
        arrangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] seats = new int[classCount];
                for (int i = 0; i < classCount; i++) {
                    seats[i] = Integer.parseInt(seatFields[i].getText());
                }
                // Pass roll ranges to SeatingDisplayFrame
                new SeatingDisplayFrame(seats, examCount, rollStart, rollEnd);
                dispose();
            }
        });
        panel.add(arrangeButton);

        add(panel);
        setVisible(true);
    }
}
class SeatingDisplayFrame extends JFrame {
    public SeatingDisplayFrame(int[] seats, int examCount, int[] rollStart, int[] rollEnd) {
        setTitle("Seating Arrangement");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        int[] currentRolls = rollStart.clone();

        for (int i = 0; i < seats.length; i++) {
            int seatCount = seats[i];
            int columns = Math.min(5, (int) Math.ceil(Math.sqrt(seatCount))); // Dynamic columns
            JPanel panel = new JPanel(new GridLayout(0, columns, 5, 5));
            int lastExamIndex = -1; // Track the last allocated exam index
            int dummy=1;
            for (int j = 0; j < seatCount; j++) {
                String rollNoText;
                JButton seatButton = new JButton();
                int allocatedExamIndex = -1;

                // Count exams with remaining rolls
                int remainingExams = 0;
                for (int k = 0; k < examCount; k++) {
                    if (currentRolls[k] <= rollEnd[k]) {
                        remainingExams++;
                        allocatedExamIndex = k; // Default to the first available exam
                    }
                }

                 if (remainingExams > 1) {
                    // Allocate a roll from a different exam, avoiding consecutive same exam
                    allocatedExamIndex = findNextExam(lastExamIndex, currentRolls, rollEnd, examCount);

                    rollNoText = "Exam " + (allocatedExamIndex + 1) + ": Roll " + currentRolls[allocatedExamIndex];
                    seatButton.setBackground(getColorForExam(allocatedExamIndex));
                    seatButton.setForeground(Color.WHITE);
                    currentRolls[allocatedExamIndex]++;
                    lastExamIndex = allocatedExamIndex; // Update last exam index
                    
                }
                else if (remainingExams == 1) {
                    // Only one exam remains
                    allocatedExamIndex = findRemainingExam(currentRolls, rollEnd, examCount);
                    dummy+=1;
                    if(dummy%2!=0) {
                    	 rollNoText = "Vacant";
                         seatButton.setBackground(Color.WHITE);
                         seatButton.setForeground(Color.BLACK);
                    }
                    else {
                    // Check if the last allocated exam is the same as the current one
                   
                        // Insert a vacant seat to avoid consecutive same exam allocations
                        
                        // Allocate the roll for the remaining exam
                        rollNoText = "Exam " + (allocatedExamIndex + 1) + ": Roll " + currentRolls[allocatedExamIndex];
                        seatButton.setBackground(getColorForExam(allocatedExamIndex));
                        seatButton.setForeground(Color.WHITE);
                        currentRolls[allocatedExamIndex]++;
                        lastExamIndex = allocatedExamIndex; // Update last exam index
                    }
                }


                else {
                    // No exams have rolls left, mark as vacant
                    rollNoText = "Vacant";
                    seatButton.setBackground(Color.WHITE);
                    seatButton.setForeground(Color.BLACK);
                }

                seatButton.setText(rollNoText);
                seatButton.setFont(new Font("Arial", Font.BOLD, 12));
                seatButton.setToolTipText("Seat for " + rollNoText);
                panel.add(seatButton);
            }

            tabbedPane.add("Class " + (i + 1), panel);
        }

        add(tabbedPane);
        setVisible(true);
    }

    private int findRemainingExam(int[] currentRolls, int[] rollEnd, int examCount) {
        for (int i = 0; i < examCount; i++) {
            if (currentRolls[i] <= rollEnd[i]) {
                return i;
            }
        }
        return -1;
    }

    private int findNextExam(int lastExamIndex, int[] currentRolls, int[] rollEnd, int examCount) {
        for (int offset = 1; offset < examCount; offset++) {
            int nextExamIndex = (lastExamIndex + offset) % examCount;
            if (currentRolls[nextExamIndex] <= rollEnd[nextExamIndex]) {
                return nextExamIndex;
            }
        }
        return -1; // This case won't occur if remainingExams > 1
    }

    private Color getColorForExam(int examIndex) {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.PINK};
        return colors[examIndex % colors.length];
    }
}




public class SeatingArrangementSystem extends JFrame {
    private JTextField examCountField, classCountField;
    private JTextField[] rollStartFields, rollEndFields;
    private JButton nextButton;

    public SeatingArrangementSystem() {
        setTitle("Seating Arrangement System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Number of Exams:"));
        examCountField = new JTextField();
        inputPanel.add(examCountField);

        inputPanel.add(new JLabel("Number of Classes:"));
        classCountField = new JTextField();
        inputPanel.add(classCountField);

        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int examCount = Integer.parseInt(examCountField.getText());
                int classCount = Integer.parseInt(classCountField.getText());
                gatherRollNumbers(examCount, classCount);
                dispose();
            }
        });

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(nextButton, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private void gatherRollNumbers(int examCount, int classCount) {
        rollStartFields = new JTextField[examCount];
        rollEndFields = new JTextField[examCount];
        
        JPanel rollPanel = new JPanel(new GridLayout(examCount + 1, 3, 10, 10));
        rollPanel.add(new JLabel("Exam"));
        rollPanel.add(new JLabel("Start Roll No"));
        rollPanel.add(new JLabel("End Roll No"));
        
        for (int i = 0; i < examCount; i++) {
            rollPanel.add(new JLabel("Exam " + (i + 1) + ":"));
            rollStartFields[i] = new JTextField();
            rollPanel.add(rollStartFields[i]);
            rollEndFields[i] = new JTextField();
            rollPanel.add(rollEndFields[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, rollPanel, "Enter Roll Number Ranges", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int[] rollStart = new int[examCount];
            int[] rollEnd = new int[examCount];
            for (int i = 0; i < examCount; i++) {
                rollStart[i] = Integer.parseInt(rollStartFields[i].getText());
                rollEnd[i] = Integer.parseInt(rollEndFields[i].getText());
            }
            new ClassInputFrame(examCount, classCount, rollStart, rollEnd);
        }
    }

    public static void main(String[] args) {
        new SeatingArrangementSystem();
    }
}
