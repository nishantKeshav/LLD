public class BasicTextReader implements TextReader {

    private final String content;

    public BasicTextReader(String content) {
        this.content = content;
    }

    @Override
    public String read() {
        return content;
    }
}
