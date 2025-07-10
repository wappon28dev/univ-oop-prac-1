package oop1.section11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SurveyApp extends JFrame {
  private static final String DATA_FILE = "survey_results.csv";
  private JTextField nameField;
  private ButtonGroup ageGroup;
  private JRadioButton age20s, age30s, age40s;
  private JCheckBox programmingBox, designBox, travelBox;
  private JTextArea resultArea;

  public SurveyApp() {
    setTitle("アンケート集計アプリ");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    initializeComponents();
    layoutComponents();
    loadExistingData();
    pack();
    setLocationRelativeTo(null);
  }

  private void initializeComponents() {
    nameField = new JTextField(20);

    // 年代選択
    ageGroup = new ButtonGroup();
    age20s = new JRadioButton("20代");
    age30s = new JRadioButton("30代");
    age40s = new JRadioButton("40代");
    ageGroup.add(age20s);
    ageGroup.add(age30s);
    ageGroup.add(age40s);

    // 興味分野選択
    programmingBox = new JCheckBox("プログラミング");
    designBox = new JCheckBox("デザイン");
    travelBox = new JCheckBox("旅行");

    // 結果表示エリア
    resultArea = new JTextArea(15, 50);
    resultArea.setEditable(false);
    resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
  }

  private void layoutComponents() {
    setLayout(new BorderLayout());

    // 入力フォーム
    JPanel inputPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    inputPanel.add(new JLabel("氏名:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(nameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    inputPanel.add(new JLabel("年代:"), gbc);
    gbc.gridx = 1;
    JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    agePanel.add(age20s);
    agePanel.add(age30s);
    agePanel.add(age40s);
    inputPanel.add(agePanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    inputPanel.add(new JLabel("興味のある分野:"), gbc);
    gbc.gridx = 1;
    JPanel interestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    interestPanel.add(programmingBox);
    interestPanel.add(designBox);
    interestPanel.add(travelBox);
    inputPanel.add(interestPanel, gbc);

    // 送信ボタン
    JButton submitButton = new JButton("回答を送信");
    submitButton.addActionListener(new SubmitListener());
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.CENTER;
    inputPanel.add(submitButton, gbc);

    add(inputPanel, BorderLayout.NORTH);
    add(new JScrollPane(resultArea), BorderLayout.CENTER);
  }

  private class SubmitListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      String name = nameField.getText().trim();
      if (name.isEmpty()) {
        JOptionPane.showMessageDialog(SurveyApp.this,
            "氏名を入力してください", "入力エラー", JOptionPane.ERROR_MESSAGE);
        return;
      }

      String age = getSelectedAge();
      String interests = getSelectedInterests();
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      String record = String.format("%s,%s,%s,%s", timestamp, name, age, interests);

      // 結果表示に追加
      resultArea.append(record + "\n");

      // ファイルに保存
      saveToFile(record);

      // フォームをクリア
      clearForm();
    }
  }

  private String getSelectedAge() {
    if (age20s.isSelected())
      return "20代";
    if (age30s.isSelected())
      return "30代";
    if (age40s.isSelected())
      return "40代";
    return "未選択";
  }

  private String getSelectedInterests() {
    List<String> interests = new ArrayList<>();
    if (programmingBox.isSelected())
      interests.add("プログラミング");
    if (designBox.isSelected())
      interests.add("デザイン");
    if (travelBox.isSelected())
      interests.add("旅行");
    return String.join(";", interests);
  }

  private void saveToFile(String record) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE, true))) {
      pw.println(record);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this,
          "ファイル保存中にエラーが発生しました: " + e.getMessage(),
          "保存エラー", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void loadExistingData() {
    File file = new File(DATA_FILE);
    if (!file.exists())
      return;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultArea.append(line + "\n");
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this,
          "ファイル読み込み中にエラーが発生しました: " + e.getMessage(),
          "読み込みエラー", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void clearForm() {
    nameField.setText("");
    ageGroup.clearSelection();
    programmingBox.setSelected(false);
    designBox.setSelected(false);
    travelBox.setSelected(false);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new SurveyApp().setVisible(true);
    });
  }
}
