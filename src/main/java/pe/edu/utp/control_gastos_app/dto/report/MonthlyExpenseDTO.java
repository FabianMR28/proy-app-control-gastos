package pe.edu.utp.control_gastos_app.dto.report;

public class MonthlyExpenseDTO {
    private String label; // "Ene 2025", "Feb 2025"...
    private Double income;
    private Double expense;

    public MonthlyExpenseDTO(String label, Double income, Double expense) {
        this.label = label;
        this.income = income;
        this.expense = expense;
    }

    public String getLabel() { return label; }
    public Double getIncome() { return income; }
    public Double getExpense() { return expense; }
}