import java.time.LocalDateTime;

public class DocumentFile {
    
    private final String fileId;
    private final String fileName;
    private final FileType fileType;
    private final String content;
    private final int sizeInKb;
    private final String uploadedBy;
    private final LocalDateTime uploadDate;

    public DocumentFile(String fileId, String fileName, FileType fileType, String content, int sizeInKb, String uploadedBy) {
        if (fileId == null || fileId.isBlank()) {
            throw new IllegalArgumentException("fileId is null or empty");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is null or empty");
        }
        if (fileType == null) {
            throw new IllegalArgumentException("fileType is null");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is null or empty");
        }
        if (sizeInKb <= 0) {
            throw new IllegalArgumentException("sizeInKb can not be equal or less than 0");
        }
        if (uploadedBy == null || uploadedBy.isBlank()) {
            throw new IllegalArgumentException("uploadedBy is null or empty");
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
