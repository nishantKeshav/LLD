
public class CsvDocumentParser implements DocumentParser {

    @Override
    public ParseResult parse(DocumentFile file) {
        System.out.println();
        System.out.println("============================================================================");
        System.out.println("Started File Parsing using " + getParserName());
        System.out.println("Parsing " + FileType.CSV +  " document: " + file.getFileName());
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
