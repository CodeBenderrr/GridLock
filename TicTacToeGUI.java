import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToeGUI extends JFrame implements ActionListener {
    JButton[] buttons = new JButton[9];
    JButton exitButton, darkModeButton;
    JLabel statusLabel, scoreLabel;

    String player1Name, player2Name;
    String player1Symbol = "X", player2Symbol = "O";
    boolean player1Turn = true;
    boolean isDarkMode = false;

    int totalRounds = 1, currentRound = 1;
    int player1Score = 0, player2Score = 0, draws = 0;

    JPanel gamePanel, controlPanel, topPanel;

    public TicTacToeGUI() {
        setTitle("Tic Tac Toe");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupPlayerSettings();

      
        gamePanel = new JPanel(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 100);

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(font);
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this);
            gamePanel.add(buttons[i]);
        }

     
        controlPanel = new JPanel(new FlowLayout());
        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        darkModeButton = new JButton("Dark Mode");
        darkModeButton.addActionListener(e -> toggleDarkMode());

        controlPanel.add(darkModeButton);
        controlPanel.add(exitButton);

     
        topPanel = new JPanel(new GridLayout(2, 1));
        statusLabel = new JLabel(getCurrentPlayerName() + "'s Turn (" + getCurrentSymbol() + ")", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        scoreLabel = new JLabel(getScoreText(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        topPanel.add(statusLabel);
        topPanel.add(scoreLabel);

        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        applyTheme(); 
        setVisible(true);
    }

    private void setupPlayerSettings() {
        player1Name = JOptionPane.showInputDialog(this, "Enter name for Player 1:");
        if (player1Name == null || player1Name.isEmpty()) player1Name = "Player 1";

        player2Name = JOptionPane.showInputDialog(this, "Enter name for Player 2:");
        if (player2Name == null || player2Name.isEmpty()) player2Name = "Player 2";

      
        String[] symbols = {"X", "O"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                player1Name + ", choose your symbol:",
                "Symbol Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                symbols,
                symbols[0]);

        if (choice == null) System.exit(0);

        player1Symbol = choice;
        player2Symbol = player1Symbol.equals("X") ? "O" : "X";

        
        String roundsInput = JOptionPane.showInputDialog(this, "Enter number of rounds to play:");
        try {
            totalRounds = Integer.parseInt(roundsInput);
            if (totalRounds <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            totalRounds = 1;
        }

        askFirstTurn();
    }

    private void askFirstTurn() {
        String[] options = {player1Name, player2Name};
        String first = (String) JOptionPane.showInputDialog(
                this,
                "Who should start Round " + currentRound + "?",
                "Choose First Turn",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (first == null) System.exit(0);
        player1Turn = first.equals(player1Name);
    }

    
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton) e.getSource();

        if (!clicked.getText().equals("")) return;

        String currentSymbol = getCurrentSymbol();
        Color currentColor = player1Turn ? Color.BLUE : Color.RED;

        clicked.setText(currentSymbol);
        clicked.setForeground(currentColor);

        if (checkWin(currentSymbol)) {
            String winner = getCurrentPlayerName();
            statusLabel.setText(winner + " wins Round " + currentRound + "!");
            if (player1Turn) player1Score++; else player2Score++;
            finishRound();
        } else if (isBoardFull()) {
            statusLabel.setText("Round " + currentRound + " is a draw!");
            draws++;
            finishRound();
        } else {
            player1Turn = !player1Turn;
            statusLabel.setText(getCurrentPlayerName() + "'s Turn (" + getCurrentSymbol() + ")");
        }
    }

    private void finishRound() {
        disableButtons();
        scoreLabel.setText(getScoreText());

        currentRound++;
        if (currentRound > totalRounds) {
            announceFinalWinner();
        } else {
            Timer timer = new Timer(2000, e -> resetBoard());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private String getCurrentSymbol() {
        return player1Turn ? player1Symbol : player2Symbol;
    }

    private String getCurrentPlayerName() {
        return player1Turn ? player1Name : player2Name;
    }

    private String getScoreText() {
        return "Round " + currentRound + " of " + totalRounds + " | "
                + player1Name + ": " + player1Score + " | "
                + player2Name + ": " + player2Score + " | Draws: " + draws;
    }

    private void announceFinalWinner() {
        String message;
        if (player1Score > player2Score) {
            message = player1Name + " wins the game!";
        } else if (player2Score > player1Score) {
            message = player2Name + " wins the game!";
        } else {
            message = "The game is a draw!";
        }

        JOptionPane.showMessageDialog(this, message + "\n\nFinal Score:\n"
                + player1Name + ": " + player1Score + "\n"
                + player2Name + ": " + player2Score + "\nDraws: " + draws,
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void resetBoard() {
        askFirstTurn();
        for (JButton b : buttons) {
            b.setText("");
            b.setEnabled(true);
            b.setBackground(isDarkMode ? Color.DARK_GRAY : null);
        }

        statusLabel.setText(getCurrentPlayerName() + "'s Turn (" + getCurrentSymbol() + ")");
    }

    private boolean checkWin(String symbol) {
        int[][] combos = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] c : combos) {
            if (buttons[c[0]].getText().equals(symbol) &&
                buttons[c[1]].getText().equals(symbol) &&
                buttons[c[2]].getText().equals(symbol)) {
                highlightWinner(c);
                return true;
            }
        }
        return false;
    }

    private void highlightWinner(int[] combo) {
        for (int i : combo) {
            buttons[i].setBackground(Color.GREEN);
        }
    }

    private boolean isBoardFull() {
        for (JButton b : buttons) {
            if (b.getText().equals("")) return false;
        }
        return true;
    }

    private void disableButtons() {
        for (JButton b : buttons) {
            b.setEnabled(false);
        }
    }

    private void toggleDarkMode() {
    isDarkMode = !isDarkMode;
    applyTheme();
    darkModeButton.setText(isDarkMode ? "Light Mode" : "Dark Mode");
    }


    private void applyTheme() {
        Color bg = isDarkMode ? Color.BLACK : Color.WHITE;
        Color fg = isDarkMode ? Color.WHITE : Color.BLACK;

        gamePanel.setBackground(bg);
        controlPanel.setBackground(bg);
        topPanel.setBackground(bg);

        statusLabel.setForeground(fg);
        scoreLabel.setForeground(fg);
        statusLabel.setBackground(bg);
        scoreLabel.setBackground(bg);

        for (JButton b : buttons) {
            b.setBackground(isDarkMode ? Color.DARK_GRAY : null);
            b.setForeground(Color.BLACK); 
        }

        darkModeButton.setBackground(isDarkMode ? Color.GRAY : null);
        exitButton.setBackground(isDarkMode ? Color.GRAY : null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeGUI());
    }
}
