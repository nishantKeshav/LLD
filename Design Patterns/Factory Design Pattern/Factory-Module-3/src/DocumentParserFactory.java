public class DocumentParserFactory {

    private DocumentParserFactory() {
        // Private Constructor
    }

    public static DocumentParser createDocumentParser(FileType fileType) {
        if (fileType == null) {
            throw new IllegalArgumentException("FileType is null");
        }
        return switch (fileType) {
            case PDF -> new PdfDocumentParser();
            case CSV -> new CsvDocumentParser();
            case XML -> new XmlDocumentParser();
            case JSON -> new JsonDocumentParser();
            default -> throw new IllegalArgumentException("Unknown file type");
        };
    }
}
