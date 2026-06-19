public class ExcelReportGenerator implements ReportGenerator{

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        System.out.println();
        System.out.println("================================================================================");
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();
        content.append("EXCEL SHEET: ").append(request.getReportTitle()).append("\n");
        content.append("| Row ID | Customer ID | Customer Name | Amount | Status |\n");
        for (ReportRow row : request.getRows()) {
            content.append("| ").append(row.getRowId()).append(" | ").append(row.getCustomerName()).append(" | ").append(row.getAmount()).append(" | ").append(row.getStatus()).append(" |\n");
        }

        return new ReportGenerationResult(
                request.getReportId(),
                request.getReportTitle(),
                request.getReportFormat(),
                true,
                "Excel report generated successfully",
                request.getReportId() + ".xlsx",
                content.toString(),
                request.getRows().size(),
                totalAmount,
                getGeneratorName()
        );
    }

    @Override
    public String getGeneratorName() {
        return this.getClass().getSimpleName();
    }
}
