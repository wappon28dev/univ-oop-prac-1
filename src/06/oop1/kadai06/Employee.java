package oop1.kadai06;

public abstract class Employee {
  protected String employeeId;
  protected String name;

  /**
   * @note 月給制の場合は月額、時給制の場合は時給
   */
  protected double basePay;

  public Employee(String employeeId, String name, double basePay) {
    this.employeeId = employeeId;
    this.name = name;
    this.basePay = basePay;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public String getName() {
    return name;
  }

  public double getBasePay() {
    return basePay;
  }

  public double calculateNetPay() {
    return calculateGrossPay() - calculateTotalDeductions();
  }

  /**
   * 総支給額（各種手当込み、控除前）を計算
   */
  public abstract double calculateGrossPay();

  /**
   * 控除額合計を計算
   */
  public abstract double calculateTotalDeductions();

  /**
   * 給与明細の主要項目を整形した文字列で取得
   */
  public abstract String getPaySlipDetails();

  /**
   * 従業員種別名を取得
   */
  public abstract String getEmployeeTypeName();
}
