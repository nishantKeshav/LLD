public class Main {
    public static void main(String[] args) {
        System.out.println("Module 4 — File/Text Reader Decorator");

        // EXAMPLE 1: BasicTextReader with TrimTextReaderDecorator
        System.out.println("EXAMPLE 1: Module 4");
        String rawContent = "   HELLO     WORLD   this has PASSWORD and SECRET token   ";
        TextReader reader = new BasicTextReader(rawContent);
        reader = new TrimTextReaderDecorator(reader);
        reader = new RemoveExtraSpacesTextReaderDecorator(reader);
        reader = new LowerCaseTextReaderDecorator(reader);
        reader = new SensitiveWordMaskingDecorator(reader);
        System.out.println("Original: " + rawContent);
        System.out.println("Processed: " + reader.read());

        // EXAMPLE 2: BasicTextReader with TrimTextReaderDecorator
        System.out.println("EXAMPLE 2: Module 4");
        rawContent = "   My    PASSWORD   is   SECRET   and TOKEN is 12345   ";
        reader = new BasicTextReader(rawContent);
        reader = new TrimTextReaderDecorator(reader);
        reader = new RemoveExtraSpacesTextReaderDecorator(reader);
        reader = new LowerCaseTextReaderDecorator(reader);
        reader = new SensitiveWordMaskingDecorator(reader);
        System.out.println("Original: " + rawContent);
        System.out.println("Processed: " + reader.read());
    }
}