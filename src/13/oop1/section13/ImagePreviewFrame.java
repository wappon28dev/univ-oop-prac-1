package oop1.section13;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImagePreviewFrame extends JFrame {
  private final File imageFile;

  public ImagePreviewFrame(File imageFile) throws IOException {
    this.imageFile = imageFile;
    initializeComponents();
    setupWindow();
  }

  private void initializeComponents() throws IOException {
    var image = ImageIO.read(imageFile);
    if (image == null) {
      throw new IOException("画像ファイルを読み込めませんでした。");
    }

    var imageIcon = new ImageIcon(image);
    var imageLabel = new JLabel(imageIcon);

    var scrollPane = new JScrollPane(imageLabel);
    scrollPane.setPreferredSize(new Dimension(800, 600));

    add(scrollPane);
  }

  private void setupWindow() {
    setTitle("画像プレビュー: " + imageFile.getName());
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
  }
}
