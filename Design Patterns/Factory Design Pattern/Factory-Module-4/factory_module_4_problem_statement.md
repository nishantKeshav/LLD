# Factory Design Pattern Practice — Module 4

## Report Generator Factory

### Difficulty Level

**Intermediate+**

### Pattern Focus

**Factory Pattern with request validation, service orchestration, format-specific generation, summary calculation, and structured result output**

### Backend Theme

**Report export system / admin dashboard export / transaction report generation**

---

# 1. Module Overview

This is **Module 4** of the Factory Design Pattern practice series.

In the earlier modules, you implemented:

```text
Module 1: NotificationType -> NotificationSender
Module 2: PaymentMode -> PaymentProcessor
Module 3: FileType -> DocumentParser -> ParseResult
```

In this module, you will build a backend-style report generation system where a user or admin can export the same report data in different formats.

The supported report formats are:

```text
PDF
CSV
EXCEL
HTML
```

The important idea is:

```text
Same input data.
Different output formats.
Different generator classes.
One common interface.
One factory to select the correct generator.
```

Instead of directly creating a generator like this:

```java
ReportGenerator generator = new PdfReportGenerator();
```

the service should ask a factory:

```java
ReportGenerator generator =
        ReportGeneratorFactory.getGenerator(request.getReportFormat());
```

The factory decides which concrete generator should be created.

---

# 2. Why This Module Exists

In real backend systems, report generation is extremely common.

Examples:

```text
Payment report export
Transaction report export
Customer report export
Invoice report export
Audit log export
Monthly MIS report
Billing summary report
Settlement report
Admin dashboard export
```

The data may be the same, but the output format can change.

For example, an admin may want:

```text
PDF   -> printable summary report
CSV   -> data export for analysis
EXCEL -> spreadsheet-like report for finance teams
HTML  -> browser-viewable report
```

The business problem is simple:

```text
Given a report request, generate the report in the requested format.
```

But the implementation should be clean.

The service should not be filled with `if-else` or `switch` blocks that create different generator classes.

That object creation responsibility belongs to the factory.

---

# 3. Core Factory Pattern Idea

Factory Pattern is used when:

```text
You have multiple classes implementing the same interface,
and you need to create the correct implementation based on input.
```

In this module:

```text
Input: ReportFormat
Output: Correct ReportGenerator implementation
```

Mapping:

```text
ReportFormat.PDF   -> PdfReportGenerator
ReportFormat.CSV   -> CsvReportGenerator
ReportFormat.EXCEL -> ExcelReportGenerator
ReportFormat.HTML  -> HtmlReportGenerator
```

The factory becomes the centralized place for report generator creation.

---

# 4. Why Module 4 Is More Difficult Than Module 3

Module 3 was:

```text
DocumentFile -> DocumentUploadService -> DocumentParserFactory -> DocumentParser -> ParseResult
```

Module 4 becomes:

```text
ReportRequest -> ReportExportService -> ReportGeneratorFactory -> ReportGenerator -> ReportGenerationResult
```

The complexity increases because Module 4 requires:

```text
1. A report request object with metadata.
2. Multiple report rows.
3. Row-level validation.
4. Request-level validation.
5. A structured result object.
6. Total row count calculation.
7. Total amount calculation.
8. Format-specific content generation.
9. Different output style for PDF, CSV, EXCEL, and HTML.
10. Correct generated file names and extensions.
11. A clean service layer.
12. A factory that creates generators only.
```

This is not just:

```text
ReportFormat -> ReportGenerator
```

It is a more realistic backend export workflow.

---

# 5. Problem Without Factory Pattern

Without Factory Pattern, the service might look like this:

```java
public class ReportExportService {

    public ReportGenerationResult exportReport(ReportRequest request) {
        ReportGenerator generator;

        if (request.getReportFormat() == ReportFormat.PDF) {
            generator = new PdfReportGenerator();
        } else if (request.getReportFormat() == ReportFormat.CSV) {
            generator = new CsvReportGenerator();
        } else if (request.getReportFormat() == ReportFormat.EXCEL) {
            generator = new ExcelReportGenerator();
        } else if (request.getReportFormat() == ReportFormat.HTML) {
            generator = new HtmlReportGenerator();
        } else {
            throw new IllegalArgumentException("Unsupported report format");
        }

        return generator.generate(request);
    }
}
```

This works, but it creates several design problems.

## Problems

```text
1. Generator creation logic is mixed with service logic.
2. ReportExportService knows every concrete generator class.
3. If a new format is added, service code must change.
4. Object creation logic can get duplicated in multiple places.
5. The service becomes harder to test and maintain.
6. The service violates separation of concerns.
```

---

# 6. Problem With Factory Pattern

With Factory Pattern, the service becomes cleaner:

```java
public class ReportExportService {

    public ReportGenerationResult exportReport(ReportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Report request cannot be null.");
        }

        ReportGenerator generator =
                ReportGeneratorFactory.getGenerator(request.getReportFormat());

        return generator.generate(request);
    }
}
```

Now the responsibilities are separated:

```text
ReportExportService
    -> coordinates the export flow

ReportGeneratorFactory
    -> creates the correct generator

PdfReportGenerator / CsvReportGenerator / ExcelReportGenerator / HtmlReportGenerator
    -> generate format-specific content

ReportGenerationResult
    -> carries generated output and metadata
```

---

# 7. Main Objective

Build a report export system using the Factory Design Pattern.

The system should:

```text
1. Accept a ReportRequest.
2. Validate report request metadata.
3. Validate all report rows.
4. Use ReportGeneratorFactory to create the correct ReportGenerator.
5. Generate format-specific report content.
6. Calculate total rows.
7. Calculate total amount.
8. Return a structured ReportGenerationResult.
9. Test PDF, CSV, EXCEL, and HTML output.
10. Avoid direct concrete generator creation in Main or service.
```

---

# 8. Required Classes

You need to implement:

```text
ReportFormat
ReportRow
ReportRequest
ReportGenerationResult
ReportGenerator
PdfReportGenerator
CsvReportGenerator
ExcelReportGenerator
HtmlReportGenerator
ReportGeneratorFactory
ReportRequestValidator
ReportExportService
AmountUtil
Main
```

---

# 9. Class Design Overview

## High-Level Structure

```text
ReportGenerator
    ├── PdfReportGenerator
    ├── CsvReportGenerator
    ├── ExcelReportGenerator
    └── HtmlReportGenerator

ReportGeneratorFactory
    └── Creates correct ReportGenerator based on ReportFormat

ReportRequest
    └── Holds report metadata and rows

ReportRow
    └── Represents one row of report data

ReportGenerationResult
    └── Holds generated output and metadata

ReportExportService
    └── Coordinates report export flow

AmountUtil
    └── Calculates total amount

Main
    └── Creates test data and tests all formats
```

## Runtime Flow

```text
Main
  -> ReportExportService
      -> ReportRequestValidator
      -> ReportGeneratorFactory
          -> Correct ReportGenerator
              -> ReportGenerationResult
```

---

# 10. Class 1: `ReportFormat`

## Purpose

`ReportFormat` represents the output format requested by the user.

Use an enum instead of raw strings.

## Required Code

```java
public enum ReportFormat {
    PDF,
    CSV,
    EXCEL,
    HTML
}
```

## Why enum is better than String

Bad:

```java
ReportGeneratorFactory.getGenerator("pdf");
```

This can lead to typo bugs:

```text
"pdf"
"PDF "
"excel-report"
"Html"
```

Better:

```java
ReportGeneratorFactory.getGenerator(ReportFormat.PDF);
```

With enum, only valid values are allowed.

---

# 11. Class 2: `ReportRow`

## Purpose

`ReportRow` represents one row of data inside the report.

For example, if this is a payment report, each row may represent one customer's payment record.

## Required Fields

```text
rowId
customerId
customerName
amount
status
```

## Required Code

```java
public class ReportRow {

    private final String rowId;
    private final String customerId;
    private final String customerName;
    private final double amount;
    private final String status;

    public ReportRow(
            String rowId,
            String customerId,
            String customerName,
            double amount,
            String status
    ) {
        if (rowId == null || rowId.isBlank()) {
            throw new IllegalArgumentException("rowId cannot be null or blank.");
        }

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId cannot be null or blank.");
        }

        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("customerName cannot be null or blank.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative.");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status cannot be null or blank.");
        }

        this.rowId = rowId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.status = status;
    }

    public String getRowId() {
        return rowId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
```

## Validation Notes

Recommended validation:

```text
rowId cannot be null/blank
customerId cannot be null/blank
customerName cannot be null/blank
amount cannot be negative
status cannot be null/blank
```

For this module, `amount = 0` may be allowed because a report row can represent failed, cancelled, or zero-value records.

If you want stricter validation, you may require:

```text
amount > 0
```

but the module requirement only needs:

```text
amount >= 0
```

---

# 12. Class 3: `ReportRequest`

## Purpose

`ReportRequest` represents a request to generate a report.

It contains report metadata and the rows to be exported.

## Required Fields

```text
reportId
reportTitle
requestedBy
reportFormat
rows
createdAt
```

## Required Code

```java
import java.time.LocalDateTime;
import java.util.List;

public class ReportRequest {

    private final String reportId;
    private final String reportTitle;
    private final String requestedBy;
    private final ReportFormat reportFormat;
    private final List<ReportRow> rows;
    private final LocalDateTime createdAt;

    public ReportRequest(
            String reportId,
            String reportTitle,
            String requestedBy,
            ReportFormat reportFormat,
            List<ReportRow> rows
    ) {
        if (reportId == null || reportId.isBlank()) {
            throw new IllegalArgumentException("reportId cannot be null or blank.");
        }

        if (reportTitle == null || reportTitle.isBlank()) {
            throw new IllegalArgumentException("reportTitle cannot be null or blank.");
        }

        if (requestedBy == null || requestedBy.isBlank()) {
            throw new IllegalArgumentException("requestedBy cannot be null or blank.");
        }

        if (reportFormat == null) {
            throw new IllegalArgumentException("reportFormat cannot be null.");
        }

        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("rows cannot be null or empty.");
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
        return rows;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
```

## Important Immutability Point

Use this in the constructor:

```java
this.rows = List.copyOf(rows);
```

This protects your request from external modification.

Bad:

```java
this.rows = rows;
```

If the caller passes a mutable `ArrayList`, they can change it after creating the request.

Good:

```java
this.rows = List.copyOf(rows);
```

This makes the request safer and more backend-friendly.

---

# 13. Class 4: `ReportGenerationResult`

## Purpose

`ReportGenerationResult` represents the generated report output.

It should include metadata, success status, generated file name, generated content, and summary values.

## Required Fields

```text
reportId
reportTitle
reportFormat
success
message
generatedFileName
generatedContent
totalRows
totalAmount
generatorUsed
```

## Required Code

```java
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

    public ReportGenerationResult(
            String reportId,
            String reportTitle,
            ReportFormat reportFormat,
            boolean success,
            String message,
            String generatedFileName,
            String generatedContent,
            int totalRows,
            double totalAmount,
            String generatorUsed
    ) {
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
```

## Responsibility

```text
Represent whether generation succeeded.
Store generated file name.
Store generated report content.
Store total row count.
Store total amount.
Store generator class name.
```

---

# 14. Class 5: `ReportGenerator`

## Purpose

This is the common interface for all report generators.

Every generator must implement this interface.

## Required Code

```java
public interface ReportGenerator {
    ReportGenerationResult generate(ReportRequest request);
    String getGeneratorName();
}
```

## Why interface is needed

The service should not care about the exact generator class.

It should be able to write:

```java
ReportGenerator generator =
        ReportGeneratorFactory.getGenerator(request.getReportFormat());

ReportGenerationResult result = generator.generate(request);
```

The actual object may be:

```text
PdfReportGenerator
CsvReportGenerator
ExcelReportGenerator
HtmlReportGenerator
```

but the service treats all of them as:

```java
ReportGenerator
```

---

# 15. Class 6: `AmountUtil`

## Purpose

`AmountUtil` centralizes the total amount calculation.

Without this class, every generator may duplicate this logic:

```java
request.getRows()
        .stream()
        .mapToDouble(ReportRow::getAmount)
        .sum();
```

## Required Code

```java
public class AmountUtil {

    private AmountUtil() {
        // Utility class
    }

    public static double calculateTotalAmount(ReportRequest request) {
        return request.getRows()
                .stream()
                .mapToDouble(ReportRow::getAmount)
                .sum();
    }
}
```

## Responsibility

```text
Calculate total amount for a report request.
Avoid duplicate total amount logic across generators.
```

---

# 16. Class 7: `PdfReportGenerator`

## Purpose

`PdfReportGenerator` simulates generating a printable PDF-style report.

You do not need to create an actual PDF file.

The generated content can be a formatted string.

## Required Code

```java
public class PdfReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();

        content.append("PDF REPORT").append(System.lineSeparator());
        content.append("Title: ").append(request.getReportTitle()).append(System.lineSeparator());
        content.append("Requested By: ").append(request.getRequestedBy()).append(System.lineSeparator());
        content.append("Rows: ").append(request.getRows().size()).append(System.lineSeparator());
        content.append("Total Amount: ").append(totalAmount).append(System.lineSeparator());
        content.append("Report Format: ").append(request.getReportFormat()).append(System.lineSeparator());
        content.append("Creation Date: ").append(request.getCreatedAt()).append(System.lineSeparator());

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
```

## Important Newline Rule

Use real newlines:

```java
"\n"
```

or:

```java
System.lineSeparator()
```

Do not use:

```java
"\\n"
```

because that prints literal backslash-n in the output.

Bad output:

```text
PDF REPORT\nTitle: Monthly Payment Report\n
```

Good output:

```text
PDF REPORT
Title: Monthly Payment Report
```

---

# 17. Class 8: `CsvReportGenerator`

## Purpose

`CsvReportGenerator` generates comma-separated report content.

## Required Code

```java
public class CsvReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();

        content.append("rowId,customerId,customerName,amount,status")
                .append(System.lineSeparator());

        for (ReportRow row : request.getRows()) {
            content.append(row.getRowId()).append(",")
                    .append(row.getCustomerId()).append(",")
                    .append(row.getCustomerName()).append(",")
                    .append(row.getAmount()).append(",")
                    .append(row.getStatus())
                    .append(System.lineSeparator());
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
```

## Expected CSV Content

```text
rowId,customerId,customerName,amount,status
ROW-1,CUST-101,Amit,500.0,SUCCESS
ROW-2,CUST-102,Rahul,900.0,SUCCESS
ROW-3,CUST-103,Priya,1200.0,FAILED
```

## Common Mistake

Do not use:

```java
"\\n"
```

Use:

```java
System.lineSeparator()
```

or:

```java
"\n"
```

---

# 18. Class 9: `ExcelReportGenerator`

## Purpose

`ExcelReportGenerator` simulates spreadsheet-style output.

You do not need Apache POI or any external library.

The content should look like a table.

## Required Code

```java
public class ExcelReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();

        content.append("EXCEL SHEET: ")
                .append(request.getReportTitle())
                .append(System.lineSeparator());

        content.append("| Row ID | Customer ID | Customer Name | Amount | Status |")
                .append(System.lineSeparator());

        for (ReportRow row : request.getRows()) {
            content.append("| ")
                    .append(row.getRowId()).append(" | ")
                    .append(row.getCustomerId()).append(" | ")
                    .append(row.getCustomerName()).append(" | ")
                    .append(row.getAmount()).append(" | ")
                    .append(row.getStatus()).append(" |")
                    .append(System.lineSeparator());
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
```

## Important Excel Rules

The header has five columns:

```text
Row ID
Customer ID
Customer Name
Amount
Status
```

Each row must also have five values.

Wrong:

```text
| ROW-1 | Amit | 500.0 | SUCCESS |
```

Correct:

```text
| ROW-1 | CUST-101 | Amit | 500.0 | SUCCESS |
```

Also, always add a newline after the header.

---

# 19. Class 10: `HtmlReportGenerator`

## Purpose

`HtmlReportGenerator` generates browser-renderable HTML content.

You do not need a real template engine.

A simple HTML string is enough.

## Required Code

```java
public class HtmlReportGenerator implements ReportGenerator {

    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        StringBuilder content = new StringBuilder();

        content.append("<!DOCTYPE html>").append(System.lineSeparator());
        content.append("<html>").append(System.lineSeparator());
        content.append("<body>").append(System.lineSeparator());

        content.append("<h1>")
                .append(request.getReportTitle())
                .append("</h1>")
                .append(System.lineSeparator());

        content.append("<table>").append(System.lineSeparator());

        content.append("<tr>").append(System.lineSeparator());
        content.append("<th>Row ID</th>").append(System.lineSeparator());
        content.append("<th>Customer ID</th>").append(System.lineSeparator());
        content.append("<th>Customer Name</th>").append(System.lineSeparator());
        content.append("<th>Amount</th>").append(System.lineSeparator());
        content.append("<th>Status</th>").append(System.lineSeparator());
        content.append("</tr>").append(System.lineSeparator());

        for (ReportRow row : request.getRows()) {
            content.append("<tr>").append(System.lineSeparator());
            content.append("<td>").append(row.getRowId()).append("</td>").append(System.lineSeparator());
            content.append("<td>").append(row.getCustomerId()).append("</td>").append(System.lineSeparator());
            content.append("<td>").append(row.getCustomerName()).append("</td>").append(System.lineSeparator());
            content.append("<td>").append(row.getAmount()).append("</td>").append(System.lineSeparator());
            content.append("<td>").append(row.getStatus()).append("</td>").append(System.lineSeparator());
            content.append("</tr>").append(System.lineSeparator());
        }

        content.append("</table>").append(System.lineSeparator());
        content.append("</body>").append(System.lineSeparator());
        content.append("</html>").append(System.lineSeparator());

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
```

## Important HTML Rules

Use `<th>` for table headers.

Use `<td>` for table data.

Wrong:

```html
<tr>
  <th>ROW-1</th>
  <th>CUST-101</th>
</tr>
```

Correct:

```html
<tr>
  <td>ROW-1</td>
  <td>CUST-101</td>
</tr>
```

The HTML should include a header row:

```html
<tr>
  <th>Row ID</th>
  <th>Customer ID</th>
  <th>Customer Name</th>
  <th>Amount</th>
  <th>Status</th>
</tr>
```

---

# 20. Class 11: `ReportGeneratorFactory`

## Purpose

This is the main Factory class in Module 4.

It creates the correct `ReportGenerator` based on `ReportFormat`.

## Required Code

```java
public class ReportGeneratorFactory {

    private ReportGeneratorFactory() {
        // Utility class
    }

    public static ReportGenerator getGenerator(ReportFormat reportFormat) {
        if (reportFormat == null) {
            throw new IllegalArgumentException("Report format cannot be null.");
        }

        return switch (reportFormat) {
            case PDF -> new PdfReportGenerator();
            case CSV -> new CsvReportGenerator();
            case EXCEL -> new ExcelReportGenerator();
            case HTML -> new HtmlReportGenerator();
        };
    }
}
```

## Mapping

| ReportFormat | Returned Generator |
|---|---|
| `PDF` | `PdfReportGenerator` |
| `CSV` | `CsvReportGenerator` |
| `EXCEL` | `ExcelReportGenerator` |
| `HTML` | `HtmlReportGenerator` |

## Important Notes

Use:

```java
IllegalArgumentException
```

for invalid arguments.

Do not add unnecessary `default` if all enum values are already handled.

Why?

If you later add:

```java
JSON
```

to `ReportFormat`, the compiler can warn you that the switch is incomplete.

With a `default`, the compiler may not help as clearly.

---

# 21. Class 12: `ReportRequestValidator`

## Purpose

This class validates the request and its rows.

Even if you already validate in constructors, this validator is useful as a centralized request-validation layer in the service flow.

## Required Code

```java
public class ReportRequestValidator {

    private ReportRequestValidator() {
        // Utility class
    }

    public static void validate(ReportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Report request cannot be null.");
        }

        if (request.getReportId() == null || request.getReportId().isBlank()) {
            throw new IllegalArgumentException("Report ID cannot be null or blank.");
        }

        if (request.getReportTitle() == null || request.getReportTitle().isBlank()) {
            throw new IllegalArgumentException("Report title cannot be null or blank.");
        }

        if (request.getRequestedBy() == null || request.getRequestedBy().isBlank()) {
            throw new IllegalArgumentException("Requested by cannot be null or blank.");
        }

        if (request.getReportFormat() == null) {
            throw new IllegalArgumentException("Report format cannot be null.");
        }

        if (request.getRows() == null || request.getRows().isEmpty()) {
            throw new IllegalArgumentException("Report rows cannot be null or empty.");
        }

        for (ReportRow row : request.getRows()) {
            validateRow(row);
        }
    }

    private static void validateRow(ReportRow row) {
        if (row == null) {
            throw new IllegalArgumentException("Report row cannot be null.");
        }

        if (row.getRowId() == null || row.getRowId().isBlank()) {
            throw new IllegalArgumentException("Row ID cannot be null or blank.");
        }

        if (row.getCustomerId() == null || row.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (row.getCustomerName() == null || row.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank.");
        }

        if (row.getAmount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        if (row.getStatus() == null || row.getStatus().isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }
    }
}
```

## Responsibility

```text
Validate request object.
Validate metadata.
Validate all rows.
Reject null or empty rows list.
Reject invalid row fields.
```

---

# 22. Class 13: `ReportExportService`

## Purpose

This service coordinates the report export flow.

It should not directly create concrete generator classes.

## Required Code

```java
public class ReportExportService {

    public ReportGenerationResult exportReport(ReportRequest request) {
        ReportRequestValidator.validate(request);

        ReportGenerator generator =
                ReportGeneratorFactory.getGenerator(request.getReportFormat());

        return generator.generate(request);
    }
}
```

## Responsibility

```text
Validate request.
Ask factory for generator.
Call generator.
Return ReportGenerationResult.
```

## Important Rule

Do not do this in the service:

```java
ReportGenerator generator = new PdfReportGenerator();
```

The service should use the factory.

---

# 23. Class 14: `Main`

## Purpose

`Main` should test all four formats.

## Required Code

```java
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
```

---

# 24. Expected Output Style

Your output should show all four formats.

## PDF Output Example

```text
Report ID: REPORT-PDF
Report Title: Monthly Payment Report
Format: PDF
Success: true
Message: PDF report generated successfully
Generated File: REPORT-PDF.pdf
Total Rows: 3
Total Amount: 2600.0
Generator Used: PdfReportGenerator
Generated Content:
PDF REPORT
Title: Monthly Payment Report
Requested By: ADMIN-101
Rows: 3
Total Amount: 2600.0
Report Format: PDF
Creation Date: <createdAt>
```

## CSV Output Example

```text
Report ID: REPORT-CSV
Report Title: Monthly Payment Report
Format: CSV
Success: true
Message: CSV report generated successfully
Generated File: REPORT-CSV.csv
Total Rows: 3
Total Amount: 2600.0
Generator Used: CsvReportGenerator
Generated Content:
rowId,customerId,customerName,amount,status
ROW-1,CUST-101,Amit,500.0,SUCCESS
ROW-2,CUST-102,Rahul,900.0,SUCCESS
ROW-3,CUST-103,Priya,1200.0,FAILED
```

## Excel Output Example

```text
Report ID: REPORT-EXCEL
Report Title: Monthly Payment Report
Format: EXCEL
Success: true
Message: Excel report generated successfully
Generated File: REPORT-EXCEL.xlsx
Total Rows: 3
Total Amount: 2600.0
Generator Used: ExcelReportGenerator
Generated Content:
EXCEL SHEET: Monthly Payment Report
| Row ID | Customer ID | Customer Name | Amount | Status |
| ROW-1 | CUST-101 | Amit | 500.0 | SUCCESS |
| ROW-2 | CUST-102 | Rahul | 900.0 | SUCCESS |
| ROW-3 | CUST-103 | Priya | 1200.0 | FAILED |
```

## HTML Output Example

```text
Report ID: REPORT-HTML
Report Title: Monthly Payment Report
Format: HTML
Success: true
Message: HTML report generated successfully
Generated File: REPORT-HTML.html
Total Rows: 3
Total Amount: 2600.0
Generator Used: HtmlReportGenerator
Generated Content:
<!DOCTYPE html>
<html>
<body>
<h1>Monthly Payment Report</h1>
<table>
<tr>
<th>Row ID</th>
<th>Customer ID</th>
<th>Customer Name</th>
<th>Amount</th>
<th>Status</th>
</tr>
<tr>
<td>ROW-1</td>
<td>CUST-101</td>
<td>Amit</td>
<td>500.0</td>
<td>SUCCESS</td>
</tr>
</table>
</body>
</html>
```

---

# 25. Correct Execution Flow

For this line:

```java
ReportGenerationResult result = service.exportReport(request);
```

Execution flow:

```text
1. Main creates ReportRequest.
2. Main calls ReportExportService.exportReport(request).
3. ReportExportService validates the request.
4. ReportExportService reads request.getReportFormat().
5. ReportExportService passes ReportFormat to ReportGeneratorFactory.
6. Factory matches the format.
7. Factory creates the correct ReportGenerator.
8. Factory returns it as ReportGenerator interface.
9. ReportExportService calls generator.generate(request).
10. Concrete generator builds format-specific content.
11. Concrete generator calculates total rows and total amount.
12. Concrete generator returns ReportGenerationResult.
13. Main prints the result.
```

---

# 26. What Makes This Factory Pattern?

The Factory Pattern exists in this module because object creation is moved into a separate factory class.

## Without Factory

```java
ReportGenerator generator = new PdfReportGenerator();
```

The caller directly creates the concrete class.

## With Factory

```java
ReportGenerator generator =
        ReportGeneratorFactory.getGenerator(ReportFormat.PDF);
```

The caller asks the factory.

The factory creates the concrete class.

The caller only receives the interface.

This is the main Factory Pattern idea.

---

# 27. What Problem This Module Solves

This module solves:

```text
Concrete report generator creation spread across service/client code.
```

Instead of creating generators everywhere, creation is centralized.

## Before Factory

```text
ReportExportService knows about PdfReportGenerator.
ReportExportService knows about CsvReportGenerator.
ReportExportService knows about ExcelReportGenerator.
ReportExportService knows about HtmlReportGenerator.
```

## After Factory

```text
ReportExportService knows only ReportGeneratorFactory.
ReportExportService knows only ReportGenerator interface.
Factory knows concrete generator classes.
```

This is cleaner and easier to maintain.

---

# 28. Design Benefits

## 28.1 Cleaner Service Code

Service code becomes:

```java
ReportGenerator generator =
        ReportGeneratorFactory.getGenerator(request.getReportFormat());

return generator.generate(request);
```

It does not need a long switch or if-else chain.

## 28.2 Centralized Object Creation

All generator creation logic lives in one place:

```java
ReportGeneratorFactory
```

If generator construction changes, update the factory.

## 28.3 Easy to Add New Formats

Suppose later you add:

```java
JSON
```

You would add:

```java
public class JsonReportGenerator implements ReportGenerator {
    @Override
    public ReportGenerationResult generate(ReportRequest request) {
        double totalAmount = AmountUtil.calculateTotalAmount(request);

        String content = "{ \"reportId\": \"" + request.getReportId() + "\" }";

        return new ReportGenerationResult(
                request.getReportId(),
                request.getReportTitle(),
                request.getReportFormat(),
                true,
                "JSON report generated successfully",
                request.getReportId() + ".json",
                content,
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
```

Then update enum:

```java
public enum ReportFormat {
    PDF,
    CSV,
    EXCEL,
    HTML,
    JSON
}
```

Then update factory:

```java
case JSON -> new JsonReportGenerator();
```

The rest of the service code still works with:

```java
ReportGenerator
```

---

# 29. Important Rules

## Rule 1: Factory creates objects only

The factory should create generators.

It should not generate reports.

Wrong:

```java
public static ReportGenerationResult generate(ReportRequest request) {
    if (request.getReportFormat() == ReportFormat.PDF) {
        // build PDF content here
    }
}
```

Correct:

```java
ReportGenerator generator =
        ReportGeneratorFactory.getGenerator(request.getReportFormat());

return generator.generate(request);
```

## Rule 2: Generators generate reports

Each generator should contain format-specific output logic.

Examples:

```text
PdfReportGenerator creates printable text.
CsvReportGenerator creates comma-separated rows.
ExcelReportGenerator creates table-like output.
HtmlReportGenerator creates HTML table output.
```

## Rule 3: Service coordinates flow

The service should coordinate:

```text
request validation
factory call
generator call
return result
```

It should not directly create concrete generator classes.

## Rule 4: Output must differ by format

The module is not complete if all generators return the same content with different file extensions.

Each generator must build content differently.

---

# 30. Common Mistakes

## Mistake 1: Creating generators directly in Main or service

Wrong:

```java
ReportGenerator generator = new PdfReportGenerator();
```

Correct:

```java
ReportGenerator generator =
        ReportGeneratorFactory.getGenerator(request.getReportFormat());
```

## Mistake 2: Returning Object from factory

Wrong:

```java
public static Object getGenerator(ReportFormat reportFormat)
```

Correct:

```java
public static ReportGenerator getGenerator(ReportFormat reportFormat)
```

## Mistake 3: Putting generation logic inside factory

Wrong:

```java
public static ReportGenerationResult generate(ReportRequest request)
```

Correct:

```java
public static ReportGenerator getGenerator(ReportFormat reportFormat)
```

## Mistake 4: Literal newline issue

Wrong:

```java
content.append("PDF REPORT\\n");
```

This prints:

```text
PDF REPORT\n
```

Correct:

```java
content.append("PDF REPORT").append(System.lineSeparator());
```

or:

```java
content.append("PDF REPORT\n");
```

## Mistake 5: Excel header and rows mismatch

Wrong header:

```text
| Row ID | Customer ID | Customer Name | Amount | Status |
```

Wrong row:

```text
| ROW-1 | Amit | 500.0 | SUCCESS |
```

Correct row:

```text
| ROW-1 | CUST-101 | Amit | 500.0 | SUCCESS |
```

## Mistake 6: Using `<th>` for HTML data

Wrong:

```html
<th>ROW-1</th>
```

Correct:

```html
<td>ROW-1</td>
```

Use `<th>` only for column headers.

## Mistake 7: Hardcoded report titles

Wrong:

```java
content.append("EXCEL SHEET: Monthly Payment Report");
```

Correct:

```java
content.append("EXCEL SHEET: ")
        .append(request.getReportTitle());
```

## Mistake 8: Not validating empty rows

Wrong:

```java
if (rows == null) {
    throw new IllegalArgumentException("rows cannot be null");
}
```

Correct:

```java
if (rows == null || rows.isEmpty()) {
    throw new IllegalArgumentException("rows cannot be null or empty");
}
```

## Mistake 9: Not copying list defensively

Wrong:

```java
this.rows = rows;
```

Correct:

```java
this.rows = List.copyOf(rows);
```

## Mistake 10: Unnecessary default in enum switch

Avoid this:

```java
default -> throw new IllegalArgumentException("Unsupported format");
```

when all enum values are already handled.

Better:

```java
return switch (reportFormat) {
    case PDF -> new PdfReportGenerator();
    case CSV -> new CsvReportGenerator();
    case EXCEL -> new ExcelReportGenerator();
    case HTML -> new HtmlReportGenerator();
};
```

---

# 31. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `ReportFormat` enum | 1 |
| Correct `ReportRow` class | 1 |
| Correct `ReportRequest` class | 1 |
| Correct defensive list copy | 0.5 |
| Correct `ReportGenerationResult` class | 1 |
| Correct `ReportGenerator` interface | 1 |
| Correct `AmountUtil` | 0.5 |
| Correct `PdfReportGenerator` | 1 |
| Correct `CsvReportGenerator` | 1 |
| Correct `ExcelReportGenerator` | 1 |
| Correct `HtmlReportGenerator` | 1 |
| Correct `ReportGeneratorFactory` | 1.2 |
| Correct `ReportRequestValidator` | 1.2 |
| Correct `ReportExportService` | 1 |
| Main tests all formats | 1 |
| Output differs correctly by format | 1 |
| Correct newlines and formatting | 0.8 |
| Code readability and naming | 0.8 |

Total: **16 marks normalized to 10**

---

# 32. Ideal Final Code Structure

Your project can look like this:

```text
factory/module4/
    ReportFormat.java
    ReportRow.java
    ReportRequest.java
    ReportGenerationResult.java
    ReportGenerator.java
    PdfReportGenerator.java
    CsvReportGenerator.java
    ExcelReportGenerator.java
    HtmlReportGenerator.java
    ReportGeneratorFactory.java
    ReportRequestValidator.java
    ReportExportService.java
    AmountUtil.java
    Main.java
```

---

# 33. Difference Between Module 3 and Module 4

Module 3:

```text
FileType -> DocumentParser -> ParseResult
```

Module 4:

```text
ReportFormat -> ReportGenerator -> ReportGenerationResult
```

Module 4 is more advanced because it adds:

```text
Multiple data rows
Report metadata
Summary calculations
Different output formats
CSV-style output
Excel-style output
HTML table output
PDF-style output
Defensive list copying
More validation
Reusable utility logic
```

This makes it a more realistic backend export system.

---

# 34. Final Learning Goal

By completing Module 4, you should understand:

```text
1. How Factory Pattern applies to report generation.
2. How to select an implementation based on output format.
3. Why generators should share a common interface.
4. Why factory should create generators but not generate reports.
5. How services coordinate factory usage.
6. How request/result objects make backend code cleaner.
7. How different implementations can produce different output from the same input.
8. How validation makes factory-based systems reliable.
9. How defensive copying protects request objects.
10. How to keep calculation logic reusable with utility classes.
```

The key mental model is:

```text
ReportRequest comes in.
ReportExportService validates it.
ReportGeneratorFactory creates the right generator.
Generator creates format-specific output.
Generator returns ReportGenerationResult.
```

For this module:

```text
PDF   -> PdfReportGenerator
CSV   -> CsvReportGenerator
EXCEL -> ExcelReportGenerator
HTML  -> HtmlReportGenerator
```

That is Factory Pattern with a realistic report-export backend scenario.
