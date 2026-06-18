# Factory Design Pattern Practice — Module 3

## Document Parser Factory

### Difficulty Level

**Intermediate**

### Pattern Focus

**Factory Pattern with validation, request object, service layer, parser selection, and structured response**

### Backend Theme

**File upload / document ingestion / document parsing system**

---

# 1. Module Overview

This is **Module 3** of the Factory Design Pattern practice series.

In Module 1, you created a notification sender factory:

```text
NotificationType -> NotificationSender
```

In Module 2, you created a payment processor factory:

```text
PaymentMode -> PaymentProcessor
```

In Module 3, you will build a backend-style document parsing system where different uploaded file types require different parsers.

The system should support:

```text
PDF
CSV
JSON
XML
```

The main goal is to understand how the Factory Pattern helps select the correct parser based on the file type of an uploaded document.

Instead of directly creating parser objects like this:

```java
DocumentParser parser = new PdfDocumentParser();
```

the service/client code should ask the factory for the correct parser:

```java
DocumentParser parser =
        DocumentParserFactory.createDocumentParser(file.getFileType());
```

The factory will decide which concrete parser class to create.

---

# 2. Background: Why This Module Exists

In real backend systems, applications often accept uploaded documents.

Examples:

```text
Invoice upload
Bank statement upload
KYC document upload
Resume upload
Transaction CSV import
Configuration XML upload
JSON profile import
Report ingestion
```

Different file types need different parsing logic.

For example:

```text
PDF   -> extract text from a document
CSV   -> parse rows and columns
JSON  -> validate and parse object structure
XML   -> validate and parse tag-based structure
```

The upload service should not contain parser-selection logic like:

```java
if (file.getFileType() == FileType.PDF) {
    parser = new PdfDocumentParser();
} else if (file.getFileType() == FileType.CSV) {
    parser = new CsvDocumentParser();
}
```

That object creation logic should be centralized inside a factory.

The upload service should focus on the high-level workflow:

```text
Receive file
Validate file
Ask factory for parser
Parse file
Return ParseResult
```

---

# 3. Core Factory Pattern Idea

Factory Pattern is used when:

```text
You have multiple classes implementing the same interface,
and you need to create the correct implementation based on input.
```

In this module:

```text
Input: FileType
Output: Correct DocumentParser implementation
```

Mapping:

```text
FileType.PDF   -> PdfDocumentParser
FileType.CSV   -> CsvDocumentParser
FileType.JSON  -> JsonDocumentParser
FileType.XML   -> XmlDocumentParser
```

The factory becomes the centralized object-creation point for document parsers.

---

# 4. Why Module 3 Is More Advanced Than Module 2

Module 2 was mainly:

```text
PaymentMode -> PaymentProcessor
```

Module 3 adds more backend-style complexity:

```text
DocumentFile -> DocumentUploadService -> DocumentParserFactory -> DocumentParser -> ParseResult
```

This module introduces:

```text
1. A richer request/input object: DocumentFile
2. File metadata: fileId, fileName, size, uploadedBy, uploadDate
3. Constructor-level validation
4. Parser-specific behavior
5. Structured result object: ParseResult
6. Service layer orchestration
7. Factory-based parser creation
8. CSV row-counting behavior
9. Basic JSON/XML content validation
10. Cleaner error handling
```

This makes Module 3 closer to a real backend document ingestion pipeline.

---

# 5. Problem Without Factory Pattern

Without Factory Pattern, the upload service might look like this:

```java
public class DocumentUploadService {

    public ParseResult parseDocument(DocumentFile file) {
        DocumentParser parser;

        if (file.getFileType() == FileType.PDF) {
            parser = new PdfDocumentParser();
        } else if (file.getFileType() == FileType.CSV) {
            parser = new CsvDocumentParser();
        } else if (file.getFileType() == FileType.JSON) {
            parser = new JsonDocumentParser();
        } else if (file.getFileType() == FileType.XML) {
            parser = new XmlDocumentParser();
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }

        return parser.parse(file);
    }
}
```

Problems:

```text
1. Parser creation logic is mixed with upload service logic.
2. DocumentUploadService knows every concrete parser class.
3. If a new file type is added, service code must change.
4. Repeated parser-selection logic can spread across the application.
5. Testing becomes harder because concrete parser creation is hardcoded.
6. The service violates separation of concerns.
```

---

# 6. Problem With Factory Pattern

With Factory Pattern, the service becomes cleaner:

```java
public class DocumentUploadService {

    public ParseResult parseDocument(DocumentFile file) {
        if (file == null) {
            throw new IllegalArgumentException("Document file cannot be null.");
        }

        DocumentParser parser =
                DocumentParserFactory.createDocumentParser(file.getFileType());

        return parser.parse(file);
    }
}
```

Now:

```text
DocumentUploadService controls the parsing flow.
DocumentParserFactory controls parser object creation.
Concrete parsers control file-specific parsing behavior.
```

The client/service depends on:

```java
DocumentParser
```

not concrete classes like:

```java
PdfDocumentParser
CsvDocumentParser
JsonDocumentParser
XmlDocumentParser
```

---

# 7. Main Objective

Build a document parsing system using the Factory Design Pattern.

The system should:

```text
1. Accept a DocumentFile object.
2. Validate file metadata and content.
3. Determine the file type.
4. Use a factory to create the correct parser.
5. Parse the document using the selected parser.
6. Return a structured ParseResult.
7. Test PDF, CSV, JSON, and XML parsing.
8. Handle invalid file data cleanly.
9. Avoid direct parser object creation in Main or service code.
```

---

# 8. Required Classes

You need to implement:

```text
FileType
DocumentFile
ParseResult
DocumentParser
PdfDocumentParser
CsvDocumentParser
JsonDocumentParser
XmlDocumentParser
DocumentParserFactory
DocumentUploadService
Main
```

Optional if validation is not inside `DocumentFile`:

```text
DocumentFileValidator
```

For this module, constructor-level validation inside `DocumentFile` is acceptable and backend-friendly.

---

# 9. Class Design Overview

## High-Level Structure

```text
DocumentParser
    ├── PdfDocumentParser
    ├── CsvDocumentParser
    ├── JsonDocumentParser
    └── XmlDocumentParser

DocumentParserFactory
    └── Creates correct DocumentParser based on FileType

DocumentFile
    └── Holds uploaded file details

ParseResult
    └── Holds parsing result

DocumentUploadService
    └── Coordinates parsing flow

Main
    └── Creates test files and prints ParseResult
```

## Runtime Flow

```text
Main
  -> DocumentUploadService
      -> DocumentParserFactory
          -> Correct DocumentParser
              -> ParseResult
```

---

# 10. Class 1: `FileType`

## Purpose

`FileType` represents the type of uploaded document.

Use an enum instead of raw strings.

## Required Code

```java
public enum FileType {
    PDF,
    CSV,
    JSON,
    XML
}
```

## Why enum is better than String

Bad:

```java
DocumentParserFactory.createDocumentParser("pdf");
```

This can lead to typo bugs:

```text
"Pdf"
"PDF "
"json-file"
"xml_document"
```

Better:

```java
DocumentParserFactory.createDocumentParser(FileType.PDF);
```

With enum, only valid supported values are allowed.

---

# 11. Class 2: `DocumentFile`

## Purpose

`DocumentFile` represents an uploaded file.

It contains both file content and metadata.

## Required Fields

```text
fileId
fileName
fileType
content
sizeInKb
uploadedBy
uploadDate
```

## Required Code

```java
import java.time.LocalDateTime;

public class DocumentFile {

    private final String fileId;
    private final String fileName;
    private final FileType fileType;
    private final String content;
    private final int sizeInKb;
    private final String uploadedBy;
    private final LocalDateTime uploadDate;

    public DocumentFile(
            String fileId,
            String fileName,
            FileType fileType,
            String content,
            int sizeInKb,
            String uploadedBy
    ) {
        if (fileId == null || fileId.isBlank()) {
            throw new IllegalArgumentException("fileId cannot be null or blank.");
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName cannot be null or blank.");
        }

        if (fileType == null) {
            throw new IllegalArgumentException("fileType cannot be null.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content cannot be null or blank.");
        }

        if (sizeInKb <= 0) {
            throw new IllegalArgumentException("sizeInKb must be greater than zero.");
        }

        if (uploadedBy == null || uploadedBy.isBlank()) {
            throw new IllegalArgumentException("uploadedBy cannot be null or blank.");
        }

        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.content = content;
        this.sizeInKb = sizeInKb;
        this.uploadedBy = uploadedBy;
        this.uploadDate = LocalDateTime.now();
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getContent() {
        return content;
    }

    public int getSizeInKb() {
        return sizeInKb;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
}
```

## Responsibility

```text
Represent an uploaded document.
Store metadata and content.
Prevent invalid document files from being created.
Automatically capture upload date/time.
```

## Why validation is useful here

A `DocumentFile` should not exist in an invalid state.

Bad:

```java
new DocumentFile(null, "", null, "", -1, "");
```

Better:

```text
Invalid file data should be rejected during object creation.
```

---

# 12. Class 3: `ParseResult`

## Purpose

`ParseResult` represents the output of the parsing operation.

Instead of only printing parsing logs, every parser should return a structured response.

## Required Fields

```text
fileId
fileName
fileType
success
message
recordsParsed
parserUsed
```

## Required Code

```java
public class ParseResult {

    private final String fileId;
    private final String fileName;
    private final FileType fileType;
    private final boolean success;
    private final String message;
    private final int recordsParsed;
    private final String parserUsed;

    public ParseResult(
            String fileId,
            String fileName,
            FileType fileType,
            boolean success,
            String message,
            int recordsParsed,
            String parserUsed
    ) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.success = success;
        this.message = message;
        this.recordsParsed = recordsParsed;
        this.parserUsed = parserUsed;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getRecordsParsed() {
        return recordsParsed;
    }

    public String getParserUsed() {
        return parserUsed;
    }
}
```

## Responsibility

```text
Represent whether parsing succeeded.
Store parsing message.
Store number of records parsed.
Store parser name used.
Return structured data from parsers.
```

---

# 13. Class 4: `DocumentParser`

## Purpose

This is the common interface for all document parsers.

Every parser must implement this interface.

## Required Code

```java
public interface DocumentParser {
    ParseResult parse(DocumentFile file);
    String getParserName();
}
```

## Why interface is needed

The service should not care about the exact parser class.

It should be able to write:

```java
DocumentParser parser =
        DocumentParserFactory.createDocumentParser(file.getFileType());

ParseResult result = parser.parse(file);
```

The actual object may be:

```text
PdfDocumentParser
CsvDocumentParser
JsonDocumentParser
XmlDocumentParser
```

but the service treats all of them as:

```java
DocumentParser
```

---

# 14. Class 5: `PdfDocumentParser`

## Purpose

This class handles PDF file parsing.

## Required Code

```java
public class PdfDocumentParser implements DocumentParser {

    @Override
    public ParseResult parse(DocumentFile file) {
        System.out.println();
        System.out.println("============================================================================");
        System.out.println("Started File Parsing using " + getParserName());
        System.out.println("Parsing " + file.getFileType() + " document: " + file.getFileName());
        System.out.println("Content : " + file.getContent());
        System.out.println("File Size : " + file.getSizeInKb());
        System.out.println("Uploaded By : " + file.getUploadedBy());
        System.out.println("Uploaded Date : " + file.getUploadDate());

        return new ParseResult(
                file.getFileId(),
                file.getFileName(),
                file.getFileType(),
                true,
                "PDF document parsed successfully",
                1,
                getParserName()
        );
    }

    @Override
    public String getParserName() {
        return this.getClass().getSimpleName();
    }
}
```

## Expected Behavior

For this module, PDF parsing can be simulated.

Use:

```text
recordsParsed = 1
```

because a PDF is treated as one document.

---

# 15. Class 6: `CsvDocumentParser`

## Purpose

This class handles CSV file parsing.

## Important CSV Requirement

CSV content should use actual newline characters:

```java
"id,name,amount\n1,Amit,500\n2,Rahul,900"
```

When written inside a Java string, this creates actual line breaks.

Do not use escaped literal backslash-n:

```java
"id,name,amount\\n1,Amit,500\\n2,Rahul,900"
```

because that produces this literal output:

```text
id,name,amount\n1,Amit,500\n2,Rahul,900
```

and row counting will not work correctly.

## Required Code

```java
public class CsvDocumentParser implements DocumentParser {

    @Override
    public ParseResult parse(DocumentFile file) {
        System.out.println();
        System.out.println("============================================================================");
        System.out.println("Started File Parsing using " + getParserName());
        System.out.println("Parsing " + file.getFileType() + " document: " + file.getFileName());
        System.out.println("Content : " + file.getContent());
        System.out.println("File Size : " + file.getSizeInKb());
        System.out.println("Uploaded By : " + file.getUploadedBy());
        System.out.println("Uploaded Date : " + file.getUploadDate());

        int recordsParsed = file.getContent().split("\\n").length;

        return new ParseResult(
                file.getFileId(),
                file.getFileName(),
                file.getFileType(),
                true,
                "CSV document parsed successfully",
                recordsParsed,
                getParserName()
        );
    }

    @Override
    public String getParserName() {
        return this.getClass().getSimpleName();
    }
}
```

## Expected Behavior

Given this content:

```text
id,name,amount
1,Amit,500
2,Rahul,900
```

`recordsParsed` should be:

```text
3
```

This includes the header row.

If you want to exclude the header row later, you may count:

```text
recordsParsed = total lines - 1
```

but for this module, count all lines.

---

# 16. Class 7: `JsonDocumentParser`

## Purpose

This class handles JSON file parsing.

For this module, actual JSON library parsing is not required.

You only need basic simulated validation.

## Required Code

```java
public class JsonDocumentParser implements DocumentParser {

    @Override
    public ParseResult parse(DocumentFile file) {
        String content = file.getContent().trim();

        if (!content.startsWith("{") || !content.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON content.");
        }

        System.out.println();
        System.out.println("============================================================================");
        System.out.println("Started File Parsing using " + getParserName());
        System.out.println("Parsing " + file.getFileType() + " document: " + file.getFileName());
        System.out.println("Content : " + file.getContent());
        System.out.println("File Size : " + file.getSizeInKb());
        System.out.println("Uploaded By : " + file.getUploadedBy());
        System.out.println("Uploaded Date : " + file.getUploadDate());

        return new ParseResult(
                file.getFileId(),
                file.getFileName(),
                file.getFileType(),
                true,
                "JSON document parsed successfully",
                1,
                getParserName()
        );
    }

    @Override
    public String getParserName() {
        return this.getClass().getSimpleName();
    }
}
```

## Expected Behavior

Valid JSON-like content:

```java
"{ \"name\": \"Amit\", \"age\": 25 }"
```

Invalid JSON-like content:

```java
"\"name\": \"Amit\""
```

The invalid content should throw:

```text
Invalid JSON content.
```

---

# 17. Class 8: `XmlDocumentParser`

## Purpose

This class handles XML file parsing.

For this module, actual XML library parsing is not required.

You only need basic simulated validation.

## Required Code

```java
public class XmlDocumentParser implements DocumentParser {

    @Override
    public ParseResult parse(DocumentFile file) {
        String content = file.getContent().trim();

        if (!content.contains("<") || !content.contains(">")) {
            throw new IllegalArgumentException("Invalid XML content.");
        }

        System.out.println();
        System.out.println("============================================================================");
        System.out.println("Started File Parsing using " + getParserName());
        System.out.println("Parsing " + file.getFileType() + " document: " + file.getFileName());
        System.out.println("Content : " + file.getContent());
        System.out.println("File Size : " + file.getSizeInKb());
        System.out.println("Uploaded By : " + file.getUploadedBy());
        System.out.println("Uploaded Date : " + file.getUploadDate());

        return new ParseResult(
                file.getFileId(),
                file.getFileName(),
                file.getFileType(),
                true,
                "XML document parsed successfully",
                1,
                getParserName()
        );
    }

    @Override
    public String getParserName() {
        return this.getClass().getSimpleName();
    }
}
```

## Expected Behavior

Valid XML-like content:

```java
"<config><env>dev</env></config>"
```

Invalid XML-like content:

```java
"config env dev"
```

The invalid content should throw:

```text
Invalid XML content.
```

---

# 18. Class 9: `DocumentParserFactory`

## Purpose

This is the main Factory class in Module 3.

It creates the correct `DocumentParser` based on `FileType`.

## Required Code

```java
public class DocumentParserFactory {

    private DocumentParserFactory() {
        // Utility class
    }

    public static DocumentParser createDocumentParser(FileType fileType) {
        if (fileType == null) {
            throw new IllegalArgumentException("FileType cannot be null.");
        }

        return switch (fileType) {
            case PDF -> new PdfDocumentParser();
            case CSV -> new CsvDocumentParser();
            case JSON -> new JsonDocumentParser();
            case XML -> new XmlDocumentParser();
        };
    }
}
```

## Mapping

| FileType | Returned Parser |
|---|---|
| `PDF` | `PdfDocumentParser` |
| `CSV` | `CsvDocumentParser` |
| `JSON` | `JsonDocumentParser` |
| `XML` | `XmlDocumentParser` |

## Important Notes

Use:

```java
IllegalArgumentException
```

instead of:

```java
NullPointerException
```

for invalid input.

Also, do not add unnecessary `default` if all enum values are already handled.

This helps the compiler warn you if you add a new `FileType` later and forget to update the switch.

---

# 19. Class 10: `DocumentUploadService`

## Purpose

This service coordinates the document parsing flow.

It should:

```text
1. Accept DocumentFile.
2. Validate null file.
3. Ask DocumentParserFactory for the correct parser.
4. Call parser.parse(file).
5. Return ParseResult.
```

## Required Code

```java
public class DocumentUploadService {

    public ParseResult parseDocument(DocumentFile file) {
        if (file == null) {
            throw new IllegalArgumentException("Document file cannot be null.");
        }

        DocumentParser parser =
                DocumentParserFactory.createDocumentParser(file.getFileType());

        return parser.parse(file);
    }
}
```

## Why this service exists

This makes the flow more realistic.

In real backend code, the flow often looks like:

```text
Controller -> Service -> Factory -> Implementation
```

For this module:

```text
Main -> DocumentUploadService -> DocumentParserFactory -> DocumentParser
```

---

# 20. Class 11: `Main`

## Purpose

`Main` should test all four file types.

## Required Code

```java
public class Main {

    public static void main(String[] args) {
        System.out.println("Factory Module 3 — Document Parser Factory");

        DocumentUploadService uploadService = new DocumentUploadService();

        DocumentFile pdfFile = new DocumentFile(
                "FILE-101",
                "invoice.pdf",
                FileType.PDF,
                "PDF content here",
                512,
                "CUST-101"
        );

        ParseResult pdfResult = uploadService.parseDocument(pdfFile);
        printResult(pdfResult);

        DocumentFile csvFile = new DocumentFile(
                "FILE-102",
                "transactions.csv",
                FileType.CSV,
                "id,name,amount\n1,Amit,500\n2,Rahul,900",
                256,
                "CUST-102"
        );

        ParseResult csvResult = uploadService.parseDocument(csvFile);
        printResult(csvResult);

        DocumentFile jsonFile = new DocumentFile(
                "FILE-103",
                "profile.json",
                FileType.JSON,
                "{ \"name\": \"Amit\", \"age\": 25 }",
                128,
                "CUST-103"
        );

        ParseResult jsonResult = uploadService.parseDocument(jsonFile);
        printResult(jsonResult);

        DocumentFile xmlFile = new DocumentFile(
                "FILE-104",
                "config.xml",
                FileType.XML,
                "<config><env>dev</env></config>",
                64,
                "CUST-104"
        );

        ParseResult xmlResult = uploadService.parseDocument(xmlFile);
        printResult(xmlResult);
    }

    private static void printResult(ParseResult result) {
        System.out.println("--------------------------------------------------");
        System.out.println("File ID: " + result.getFileId());
        System.out.println("File Name: " + result.getFileName());
        System.out.println("File Type: " + result.getFileType());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Records Parsed: " + result.getRecordsParsed());
        System.out.println("Parser Used: " + result.getParserUsed());
        System.out.println("--------------------------------------------------");
    }
}
```

---

# 21. Expected Output

Expected output style:

```text
Factory Module 3 — Document Parser Factory

============================================================================
Started File Parsing using PdfDocumentParser
Parsing PDF document: invoice.pdf
Content : PDF content here
File Size : 512
Uploaded By : CUST-101
Uploaded Date : <current date-time>
--------------------------------------------------
File ID: FILE-101
File Name: invoice.pdf
File Type: PDF
Success: true
Message: PDF document parsed successfully
Records Parsed: 1
Parser Used: PdfDocumentParser
--------------------------------------------------

============================================================================
Started File Parsing using CsvDocumentParser
Parsing CSV document: transactions.csv
Content : id,name,amount
1,Amit,500
2,Rahul,900
File Size : 256
Uploaded By : CUST-102
Uploaded Date : <current date-time>
--------------------------------------------------
File ID: FILE-102
File Name: transactions.csv
File Type: CSV
Success: true
Message: CSV document parsed successfully
Records Parsed: 3
Parser Used: CsvDocumentParser
--------------------------------------------------

============================================================================
Started File Parsing using JsonDocumentParser
Parsing JSON document: profile.json
Content : { "name": "Amit", "age": 25 }
File Size : 128
Uploaded By : CUST-103
Uploaded Date : <current date-time>
--------------------------------------------------
File ID: FILE-103
File Name: profile.json
File Type: JSON
Success: true
Message: JSON document parsed successfully
Records Parsed: 1
Parser Used: JsonDocumentParser
--------------------------------------------------

============================================================================
Started File Parsing using XmlDocumentParser
Parsing XML document: config.xml
Content : <config><env>dev</env></config>
File Size : 64
Uploaded By : CUST-104
Uploaded Date : <current date-time>
--------------------------------------------------
File ID: FILE-104
File Name: config.xml
File Type: XML
Success: true
Message: XML document parsed successfully
Records Parsed: 1
Parser Used: XmlDocumentParser
--------------------------------------------------
```

---

# 22. Correct Execution Flow

For this line:

```java
ParseResult csvResult = uploadService.parseDocument(csvFile);
```

Execution flow:

```text
1. Main creates DocumentFile with FileType.CSV.
2. Main calls uploadService.parseDocument(csvFile).
3. DocumentUploadService checks file is not null.
4. DocumentUploadService calls csvFile.getFileType().
5. DocumentUploadService passes FileType.CSV to DocumentParserFactory.
6. Factory switch matches CSV.
7. Factory creates new CsvDocumentParser().
8. Factory returns it as DocumentParser.
9. DocumentUploadService calls parser.parse(csvFile).
10. CsvDocumentParser parses the CSV content.
11. CsvDocumentParser returns ParseResult.
12. Main prints ParseResult.
```

The same flow applies for PDF, JSON, and XML.

---

# 23. What Makes This Factory Pattern?

The Factory Pattern exists in this module because parser object creation is moved into a separate factory class.

## Without Factory

```java
DocumentParser parser = new CsvDocumentParser();
```

The caller directly creates the concrete parser.

## With Factory

```java
DocumentParser parser =
        DocumentParserFactory.createDocumentParser(FileType.CSV);
```

The caller asks the factory.

The factory creates the concrete parser.

The caller only receives the interface.

This is the main Factory Pattern idea.

---

# 24. What Problem This Module Solves

This module solves:

```text
Concrete parser creation spread across service/client code.
```

Instead of creating parsers everywhere, parser creation is centralized.

## Before Factory

```text
DocumentUploadService knows about PdfDocumentParser.
DocumentUploadService knows about CsvDocumentParser.
DocumentUploadService knows about JsonDocumentParser.
DocumentUploadService knows about XmlDocumentParser.
```

## After Factory

```text
DocumentUploadService knows only DocumentParserFactory.
DocumentUploadService knows only DocumentParser interface.
Factory knows concrete parser classes.
```

This is cleaner and easier to maintain.

---

# 25. Design Benefits

## 25.1 Cleaner Service Code

Service code becomes:

```java
DocumentParser parser =
        DocumentParserFactory.createDocumentParser(file.getFileType());

return parser.parse(file);
```

It does not need a long switch or if-else chain.

## 25.2 Centralized Object Creation

All parser creation logic lives in one place:

```java
DocumentParserFactory
```

If parser construction changes, update the factory.

## 25.3 Easy to Add New File Types

Suppose later you add:

```java
XLSX
```

You would add:

```java
public class XlsxDocumentParser implements DocumentParser {
    @Override
    public ParseResult parse(DocumentFile file) {
        return new ParseResult(
                file.getFileId(),
                file.getFileName(),
                file.getFileType(),
                true,
                "XLSX document parsed successfully",
                1,
                getParserName()
        );
    }

    @Override
    public String getParserName() {
        return this.getClass().getSimpleName();
    }
}
```

Then update the enum:

```java
public enum FileType {
    PDF,
    CSV,
    JSON,
    XML,
    XLSX
}
```

And update the factory:

```java
case XLSX -> new XlsxDocumentParser();
```

The rest of the service code still works with:

```java
DocumentParser
```

---

# 26. Important Rules

## Rule 1: Factory creates objects

The factory should create parsers.

It should not parse files.

Wrong:

```java
public static ParseResult parse(DocumentFile file) {
    if (file.getFileType() == FileType.PDF) {
        System.out.println("Parsing PDF...");
    }
}
```

Correct:

```java
DocumentParser parser =
        DocumentParserFactory.createDocumentParser(file.getFileType());

ParseResult result = parser.parse(file);
```

## Rule 2: Parser parses files

Each parser should contain file-specific parsing behavior.

Examples:

```text
CsvDocumentParser counts CSV rows.
JsonDocumentParser validates JSON-like structure.
XmlDocumentParser validates XML-like structure.
PdfDocumentParser treats PDF as one document.
```

## Rule 3: Service coordinates flow

The service should coordinate:

```text
null check
factory call
parser call
return result
```

It should not directly create concrete parser classes.

---

# 27. Common Mistakes

## Mistake 1: Creating parsers directly in Main or service

Wrong:

```java
DocumentParser parser = new PdfDocumentParser();
```

Correct:

```java
DocumentParser parser =
        DocumentParserFactory.createDocumentParser(file.getFileType());
```

## Mistake 2: Factory returning Object

Wrong:

```java
public static Object createDocumentParser(FileType fileType)
```

Correct:

```java
public static DocumentParser createDocumentParser(FileType fileType)
```

## Mistake 3: Putting parsing logic inside factory

Wrong:

```java
public static ParseResult parse(DocumentFile file)
```

Correct:

```java
public static DocumentParser createDocumentParser(FileType fileType)
```

## Mistake 4: Wrong parse success messages

CSV should return:

```text
CSV document parsed successfully
```

XML should return:

```text
XML document parsed successfully
```

Do not accidentally return:

```text
PDF document parsed successfully
```

from CSV/XML parsers.

## Mistake 5: Incorrect CSV newline input

Wrong:

```java
"id,name,amount\\n1,Amit,500\\n2,Rahul,900"
```

Correct:

```java
"id,name,amount\n1,Amit,500\n2,Rahul,900"
```

## Mistake 6: Throwing NullPointerException for invalid input

Avoid:

```java
throw new NullPointerException("FileType is null");
```

Use:

```java
throw new IllegalArgumentException("FileType cannot be null.");
```

## Mistake 7: Unnecessary default in enum switch

Avoid unnecessary `default` when all enum values are handled.

Better:

```java
return switch (fileType) {
    case PDF -> new PdfDocumentParser();
    case CSV -> new CsvDocumentParser();
    case JSON -> new JsonDocumentParser();
    case XML -> new XmlDocumentParser();
};
```

## Mistake 8: Not checking null in service

Wrong:

```java
FileType fileType = file.getFileType();
```

without checking whether `file` is null.

Correct:

```java
if (file == null) {
    throw new IllegalArgumentException("Document file cannot be null.");
}
```

---

# 28. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `FileType` enum | 1 |
| Correct `DocumentFile` class with validation | 1 |
| Correct `ParseResult` class | 1 |
| Correct `DocumentParser` interface | 1 |
| Correct `PdfDocumentParser` | 1 |
| Correct `CsvDocumentParser` with proper row counting | 1 |
| Correct `JsonDocumentParser` with basic validation | 0.8 |
| Correct `XmlDocumentParser` with basic validation | 0.8 |
| Correct `DocumentParserFactory` | 1.2 |
| Correct `DocumentUploadService` | 1 |
| Main tests all file types | 1 |
| Correct output messages | 0.7 |
| Code readability and naming | 0.7 |

Total: **12.2 marks normalized to 10**

---

# 29. Ideal Final Code Structure

Your package/project can look like this:

```text
factory/module3/
    FileType.java
    DocumentFile.java
    ParseResult.java
    DocumentParser.java
    PdfDocumentParser.java
    CsvDocumentParser.java
    JsonDocumentParser.java
    XmlDocumentParser.java
    DocumentParserFactory.java
    DocumentUploadService.java
    Main.java
```

Optional:

```text
factory/module3/
    DocumentFileValidator.java
```

---

# 30. Difference Between Module 2 and Module 3

Module 2:

```text
PaymentMode -> PaymentProcessor
```

Module 3:

```text
FileType -> DocumentParser -> ParseResult
```

Module 3 is more advanced because it adds:

```text
DocumentFile metadata
constructor validation
structured ParseResult
service layer coordination
file-type-specific parsing behavior
basic JSON/XML validation
CSV row counting
```

This makes it closer to a real backend ingestion pipeline.

---

# 31. Final Learning Goal

By completing Module 3, you should understand:

```text
1. How Factory Pattern applies to document/file processing.
2. How to select an implementation based on FileType.
3. Why parsers should share a common interface.
4. Why factory should create parsers but not parse files.
5. How service classes coordinate factory usage.
6. Why structured results are better than only printing output.
7. How validation improves backend-style design.
8. How parser-specific behavior differs while sharing a common contract.
```

The main mental model is:

```text
DocumentFile comes in.
DocumentUploadService receives it.
DocumentUploadService asks DocumentParserFactory for parser.
DocumentParserFactory creates correct parser.
Parser parses the file.
Parser returns ParseResult.
Main/service uses the result.
```

For this module:

```text
PDF   -> PdfDocumentParser
CSV   -> CsvDocumentParser
JSON  -> JsonDocumentParser
XML   -> XmlDocumentParser
```

That is Factory Pattern with a realistic document-upload backend scenario.
