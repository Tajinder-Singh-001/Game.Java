import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PenaltyGame extends JPanel implements ActionListener {

    final int WIDTH = 800;
    final int HEIGHT = 500;

    int score = 0;
    int attempts = 0;
    final int MAX_ATTEMPTS = 5;

    // Game states
    String phase = "READY";
    String message = "Choose your shooting direction!";

    // Directions
    String playerDirection = "";
    String goalkeeperDirection = "";

    // Ball position
    double ballX = 390;
    double ballY = 330;

    double startBallX = 390;
    double startBallY = 330;

    double targetBallX = 390;
    double targetBallY = 110;

    // Goalkeeper position
    double keeperX = 375;
    double startKeeperX = 375;
    double targetKeeperX = 375;

    // Animation variables
    int animationFrame = 0;
    final int MAX_FRAMES = 60;

    boolean shotSaved = false;

    Random random = new Random();

    // Buttons
    JButton leftButton;
    JButton centerButton;
    JButton rightButton;
    JButton restartButton;

    // Animation timer
    Timer gameTimer;

    public PenaltyGame() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);

        leftButton = new JButton("LEFT");
        leftButton.setBounds(150, 440, 100, 35);
        leftButton.addActionListener(this);
        add(leftButton);

        centerButton = new JButton("CENTER");
        centerButton.setBounds(350, 440, 100, 35);
        centerButton.addActionListener(this);
        add(centerButton);

        rightButton = new JButton("RIGHT");
        rightButton.setBounds(550, 440, 100, 35);
        rightButton.addActionListener(this);
        add(rightButton);

        restartButton = new JButton("RESTART");
        restartButton.setBounds(650, 10, 120, 30);
        restartButton.addActionListener(this);
        add(restartButton);

        // Timer runs approximately 60 times per second
        gameTimer = new Timer(16, e -> updateGame());
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Football ground
        g2.setColor(new Color(40, 150, 60));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Ground lines
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));

        g2.drawRect(30, 20, 740, 390);

        // Penalty area
        g2.drawRect(180, 140, 440, 180);

        // Goalpost
        g2.setColor(Color.WHITE);
        g2.drawRect(280, 40, 240, 100);

        // Goal net
        g2.setColor(Color.LIGHT_GRAY);

        for (int x = 290; x < 520; x += 20) {
            g2.drawLine(x, 40, x, 140);
        }

        for (int y = 50; y < 140; y += 15) {
            g2.drawLine(280, y, 520, y);
        }

        // Draw goalkeeper
        drawGoalkeeper(g2);

        // Draw player
        drawPlayer(g2);

        // Draw animated football
        drawFootball(g2);

        // Scoreboard
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(20, 10, 230, 80, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));

        g2.drawString("PENALTY SHOOTOUT", 35, 35);
        g2.drawString("Score: " + score, 35, 60);
        g2.drawString("Attempts: " + attempts + "/" + MAX_ATTEMPTS, 35, 82);

        // Message
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.setColor(Color.YELLOW);

        FontMetrics fm = g2.getFontMetrics();

        int messageX = (WIDTH - fm.stringWidth(message)) / 2;

        g2.drawString(message, messageX, 200);

        // Goal celebration
        if (phase.equals("RESULT") && !shotSaved) {

            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 35));

            g2.drawString("GOOOAL!", 325, 250);
        }

        // Save message
        if (phase.equals("RESULT") && shotSaved) {

            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 30));

            g2.drawString("SAVED!", 345, 250);
        }
    }

    // Draw goalkeeper
    public void drawGoalkeeper(Graphics2D g2) {

        int x = (int) keeperX;

        // Head
        g2.setColor(Color.ORANGE);
        g2.fillOval(x, 65, 30, 30);

        // Body
        g2.setColor(Color.BLUE);
        g2.fillRoundRect(x - 5, 95, 40, 40, 10, 10);

        // Arms
        g2.setStroke(new BasicStroke(6));
        g2.drawLine(x - 5, 105, x - 25, 90);
        g2.drawLine(x + 35, 105, x + 55, 90);

        // Legs
        g2.drawLine(x + 5, 135, x - 5, 150);
        g2.drawLine(x + 30, 135, x + 40, 150);
    }

    // Draw player
    public void drawPlayer(Graphics2D g2) {

        // Head
        g2.setColor(Color.ORANGE);
        g2.fillOval(385, 275, 30, 30);

        // Body
        g2.setColor(Color.BLUE);
        g2.fillRect(380, 305, 40, 50);

        // Legs
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(390, 355, 380, 380);
        g2.drawLine(410, 355, 420, 380);
    }

    // Draw football
    public void drawFootball(Graphics2D g2) {

        int x = (int) ballX;
        int y = (int) ballY;

        g2.setColor(Color.WHITE);
        g2.fillOval(x - 12, y - 12, 24, 24);

        g2.setColor(Color.BLACK);
        g2.drawOval(x - 12, y - 12, 24, 24);

        // Football pattern
        g2.fillOval(x - 4, y - 4, 8, 8);
    }

    // Button actions
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == restartButton) {

            restartGame();
            return;
        }

        if (!phase.equals("READY")) {
            return;
        }

        if (attempts >= MAX_ATTEMPTS) {
            return;
        }

        if (e.getSource() == leftButton) {
            playerDirection = "LEFT";
        }
        else if (e.getSource() == centerButton) {
            playerDirection = "CENTER";
        }
        else if (e.getSource() == rightButton) {
            playerDirection = "RIGHT";
        }
        else {
            return;
        }

        startShot();
    }

    // Start shooting animation
    public void startShot() {

        String[] directions = {
            "LEFT", "CENTER", "RIGHT"
        };

        goalkeeperDirection =
            directions[random.nextInt(3)];

        // Check whether goalkeeper saves the shot
        shotSaved = playerDirection.equals(goalkeeperDirection);

        // Set ball target
        if (playerDirection.equals("LEFT")) {
            targetBallX = 320;
        }
        else if (playerDirection.equals("CENTER")) {
            targetBallX = 390;
        }
        else {
            targetBallX = 460;
        }

        targetBallY = 110;

        // Set goalkeeper target
        if (goalkeeperDirection.equals("LEFT")) {
            targetKeeperX = 295;
        }
        else if (goalkeeperDirection.equals("CENTER")) {
            targetKeeperX = 375;
        }
        else {
            targetKeeperX = 455;
        }

        startBallX = ballX;
        startBallY = ballY;

        startKeeperX = keeperX;

        animationFrame = 0;

        attempts++;

        phase = "SHOOTING";

        message = "Shot in progress...";

        enableButtons(false);

        gameTimer.start();
    }

    // Update animations
    public void updateGame() {

        if (phase.equals("SHOOTING")) {

            animationFrame++;

            double t = Math.min(
                1.0,
                animationFrame / (double) MAX_FRAMES
            );

            // Smooth movement
            double smooth = t * t * (3 - 2 * t);

            // Football movement
            ballX = startBallX +
                (targetBallX - startBallX) * smooth;

            ballY = startBallY +
                (targetBallY - startBallY) * t
                - 35 * Math.sin(Math.PI * t);

            // Goalkeeper movement
            keeperX = startKeeperX +
                (targetKeeperX - startKeeperX) * smooth;

            if (animationFrame >= MAX_FRAMES) {

                ballX = targetBallX;
                ballY = targetBallY;
                keeperX = targetKeeperX;

                gameTimer.stop();

                if (shotSaved) {

                    message = "The goalkeeper saved your shot!";
                }
                else {

                    score++;

                    message = "GOAL! Great shot!";
                }

                phase = "RESULT";

                // Wait before next shot
                Timer nextTimer = new Timer(1300, e -> {

                    ((Timer)e.getSource()).stop();

                    if (attempts >= MAX_ATTEMPTS) {

                        message = "GAME OVER! Final Score: " + score;

                        phase = "FINISHED";
                    }
                    else {

                        resetPositions();

                        phase = "READY";

                        message = "Choose your next shot!";

                        enableButtons(true);
                    }

                    repaint();
                });

                nextTimer.setRepeats(false);
                nextTimer.start();
            }
        }

        repaint();
    }

    // Reset ball and goalkeeper
    public void resetPositions() {

        ballX = 390;
        ballY = 330;

        keeperX = 375;

        playerDirection = "";
        goalkeeperDirection = "";
    }

    // Restart complete game
    public void restartGame() {

        gameTimer.stop();

        score = 0;
        attempts = 0;

        resetPositions();

        animationFrame = 0;

        shotSaved = false;

        phase = "READY";

        message = "Choose your shooting direction!";

        enableButtons(true);

        repaint();
    }

    // Enable or disable buttons
    public void enableButtons(boolean enabled) {

        leftButton.setEnabled(enabled);
        centerButton.setEnabled(enabled);
        rightButton.setEnabled(enabled);
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(
                "2D Penalty Shootout Game"
            );

            PenaltyGame game = new PenaltyGame();

            frame.add(game);

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.pack();

            frame.setLocationRelativeTo(null);

            frame.setResizable(false);

            frame.setVisible(true);
        });
    }
}