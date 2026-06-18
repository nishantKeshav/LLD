public abstract class TextReaderDecorator implements TextReader {

    private final TextReader wrappedReader;

    protected TextReaderDecorator(TextReader wrappedReader) {
        if (wrappedReader == null) {
            throw new IllegalArgumentException("TextReader cannot be null");
        }
        this.wrappedReader = wrappedReader;
    }

    protected TextReader getWrappedReader() {
        return wrappedReader;
    }

    @Override
    public String read() {
        return wrappedReader.read();
    }

}
