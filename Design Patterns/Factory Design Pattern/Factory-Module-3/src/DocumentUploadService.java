public class DocumentUploadService {

    public ParseResult parseDocument(DocumentFile file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        FileType fileType = file.getFileType();
        DocumentParser parser = DocumentParserFactory.createDocumentParser(fileType);
        return parser.parse(file);
    }
}
