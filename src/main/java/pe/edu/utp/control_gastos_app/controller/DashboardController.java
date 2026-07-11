package pe.edu.utp.control_gastos_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Account;
import pe.edu.utp.control_gastos_app.model.Budget;
import pe.edu.utp.control_gastos_app.model.Transaction;
import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.AccountService;
import pe.edu.utp.control_gastos_app.service.BudgetService;
import pe.edu.utp.control_gastos_app.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final BudgetService budgetService;

    public DashboardController(TransactionService transactionService,
                               AccountService accountService,
                               BudgetService budgetService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.budgetService = budgetService;
    }

    @GetMapping
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();

        LocalDate now = LocalDate.now();

        List<Account> accounts = accountService.findByUserId(userId);
        List<Transaction> recentTransactions = transactionService
                .findByUserIdAndDateBetween(userId, now.withDayOfMonth(1), now);
        List<Budget> budgets = budgetService
                .findByUserIdAndMonthAndYear(userId, now.getMonthValue(), now.getYear());

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = recentTransactions.stream()
                .filter(t -> t.getType() == CategoryType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = recentTransactions.stream()
                .filter(t -> t.getType() == CategoryType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("accounts", accounts);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("budgets", budgets);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);

        return "dashboard";
    }
}