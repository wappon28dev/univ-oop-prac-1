package oop1.kadai06;

import javax.swing.SwingUtilities;

/**
 * アプリケーションのエントリーポイントとなるクラスです。
 * 簡易給与計算シミュレータのGUIを起動します。
 */
public class Main {
  /**
   * メインメソッド。
   * イベントディスパッチスレッドで {@link SalarySimulatorFrame} を作成し、表示します。
   *
   * @param args コマンドライン引数（使用しません）
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new SalarySimulatorFrame().setVisible(true));
  }
}
