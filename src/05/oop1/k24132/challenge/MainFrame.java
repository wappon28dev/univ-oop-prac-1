package oop1.k24132.challenge;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

enum ShapeType {
  CIRCLE(Circle.class),
  RECTANGLE(Rectangle.class),
  TRIANGLE(Triangle.class);

  public final Class<? extends Shape> shapeClass;

  ShapeType(Class<? extends Shape> shapeClass) {
    this.shapeClass = shapeClass;
  }

  public static ShapeType from(String commandName) {
    return switch (commandName) {
      case Circle.commandName -> CIRCLE;
      case Rectangle.commandName -> RECTANGLE;
      case Triangle.commandName -> TRIANGLE;
      default -> throw new IllegalArgumentException("Unknown shape type: " + commandName);
    };
  }
}

enum Colors {
  RED("赤", Color.RED),
  BLUE("青", Color.BLUE),
  GREEN("緑", Color.GREEN);

  public final String displayName;
  public final Color value;

  Colors(String displayName, Color color) {
    this.displayName = displayName;
    this.value = color;
  }

  public static Colors from(String displayName) {
    return switch (displayName) {
      case "赤" -> RED;
      case "青" -> BLUE;
      case "緑" -> GREEN;
      default -> throw new IllegalArgumentException("Unknown color name: " + displayName);
    };
  }

  public JRadioButton toJRadioButton() {
    var radioButton = new JRadioButton(this.displayName);
    radioButton.setForeground(this.value);
    radioButton.setActionCommand(this.displayName);
    return radioButton;
  }
}

class Shape {
  protected int x;
  protected int y;
  protected Colors color;

  public Shape(int x, int y, Colors color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public void draw(Graphics g) {
    throw new UnsupportedOperationException("draw() メソッドはサブクラスで実装してくださいね〜");
  }

  @FunctionalInterface
  public interface Factory {
    Shape create(Point p1, Point p2);
  }

  public static Factory fromPoints(ShapeType type, Colors color) {
    throw new UnsupportedOperationException("fromPoints() メソッドはサブクラスで実装してくださいね〜");
  }
}

class Circle extends Shape {
  public static final String commandName = "Circle";
  public static final String displayName = "円";
  public int radius;

  public Circle(int x, int y, int radius, Colors color) {
    super(x, y, color);
    this.radius = radius;
  }

  public static Shape.Factory fromPoints(Colors color) {
    return (Point p1, Point p2) -> {
      int r = (int) p1.distance(p2);
      if (r > 0) {
        return new Circle(p1.x, p1.y, r, color);
      }
      return null;
    };
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(this.color.value);
    g.fillOval(this.x - this.radius, this.y - this.radius, this.radius * 2, this.radius * 2);
  }

  public static JRadioButton toJRadioButton() {
    var radioButton = new JRadioButton(Circle.displayName);
    radioButton.setActionCommand(Circle.commandName);
    return radioButton;
  }
}

class Rectangle extends Shape {
  public static final String commandName = "Rectangle";
  public static final String displayName = "長方形";

  public final int width;
  public final int height;

  public Rectangle(int x, int y, int width, int height, Colors color) {
    super(x, y, color);
    this.width = width;
    this.height = height;
  }

  public static Shape.Factory fromPoints(Colors color) {
    return (Point p1, Point p2) -> {
      var x = Math.min(p1.x, p2.x);
      var y = Math.min(p1.y, p2.y);
      var w = Math.abs(p1.x - p2.x);
      var h = Math.abs(p1.y - p2.y);
      if (w > 0 && h > 0) {
        return new Rectangle(x, y, w, h, color);
      }
      return null;
    };
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(this.color.value);
    g.fillRect(this.x, this.y, this.width, this.height);
  }

  public static JRadioButton toJRadioButton() {
    var radioButton = new JRadioButton(Rectangle.displayName);
    radioButton.setActionCommand(Rectangle.commandName);
    return radioButton;
  }
}

class Triangle extends Shape {
  public static final String commandName = "Triangle";
  public static final String displayName = "三角形";
  private final List<Integer> xPoints;
  private final List<Integer> yPoints;

  public Triangle(List<Integer> xPoints, List<Integer> yPoints, Colors color) {
    // NOTE: super の x,y は描画に使ってない（泣）
    super(xPoints.get(0), yPoints.get(0), color);
    this.xPoints = xPoints;
    this.yPoints = yPoints;
  }

  public static Shape.Factory fromPoints(Colors color) {
    return (Point p1, Point p2) -> {
      var x = Math.min(p1.x, p2.x);
      var y = Math.min(p1.y, p2.y);
      var width = Math.abs(p2.x - p1.x);
      var height = Math.abs(p2.y - p1.y);
      var isValid = (width > 0 && height > 0);

      if (!isValid) {
        return null;
      }

      // 頂点
      var x1 = x + width / 2;
      var y1 = y;

      // 下端左
      var x2 = x;
      var y2 = y + height;

      // 下端右
      var x3 = x + width;
      var y3 = y + height;

      return new Triangle(
          List.of(x1, x2, x3),
          List.of(y1, y2, y3),
          color);
    };
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(this.color.value);
    var xPoints = this.xPoints.stream().mapToInt(i -> i).toArray();
    var yPoints = this.yPoints.stream().mapToInt(i -> i).toArray();
    g.fillPolygon(xPoints, yPoints, 3);
  }

  public static JRadioButton toJRadioButton() {
    var radioButton = new JRadioButton(displayName);
    radioButton.setActionCommand(commandName);
    return radioButton;
  }
}

class DrawingPanel extends JPanel {
  public List<Shape> shapes;
  public ShapeType currentShapeType;
  public Colors currentColor;
  private Point startPoint;
  private Shape previewShape;

  private MouseInputAdapter mouseInputHandler = new MouseInputAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
      startPoint = e.getPoint();
      previewShape = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (startPoint == null) {
        return;
      }

      previewShape = makeShape(startPoint, e.getPoint());
      repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (startPoint == null) {
        return;
      }

      var finalShape = makeShape(startPoint, e.getPoint());
      if (finalShape != null) {
        shapes.add(finalShape);
      }

      startPoint = null;
      previewShape = null;
      repaint();
    }
  };

  private Shape makeShape(Point p1, Point p2) {
    var shape = switch (currentShapeType) {
      case CIRCLE -> Circle.fromPoints(currentColor);
      case RECTANGLE -> Rectangle.fromPoints(currentColor);
      case TRIANGLE -> Triangle.fromPoints(currentColor);
    };

    return shape.create(p1, p2);
  }

  public DrawingPanel() {
    shapes = new ArrayList<>();
    super.setBackground(Color.WHITE);
    super.addMouseListener(mouseInputHandler);
    super.addMouseMotionListener(mouseInputHandler);
  }

  public void addShape(Shape shape) {
    shapes.add(shape);
  }

  public void clearShapes() {
    this.shapes.clear();
    this.repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    shapes.forEach(shape -> shape.draw(g));
    if (previewShape != null) {
      previewShape.draw(g);
    }
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
    panel.currentShapeType = ShapeType.CIRCLE;
    panel.currentColor = Colors.BLUE;
    return panel;
  }

  private JToolBar createToolBar() {
    // 図形選択ラジオボタン
    var circleRadioButton = Circle.toJRadioButton();
    var rectangleRadioButton = Rectangle.toJRadioButton();
    var triangleRadioButton = Triangle.toJRadioButton();

    ButtonGroup shapeGroup = new ButtonGroup();
    var shapeButtons = List.of(circleRadioButton, rectangleRadioButton, triangleRadioButton);

    shapeButtons.forEach(shapeGroup::add);
    shapeButtons.forEach(radioButton -> {
      radioButton.addActionListener(e -> {
        drawingPanel.currentShapeType = ShapeType.from(e.getActionCommand());
      });
    });

    // 色選択ラジオボタン
    var redRadioButton = Colors.RED.toJRadioButton();
    var blueRadioButton = Colors.BLUE.toJRadioButton();
    var greenRadioButton = Colors.GREEN.toJRadioButton();

    var colorButtons = List.of(redRadioButton, blueRadioButton, greenRadioButton);
    var colorGroup = new ButtonGroup();

    colorButtons.forEach(colorGroup::add);
    colorButtons.forEach(radioButton -> {
      radioButton.addActionListener(e -> {
        drawingPanel.currentColor = Colors.from(e.getActionCommand());
      });
    });

    // 初期設定：円／青
    circleRadioButton.setSelected(true);
    blueRadioButton.setSelected(true);

    // クリアボタン
    var clearButton = new JButton("クリア");
    clearButton.addActionListener(e -> drawingPanel.clearShapes());

    // ツールバー組み立て
    var toolBar = new JToolBar();
    toolBar.add(new JLabel("図形: "));
    shapeButtons.forEach(toolBar::add);
    toolBar.addSeparator();
    toolBar.add(new JLabel("色: "));
    colorButtons.forEach(toolBar::add);
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
