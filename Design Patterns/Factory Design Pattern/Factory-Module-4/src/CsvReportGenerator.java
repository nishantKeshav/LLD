public class CsvReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        System.out.println();
        System.out.println("================================================================================");
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();
        content.append("rowId,customerId,customerName,amount,status\n");

        for (ReportRow row : request.getRows()) {
            content.append(row.getRowId()).append(",")
                    .append(row.getCustomerId()).append(",")
                    .append(row.getCustomerName()).append(",")
                    .append(row.getAmount()).append(",")
                    .append(row.getStatus()).append("\n");
        }

        return new ReportGenerationResult(
                request.getReportId(),
                request.getReportTitle(),
                request.getReportFormat(),
                true,
                "CSV report generated successfully",
                request.getReportId() + ".csv",
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
