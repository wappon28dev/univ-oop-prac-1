package oop1.section09.kadai1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

record Point(double x, double y) {
  public final static Point AIT = new Point(35.1834122, 137.1130419);

  public double calcDistance(Point other) {
    return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
  }

  public static Point parse(String point) {
    var parts = point
        .replace("POINT(", "")
        .replace(")", "")
        .split(" ");

    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid point format: " + point);
    }

    var x = Double.parseDouble(parts[0]);
    var y = Double.parseDouble(parts[1]);

    return new Point(x, y);
  }
}

record Spot(
    String id,
    String name,
    Point point) {

  public static Spot parse(String id, String name, String pointStr) {
    var point = Point.parse(pointStr);
    return new Spot(id, name, point);
  }
}

record SpotStat(
    double latitude,
    double longitude,
    double distance,
    String name) {
  public static SpotStat from(Spot spot) {
    var x = spot.point().x();
    var y = spot.point().y();
    var distance = spot.point().calcDistance(Point.AIT);
    return new SpotStat(y, x, distance, spot.name());
  }

  public static List<String> header = List.of(
      "緯度情報",
      "経度情報",
      "愛工大からの距離",
      "データ名");

  public List<String> into() {
    var values = List.of(
        String.format("%.10f", latitude),
        String.format("%.10f", longitude),
        String.format("%.10f", distance),
        name);
    var valuesStr = String.join(",", values);

    return List.of(valuesStr);
  }

  public void writeToCSV(String fileName) {
    var lines = this.into();
    try (var writer = java.nio.file.Files.newBufferedWriter(Paths.get(fileName))) {
      for (var line : lines) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException("ファイルの書き込みに失敗しました: " + fileName, e);
    }
  }
}

record DataStat(
    String fileName,
    String name) {

  public static List<DataStat> DATA_STATS = List.of(
      /**
       * あいちの都市・農村交流ガイドルートマップ - c200326.csv - (フィーチャID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),ルートID,名称,問い合わせ先,ホームページ,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200326.csv", "あいちの都市・農村交流ガイドルートマップ"),

      /**
       * あいちの都市・農村交流ガイドルートマップ 寄ってこみゃ - c200328.csv - (フィーチャID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),名称,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200328.csv", "あいちの都市・農村交流ガイドルートマップ 寄ってこみゃ"),
      /**
       * あいちの都市・農村交流ガイドルートマップ 地域資源スポット（風景・自然） - c200329.csv - (フィーチャID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),データID,データ名,特色、概要,問い合わせ先,問い合わせ電話番号,問い合わせホームページ,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200329.csv", "あいちの都市・農村交流ガイドルートマップ 地域資源スポット（風景・自然）"),
      /**
       * あいちの都市・農村交流ガイドルートマップ 地域資源スポット（施設） - c200330.csv - (フィーチャID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),データID,データ名,特色、概要,問い合わせ先,問い合わせ先電話番号,問い合わせ先ホームページ,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200330.csv", "あいちの都市・農村交流ガイドルートマップ 地域資源スポット（施設）"),
      /**
       * 愛知県文化財マップ（ナビ愛知）建造物 - c200361.csv - (フィーチャーID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),番号,指定区分,名称,市町村名,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200361.csv", "愛知県文化財マップ（ナビ愛知）建造物"),
      /**
       * 愛知県文化財マップ（ナビ愛知）名勝 - c200362.csv - (フィーチャーID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),番号,指定区分,種別,名称,市町村名,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200362.csv", "愛知県文化財マップ（ナビ愛知）名勝"),
      /**
       * 愛知県文化財マップ（ナビ愛知）天然記念物 - c200363.csv - (フィーチャーID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),番号,指定区分,種別,名称,市町村名,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200363.csv", "愛知県文化財マップ（ナビ愛知）天然記念物"),
      /**
       * 愛知県文化財マップ（ナビ愛知）史跡 - c200364.csv - (フィーチャーID, 名称, 形状(WKT))
       * HEADER:
       * フィーチャID,形状(WKT),番号,指定区分,種別,名称,市町村名,期間開始日時,期間終了日時,作成者,作成日時,更新者,更新日時
       */
      new DataStat("c200364.csv", "愛知県文化財マップ（ナビ愛知）史跡"));

  private List<List<String>> read() {
    var path = Paths.get("assets/" + fileName);

    var lines = new ArrayList<List<String>>();
    try (var scanner = new Scanner(path, "MS932")) {
      while (scanner.hasNextLine()) {
        var line = scanner.nextLine();
        var fields = line.split(",");
        lines.add(List.of(fields));
      }
    } catch (IOException e) {
      throw new RuntimeException("ファイルの読み込みに失敗しました: " + this.toString(), e);
    }

    return lines;
  }

  public List<Spot> readAndParse() {
    var lines = this.read();
    var header = lines.get(0);
    var body = lines.subList(1, lines.size());

    System.out.println("header: " + header);

    var idxId = header.indexOf("フィーチャID");
    var idxName = header.indexOf("名称") != -1 ? header.indexOf("名称") : header.indexOf("データ名");
    var idxPoint = header.indexOf("形状(WKT)");
    if (idxId == -1 || idxName == -1 || idxPoint == -1) {
      throw new IllegalArgumentException("必要なカラムが見つかりません: " + header);
    }

    return body
        .stream()
        .map(line -> {
          var id = line.get(idxId);
          var name = line.get(idxName);
          var pointStr = line.get(idxPoint);

          return Spot.parse(id, name, pointStr);
        })
        .toList();
  }
}

public class AichiTouristSpot {
  public static void main(String[] args) {
    var spots = DataStat.DATA_STATS
        .stream()
        .map(dataStat -> dataStat.readAndParse())
        .flatMap(List::stream)
        .toList();

    var spotStats = spots
        .stream()
        .map(SpotStat::from)
        .toList();

    // 出力データは愛工大からの距離昇順で並び替えを行う
    Comparator<SpotStat> distanceComparator = (s1, s2) -> Double.compare(s1.distance(), s2.distance());

    var sortedSpotStats = spotStats
        .stream()
        .sorted(distanceComparator)
        .toList();

    var statCSV = sortedSpotStats
        .stream()
        .map(SpotStat::into)
        .flatMap(List::stream)
        .toList();

    var outputFileName = "out/TouristSpot.csv";
    try (var writer = Files.newBufferedWriter(Paths.get(outputFileName))) {
      writer.write(String.join(",", SpotStat.header));
      writer.newLine();
      for (var line : statCSV) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException("ファイルの書き込みに失敗しました: " +
          outputFileName, e);
    }
  }
}
