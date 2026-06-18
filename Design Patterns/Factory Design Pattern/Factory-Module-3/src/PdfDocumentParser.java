public class PdfDocumentParser implements DocumentParser {

    @Override
    public String getParserName() {
        return PdfDocumentParser.class.getSimpleName();
    }

    @Override
    public ParseResult parse(DocumentFile file) {
        System.out.println();
        System.out.println("============================================================================");
        System.out.println("Started File Parsing using " + getParserName());
        System.out.println("Parsing " + FileType.PDF +  " document: " + file.getFileName());
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
}
