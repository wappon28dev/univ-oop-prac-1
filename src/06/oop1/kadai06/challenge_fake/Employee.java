package oop1.kadai06.challenge_fake;

public record Employee(
    String employeeId,
    String name,
    /**
     * @note 月給制の場合は月額、時給制の場合は時給
     */
    double basePay) {

  public static Employee from(String employeeId, String name, double basePay) {
    if (employeeId.isEmpty()) {
      throw new IllegalArgumentException("従業員IDは必須です。");
    }
    if (name.isEmpty()) {
      throw new IllegalArgumentException("氏名は必須です。");
    }

    return new Employee(employeeId, name, basePay);
  }

  public static Employee from(String employeeId, String name, String basePayStr) {
    // NOTE: 特に月額/時給の規定なし
    return from(employeeId, name, Double.parseDouble(basePayStr));
  }

  public String into() {
    return String.format("従業員ID: %s\n氏名: %s", this.employeeId, this.name);
  }
};

interface EmployeeImpl {
  Employee getEmployee();

  String getEmployeeType();

  /**
   * 総支給額（各種手当込み、控除前）を計算
   */
  double calculateGrossPay();

  /**
   * 控除額合計を計算
   */
  double calculateTotalDeductions();

  /**
   * 給与明細の主要項目を整形した文字列で取得
   */
  String getPaySlipDetails();

  default double calculateNetPay() {
    return calculateGrossPay() - calculateTotalDeductions();
  }
}

enum EmployeeType {
  FULL_TIME(FullTimeEmployee.class),
  PART_TIME(PartTimeEmployee.class);

  public final Class<? extends EmployeeImpl> impl;

  EmployeeType(Class<? extends EmployeeImpl> impl) {
    this.impl = impl;
  }

  public static EmployeeType from(String displayName) {
    return switch (displayName) {
      case FullTimeEmployee.DISPLAY_NAME -> FULL_TIME;
      case PartTimeEmployee.DISPLAY_NAME -> PART_TIME;
      default -> throw new IllegalArgumentException("Unknown employee type: " + displayName);
    };
  }

  public static EmployeeType from(EmployeeImpl employeeImpl) {
    return switch (employeeImpl) {
      case FullTimeEmployee __ -> FULL_TIME;
      case PartTimeEmployee __ -> PART_TIME;
      default ->
        throw new IllegalArgumentException("Unknown employee implementation: " + employeeImpl.getClass().getName());
    };
  }

}
