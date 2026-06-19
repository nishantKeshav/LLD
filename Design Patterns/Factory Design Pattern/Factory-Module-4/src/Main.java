import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Factory Module 4 — Report Generator Factory");

        List<ReportRow> rows = List.of(
                new ReportRow("ROW-1", "CUST-101", "Amit", 500.0, "SUCCESS"),
                new ReportRow("ROW-2", "CUST-102", "Rahul", 900.0, "SUCCESS"),
                new ReportRow("ROW-3", "CUST-103", "Priya", 1200.0, "FAILED")
        );

        ReportExportService service = new ReportExportService();

        runReport(service, ReportFormat.PDF, rows);
        runReport(service, ReportFormat.CSV, rows);
        runReport(service, ReportFormat.EXCEL, rows);
        runReport(service, ReportFormat.HTML, rows);
    }

    private static void runReport(
            ReportExportService service,
            ReportFormat format,
            List<ReportRow> rows
    ) {
        ReportRequest request = new ReportRequest(
                "REPORT-" + format,
                "Monthly Payment Report",
                "ADMIN-101",
                format,
                rows
        );

        ReportGenerationResult result = service.exportReport(request);
        printResult(result);
    }

    private static void printResult(ReportGenerationResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Report ID: " + result.getReportId());
        System.out.println("Report Title: " + result.getReportTitle());
        System.out.println("Format: " + result.getReportFormat());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Generated File: " + result.getGeneratedFileName());
        System.out.println("Total Rows: " + result.getTotalRows());
        System.out.println("Total Amount: " + result.getTotalAmount());
        System.out.println("Generator Used: " + result.getGeneratorUsed());
        System.out.println("Generated Content:");
        System.out.println(result.getGeneratedContent());
        System.out.println("==================================================");
    }
}