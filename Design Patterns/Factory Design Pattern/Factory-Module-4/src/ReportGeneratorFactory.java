public class ReportGeneratorFactory {

    private ReportGeneratorFactory() {
        // Utility Class
    }

    public static ReportGenerator getInstance(ReportFormat reportFormat) {
        if (reportFormat == null) {
            throw new IllegalArgumentException("Report Format cannot be null");
        }
        return switch (reportFormat) {
            case CSV -> new CsvReportGenerator();
            case HTML -> new HtmlReportGenerator();
            case PDF -> new PdfReportGenerator();
            case EXCEL -> new ExcelReportGenerator();
            default -> throw new IllegalArgumentException("Report Format not supported");
        };
    }
}
