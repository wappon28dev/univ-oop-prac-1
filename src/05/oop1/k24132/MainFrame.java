package oop1.k24132;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

enum ShapeType {
  CIRCLE("Circle"),
  RECTANGLE("Rectangle");

  public final String name;

  ShapeType(String name) {
    this.name = name;
  }

  public static ShapeType fromName(String name) {
    return switch (name) {
      case "Circle" -> CIRCLE;
      case "Rectangle" -> RECTANGLE;
      default -> throw new IllegalArgumentException("Unknown shape type: " + name);
    };
  }
}

enum Colors {
  RED(Color.RED, "赤"),
  BLUE(Color.BLUE, "青"),
  GREEN(Color.GREEN, "緑");

  public final Color value;
  public final String name;

  Colors(Color color, String name) {
    this.value = color;
    this.name = name;
  }

  public static Colors fromName(String name) {
    return switch (name) {
      case "赤" -> RED;
      case "青" -> BLUE;
      case "緑" -> GREEN;
      default -> throw new IllegalArgumentException("Unknown color name: " + name);
    };
  }
}

class Shape {
  protected int x;
  protected int y;
  protected Color color;

  public Shape(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public void draw(Graphics g) {
    throw new UnsupportedOperationException("draw() メソッドはサブクラスで実装してくださいね");
  };

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public Color getColor() {
    return this.color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}

class Circle extends Shape {
  private int radius;

  public Circle(int x, int y, int radius, Color color) {
    super(x, y, color);
    this.radius = radius;
  }

  public int getRadius() {
    return this.radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(this.color);
    g.fillOval(this.x - this.radius, this.y - this.radius, this.radius * 2, this.radius * 2);
  }
}

class Rectangle extends Shape {
  private int width;
  private int height;

  public Rectangle(int x, int y, int width, int height, Color color) {
    super(x, y, color);
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(this.color);
    g.fillRect(this.x, this.y, this.width, this.height);
  }
}

class DrawingPanel extends JPanel {
  private Shape[] shapes;
  private String currentShapeType;
  private Color currentColor;

  public DrawingPanel() {
    shapes = new Shape[0];
    setBackground(Color.WHITE);

    super.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        var shape = switch (ShapeType.fromName(currentShapeType)) {
          case CIRCLE -> new Circle(e.getX(), e.getY(), 30, currentColor);
          case RECTANGLE -> new Rectangle(e.getX(), e.getY(), 30, 30, currentColor);
        };

        addShape(shape);
        repaint();
      }
    });
  }

  public void setCurrentShapeType(String type) {
    this.currentShapeType = type;
  }

  public void setCurrentShapeType(ShapeType type) {
    this.currentShapeType = type.name;
  }

  public void setCurrentColor(Color color) {
    this.currentColor = color;
  }

  private List<Shape> getShapesAsList() {
    return List.of(shapes);
  }

  public void addShape(Shape shape) {
    var newShapes = new ArrayList<>(this.getShapesAsList());
    newShapes.add(shape);
    this.shapes = newShapes.toArray(Shape[]::new);
    return;
  }

  public void clearShapes() {
    this.shapes = new Shape[0];
    this.repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    this.getShapesAsList().forEach(shape -> shape.draw(g));
  }
}

public class MainFrame extends JFrame {
  private DrawingPanel drawingPanel;

  private void initWindow() {
    super.setTitle("図形描画");
    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    super.setSize(800, 600);
    super.setLocationRelativeTo(null);
  }

  private DrawingPanel createDrawingPanel() {
    DrawingPanel panel = new DrawingPanel();
    // 初期設定：円／青
    panel.setCurrentShapeType(ShapeType.CIRCLE);
    panel.setCurrentColor(Colors.BLUE.value);
    return panel;
  }

  private JToolBar createToolBar() {
    // 図形選択ラジオボタン
    var circleRadioButton = new JRadioButton("円");
    circleRadioButton.setActionCommand(ShapeType.CIRCLE.name());
    circleRadioButton.setSelected(true);

    var rectangleRadioButton = new JRadioButton("四角形");
    rectangleRadioButton.setActionCommand(ShapeType.RECTANGLE.name());

    ButtonGroup shapeGroup = new ButtonGroup();
    var shapeButtons = List.of(circleRadioButton, rectangleRadioButton);

    shapeButtons.forEach(shapeGroup::add);
    shapeButtons.forEach(radioButton -> {
      radioButton.addActionListener(e -> {
        drawingPanel.setCurrentShapeType(ShapeType.valueOf(e.getActionCommand()));
      });
    });

    // 色選択ラジオボタン
    var redRadioButton = new JRadioButton(Colors.RED.name);
    redRadioButton.setForeground(Colors.RED.value);
    var blueRadioButton = new JRadioButton(Colors.BLUE.name);
    blueRadioButton.setForeground(Colors.BLUE.value);
    var greenRadioButton = new JRadioButton(Colors.GREEN.name);
    greenRadioButton.setForeground(Colors.GREEN.value);

    var colorButtons = List.of(redRadioButton, blueRadioButton, greenRadioButton);
    var colorGroup = new ButtonGroup();

    colorButtons.forEach(colorGroup::add);
    colorButtons.forEach(radioButton -> {
      radioButton.addActionListener(e -> {
        var color = Colors.fromName(e.getActionCommand()).value;
        drawingPanel.setCurrentColor(color);
      });
    });

    // 初期設定：円／青
    drawingPanel.setCurrentShapeType(ShapeType.CIRCLE);
    drawingPanel.setCurrentColor(Colors.BLUE.value);
    blueRadioButton.setSelected(true);

    // クリアボタン
    var clearButton = new JButton("クリア");
    clearButton.addActionListener(e -> drawingPanel.clearShapes());

    // ツールバー組み立て
    var toolBar = new JToolBar();
    toolBar.add(new JLabel("図形: "));
    toolBar.add(circleRadioButton);
    toolBar.add(rectangleRadioButton);
    toolBar.addSeparator();
    toolBar.add(new JLabel("色: "));
    toolBar.add(redRadioButton);
    toolBar.add(blueRadioButton);
    toolBar.add(greenRadioButton);
    toolBar.addSeparator();
    toolBar.add(clearButton);

    return toolBar;
  }

  private void initComponents() {
    // 描画パネルとツールバーを生成して配置
    drawingPanel = createDrawingPanel();
    JToolBar toolBar = createToolBar();

    super.add(toolBar, BorderLayout.NORTH);
    super.add(drawingPanel, BorderLayout.CENTER);
  }

  public MainFrame() {
    initWindow();
    initComponents();

    super.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainFrame::new);
  }
}
