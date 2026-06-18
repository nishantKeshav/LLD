# LLD Design Patterns

This repository is a Java low-level design practice workspace focused on design
patterns. Each pattern is organized into small modules that demonstrate one
concept at a time using simple classes, interfaces, and `Main` examples.

The current workspace covers:

- Strategy Design Pattern
- Observer Design Pattern
- Factory Design Pattern
- Decorator Design Pattern

## Project Structure

```text
Design Patterns/
├── Strategy Design Pattern/
├── Observer Design Pattern/
├── Factory Design Pattern/
└── Decorator Design Pattern/
```

Each pattern folder contains numbered modules. Most modules include:

- `src/` with Java source files
- `Main.java` for running the example
- a problem statement markdown file explaining the exercise
- IntelliJ IDEA `.iml` project files

## Modules

### Strategy Design Pattern

Demonstrates replacing conditional logic with interchangeable strategies.

- Module 1: discount calculation with and without strategy
- Module 2: notification sending with and without strategy
- Module 3: notification strategy lookup by type
- Module 4: payment processing strategies
- Module 5: refund processing strategies with validation

### Observer Design Pattern

Demonstrates subjects, observers, event notification, filtering, retries, and
event-bus style dispatch.

- Module 1: YouTube channel subscribers
- Module 2: multiple subscriber types with subscribe and unsubscribe behavior
- Module 3: product stock notification observers
- Module 4: product events with typed observers
- Module 5: order event dispatcher with multiple observers
- Module 6: advanced event dispatcher with priority, retry, and dead-letter flow
- Module 7: queued event bus with metrics and dispatch results

### Factory Design Pattern

Demonstrates centralizing object creation behind factory classes.

- Module 1: notification sender factory
- Module 2: payment processor factory
- Module 3: document parser factory

### Decorator Design Pattern

Demonstrates adding behavior by wrapping objects instead of changing the base
class.

- Module 1: coffee add-ons
- Module 2: pizza toppings
- Module 3: notification sender decorators
- Module 4: text reader decorators
- Module 5: payment processor decorators
- Module 6: HTTP client decorators
- Module 7: configurable HTTP client decorator chain builder

## How to Run an Example

This repository does not use Maven or Gradle. Open the project in IntelliJ IDEA,
or compile a module directly with `javac`.

Example from PowerShell:

```powershell
cd "Design Patterns\Decorator Design Pattern\Decorator-Module-1\src"
javac *.java
java Main
```

For modules that use package folders, compile from the module `src` directory and
run the package-qualified `Main` class.

## Working Style

The code is intentionally small and example-driven. When adding new modules or
changing existing ones:

- keep each module focused on one design-pattern concept
- match the naming and structure already used in that pattern folder
- update only the files required for the change
- run the relevant `Main` example after changes when possible

## Repository Notes

- `AGENTS.md` and `CLAUDE.md` define the workflow rules for agentic coding in
  this workspace.
- `graphify-out/` contains generated graph analysis for navigating the codebase.
- Problem statement files inside modules describe the expected behavior and
  learning goal for each exercise.
