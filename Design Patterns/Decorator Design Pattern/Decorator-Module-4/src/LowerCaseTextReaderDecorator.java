public class LowerCaseTextReaderDecorator extends TextReaderDecorator {

    public LowerCaseTextReaderDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        return getWrappedReader().read().toLowerCase();
    }
}
