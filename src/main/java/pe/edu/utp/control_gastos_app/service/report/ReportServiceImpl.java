package pe.edu.utp.control_gastos_app.service.report;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.dto.report.CategoryExpenseDTO;
import pe.edu.utp.control_gastos_app.repository.TransactionRepository;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final ChartService chartService;

    public ReportServiceImpl(TransactionRepository transactionRepository,
                             ChartService chartService) {
        this.transactionRepository = transactionRepository;
        this.chartService = chartService;
    }

    @Override
    public void generateFinancialSummaryPdf(
            HttpServletResponse response, Long userId) throws Exception {

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        String mesAnio = now.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es"))
                .substring(0, 1).toUpperCase()
                + now.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es"))
                .substring(1)
                + " " + now.getYear();

        // Datos del mes actual
        Double income = transactionRepository
                .getTotalIncomeByUserAndPeriod(userId, startOfMonth, now);
        Double expenses = transactionRepository
                .getTotalExpensesByUserAndPeriod(userId, startOfMonth, now);
        Double balance = income - expenses;

        // Datos históricos totales
        Double totalIncome = transactionRepository.getTotalIncomeByUser(userId);
        Double totalExpenses = transactionRepository.getTotalExpensesByUser(userId);

        // Gastos por categoría (mes actual)
        List<CategoryExpenseDTO> byCategory = transactionRepository
                .findExpensesByCategoryAndPeriod(userId, startOfMonth, now);

        // Estado financiero relativo al mes
        String status;
        double savingsRate = income > 0 ? ((income - expenses) / income) * 100 : 0;
        if (savingsRate >= 30) status = "EXCELENTE";
        else if (savingsRate >= 10) status = "BUENO";
        else if (savingsRate >= 0)  status = "REGULAR";
        else                        status = "CRÍTICO";

        // --- Configurar respuesta ---
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=reporte_financiero_" + now.getYear()
                        + "_" + now.getMonthValue() + ".pdf");

        // --- Construir PDF ---
        Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // Colores
        Color colorPrimary   = new Color(30, 41, 59);   // #1e293b
        Color colorAccent    = new Color(59, 130, 246);  // azul
        Color colorGreen     = new Color(16, 185, 129);  // verde
        Color colorRed       = new Color(239, 68, 68);   // rojo
        Color colorMuted     = new Color(148, 163, 184); // gris
        Color colorBorder    = new Color(51, 65, 85);    // borde
        Color colorWhite     = Color.WHITE;

        // Fuentes
        Font fontTitle    = new Font(Font.HELVETICA, 20, Font.BOLD, colorAccent);
        Font fontSubtitle = new Font(Font.HELVETICA, 10, Font.NORMAL, colorMuted);
        Font fontSection  = new Font(Font.HELVETICA, 12, Font.BOLD, colorPrimary);
        Font fontLabel    = new Font(Font.HELVETICA, 9,  Font.NORMAL, colorMuted);
        Font fontValue    = new Font(Font.HELVETICA, 13, Font.BOLD, colorPrimary);
        Font fontGreen    = new Font(Font.HELVETICA, 13, Font.BOLD, colorGreen);
        Font fontRed      = new Font(Font.HELVETICA, 13, Font.BOLD, colorRed);
        Font fontTableHdr = new Font(Font.HELVETICA, 9,  Font.BOLD, colorWhite);
        Font fontTableRow = new Font(Font.HELVETICA, 9,  Font.NORMAL, colorPrimary);
        Font fontSmall    = new Font(Font.HELVETICA, 8,  Font.NORMAL, colorMuted);

        // ===== ENCABEZADO =====
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3f, 1f});
        header.setSpacingAfter(20);

        PdfPCell cellTitle = new PdfPCell();
        cellTitle.setBorder(Rectangle.NO_BORDER);
        cellTitle.addElement(new Paragraph("FinanceApp", fontTitle));
        cellTitle.addElement(new Paragraph(
                "Reporte Financiero — " + mesAnio, fontSubtitle));
        header.addCell(cellTitle);

        PdfPCell cellDate = new PdfPCell(new Phrase(
                "Generado el\n" + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fontSmall));
        cellDate.setBorder(Rectangle.NO_BORDER);
        cellDate.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellDate.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(cellDate);

        doc.add(header);

        // Línea separadora
        doc.add(lineSeparator(colorAccent));
        doc.add(Chunk.NEWLINE);

        // ===== TARJETAS DE RESUMEN (mes actual) =====
        doc.add(sectionTitle("Resumen del mes", fontSection));
        doc.add(Chunk.NEWLINE);

        PdfPTable cards = new PdfPTable(3);
        cards.setWidthPercentage(100);
        cards.setSpacingAfter(20);

        cards.addCell(summaryCard("Ingresos del mes",
                "S/ " + String.format("%.2f", income), colorGreen, fontLabel, fontGreen));
        cards.addCell(summaryCard("Gastos del mes",
                "S/ " + String.format("%.2f", expenses), colorRed, fontLabel, fontRed));
        cards.addCell(summaryCard("Balance neto",
                "S/ " + String.format("%.2f", balance),
                balance >= 0 ? colorGreen : colorRed,
                fontLabel,
                balance >= 0 ? fontGreen : fontRed));

        doc.add(cards);

        // ===== ESTADO FINANCIERO =====
        Color statusColor = switch (status) {
            case "EXCELENTE" -> colorGreen;
            case "BUENO"     -> new Color(34, 197, 94);
            case "REGULAR"   -> new Color(234, 179, 8);
            default          -> colorRed;
        };
        Font fontStatus = new Font(Font.HELVETICA, 11, Font.BOLD, statusColor);

        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(100);
        statusTable.setSpacingAfter(20);

        PdfPCell statusCell = new PdfPCell();
        statusCell.setPadding(10);
        statusCell.setBackgroundColor(new Color(248, 250, 252));
        statusCell.setBorderColor(statusColor);
        statusCell.setBorderWidth(1.5f);
        statusCell.addElement(new Phrase(
                "Estado financiero del mes: " + status
                        + "   (tasa de ahorro: " + String.format("%.1f", savingsRate) + "%)",
                fontStatus));
        statusTable.addCell(statusCell);
        doc.add(statusTable);

        // ===== GASTOS POR CATEGORÍA =====
        doc.add(sectionTitle("Gastos por categoría (mes actual)", fontSection));
        doc.add(Chunk.NEWLINE);

        if (byCategory.isEmpty()) {
            doc.add(new Paragraph("No hay gastos registrados este mes.", fontLabel));
        } else {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1.5f, 1.5f});
            table.setSpacingAfter(20);

            // Cabecera
            for (String h : new String[]{"Categoría", "Monto", "% del total"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, fontTableHdr));
                c.setBackgroundColor(colorPrimary);
                c.setPadding(7);
                c.setBorder(Rectangle.NO_BORDER);
                table.addCell(c);
            }

            boolean alternate = false;
            Color rowAlt = new Color(248, 250, 252);
            for (CategoryExpenseDTO cat : byCategory) {
                double pct = expenses > 0 ? (cat.getTotal() / expenses) * 100 : 0;
                Color bg = alternate ? rowAlt : colorWhite;

                PdfPCell cName = new PdfPCell(new Phrase(cat.getCategoryName(), fontTableRow));
                cName.setBackgroundColor(bg);
                cName.setPadding(6);
                cName.setBorderColor(colorBorder);

                PdfPCell cAmt = new PdfPCell(new Phrase(
                        "S/ " + String.format("%.2f", cat.getTotal()), fontTableRow));
                cAmt.setBackgroundColor(bg);
                cAmt.setPadding(6);
                cAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cAmt.setBorderColor(colorBorder);

                PdfPCell cPct = new PdfPCell(new Phrase(
                        String.format("%.1f%%", pct), fontTableRow));
                cPct.setBackgroundColor(bg);
                cPct.setPadding(6);
                cPct.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cPct.setBorderColor(colorBorder);

                table.addCell(cName);
                table.addCell(cAmt);
                table.addCell(cPct);
                alternate = !alternate;
            }
            doc.add(table);
        }

        // ===== TOTALES HISTÓRICOS =====
        doc.add(lineSeparator(colorBorder));
        doc.add(Chunk.NEWLINE);
        doc.add(sectionTitle("Totales históricos", fontSection));
        doc.add(Chunk.NEWLINE);

        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(60);
        totals.setHorizontalAlignment(Element.ALIGN_LEFT);
        totals.setSpacingAfter(20);

        addTotalRow(totals, "Total ingresos acumulados",
                "S/ " + String.format("%.2f", totalIncome), fontLabel, fontGreen);
        addTotalRow(totals, "Total gastos acumulados",
                "S/ " + String.format("%.2f", totalExpenses), fontLabel, fontRed);
        addTotalRow(totals, "Balance acumulado",
                "S/ " + String.format("%.2f", totalIncome - totalExpenses),
                fontLabel,
                (totalIncome - totalExpenses) >= 0 ? fontGreen : fontRed);

        doc.add(totals);

        // Pie de página
        doc.add(lineSeparator(colorBorder));
        Paragraph footer = new Paragraph(
                "Este reporte fue generado automáticamente por FinanceApp. "
                        + "Los datos corresponden al usuario autenticado.", fontSmall);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(8);
        doc.add(footer);

        doc.close();
    }

    // ---- Helpers ----

    private Paragraph sectionTitle(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(6);
        return p;
    }

    private LineSeparator lineSeparator(Color color) {
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(color);
        ls.setLineWidth(0.8f);
        return ls;
    }

    private PdfPCell summaryCard(String label, String value,
                                 Color valueColor, Font fontLabel, Font fontValue) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(12);
        cell.setBorderColor(new Color(226, 232, 240));
        cell.addElement(new Paragraph(label, fontLabel));
        Paragraph val = new Paragraph(value, fontValue);
        val.setSpacingBefore(4);
        cell.addElement(val);
        return cell;
    }

    private void addTotalRow(PdfPTable table, String label,
                             String value, Font fontLabel, Font fontValue) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, fontLabel));
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setPadding(4);

        PdfPCell cValue = new PdfPCell(new Phrase(value, fontValue));
        cValue.setBorder(Rectangle.NO_BORDER);
        cValue.setPadding(4);
        cValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(cLabel);
        table.addCell(cValue);
    }
}