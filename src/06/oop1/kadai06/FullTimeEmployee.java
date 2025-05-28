package oop1.kadai06;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.*;

public class FullTimeEmployee extends Employee implements CommuteAllowanceCalculable {
  private double overtimeHours;

  /**
   * @note 固定額または基本給のXヶ月分など簡易計算でOK
   */
  private double bonus;
  private double commuteAllowance;

  public static final double STANDARD_MONTHLY_HOURS = 160.0;
  public static final double OVERTIME_RATE_MULTIPLIER = 1.25;
  public static final double SOCIAL_INSURANCE_RATE = 0.15;
  public static final double INCOME_TAX_RATE_FULLTIME = 0.10;

  public FullTimeEmployee(String employeeId, String name, double basePay, double overtimeHours, double bonus,
      double commuteAllowance) {
    super(employeeId, name, basePay);
    this.overtimeHours = overtimeHours;
    this.bonus = bonus;
    this.commuteAllowance = commuteAllowance;
  }

  public double calculateOvertimePay() {
    if (STANDARD_MONTHLY_HOURS <= 0) {
      return 0.0;
    }
    return (super.basePay / STANDARD_MONTHLY_HOURS) * OVERTIME_RATE_MULTIPLIER * this.overtimeHours;
  }

  public double calculateSocialInsurance() {
    return super.basePay * SOCIAL_INSURANCE_RATE;
  }

  public double calculateIncomeTax() {
    return calculateGrossPay() * INCOME_TAX_RATE_FULLTIME;
  }

  @Override
  public double calculateGrossPay() {
    return super.basePay + calculateOvertimePay() + this.bonus + getCommuteAllowance();
  }

  @Override
  public double calculateTotalDeductions() {
    return calculateSocialInsurance() + calculateIncomeTax();
  }

  @Override
  public String getPaySlipDetails() {
    var fmt = NumberFormat.getCurrencyInstance(Locale.JAPAN);
    var lineSep = System.lineSeparator();
    var details = new StringBuilder();

    // ref: https://qiita.com/opengl-8080/items/22c4405a38127ed86a31
    // Java で実質部分適用できる感じ
    BiConsumer<String, String> ap = (l, v) -> {
      details.append(l + ": " + v + lineSep);
    };

    ap.accept("従業員ID", super.employeeId);
    ap.accept("氏名", super.name);
    ap.accept("従業員種別", getEmployeeTypeName());

    // 基本給、残業手当、賞与、総支給額、社会保険料、所得税、控除合計、差引支給額
    ap.accept("従業員種別", getEmployeeTypeName());
    ap.accept("基本給", fmt.format(super.basePay));
    ap.accept("残業手当", fmt.format(calculateOvertimePay()));
    ap.accept("賞与", fmt.format(this.bonus));
    ap.accept("交通費", fmt.format(getCommuteAllowance()));
    ap.accept("総支給額", fmt.format(calculateGrossPay()));
    ap.accept("社会保険料", fmt.format(calculateSocialInsurance()));
    ap.accept("所得税", fmt.format(calculateIncomeTax()));
    ap.accept("控除合計", fmt.format(calculateTotalDeductions()));
    ap.accept("差引支給額", fmt.format(calculateNetPay()));

    return details.toString();
  }

  @Override
  public String getEmployeeTypeName() {
    return "正社員";
  }

  @Override
  public double getCommuteAllowance() {
    return this.commuteAllowance;
  }

  public double getBonus() {
    return this.bonus;
  }

  public double getOvertimeHours() {
    return this.overtimeHours;
  }
}
