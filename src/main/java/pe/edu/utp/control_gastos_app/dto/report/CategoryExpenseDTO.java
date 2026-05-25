package pe.edu.utp.control_gastos_app.dto.report;

public class CategoryExpenseDTO {
    private String categoryName;
    private Double total;

    public CategoryExpenseDTO(String categoryName, Double total) {
        this.categoryName = categoryName;
        this.total = total;
    }

    public String getCategoryName() { return categoryName; }
    public Double getTotal() { return total; }
}