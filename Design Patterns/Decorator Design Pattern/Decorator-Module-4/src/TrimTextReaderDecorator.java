public class TrimTextReaderDecorator extends TextReaderDecorator {

    public TrimTextReaderDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        return getWrappedReader().read().trim();
    }
}
