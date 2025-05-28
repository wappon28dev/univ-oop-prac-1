package oop1.kadai06.challenge_fake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * 簡易給与計算シミュレータのメインGUIフレームです。
 * 従業員情報を入力し、計算結果を {@link PaySlipPanel} に表示します。
 */
public class SalarySimulatorFrame extends JFrame {

  /** 従業員種別選択用のコンボボックス */
  private JComboBox<String> employeeTypeComboBox;
  /** 従業員ID入力用のテキストフィールド */
  private JTextField employeeIdField;
  /** 氏名入力用のテキストフィールド */
  private JTextField nameField;
  /** 基本給または時給入力用のテキストフィールド */
  private JTextField basePayField;
  /** 残業時間入力用のテキストフィールド (正社員用) */
  private JTextField overtimeHoursField;
  /** 賞与入力用のテキストフィールド (正社員用) */
  private JTextField bonusField;
  /** 交通費入力用のテキストフィールド (正社員用) */
  private JTextField commuteAllowanceField;
  /** 労働時間入力用のテキストフィールド (アルバイト用) */
  private JTextField hoursWorkedField;

  /** 残業時間入力のラベル (表示/非表示制御用) */
  private JLabel overtimeHoursLabel;
  /** 賞与入力のラベル (表示/非表示制御用) */
  private JLabel bonusLabel;
  /** 交通費入力のラベル (表示/非表示制御用) */
  private JLabel commuteAllowanceLabel;
  /** 労働時間入力のラベル (表示/非表示制御用) */
  private JLabel hoursWorkedLabel;

  /** 給与明細表示用のカスタムパネル */
  private PaySlipPanel paySlipPanel;

  /**
   * {@code SalarySimulatorFrame} を構築します。
   * GUIコンポーネントの初期化と配置、イベントリスナーの設定を行います。
   */
  public SalarySimulatorFrame() {
    setTitle("簡易給与計算シミュレータ");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null); // 画面中央に表示

    initComponents();
    layoutComponents();
    attachListeners();

    // 初期状態でアルバイト用のフィールドを非表示にする
    updateInputFieldsVisibility((String) employeeTypeComboBox.getSelectedItem());
  }

  /**
   * GUIコンポーネントを初期化します。
   */
  private void initComponents() {
    employeeTypeComboBox = new JComboBox<>(new String[] { "正社員", "アルバイト" });
    employeeIdField = new JTextField(15);
    nameField = new JTextField(15);
    basePayField = new JTextField(10);
    overtimeHoursField = new JTextField(5);
    bonusField = new JTextField(10);
    commuteAllowanceField = new JTextField(10);
    hoursWorkedField = new JTextField(5);

    overtimeHoursLabel = new JLabel("残業時間 (h):");
    bonusLabel = new JLabel("賞与 (円):");
    commuteAllowanceLabel = new JLabel("交通費 (円):");
    hoursWorkedLabel = new JLabel("労働時間 (h):");

    paySlipPanel = new PaySlipPanel();
  }

  /**
   * GUIコンポーネントをフレームに配置します。
   */
  private void layoutComponents() {
    // 入力パネルの作成
    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBorder(BorderFactory.createTitledBorder("従業員情報入力"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // コンポーネント間の余白
    gbc.anchor = GridBagConstraints.WEST; // 左寄せ

    // 1行目: 従業員種別
    gbc.gridx = 0;
    gbc.gridy = 0;
    inputPanel.add(new JLabel("従業員種別:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(employeeTypeComboBox, gbc);

    // 2行目: 従業員ID
    gbc.gridx = 0;
    gbc.gridy = 1;
    inputPanel.add(new JLabel("従業員ID:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(employeeIdField, gbc);

    // 3行目: 氏名
    gbc.gridx = 0;
    gbc.gridy = 2;
    inputPanel.add(new JLabel("氏名:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(nameField, gbc);

    // 4行目: 基本給/時給
    gbc.gridx = 0;
    gbc.gridy = 3;
    inputPanel.add(new JLabel("基本給/時給 (円):"), gbc);
    gbc.gridx = 1;
    inputPanel.add(basePayField, gbc);

    // 5行目: 残業時間 (正社員用)
    gbc.gridx = 0;
    gbc.gridy = 4;
    inputPanel.add(overtimeHoursLabel, gbc);
    gbc.gridx = 1;
    inputPanel.add(overtimeHoursField, gbc);

    // 6行目: 賞与 (正社員用)
    gbc.gridx = 0;
    gbc.gridy = 5;
    inputPanel.add(bonusLabel, gbc);
    gbc.gridx = 1;
    inputPanel.add(bonusField, gbc);

    // 7行目: 交通費 (正社員用)
    gbc.gridx = 0;
    gbc.gridy = 6;
    inputPanel.add(commuteAllowanceLabel, gbc);
    gbc.gridx = 1;
    inputPanel.add(commuteAllowanceField, gbc);

    // 8行目: 労働時間 (アルバイト用)
    gbc.gridx = 0;
    gbc.gridy = 7;
    inputPanel.add(hoursWorkedLabel, gbc);
    gbc.gridx = 1;
    inputPanel.add(hoursWorkedField, gbc);

    // ボタンパネルの作成
    JButton calculateButton = new JButton("給与計算実行");
    JButton clearButton = new JButton("入力クリア");
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(calculateButton);
    buttonPanel.add(clearButton);

    // 入力フォームとボタンをまとめるパネル
    JPanel formPanel = new JPanel(new BorderLayout());
    formPanel.add(inputPanel, BorderLayout.CENTER);
    formPanel.add(buttonPanel, BorderLayout.SOUTH);

    // メインフレームのレイアウト
    setLayout(new BorderLayout(10, 10)); // パネル間の隙間
    add(formPanel, BorderLayout.WEST);
    add(paySlipPanel, BorderLayout.CENTER);

    // イベントリスナーの設定
    calculateButton.addActionListener(new CalculateButtonListener());
    clearButton.addActionListener(new ClearButtonListener());
    employeeTypeComboBox.addItemListener(new EmployeeTypeChangeListener());
  }

  /**
   * 従業員種別の変更に応じて、関連する入力フィールドの表示/非表示を切り替えます。
   * 
   * @param selectedType 選択された従業員種別 ("正社員" または "アルバイト")
   */
  private void updateInputFieldsVisibility(String selectedType) {
    boolean isFullTime = "正社員".equals(selectedType);

    overtimeHoursLabel.setVisible(isFullTime);
    overtimeHoursField.setVisible(isFullTime);
    bonusLabel.setVisible(isFullTime);
    bonusField.setVisible(isFullTime);
    commuteAllowanceLabel.setVisible(isFullTime);
    commuteAllowanceField.setVisible(isFullTime);

    hoursWorkedLabel.setVisible(!isFullTime);
    hoursWorkedField.setVisible(!isFullTime);
  }

  /**
   * イベントリスナーをGUIコンポーネントにアタッチします。
   * このメソッドは現在 `layoutComponents` 内で直接設定しているため、
   * 別途呼び出す必要はありませんが、将来的な拡張のために分離しておくことも可能です。
   */
  private void attachListeners() {
    // layoutComponents内で設定済み
  }

  /**
   * 「給与計算実行」ボタンのアクションリスナークラスです。
   */
  private class CalculateButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      var employeeType = (String) employeeTypeComboBox.getSelectedItem();
      var id = employeeIdField.getText();
      var name = nameField.getText();

      var employee = Employee.from(id, name, basePayField.getText());
      var employeeImpl = switch (EmployeeType.from(employeeType)) {
        case FULL_TIME -> FullTimeEmployee.from(employee, overtimeHoursField.getText(), bonusField.getText(),
            commuteAllowanceField.getText());
        case PART_TIME -> PartTimeEmployee.from(employee, hoursWorkedField.getText());
      };

      paySlipPanel.displayPaySlip(employeeImpl);
    }
  }

  /**
   * 「入力クリア」ボタンのアクションリスナークラスです。
   * privateな内部クラスで作成しています。
   */
  private class ClearButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      employeeIdField.setText("");
      nameField.setText("");
      basePayField.setText("");
      overtimeHoursField.setText("");
      bonusField.setText("");
      commuteAllowanceField.setText("");
      hoursWorkedField.setText("");
      employeeTypeComboBox.setSelectedIndex(0); // "正社員" をデフォルトに
      paySlipPanel.clearPaySlip();
      updateInputFieldsVisibility((String) employeeTypeComboBox.getSelectedItem());
    }
  }

  /**
   * 従業員種別コンボボックスのアイテム変更リスナークラスです。
   */
  private class EmployeeTypeChangeListener implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        String selectedType = (String) e.getItem();
        updateInputFieldsVisibility(selectedType);
      }
    }
  }
}
