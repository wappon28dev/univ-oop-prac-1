import javax.swing.*;
import java.awt.*;

public class GreetAndShowLength extends JFrame {
  private JLabel messageLabel;

  private void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(300, 200);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(4, 1));
  }

  private void onGreet(String name) {
    if (name.isEmpty()) {
      messageLabel.setText("名前を入力してください");
      return;
    }

    setTitle("こんにちは！" + name + "さん");
    var charCount = name.length();
    messageLabel.setText(charCount + "文字です");
  }

  public GreetAndShowLength() {
    init();
    setTitle("");

    var nameLabel = new JLabel("名前を入力してください:");
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(nameLabel);

    var nameTextField = new JTextField(15);
    add(nameTextField);

    messageLabel = new JLabel("");
    messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(messageLabel);

    var greetButton = new JButton("挨拶する");
    greetButton.addActionListener(__ -> {
      onGreet(nameTextField.getText());
    });
    add(greetButton);

    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GreetAndShowLength());
  }
}
