# Decorator Design Pattern Practice — Module 4

## File/Text Reader Decorator

### Difficulty Level

**Intermediate+**

This is the fourth practice module for the **Decorator Design Pattern**.

In the previous modules, you practiced decorators using:

```text
Module 1: Coffee add-ons
Module 2: Pizza toppings
Module 3: Notification sender with logging, encryption, timestamp, and metrics
```

Module 4 moves closer to a classic real-world use case of Decorator Pattern: **file/text processing pipelines**.

In this module, you will build a text reader system where the base reader returns raw text, and decorators transform that text step by step.

This is similar to how Java I/O works internally with classes like:

```java
new BufferedInputStream(new FileInputStream("data.txt"));
```

Each wrapper adds extra behavior around the original reader.

---

## 1. Problem Statement

You are building a simple text reader system.

The base reader should return raw text exactly as it was given.

Then different decorators should transform the text:

```text
Trim leading and trailing spaces
Remove extra spaces between words
Convert text to lowercase
Mask sensitive words
```

The goal is to build a flexible processing pipeline using the **Decorator Design Pattern**.

You should be able to start with:

```java
TextReader reader = new BasicTextReader(rawContent);
```

Then wrap it:

```java
reader = new TrimTextReaderDecorator(reader);
reader = new RemoveExtraSpacesTextReaderDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
reader = new SensitiveWordMaskingDecorator(reader);
```

Finally:

```java
reader.read();
```

should return the fully processed text.

---

## 2. Example Requirement

Raw input:

```text
"   HELLO     WORLD   this has PASSWORD and SECRET token   "
```

Expected processed output:

```text
"hello world this has ****** and ****** ******"
```

The transformations should happen like this:

```text
1. Trim leading/trailing spaces
2. Remove extra spaces between words
3. Convert to lowercase
4. Mask sensitive words
```

---

## 3. Why This Problem Needs Decorator Pattern

Without Decorator Pattern, you might create many different reader classes:

```java
TrimmedTextReader
LowerCaseTextReader
TrimmedLowerCaseTextReader
MaskedLowerCaseTextReader
TrimmedMaskedLowerCaseTextReader
TrimmedMaskedLowerCaseExtraSpaceRemovedTextReader
```

This becomes messy quickly.

If tomorrow you add more transformations like:

```text
Remove punctuation
Capitalize words
Replace emojis
Normalize line breaks
Mask email addresses
Mask phone numbers
```

then class combinations will explode.

Decorator Pattern solves this by allowing you to build a transformation chain dynamically.

Instead of creating a class for every possible combination, you create small single-purpose decorators:

```text
Trim decorator
Lowercase decorator
Extra-space remover decorator
Sensitive-word masking decorator
```

Then combine them as needed.

---

## 4. Real-Life Analogy

Think of cleaning text before storing it or showing it to users.

Raw text may be messy:

```text
"   My    PASSWORD   is   SECRET   and TOKEN is 12345   "
```

Before using it, you may want to:

```text
Remove unnecessary spaces
Normalize case
Hide sensitive information
```

Each cleaning step is separate.

Decorator Pattern lets you apply those steps one by one, like layers.

---

## 5. Decorator Pattern Mapping

| Decorator Pattern Term | Meaning in This Module |
|---|---|
| Component | `TextReader` interface |
| Concrete Component | `BasicTextReader` |
| Base Decorator | `TextReaderDecorator` |
| Concrete Decorators | `TrimTextReaderDecorator`, `LowerCaseTextReaderDecorator`, `RemoveExtraSpacesTextReaderDecorator`, `SensitiveWordMaskingDecorator` |
| Wrapped Object | The `TextReader` object inside each decorator |
| Operation | `read()` |

---

## 6. Key Difference from Module 3

### Module 3

In Module 3, decorators wrapped a sender and modified behavior around:

```java
send(String message)
```

Some decorators performed before/after behavior, such as logging and metrics.

### Module 4

In Module 4, decorators form a **text transformation pipeline**.

Each decorator takes the output from the previous reader and transforms it.

Example:

```text
Raw text
   ↓
Trim
   ↓
Remove extra spaces
   ↓
Lowercase
   ↓
Mask sensitive words
   ↓
Final text
```

This makes Module 4 more focused on transformation order.

---

## 7. Required Classes

You need to create:

```text
TextReader
BasicTextReader
TextReaderDecorator
TrimTextReaderDecorator
LowerCaseTextReaderDecorator
RemoveExtraSpacesTextReaderDecorator
SensitiveWordMaskingDecorator
Main
```

---

## 8. Class 1 — `TextReader` Interface

Create:

```java
public interface TextReader {
    String read();
}
```

### Purpose

This is the **Component interface**.

Both the base reader and all decorators must implement this interface.

Because every reader and decorator is a `TextReader`, you can keep writing:

```java
TextReader reader = new BasicTextReader(rawContent);
reader = new TrimTextReaderDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
```

The variable type remains:

```java
TextReader
```

But the behavior changes as more decorators wrap it.

---

## 9. Class 2 — `BasicTextReader`

Create:

```java
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
```

### Purpose

This is the **Concrete Component**.

It returns the original raw content.

It should not know anything about:

```text
Trimming
Lowercase conversion
Extra-space removal
Sensitive-word masking
```

This keeps the base component simple.

---

## 10. Class 3 — `TextReaderDecorator`

Create:

```java
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
```

### Purpose

This is the **Base Decorator**.

It wraps another `TextReader`.

The important field is:

```java
private final TextReader wrappedReader;
```

This means every decorator contains another reader inside it.

Example:

```java
new TrimTextReaderDecorator(new BasicTextReader(rawContent))
```

Means:

```text
TrimTextReaderDecorator wraps BasicTextReader
```

### Why use `private final` with a protected getter?

You can also write:

```java
protected final TextReader wrappedReader;
```

That is acceptable.

But this module uses:

```java
private final TextReader wrappedReader;

protected TextReader getWrappedReader() {
    return wrappedReader;
}
```

This is slightly more encapsulated.

It keeps the field private while still allowing child decorators to access the wrapped reader through a controlled method.

---

## 11. Class 4 — `TrimTextReaderDecorator`

Create:

```java
public class TrimTextReaderDecorator extends TextReaderDecorator {

    public TrimTextReaderDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        return getWrappedReader().read().trim();
    }
}
```

### Purpose

This decorator removes leading and trailing spaces.

Example:

```text
"   hello world   "
```

becomes:

```text
"hello world"
```

Important:

```java
getWrappedReader().read().trim()
```

This means:

```text
First get text from wrapped reader,
then trim it.
```

---

## 12. Class 5 — `RemoveExtraSpacesTextReaderDecorator`

Create:

```java
public class RemoveExtraSpacesTextReaderDecorator extends TextReaderDecorator {

    public RemoveExtraSpacesTextReaderDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        return getWrappedReader().read().replaceAll("\\s+", " ");
    }
}
```

### Purpose

This decorator replaces multiple spaces or whitespace characters with a single space.

Example:

```text
"hello     world     from     java"
```

becomes:

```text
"hello world from java"
```

### Explanation of Regex

```java
"\\s+"
```

means:

```text
one or more whitespace characters
```

So:

```java
replaceAll("\\s+", " ")
```

means:

```text
replace one or more whitespace characters with a single normal space
```

---

## 13. Class 6 — `LowerCaseTextReaderDecorator`

Create:

```java
public class LowerCaseTextReaderDecorator extends TextReaderDecorator {

    public LowerCaseTextReaderDecorator(TextReader wrappedReader) {
        super(wrappedReader);
    }

    @Override
    public String read() {
        return getWrappedReader().read().toLowerCase();
    }
}
```

### Purpose

This decorator converts the text to lowercase.

Example:

```text
"HELLO WORLD"
```

becomes:

```text
"hello world"
```

---

## 14. Class 7 — `SensitiveWordMaskingDecorator`

Create:

```java
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
```

### Purpose

This decorator masks sensitive words.

Sensitive words:

```text
password
secret
token
```

Example:

```text
"my password is secret and token is abc"
```

becomes:

```text
"my ****** is ****** and ****** is abc"
```

### Why use `(?i)`?

`(?i)` means case-insensitive.

So this will match:

```text
password
PASSWORD
Password
PaSsWoRd
```

This makes the masking decorator safer.

---

## 15. Class 8 — `Main`

Create:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Module 4 — File/Text Reader Decorator");

        String rawContent = "   HELLO     WORLD   this has PASSWORD and SECRET token   ";

        TextReader reader = new BasicTextReader(rawContent);

        reader = new TrimTextReaderDecorator(reader);
        reader = new RemoveExtraSpacesTextReaderDecorator(reader);
        reader = new LowerCaseTextReaderDecorator(reader);
        reader = new SensitiveWordMaskingDecorator(reader);

        System.out.println("Original: " + rawContent);
        System.out.println("Processed: " + reader.read());
    }
}
```

Expected output:

```text
Module 4 — File/Text Reader Decorator
Original:    HELLO     WORLD   this has PASSWORD and SECRET token   
Processed: hello world this has ****** and ****** ******
```

---

## 16. Required Second Test Case

Also test:

```java
String rawContent = "   My    PASSWORD   is   SECRET   and TOKEN is 12345   ";
```

Use the same decorator chain:

```java
TextReader reader = new BasicTextReader(rawContent);
reader = new TrimTextReaderDecorator(reader);
reader = new RemoveExtraSpacesTextReaderDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
reader = new SensitiveWordMaskingDecorator(reader);
```

Expected output:

```text
my ****** is ****** and ****** is 12345
```

---

## 17. What Actually Happens Internally

This code:

```java
TextReader reader = new BasicTextReader(rawContent);

reader = new TrimTextReaderDecorator(reader);
reader = new RemoveExtraSpacesTextReaderDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
reader = new SensitiveWordMaskingDecorator(reader);
```

creates this structure:

```text
SensitiveWordMaskingDecorator
    wraps LowerCaseTextReaderDecorator
        wraps RemoveExtraSpacesTextReaderDecorator
            wraps TrimTextReaderDecorator
                wraps BasicTextReader
```

When you call:

```java
reader.read();
```

execution enters the outermost decorator first:

```text
SensitiveWordMaskingDecorator.read()
```

Then it asks its wrapped reader for text:

```text
LowerCaseTextReaderDecorator.read()
```

That asks its wrapped reader:

```text
RemoveExtraSpacesTextReaderDecorator.read()
```

That asks:

```text
TrimTextReaderDecorator.read()
```

That asks:

```text
BasicTextReader.read()
```

Then the result flows back outward with transformations applied.

---

## 18. Step-by-Step Transformation Example

Raw text:

```text
"   HELLO     WORLD   this has PASSWORD and SECRET token   "
```

### Step 1: Trim

```text
"HELLO     WORLD   this has PASSWORD and SECRET token"
```

### Step 2: Remove Extra Spaces

```text
"HELLO WORLD this has PASSWORD and SECRET token"
```

### Step 3: Lowercase

```text
"hello world this has password and secret token"
```

### Step 4: Mask Sensitive Words

```text
"hello world this has ****** and ****** ******"
```

---

## 19. Important Learning: Order Matters

Decorator order matters.

This order:

```java
reader = new TrimTextReaderDecorator(reader);
reader = new RemoveExtraSpacesTextReaderDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
reader = new SensitiveWordMaskingDecorator(reader);
```

is different from:

```java
reader = new SensitiveWordMaskingDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
reader = new RemoveExtraSpacesTextReaderDecorator(reader);
reader = new TrimTextReaderDecorator(reader);
```

If masking is case-sensitive, then masking before lowercase may fail for words like:

```text
PASSWORD
SECRET
TOKEN
```

That is why the recommended masking implementation uses:

```java
"(?i)"
```

for case-insensitive matching.

Still, decorator order remains important because each wrapper transforms the output of the previous wrapper.

---

## 20. Important Rules

### Rule 1: Do Not Call `this.read()` Inside a Decorator

Bad:

```java
@Override
public String read() {
    return this.read().trim();
}
```

This causes infinite recursion.

Correct:

```java
@Override
public String read() {
    return getWrappedReader().read().trim();
}
```

### Rule 2: Each Decorator Should Do One Thing

Good:

```text
TrimTextReaderDecorator only trims
LowerCaseTextReaderDecorator only lowercases
RemoveExtraSpacesTextReaderDecorator only removes extra spaces
SensitiveWordMaskingDecorator only masks sensitive words
```

Avoid putting every transformation into one class.

### Rule 3: Do Not Modify `BasicTextReader`

`BasicTextReader` should only return raw content.

All transformations should happen in decorators.

### Rule 4: Do Not Hardcode Final Output

Bad:

```java
public String read() {
    return "hello world this has ****** and ****** ******";
}
```

Correct:

```java
public String read() {
    return getWrappedReader().read().replaceAll("(?i)password", "******");
}
```

### Rule 5: Use Public Constructors for Concrete Decorators

Preferred:

```java
public TrimTextReaderDecorator(TextReader wrappedReader) {
    super(wrappedReader);
}
```

Avoid protected constructors in concrete decorators unless you have a specific reason.

---

## 21. Common Mistakes to Avoid

### Mistake 1: Infinite Recursion

Problem:

```java
return this.read().trim();
```

Why it is wrong:

```text
TrimTextReaderDecorator.read()
    calls TrimTextReaderDecorator.read()
        calls TrimTextReaderDecorator.read()
            calls TrimTextReaderDecorator.read()
                ...
```

This ends in:

```text
StackOverflowError
```

### Mistake 2: Forgetting to Delegate

Bad:

```java
@Override
public String read() {
    return "trimmed text";
}
```

Correct:

```java
@Override
public String read() {
    return getWrappedReader().read().trim();
}
```

### Mistake 3: Case-Sensitive Masking Only

Less robust:

```java
text.replaceAll("password", "******")
```

Better:

```java
text.replaceAll("(?i)password", "******")
```

### Mistake 4: Combining Too Many Responsibilities

Bad:

```java
public class CleanTextReaderDecorator extends TextReaderDecorator {
    public String read() {
        return getWrappedReader().read()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("(?i)password", "******");
    }
}
```

This works but does not practice Decorator Pattern properly.

Each decorator should have one responsibility.

---

## 22. What This Module Tests

This module checks whether you understand:

```text
Decorator Pattern for transformation pipelines
Component interface
Concrete component
Base decorator
Concrete decorators
Delegation to wrapped object
Order-sensitive transformation
Regex usage for whitespace cleanup
Case-insensitive masking
Single Responsibility Principle
Open/Closed Principle
```

---

## 23. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `TextReader` interface | 1 |
| Correct `BasicTextReader` | 1 |
| Correct `TextReaderDecorator` | 1.5 |
| Correct trim decorator | 1 |
| Correct lowercase decorator | 1 |
| Correct extra-space remover decorator | 1.5 |
| Correct sensitive-word masking decorator | 1.5 |
| Correct wrapping in `Main` | 1 |
| Code readability | 0.5 |

Total: **10 marks**

---

## 24. Key Learning

The key learning of Module 4 is:

```text
Decorators can be used as a processing pipeline.
```

Each decorator receives output from the wrapped reader, transforms it, and returns the result.

The chain:

```java
reader = new TrimTextReaderDecorator(reader);
reader = new RemoveExtraSpacesTextReaderDecorator(reader);
reader = new LowerCaseTextReaderDecorator(reader);
reader = new SensitiveWordMaskingDecorator(reader);
```

means:

```text
SensitiveWordMaskingDecorator(
    LowerCaseTextReaderDecorator(
        RemoveExtraSpacesTextReaderDecorator(
            TrimTextReaderDecorator(
                BasicTextReader
            )
        )
    )
)
```

Each layer adds one transformation.

---

## 25. Final Mental Model

Remember:

```text
Decorator Pattern = same interface + wrapped object + extra behavior
```

For this module:

```text
TextReader = common interface
BasicTextReader = raw content provider
TextReaderDecorator = base wrapper
Concrete decorators = text transformation steps
```

In simple words:

> Module 4 teaches you how to use Decorator Pattern to build flexible text processing pipelines without modifying the original text reader.
