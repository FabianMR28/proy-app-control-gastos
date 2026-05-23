package pe.edu.utp.control_gastos_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.control_gastos_app.enums.AccountType;
import pe.edu.utp.control_gastos_app.model.Account;
import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.AccountService;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("accounts", accountService.findByUserId(userId));
        return "accounts/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("accountTypes", AccountType.values());
        return "accounts/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Account account,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        try {
            Long userId = ((CustomUserDetails) userDetails).getId();
            accountService.save(account, userId);
            redirectAttributes.addFlashAttribute("success", "Account created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/accounts";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        Account account = accountService.findById(id);
        model.addAttribute("account", account);
        model.addAttribute("accountTypes", AccountType.values());
        return "accounts/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute Account account,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        try {
            Long userId = ((CustomUserDetails) userDetails).getId();
            accountService.update(id, account, userId);
            redirectAttributes.addFlashAttribute("success", "Account updated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/accounts";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        try {
            Long userId = ((CustomUserDetails) userDetails).getId();
            accountService.delete(id, userId);
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/accounts";
    }
}