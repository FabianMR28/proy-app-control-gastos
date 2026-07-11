package pe.edu.utp.control_gastos_app.service.report;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.dto.report.BalancePointDTO;
import pe.edu.utp.control_gastos_app.dto.report.BudgetVsActualDTO;
import pe.edu.utp.control_gastos_app.dto.report.CategoryExpenseDTO;
import pe.edu.utp.control_gastos_app.dto.report.MonthlyExpenseDTO;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Budget;
import pe.edu.utp.control_gastos_app.repository.BudgetRepository;
import pe.edu.utp.control_gastos_app.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class ChartService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public ChartService(TransactionRepository transactionRepository,
                        BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    // --- GRÁFICO 1: Gastos por categoría ---
    public List<CategoryExpenseDTO> getExpensesByCategory(Long userId) {
        return transactionRepository.findExpensesByCategory(userId);
    }

    // --- GRÁFICO 2: Ingresos vs Gastos por mes (últimos 6 meses) ---
    public List<MonthlyExpenseDTO> getMonthlyIncomeVsExpense(Long userId) {
        LocalDate from = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        List<Object[]> rows = transactionRepository.findMonthlyTotalsRaw(userId, from);

        // Mapa: "2025-1" -> [income, expense]
        Map<String, double[]> map = new LinkedHashMap<>();

        // Pre-llenar los últimos 6 meses en orden
        for (int i = 5; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            String key = month.getYear() + "-" + month.getMonthValue();
            map.put(key, new double[]{0.0, 0.0});
        }

        for (Object[] row : rows) {
            int yr = ((Number) row[0]).intValue();
            int mo = ((Number) row[1]).intValue();
            CategoryType type = (CategoryType) row[2];
            double total = ((Number) row[3]).doubleValue();
            String key = yr + "-" + mo;
            if (map.containsKey(key)) {
                if (type == CategoryType.INCOME) map.get(key)[0] = total;
                else map.get(key)[1] = total;
            }
        }

        List<MonthlyExpenseDTO> result = new ArrayList<>();
        for (Map.Entry<String, double[]> entry : map.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int yr = Integer.parseInt(parts[0]);
            int mo = Integer.parseInt(parts[1]);
            String label = Month.of(mo)
                    .getDisplayName(TextStyle.SHORT, new Locale("es")) + " " + yr;
            result.add(new MonthlyExpenseDTO(label, entry.getValue()[0], entry.getValue()[1]));
        }
        return result;
    }

    // --- GRÁFICO 3: Presupuesto vs Gasto real (mes actual) ---
    public List<BudgetVsActualDTO> getBudgetVsActual(Long userId) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        List<Budget> budgets = budgetRepository
                .findByUserIdAndMonthAndYear(userId, month, year);

        List<BudgetVsActualDTO> result = new ArrayList<>();
        LocalDate start = now.withDayOfMonth(1);
        List<CategoryExpenseDTO> actuals = transactionRepository
                .findExpensesByCategoryAndPeriod(userId, start, now);

        Map<String, Double> actualMap = new HashMap<>();
        for (CategoryExpenseDTO c : actuals) {
            actualMap.put(c.getCategoryName(), c.getTotal());
        }

        for (Budget b : budgets) {
            double spent = actualMap.getOrDefault(b.getCategory().getName(), 0.0);
            result.add(new BudgetVsActualDTO(
                    b.getCategory().getName(),
                    b.getLimitAmount().doubleValue(),
                    spent
            ));
        }
        return result;
    }

    // --- GRÁFICO 4: Balance acumulado ---
    public List<BalancePointDTO> getBalanceOverTime(Long userId) {
        List<Object[]> rows = transactionRepository.findAllForBalanceChart(userId);
        List<BalancePointDTO> result = new ArrayList<>();
        double accumulated = 0.0;

        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            CategoryType type = (CategoryType) row[1];
            double amount = ((BigDecimal) row[2]).doubleValue();

            if (type == CategoryType.INCOME) accumulated += amount;
            else accumulated -= amount;

            result.add(new BalancePointDTO(date.toString(), accumulated));
        }
        return result;
    }

}