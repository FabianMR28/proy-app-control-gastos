package pe.edu.utp.control_gastos_app.dto.report;

public class BalancePointDTO {
    private String date;       // "2025-01-15"
    private Double balance;    // balance acumulado

    public BalancePointDTO(String date, Double balance) {
        this.date = date;
        this.balance = balance;
    }

    public String getDate() { return date; }
    public Double getBalance() { return balance; }
}