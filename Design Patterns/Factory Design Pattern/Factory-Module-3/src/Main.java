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
                "id,name,amount\\n1,Amit,500\\n2,Rahul,900",
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