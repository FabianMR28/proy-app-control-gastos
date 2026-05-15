package pe.edu.utp.control_gastos_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Transaction;
import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.AccountService;
import pe.edu.utp.control_gastos_app.service.CategoryService;
import pe.edu.utp.control_gastos_app.service.TransactionService;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public TransactionController(TransactionService transactionService,
                                 AccountService accountService,
                                 CategoryService categoryService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("transactions", transactionService.findByUserId(userId));
        return "transactions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.findByUserId(userId));
        model.addAttribute("categories", categoryService.findAllByUserId(userId));
        model.addAttribute("categoryTypes", CategoryType.values());
        return "transactions/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Transaction transaction,
                         @RequestParam Long accountId,
                         @RequestParam Long categoryId,
                         RedirectAttributes redirectAttributes) {
        try {
            transactionService.save(transaction, accountId, categoryId);
            redirectAttributes.addFlashAttribute("success", "Transaction created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transactionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Transaction deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }
}