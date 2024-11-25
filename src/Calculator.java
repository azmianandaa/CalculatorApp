import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

public class Calculator extends JFrame {
  private JTextField display;
  private JLabel expressionLabel;
  private double result = 0;
  private String lastCommand = "=";
  private boolean start = true;
  private StringBuilder expression = new StringBuilder();

  private final Color DARK_PURPLE = new Color(103, 58, 183);
  private final Color LIGHT_PURPLE = new Color(149, 117, 205);
  private final Color PINK_ACCENT = new Color(233, 30, 99);
  private final Color TEAL_ACCENT = new Color(0, 150, 136);
  private final Color ORANGE_ACCENT = new Color(255, 87, 34);
  private final Color BG_COLOR = new Color(240, 240, 245);
  private final Color TEXT_COLOR = Color.WHITE;
  private final Color DISPLAY_BG = new Color(245, 245, 245);

  private final Color PRESSED_PURPLE = new Color(81, 45, 168);
  private final Color PRESSED_LIGHT_PURPLE = new Color(126, 87, 194);
  private final Color PRESSED_PINK = new Color(194, 24, 91);
  private final Color PRESSED_TEAL = new Color(0, 121, 107);
  private final Color PRESSED_ORANGE = new Color(230, 74, 25);

  public Calculator() {
    setTitle("Modern Calculator");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("Could not set look and feel: " + e.getMessage());
    }

    setLayout(new BorderLayout(5, 5));
    getContentPane().setBackground(BG_COLOR);

    JPanel displayPanel = new JPanel(new BorderLayout(5, 5));
    displayPanel.setBackground(BG_COLOR);
    displayPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

    expressionLabel = new JLabel(" ");
    expressionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    expressionLabel.setHorizontalAlignment(JLabel.RIGHT);
    expressionLabel.setForeground(DARK_PURPLE);
    expressionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
    displayPanel.add(expressionLabel, BorderLayout.NORTH);

    display = new JTextField("0") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        super.paintComponent(g);
        g2.dispose();
      }
    };
    display.setFont(new Font("Arial", Font.BOLD, 36));
    display.setHorizontalAlignment(JTextField.RIGHT);
    display.setEditable(false);
    display.setBackground(DISPLAY_BG);
    display.setForeground(DARK_PURPLE);
    display.setOpaque(false);
    display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel roundedDisplayPanel = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(DISPLAY_BG);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        g2.dispose();
      }
    };
    roundedDisplayPanel.setOpaque(false);
    roundedDisplayPanel.add(display);
    roundedDisplayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    displayPanel.add(roundedDisplayPanel);

    add(displayPanel, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 12, 12));
    buttonPanel.setBackground(BG_COLOR);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

    String[] buttonLabels = {
        "C", "←", "%", "/",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "±", "0", ".", "="
    };

    for (String label : buttonLabels) {
      JButton button = createRoundButton(label);
      button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      buttonPanel.add(button);
    }

    add(buttonPanel, BorderLayout.CENTER);

    addKeyboardSupport();

    pack();
    setSize(380, 580);
    setLocationRelativeTo(null);
  }

  private void addKeyboardSupport() {
    setFocusable(true);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        handleKeyPress(e);
      }
    });

    display.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        handleKeyPress(e);
      }
    });

    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowOpened(java.awt.event.WindowEvent evt) {
        requestFocus();
      }
    });
  }

  private void handleKeyPress(KeyEvent e) {
    if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
      return;
    }

    switch (e.getKeyChar()) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '.':
        numberPressed(String.valueOf(e.getKeyChar()));
        break;

      case '+':
        operatorPressed("+");
        break;
      case '-':
        operatorPressed("-");
        break;
      case '*':
      case '×':
        operatorPressed("×");
        break;
      case '/':
        operatorPressed("/");
        break;
      case '%':
        specialButtonPressed("%");
        break;

      case KeyEvent.VK_ENTER:
      case '=':
        operatorPressed("=");
        break;
      case KeyEvent.VK_ESCAPE:
        specialButtonPressed("C");
        break;
    }

    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
      specialButtonPressed("←");
    }

    e.consume();
  }

  private JButton createRoundButton(String label) {
    JButton button = new JButton(label) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
          g2.setColor(getPressedColor(label));
        } else if (getModel().isRollover()) {
          g2.setColor(getBackground().brighter());
        } else {
          g2.setColor(getBackground());
        }

        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

        if (!getModel().isPressed()) {
          GradientPaint gradient = new GradientPaint(
              0, 0, new Color(255, 255, 255, 50),
              0, getHeight(), new Color(255, 255, 255, 0));
          g2.setPaint(gradient);
          g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        }

        if (!getModel().isPressed()) {
          g2.setColor(new Color(0, 0, 0, 30));
          g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }

        FontMetrics metrics = g2.getFontMetrics(getFont());
        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);
        g2.dispose();
      }
    };

    button.setFont(new Font("Arial", Font.BOLD, 20));
    button.setForeground(TEXT_COLOR);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);

    switch (label) {
      case "+":
      case "-":
      case "×":
      case "/":
        button.setBackground(DARK_PURPLE);
        break;
      case "=":
        button.setBackground(PINK_ACCENT);
        break;
      case "C":
      case "←":
        button.setBackground(ORANGE_ACCENT);
        break;
      case "%":
      case "±":
        button.setBackground(TEAL_ACCENT);
        break;
      default:
        button.setBackground(LIGHT_PURPLE);
    }

    button.addActionListener(e -> {
      String command = e.getActionCommand();
      if (command.matches("[0-9.]")) {
        numberPressed(command);
      } else if (command.matches("[+\\-×/=]")) {
        operatorPressed(command);
      } else {
        specialButtonPressed(command);
      }
    });

    return button;
  }

  private Color getPressedColor(String label) {
    switch (label) {
      case "+":
      case "-":
      case "×":
      case "/":
        return PRESSED_PURPLE;
      case "=":
        return PRESSED_PINK;
      case "C":
      case "←":
        return PRESSED_ORANGE;
      case "%":
      case "±":
        return PRESSED_TEAL;
      default:
        return PRESSED_LIGHT_PURPLE;
    }
  }

  private void numberPressed(String number) {
    if (start) {
      display.setText(number);
      start = false;
    } else {
      display.setText(display.getText() + number);
    }
  }

  private void operatorPressed(String command) {
    if (!start) {
      calculate(Double.parseDouble(display.getText()));
      lastCommand = command;
      if (!command.equals("=")) {
        expression.append(display.getText()).append(" ").append(command).append(" ");
        expressionLabel.setText(expression.toString());
      } else {
        expression = new StringBuilder();
        expressionLabel.setText(" ");
      }
      start = true;
    }
  }

  private void specialButtonPressed(String command) {
    switch (command) {
      case "C":
        result = 0;
        lastCommand = "=";
        display.setText("0");
        expression = new StringBuilder();
        expressionLabel.setText(" ");
        start = true;
        break;
      case "←":
        if (!start) {
          String text = display.getText();
          if (text.length() > 1 && !text.equals("0")) {
            display.setText(text.substring(0, text.length() - 1));
          } else {
            display.setText("0");
            start = true;
          }
        }
        break;
      case "±":
        if (!start) {
          double x = Double.parseDouble(display.getText());
          display.setText(String.valueOf(-x));
        }
        break;
      case "%":
        if (!start) {
          try {
            double x = Double.parseDouble(display.getText());
            switch (lastCommand) {
              case "+":
              case "-":
                // Calculate percentage of previous result
                x = result * (x / 100.0);
                if (lastCommand.equals("+")) {
                  result += x;
                } else {
                  result -= x;
                }
                break;
              case "×":
                // Converts percentage to decimal for multiplication
                result *= (x / 100.0);
                break;
              case "/":
                // Converts percentage to decimal for division
                result /= (x / 100.0);
                break;
              case "=":
                // If no previous operation, treat as a percentage of the current number
                result = x / 100.0;
                break;
            }

            if (result == (long) result) {
              display.setText(String.format("%d", (long) result));
            } else {
              display.setText(String.format("%.8g", result));
            }

            start = true;
            lastCommand = "=";
          } catch (NumberFormatException e) {
            display.setText("Error");
            start = true;
          }
        }
        break;
    }
  }

  private void calculate(double x) {
    if (Double.isInfinite(x) || Double.isNaN(x)) {
      display.setText("Error");
      result = 0;
      start = true;
      return;
    }

    switch (lastCommand) {
      case "+":
        result += x;
        break;
      case "-":
        result -= x;
        break;
      case "×":
        result *= x;
        break;
      case "/":
        try {
          if (x == 0) {
            display.setText("Div by Zero");
            result = 0;
            start = true;
            return;
          }
          result /= x;
        } catch (ArithmeticException e) {
          display.setText("Error");
          result = 0;
          start = true;
          return;
        }
        break;
      case "=":
        result = x;
        break;
    }

    if (Math.abs(result) < 1e-10) {
      result = 0;
    }

    if (result == (long) result) {
      display.setText(String.format("%d", (long) result));
    } else {
      display.setText(String.format("%.8g", result));
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      Calculator calc = new Calculator();
      calc.setVisible(true);
    });
  }
}