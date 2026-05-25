package pe.edu.utp.control_gastos_app.dto.report;

public class FinancialSummaryDTO {

    private Double totalIncome;
    private Double totalExpenses;
    private Double balance;
    private String status;

    public FinancialSummaryDTO() {
    }

    public FinancialSummaryDTO(
            Double totalIncome,
            Double totalExpenses,
            Double balance,
            String status
    ) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.balance = balance;
        this.status = status;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}