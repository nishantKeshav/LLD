public class SensitiveWordMaskingDecorator extends TextReaderDecorator {

    private final String[] words = {"password", "secret", "token"};

    public SensitiveWordMaskingDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        String text = getWrappedReader().read();

        for (String word : words) {
            text = text.replaceAll("(?i)" + word, "******");
        }

        return text;
    }
}