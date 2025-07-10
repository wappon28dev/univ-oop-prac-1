package oop1.section11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BmiCalculatorApp extends JFrame {
  private JTextField heightField;
  private JTextField weightField;
  private JLabel resultLabel;

  public BmiCalculatorApp() {
    setTitle("BMI計算機");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new GridLayout(4, 2, 10, 10));

    // コンポーネントの初期化
    add(new JLabel("身長（cm）:"));
    heightField = new JTextField();
    add(heightField);

    add(new JLabel("体重（kg）:"));
    weightField = new JTextField();
    add(weightField);

    JButton calculateButton = new JButton("計算実行");
    calculateButton.addActionListener(new CalculateListener());
    add(new JLabel()); // スペース
    add(calculateButton);

    resultLabel = new JLabel("身長と体重を入力してください");
    resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(resultLabel);
    add(new JLabel()); // スペース

    pack();
    setLocationRelativeTo(null);
  }

  private class CalculateListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        String heightText = heightField.getText().trim();
        String weightText = weightField.getText().trim();

        if (heightText.isEmpty() || weightText.isEmpty()) {
          JOptionPane.showMessageDialog(BmiCalculatorApp.this,
              "数値を入力してください", "入力エラー", JOptionPane.ERROR_MESSAGE);
          return;
        }

        double height = Double.parseDouble(heightText) / 100.0; // cmをmに変換
        double weight = Double.parseDouble(weightText);

        double bmi = weight / (height * height);
        String category = getBmiCategory(bmi);

        resultLabel.setText(String.format("BMI: %.2f (%s)", bmi, category));

      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(BmiCalculatorApp.this,
            "数値を入力してください", "入力エラー", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private String getBmiCategory(double bmi) {
    if (bmi < 18.5) {
      return "低体重（痩せ型）";
    } else if (bmi < 25) {
      return "普通体重";
    } else if (bmi < 30) {
      return "肥満（1度）";
    } else {
      return "肥満（2度以上）";
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new BmiCalculatorApp().setVisible(true));
  }
}
