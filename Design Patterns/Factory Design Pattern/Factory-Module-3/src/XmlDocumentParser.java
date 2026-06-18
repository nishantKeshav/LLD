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
        System.out.println("Parsing " + FileType.XML +  " document: " + file.getFileName());
        System.out.println("Content : " + file.getContent());
        System.out.println("File Size : " + file.getSizeInKb());
        System.out.println("Uploaded By : " + file.getUploadedBy());
        System.out.println("Uploaded Date : " + file.getUploadDate());
        int recordsParsed = file.getContent().contains("<") ? 1 : 0;
        return new ParseResult(
                file.getFileId(),
                file.getFileName(),
                file.getFileType(),
                true,
                "XML document parsed successfully",
                recordsParsed,
                getParserName()
        );
    }

    @Override
    public String getParserName() {
        return this.getClass().getSimpleName();
    }
}
