package pe.edu.utp.control_gastos_app.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.utp.control_gastos_app.security.CustomUserDetails;
import pe.edu.utp.control_gastos_app.service.report.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/financial-summary/pdf")
    public void downloadFinancialSummaryPdf(
            HttpServletResponse response,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        Long userId = ((CustomUserDetails) userDetails).getId();
        reportService.generateFinancialSummaryPdf(response, userId);
    }
}