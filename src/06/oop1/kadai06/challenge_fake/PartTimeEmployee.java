package oop1.kadai06.challenge_fake;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiConsumer;

public class PartTimeEmployee implements EmployeeImpl {
  public static final String DISPLAY_NAME = "アルバイト";

  public final Employee employee;
  public final double hoursWorked;

  public static final double INCOME_TAX_RATE_PARTTIME = 0.05;
  public static final double MIN_TAXABLE_GROSS_PAY_PARTTIME = 80000.0;

  public PartTimeEmployee(Employee employee, double hoursWorked) {
    this.employee = employee;

    this.hoursWorked = hoursWorked;
  }

  public static PartTimeEmployee from(Employee employee, double hoursWorked) {
    // NOTE: 特に実働時間の規定なし
    return new PartTimeEmployee(employee, hoursWorked);
  }

  public static PartTimeEmployee from(Employee employee, String hoursWorkedStr) {
    if (hoursWorkedStr.isEmpty()) {
      throw new IllegalArgumentException("労働時間は必須入力です。");
    }

    return new PartTimeEmployee(employee, Double.parseDouble(hoursWorkedStr));
  }

  @Override
  public Employee getEmployee() {
    return this.employee;
  }

  @Override
  public String getEmployeeType() {
    return DISPLAY_NAME;
  }

  @Override
  public double calculateGrossPay() {
    return this.employee.basePay() * this.hoursWorked; // 時給 × 実働時間
  }

  @Override
  public double calculateTotalDeductions() {
    return calculateIncomeTax(); // アルバイトは所得税のみ控除
  }

  @Override
  public String getPaySlipDetails() {
    var fmt = NumberFormat.getCurrencyInstance(Locale.JAPAN);
    var lineSep = System.lineSeparator();
    var details = new StringBuilder();

    BiConsumer<String, String> ap = (l, v) -> {
      details.append(l + ": " + v + lineSep);
    };

    ap.accept("従業員ID", this.employee.employeeId());
    ap.accept("氏名", this.employee.name());
    ap.accept("従業員種別", DISPLAY_NAME);

    // 時給、労働時間、総支給額、所得税、控除合計、差引支給額
    ap.accept("時給", fmt.format(this.employee.basePay()));
    ap.accept("労働時間", String.format("%.2f 時間", this.hoursWorked));
    ap.accept("総支給額", fmt.format(calculateGrossPay()));
    ap.accept("所得税", fmt.format(calculateIncomeTax()));
    ap.accept("控除合計", fmt.format(calculateTotalDeductions()));
    ap.accept("差引支給額", fmt.format(calculateNetPay()));
    return details.toString();
  }

  public double calculateIncomeTax() {
    double grossPay = calculateGrossPay();
    if (grossPay >= MIN_TAXABLE_GROSS_PAY_PARTTIME) {
      return grossPay * INCOME_TAX_RATE_PARTTIME;
    }
    return 0.0;
  }

  public double getHoursWorked() {
    return this.hoursWorked;
  }

  public double getHourlyRate() {
    return this.employee.basePay();
  }

}
