package pe.edu.utp.control_gastos_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.utp.control_gastos_app.dto.report.BalancePointDTO;
import pe.edu.utp.control_gastos_app.dto.report.BudgetVsActualDTO;
import pe.edu.utp.control_gastos_app.dto.report.CategoryExpenseDTO;
import pe.edu.utp.control_gastos_app.dto.report.MonthlyExpenseDTO;
import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.report.ChartService;

import java.util.List;

@Controller
@RequestMapping("/reports/charts")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    // Vista HTML principal
    @GetMapping
    public String chartsPage(Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("userId", userId);
        return "reports/charts";
    }

    // ---- Endpoints JSON para Chart.js ----

    @GetMapping("/data/expenses-by-category")
    @ResponseBody
    public List<CategoryExpenseDTO> expensesByCategory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return chartService.getExpensesByCategory(userId);
    }

    @GetMapping("/data/monthly")
    @ResponseBody
    public List<MonthlyExpenseDTO> monthly(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return chartService.getMonthlyIncomeVsExpense(userId);
    }

    @GetMapping("/data/budget-vs-actual")
    @ResponseBody
    public List<BudgetVsActualDTO> budgetVsActual(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return chartService.getBudgetVsActual(userId);
    }

    @GetMapping("/data/balance")
    @ResponseBody
    public List<BalancePointDTO> balance(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        return chartService.getBalanceOverTime(userId);
    }
}