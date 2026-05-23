package pe.edu.utp.control_gastos_app.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.utp.control_gastos_app.service.report.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/financial-summary/pdf")
    public void downloadFinancialSummaryPdf(
            HttpServletResponse response
    ) throws Exception {

        reportService.generateFinancialSummaryPdf(response);
    }
}