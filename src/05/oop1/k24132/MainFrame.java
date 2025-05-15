package oop1.k24132;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

abstract class Shape {
  protected int x;
  protected int y;
  protected Color color;

  public Shape(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public abstract void draw(Graphics g);

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public Color getColor() {
    return color;
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
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(color);
    g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
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
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public void draw(Graphics g) {
    g.setColor(color);
    g.fillRect(x, y, width, height);
  }
}

class DrawingPanel extends JPanel {
  private Shape[] shapes;
  private String currentShapeType;
  private Color currentColor;

  public DrawingPanel() {
    shapes = new Shape[0];
    setBackground(Color.WHITE);

    // マウスリスナーを追加してクリックされた位置に図形を追加
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        Shape newShape = null;
        // currentShapeTypeに応じて適切な図形オブジェクトを生成
        // たとえば、円が選択されている場合の制御
        // TODO: ここに追加の実装する
        if ("Circle".equals(currentShapeType)) {
          newShape = new Circle(e.getX(), e.getY(), 30, currentColor);
        }

        if (newShape != null) {
          addShape(newShape);
          repaint(); // パネルを再描画
        }
      }
    });
  }

  public void setCurrentShapeType(String type) {
    this.currentShapeType = type;
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
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    this.getShapesAsList().forEach(shape -> shape.draw(g));
  }
}

public class MainFrame extends JFrame {
  private DrawingPanel drawingPanel;

  private void init() {
    super.setTitle("図形描画");
    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    super.setSize(800, 600);
    super.setLocationRelativeTo(null);
  }

  public MainFrame() {
    this.init();
    this.drawingPanel = new DrawingPanel();

    //////
    // --- 図形選択ラジオボタン ---
    JRadioButton circleRadioButton = new JRadioButton("円");
    circleRadioButton.setActionCommand("Circle"); // アクションコマンドを設定
    circleRadioButton.setSelected(true); // 最初は円を選択状態にする
    drawingPanel.setCurrentShapeType("Circle"); // DrawingPanelの初期状態も合わせる

    JRadioButton rectangleRadioButton = new JRadioButton("四角形");
    rectangleRadioButton.setActionCommand("Rectangle");

    // ButtonGroupを作成し、ラジオボタンをグループ化する
    // これにより、一度に1つのラジオボタンのみが選択されるようになる
    ButtonGroup shapeGroup = new ButtonGroup();
    shapeGroup.add(circleRadioButton);
    shapeGroup.add(rectangleRadioButton);

    // ラジオボタン用のアクションリスナー
    ActionListener shapeSelectionListener = e -> {
      // 選択されたラジオボタンのアクションコマンドをDrawingPanelに伝える
      drawingPanel.setCurrentShapeType(e.getActionCommand());
      // if (e.getSource() == circleRadioButton) でイベントの発生元コンポーネントで条件分岐可能
    };

    circleRadioButton.addActionListener(shapeSelectionListener);
    rectangleRadioButton.addActionListener(shapeSelectionListener);
    // --- ここまで図形選択ラジオボタン ---

    // --- 色選択ラジオボタン ---
    JRadioButton redRadioButton = new JRadioButton("赤");
    redRadioButton.setForeground(Color.RED);
    JRadioButton blueRadioButton = new JRadioButton("青");
    blueRadioButton.setForeground(Color.BLUE);
    JRadioButton greenRadioButton = new JRadioButton("緑");
    greenRadioButton.setForeground(Color.GREEN);

    // ButtonGroupで色選択ラジオボタンをグループ化
    ButtonGroup colorGroup = new ButtonGroup();
    colorGroup.add(redRadioButton);
    colorGroup.add(blueRadioButton);
    colorGroup.add(greenRadioButton);

    // 初期選択色を設定 (例: 青)
    blueRadioButton.setSelected(true);
    drawingPanel.setCurrentColor(Color.BLUE);

    // 色選択ラジオボタン用のアクションリスナー
    ActionListener colorSelectionListener = e -> {
      if (e.getSource() == redRadioButton) {
        drawingPanel.setCurrentColor(Color.RED);
      }

      // TODO: 他のラジオボタンの処理を実装する
    };

    redRadioButton.addActionListener(colorSelectionListener);
    blueRadioButton.addActionListener(colorSelectionListener);
    greenRadioButton.addActionListener(colorSelectionListener);
    // --- ここまで色選択ラジオボタン ---

    // --- クリアボタン ---
    JButton clearButton = new JButton("クリア");
    clearButton.addActionListener(e -> {
      // TODO: クリアボタンの処理を実装する
    });
    // --- ここまでクリアボタン ---

    // ツールバーにコンポーネントを配置
    JToolBar toolBar = new JToolBar();
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

    add(toolBar, BorderLayout.NORTH);
    add(drawingPanel, BorderLayout.CENTER);
    //////
    super.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainFrame::new);
  }
}
