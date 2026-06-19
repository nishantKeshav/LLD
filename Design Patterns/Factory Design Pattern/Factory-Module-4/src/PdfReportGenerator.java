public class PdfReportGenerator implements ReportGenerator{

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        System.out.println();
        System.out.println("================================================================================");
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();
        content.append("PDF REPORT\n");
        content.append("Title: ").append(request.getReportTitle()).append("\n");
        content.append("Requested By: ").append(request.getRequestedBy()).append("\n");
        content.append("Rows: ").append(request.getRows().size()).append("\n");
        content.append("Total Amount: ").append(totalAmount).append("\n");
        content.append("Report Format: ").append(request.getReportFormat()).append("\n");
        content.append("Creation Date: ").append(request.getCreatedAt()).append("\n");

        return new ReportGenerationResult(
                request.getReportId(),
                request.getReportTitle(),
                request.getReportFormat(),
                true,
                "PDF report generated successfully",
                request.getReportId() + ".pdf",
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
