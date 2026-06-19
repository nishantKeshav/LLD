public class HtmlReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        System.out.println();
        System.out.println("================================================================================");
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        content.append("<h1>").append(request.getReportTitle()).append("</h1>\n");
        content.append("<table>\n");
        content.append("<tr>")
                .append("<th>Row ID</th>")
                .append("<th>Customer ID</th>")
                .append("<th>Customer Name</th>")
                .append("<th>Amount</th>")
                .append("<th>Status</th>")
                .append("</tr>\n");
        for (ReportRow row : request.getRows()) {
            content.append("<tr>\n");
            content.append("<td>").append(row.getRowId()).append("</td>\n");
            content.append("<td>").append(row.getCustomerId()).append("</td>\n");
            content.append("<td>").append(row.getCustomerName()).append("</td>\n");
            content.append("<td>").append(row.getAmount()).append("</td>\n");
            content.append("<td>").append(row.getStatus()).append("</td>\n");
            content.append("</tr>\n");
        }
        content.append("</table>\n");
        content.append("</table>\n");
        content.append("</body>\n");
        content.append("</html>\n");

        return new ReportGenerationResult(
                request.getReportId(),
                request.getReportTitle(),
                request.getReportFormat(),
                true,
                "HTML report generated successfully",
                request.getReportId() + ".html",
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
