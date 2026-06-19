import java.util.List;
import java.time.LocalDateTime;

public class ReportRequest {

    private final String reportId;
    private final String reportTitle;
    private final String requestedBy;
    private final ReportFormat reportFormat;
    private final List<ReportRow> rows;
    private final LocalDateTime createdAt;

    public ReportRequest(String reportId, String reportTitle, String requestedBy, ReportFormat reportFormat, List<ReportRow> rows) {
        if (reportId == null || reportId.isBlank()) {
            throw new IllegalArgumentException("reportId can not be null or blank");
        }
        if (reportTitle == null || reportTitle.isBlank()) {
            throw new IllegalArgumentException("reportTitle can not be null or blank");
        }
        if (requestedBy == null || requestedBy.isBlank()) {
            throw new IllegalArgumentException("requestedBy can not be null or blank");
        }
        if (reportFormat == null) {
            throw new IllegalArgumentException("reportFormat can not be null");
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("rows cannot be null or empty");
        }
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.requestedBy = requestedBy;
        this.reportFormat = reportFormat;
        this.rows = List.copyOf(rows);
        this.createdAt = LocalDateTime.now();
    }

    public String getReportId() {
        return reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    public List<ReportRow> getRows() {
        return List.copyOf(rows);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
