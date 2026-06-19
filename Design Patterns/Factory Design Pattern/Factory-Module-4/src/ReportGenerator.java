public interface ReportGenerator {
    ReportGenerationResult generate(ReportRequest request);
    String getGeneratorName();
}