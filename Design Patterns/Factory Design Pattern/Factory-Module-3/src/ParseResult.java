public class ParseResult {

    private final String fileId;
    private final String fileName;
    private final FileType fileType;
    private final boolean success;
    private final String message;
    private final int recordsParsed;
    private final String parserUsed;

    public ParseResult(String fileId, String fileName, FileType fileType, boolean success,
                       String message, int recordsParsed, String parserUsed) {
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
