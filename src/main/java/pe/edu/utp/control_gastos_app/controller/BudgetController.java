package pe.edu.utp.control_gastos_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.control_gastos_app.enums.BudgetPeriod;
import pe.edu.utp.control_gastos_app.model.Budget;
import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.BudgetService;
import pe.edu.utp.control_gastos_app.service.CategoryService;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;

    public BudgetController(BudgetService budgetService, CategoryService categoryService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("budgets", budgetService.findByUserId(userId));
        return "budgets/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", categoryService.findAllByUserId(userId));
        model.addAttribute("periods", BudgetPeriod.values());
        return "budgets/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Budget budget,
                         @RequestParam Long categoryId,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        try {
            Long userId = ((CustomUserDetails) userDetails).getId();
            budgetService.save(budget, userId, categoryId);
            redirectAttributes.addFlashAttribute("success", "Budget created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/budgets";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("budget", budgetService.findById(id));
        model.addAttribute("categories", categoryService.findAllByUserId(userId));
        model.addAttribute("periods", BudgetPeriod.values());
        return "budgets/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute Budget budget,
                         RedirectAttributes redirectAttributes) {
        try {
            budgetService.update(id, budget);
            redirectAttributes.addFlashAttribute("success", "Budget updated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/budgets";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            budgetService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Budget deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/budgets";
    }
}