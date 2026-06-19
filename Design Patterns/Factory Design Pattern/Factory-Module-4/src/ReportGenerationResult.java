public class ReportGenerationResult {

    private final String reportId;
    private final String reportTitle;
    private final ReportFormat reportFormat;
    private final boolean success;
    private final String message;
    private final String generatedFileName;
    private final String generatedContent;
    private final int totalRows;
    private final double totalAmount;
    private final String generatorUsed;

    public ReportGenerationResult(String reportId, String reportTitle, ReportFormat reportFormat, boolean success,
                                  String message, String generatedFileName, String generatedContent, int totalRows,
                                  double totalAmount, String generatorUsed) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.reportFormat = reportFormat;
        this.success = success;
        this.message = message;
        this.generatedFileName = generatedFileName;
        this.generatedContent = generatedContent;
        this.totalRows = totalRows;
        this.totalAmount = totalAmount;
        this.generatorUsed = generatorUsed;
    }

    public String getReportId() {
        return reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public String getGeneratedContent() {
        return generatedContent;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getGeneratorUsed() {
        return generatorUsed;
    }

}
