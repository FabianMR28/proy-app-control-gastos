package pe.edu.utp.control_gastos_app.service.report;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.dto.report.FinancialSummaryDTO;
import pe.edu.utp.control_gastos_app.repository.TransactionRepository;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void generateFinancialSummaryPdf(
            HttpServletResponse response
    ) throws Exception {

        Double income =
                transactionRepository.getTotalIncome();

        Double expenses =
                transactionRepository.getTotalExpenses();

        Double balance = income - expenses;

        String status;

        if (balance > 1000) {
            status = "MUY BUENO";
        } else if (balance > 500) {
            status = "BUENO";
        } else if (balance > 0) {
            status = "REGULAR";
        } else {
            status = "CRITICO";
        }

        FinancialSummaryDTO summary =
                new FinancialSummaryDTO(
                        income,
                        expenses,
                        balance,
                        status
                );

        generatePdf(response, summary);
    }

    private void generatePdf(
            HttpServletResponse response,
            FinancialSummaryDTO summary
    ) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=reporte_financiero.pdf"
        );

        Document document = new Document();

        PdfWriter.getInstance(
                document,
                response.getOutputStream()
        );

        document.open();

        document.add(
                new Paragraph("REPORTE FINANCIERO")
        );

        document.add(new Paragraph(" "));

        document.add(
                new Paragraph(
                        "Ingresos Totales: S/ "
                                + summary.getTotalIncome()
                )
        );

        document.add(
                new Paragraph(
                        "Gastos Totales: S/ "
                                + summary.getTotalExpenses()
                )
        );

        document.add(
                new Paragraph(
                        "Balance: S/ "
                                + summary.getBalance()
                )
        );

        document.add(
                new Paragraph(
                        "Estado Financiero: "
                                + summary.getStatus()
                )
        );

        document.close();
    }
}