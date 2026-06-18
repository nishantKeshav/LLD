public interface DocumentParser {
    ParseResult parse(DocumentFile file);
    String getParserName();
}