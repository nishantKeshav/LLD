public class RemoveExtraSpacesTextReaderDecorator extends TextReaderDecorator {

    public RemoveExtraSpacesTextReaderDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        return super.read().replaceAll("\\s+", " ");
    }
}
