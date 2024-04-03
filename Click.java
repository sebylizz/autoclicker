import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Click {

    private static volatile boolean running = false;
    private static volatile boolean ctrlPressed = false; // Track the state of the CTRL key
    private static Robot robot;

    public static void main(String[] args) {
        new Click();
    }

    public Click() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Failed to create Robot instance: " + e.getMessage());
            System.exit(1);
        }

        JFrame frame = new JFrame("AAU klik");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 120);
        frame.setLayout(new FlowLayout());

        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JTextField timeInput = new JTextField("2000");
        timeInput.setPreferredSize(new Dimension(75, 25)); // Set preferred size
        JLabel desc = new JLabel("milisekunder");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    running = true;
                    int temp = Integer.valueOf(timeInput.getText());
                    if(temp < 500){timeInput.setText("500"); temp = 500;}
                    startAutoClicker(temp);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
            }
        });

        timeInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlPressed = false;
                }
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        
        frame.add(timeInput);
        frame.add(desc);
        frame.add(startButton);
        frame.add(stopButton);


        frame.setVisible(true);
    }

    private static void startAutoClicker(int interval) {
        Thread autoClickThread = new Thread(new Runnable() {
            public void run() {
                long lastMoveTime = System.currentTimeMillis();
                Point lastPoint = MouseInfo.getPointerInfo().getLocation();
                Point lastClicked = new Point(0, 0);

                while (running) {
                    Point currentPoint = MouseInfo.getPointerInfo().getLocation();
                    
                    if (!currentPoint.equals(lastPoint)) {
                        lastMoveTime = System.currentTimeMillis();
                        lastPoint = currentPoint;
                    }

                    try {
                        if (System.currentTimeMillis() - lastMoveTime >= interval && !currentPoint.equals(lastClicked)) {
                            lastMoveTime = System.currentTimeMillis();
                            currentPoint = MouseInfo.getPointerInfo().getLocation();
                            robot.mouseMove((int)currentPoint.getX(),(int) currentPoint.getY());

                            System.out.println(ctrlPressed);

                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            lastClicked = currentPoint;
                        }

                        Thread.sleep(500);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        autoClickThread.start();
    }
}
