package oop1.section13;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class TextPreviewFrame extends JFrame {
  private final File textFile;

  public TextPreviewFrame(File textFile) throws IOException {
    this.textFile = textFile;
    initializeComponents();
    setupWindow();
  }

  private void initializeComponents() throws IOException {
    var content = Files.readString(textFile.toPath(), StandardCharsets.UTF_8);

    var textArea = new JTextArea(content);
    textArea.setEditable(false);
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    var scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(800, 600));

    add(scrollPane);
  }

  private void setupWindow() {
    setTitle("テキストプレビュー: " + textFile.getName());
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
  }
}
