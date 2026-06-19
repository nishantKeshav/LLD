public class ReportExportService {

    public ReportGenerationResult exportReport(ReportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        ReportFormat reportFormat = request.getReportFormat();
        ReportGenerator generator = ReportGeneratorFactory.getInstance(reportFormat);
        return generator.generate(request);
    }
}