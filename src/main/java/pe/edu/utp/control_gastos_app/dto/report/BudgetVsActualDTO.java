package pe.edu.utp.control_gastos_app.dto.report;

public class BudgetVsActualDTO {
    private String categoryName;
    private Double budgetLimit;
    private Double actualSpent;

    public BudgetVsActualDTO(String categoryName, Double budgetLimit, Double actualSpent) {
        this.categoryName = categoryName;
        this.budgetLimit = budgetLimit;
        this.actualSpent = actualSpent;
    }

    public String getCategoryName() { return categoryName; }
    public Double getBudgetLimit() { return budgetLimit; }
    public Double getActualSpent() { return actualSpent; }
}