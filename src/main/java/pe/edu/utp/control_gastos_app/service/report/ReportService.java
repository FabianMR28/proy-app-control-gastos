package pe.edu.utp.control_gastos_app.service.report;

import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {

    void generateFinancialSummaryPdf(
            HttpServletResponse response
    ) throws Exception;
}