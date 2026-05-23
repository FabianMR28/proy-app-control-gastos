package pe.edu.utp.control_gastos_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.control_gastos_app.model.Transfer;
import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.AccountService;
import pe.edu.utp.control_gastos_app.service.TransferService;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;
    private final AccountService accountService;

    public TransferController(TransferService transferService, AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("transfers", transferService.findByUserId(userId));
        return "transfers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        model.addAttribute("transfer", new Transfer());
        model.addAttribute("accounts", accountService.findByUserId(userId));
        return "transfers/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Transfer transfer,
                         @RequestParam Long sourceAccountId,
                         @RequestParam Long destinationAccountId,
                         RedirectAttributes redirectAttributes) {
        try {
            transferService.save(transfer, sourceAccountId, destinationAccountId);
            redirectAttributes.addFlashAttribute("success", "Transfer completed successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transfers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transferService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Transfer deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transfers";
    }
}