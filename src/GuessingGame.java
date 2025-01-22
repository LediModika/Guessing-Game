import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GuessingGame extends JFrame {
    private Card[][] board;
    private JButton[][] buttons;
    private int boardSize;
    private Card firstSelection = null;
    private Card secondSelection = null;
    private JButton firstButton = null;
    private boolean waitingForMatch = false;

    public GuessingGame(int boardSize) {
        if (boardSize % 2 != 0) {
            throw new IllegalArgumentException("Board size must be even.");
        }

        this.boardSize = boardSize;
        this.board = new Card[boardSize][boardSize];
        this.buttons = new JButton[boardSize][boardSize];

        initialiseBoard();
        shuffleBoard();
        setupGUI();
        showInitialBoard();  // Add initial board display
    }

    private void initialiseBoard() {
        int totalCards = boardSize * boardSize;
        int[] numbers = new int[totalCards];
        for (int i = 0; i < totalCards; i++) {
            numbers[i] = i / 2 + 1; // Ensure pairs
        }

        // Shuffle the numbers
        Random random = new Random();
        for (int i = 0; i < totalCards; i++) {
            int swapIndex = random.nextInt(totalCards);
            int temp = numbers[i];
            numbers[i] = numbers[swapIndex];
            numbers[swapIndex] = temp;
        }

        // Assign numbers to the board
        int index = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = new Card(numbers[index++]);
            }
        }
    }

    private void showInitialBoard() {
        // Show all numbers initially
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j].setText(String.valueOf(board[i][j].getNumber()));
            }
        }

        // Create a timer to hide the numbers after 3 seconds
        Timer hideTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        buttons[i][j].setText("*");
                    }
                }
            }
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void shuffleBoard() {
        Random random = new Random();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int x = random.nextInt(boardSize);
                int y = random.nextInt(boardSize);

                Card temp = board[i][j];
                board[i][j] = board[x][y];
                board[x][y] = temp;
            }
        }
    }

    private void setupGUI() {
        setTitle("Guessing Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(boardSize, boardSize));

        // Create buttons
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                JButton button = new JButton("*");
                buttons[i][j] = button;

                int row = i;
                int col = j;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleCardSelection(row, col, button);
                    }
                });

                add(button);
            }
        }

        setVisible(true);
    }

    private void handleCardSelection(int row, int col, JButton button) {
        if (waitingForMatch || board[row][col].isFaceUp()) {
            return;
        }

        board[row][col].flipCard();
        button.setText(String.valueOf(board[row][col].getNumber()));

        if (firstSelection == null) {
            firstSelection = board[row][col];
            firstButton = button;
        } else if (secondSelection == null) {
            secondSelection = board[row][col];
            waitingForMatch = true;

            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (firstSelection.compareCard(secondSelection)) {
                        firstButton.setEnabled(false);
                        button.setEnabled(false);
                    } else {
                        firstSelection.flipCard();
                        secondSelection.flipCard();
                        firstButton.setText("*");
                        button.setText("*");
                    }

                    firstSelection = null;
                    secondSelection = null;
                    firstButton = null;
                    waitingForMatch = false;

                    if (isGameOver()) {
                        JOptionPane.showMessageDialog(null, "Congratulations! You've matched all cards!");
                        System.exit(0);
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private boolean isGameOver() {
        for (Card[] row : board) {
            for (Card card : row) {
                if (!card.isFaceUp()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new GuessingGame(4); // Creates a 4x4 board
    }

    // Inner class for Card
    static class Card {
        private int number;
        private boolean faceUp;

        public Card(int number) {
            this.number = number;
            this.faceUp = false;
        }

        public int getNumber() {
            return number;
        }

        public boolean isFaceUp() {
            return faceUp;
        }

        public void flipCard() {
            faceUp = !faceUp;
        }

        public boolean compareCard(Card other) {
            return this.number == other.number;
        }
    }
}