# Decorator Design Pattern Practice — Module 1

## Coffee Add-ons Decorator

### Difficulty Level

**Beginner**

This is the first practice module for the **Decorator Design Pattern**.

The goal of this module is to understand the most basic structure of the Decorator Pattern using a simple and beginner-friendly example: a coffee ordering system.

You will build a system where a customer starts with a simple coffee and can add extra add-ons such as:

```text
Milk
Sugar
Caramel
```

Each add-on should increase the total cost and update the coffee description.

---

## 1. Problem Statement

You are building a simple coffee ordering system.

A customer can order:

```text
Simple Coffee
```

Then the customer can add optional add-ons:

```text
Milk
Sugar
Caramel
```

Each add-on should do two things:

```text
1. Add its name to the coffee description
2. Add its price to the total coffee cost
```

Example:

```text
Simple Coffee = 50.0
Milk          = 10.0
Sugar         = 5.0
Caramel       = 20.0
```

If the customer selects all three add-ons, the final result should be:

```text
Description: Simple Coffee, Milk, Sugar, Caramel
Total Cost: 85.0
```

You must solve this using the **Decorator Design Pattern**.

---

## 2. Why This Problem Needs Decorator Pattern

Suppose you try to solve this using only inheritance.

You may create classes like:

```java
SimpleCoffee
MilkCoffee
SugarCoffee
CaramelCoffee
MilkSugarCoffee
MilkCaramelCoffee
SugarCaramelCoffee
MilkSugarCaramelCoffee
```

This becomes messy very quickly.

If tomorrow you add more add-ons like:

```text
Chocolate
Whipped Cream
Vanilla
Hazelnut
```

then the number of possible class combinations will increase a lot.

This problem is called **class explosion**.

Decorator Pattern solves this by allowing us to start with a base object and wrap it with extra behavior.

Instead of creating a class for every combination, we compose the coffee at runtime.

Example:

```java
Coffee coffee = new SimpleCoffee();

coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new CaramelDecorator(coffee);
```

This creates:

```text
CaramelDecorator(
    SugarDecorator(
        MilkDecorator(
            SimpleCoffee
        )
    )
)
```

Each decorator adds its own behavior and delegates the rest to the object it wraps.

---

## 3. Real-Life Analogy

Think of ordering coffee at a cafe.

You first choose the base drink:

```text
Simple Coffee
```

Then you ask the cafe to add:

```text
Milk
Sugar
Caramel
```

The base coffee does not change its original class.

The add-ons are layered on top.

Each layer adds something extra:

```text
Milk adds taste and cost
Sugar adds sweetness and cost
Caramel adds flavor and cost
```

That is exactly how Decorator Pattern works.

---

## 4. Decorator Pattern Mapping

| Decorator Pattern Term | Meaning in This Module |
|---|---|
| Component | `Coffee` interface |
| Concrete Component | `SimpleCoffee` |
| Base Decorator | `CoffeeDecorator` |
| Concrete Decorators | `MilkDecorator`, `SugarDecorator`, `CaramelDecorator` |
| Wrapped Object | The `Coffee` object inside each decorator |
| Operation | `getDescription()` and `getCost()` |

---

## 5. Core Design Idea

The important rule is:

> Decorators must implement the same interface as the original object.

In this module:

```java
SimpleCoffee implements Coffee
MilkDecorator extends CoffeeDecorator
SugarDecorator extends CoffeeDecorator
CaramelDecorator extends CoffeeDecorator
CoffeeDecorator implements Coffee
```

Because all of them are `Coffee`, you can write:

```java
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new CaramelDecorator(coffee);
```

The variable type remains:

```java
Coffee
```

But the actual object keeps getting wrapped.

---

## 6. Required Classes

You need to create the following classes and interface:

```text
Coffee
SimpleCoffee
CoffeeDecorator
MilkDecorator
SugarDecorator
CaramelDecorator
Main
```

---

## 7. Class 1 — `Coffee` Interface

Create:

```java
public interface Coffee {
    String getDescription();
    double getCost();
}
```

### Purpose

This is the **Component interface**.

It defines the common behavior for both:

```text
1. The base coffee
2. All coffee decorators
```

Every coffee-like object must be able to tell:

```text
What is the description?
What is the cost?
```

So all classes in this module should follow this contract.

---

## 8. Class 2 — `SimpleCoffee`

Create:

```java
public class SimpleCoffee implements Coffee {

    @Override
    public String getDescription() {
        return "Simple Coffee";
    }

    @Override
    public double getCost() {
        return 50.0;
    }
}
```

### Purpose

This is the **Concrete Component**.

It is the original object before any decorators are applied.

Expected behavior:

```text
Description: Simple Coffee
Cost: 50.0
```

This class should not know anything about milk, sugar, or caramel.

That is important.

The base coffee should stay simple.

---

## 9. Class 3 — `CoffeeDecorator`

Create:

```java
public abstract class CoffeeDecorator implements Coffee {

    protected final Coffee coffee;

    protected CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }

    @Override
    public double getCost() {
        return coffee.getCost();
    }
}
```

### Purpose

This is the **Base Decorator**.

It also implements `Coffee`.

It stores another `Coffee` object inside it:

```java
protected final Coffee coffee;
```

This means every decorator wraps another coffee object.

Example:

```java
new MilkDecorator(new SimpleCoffee())
```

Here:

```text
MilkDecorator wraps SimpleCoffee
```

### Why Do We Need `CoffeeDecorator`?

Technically, each concrete decorator can directly implement `Coffee`.

Example:

```java
public class MilkDecorator implements Coffee {
    private final Coffee coffee;
}
```

That works.

But then every decorator repeats the same wrapper code:

```java
private final Coffee coffee;

public SomeDecorator(Coffee coffee) {
    this.coffee = coffee;
}
```

The abstract `CoffeeDecorator` avoids this duplication.

It centralizes the common wrapper logic.

### Why `protected`, Not `public`?

Use:

```java
protected final Coffee coffee;
```

not:

```java
public final Coffee coffee;
```

Because only child decorators need direct access to the wrapped coffee.

Outside classes should not directly access it.

---

## 10. Class 4 — `MilkDecorator`

Create:

```java
public class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 10.0;
    }
}
```

### Purpose

This decorator adds milk to the coffee.

It does not replace the original coffee behavior.

It extends it.

### Description Behavior

```java
coffee.getDescription() + ", Milk"
```

If the wrapped coffee description is:

```text
Simple Coffee
```

then after milk:

```text
Simple Coffee, Milk
```

### Cost Behavior

```java
coffee.getCost() + 10.0
```

If the wrapped coffee cost is:

```text
50.0
```

then after milk:

```text
60.0
```

---

## 11. Class 5 — `SugarDecorator`

Create:

```java
public class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 5.0;
    }
}
```

### Purpose

This decorator adds sugar.

Expected addition:

```text
Description adds: Sugar
Cost adds: 5.0
```

---

## 12. Class 6 — `CaramelDecorator`

Create:

```java
public class CaramelDecorator extends CoffeeDecorator {

    public CaramelDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Caramel";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 20.0;
    }
}
```

### Purpose

This decorator adds caramel.

Expected addition:

```text
Description adds: Caramel
Cost adds: 20.0
```

---

## 13. Class 7 — `Main`

Create:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Module 1 — Coffee Add-ons (Decorator Design Pattern)");

        Coffee coffee = new SimpleCoffee();

        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        coffee = new CaramelDecorator(coffee);

        System.out.println("Description: " + coffee.getDescription());
        System.out.println("Total Cost: " + coffee.getCost());
    }
}
```

### Purpose

This class tests the decorator chain.

The important part is this:

```java
Coffee coffee = new SimpleCoffee();

coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new CaramelDecorator(coffee);
```

Each line wraps the previous object.

---

## 14. What Actually Happens Internally

This code:

```java
Coffee coffee = new SimpleCoffee();

coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new CaramelDecorator(coffee);
```

creates this object structure:

```text
CaramelDecorator
    wraps SugarDecorator
        wraps MilkDecorator
            wraps SimpleCoffee
```

So when you call:

```java
coffee.getDescription();
```

it executes like this:

```text
CaramelDecorator.getDescription()
    -> SugarDecorator.getDescription()
        -> MilkDecorator.getDescription()
            -> SimpleCoffee.getDescription()
```

Result:

```text
Simple Coffee, Milk, Sugar, Caramel
```

When you call:

```java
coffee.getCost();
```

it executes like this:

```text
CaramelDecorator adds 20
    SugarDecorator adds 5
        MilkDecorator adds 10
            SimpleCoffee returns 50
```

Final total:

```text
50 + 10 + 5 + 20 = 85
```

---

## 15. Expected Output

```text
Module 1 — Coffee Add-ons (Decorator Design Pattern)
Description: Simple Coffee, Milk, Sugar, Caramel
Total Cost: 85.0
```

---

## 16. Expected Cost Calculation

| Item | Cost |
|---|---:|
| Simple Coffee | 50.0 |
| Milk | 10.0 |
| Sugar | 5.0 |
| Caramel | 20.0 |
| Total | 85.0 |

---

## 17. Important Rules

### Rule 1: Do Not Hardcode the Final Total

Bad:

```java
public double getCost() {
    return 85.0;
}
```

Correct:

```java
public double getCost() {
    return coffee.getCost() + 20.0;
}
```

Each decorator should only add its own cost.

---

### Rule 2: Do Not Create Combination Classes

Avoid:

```java
MilkSugarCoffee
MilkSugarCaramelCoffee
SugarCaramelCoffee
```

These classes defeat the purpose of Decorator Pattern.

---

### Rule 3: Each Decorator Should Wrap a `Coffee`

Every decorator constructor should accept:

```java
Coffee coffee
```

Example:

```java
public MilkDecorator(Coffee coffee) {
    super(coffee);
}
```

---

### Rule 4: Each Decorator Should Extend `CoffeeDecorator`

Preferred:

```java
public class MilkDecorator extends CoffeeDecorator
```

Less clean:

```java
public class MilkDecorator implements Coffee
```

Directly implementing `Coffee` can work, but using the base decorator class avoids repeated code.

---

### Rule 5: Description Formatting Should Be Clean

Good:

```text
Simple Coffee, Milk, Sugar, Caramel
```

Avoid:

```text
Simple Coffee Description adds: Milk Description adds: Sugar
```

---

## 18. Common Mistakes to Avoid

### Mistake 1: Forgetting to Delegate

Bad:

```java
@Override
public double getCost() {
    return 10.0;
}
```

This returns only milk cost.

Correct:

```java
@Override
public double getCost() {
    return coffee.getCost() + 10.0;
}
```

---

### Mistake 2: Wrong Description in Decorator

Bad in `SugarDecorator`:

```java
return coffee.getDescription() + ", Caramel";
```

Correct:

```java
return coffee.getDescription() + ", Sugar";
```

---

### Mistake 3: Public Wrapped Field

Bad:

```java
public final Coffee coffee;
```

Better:

```java
protected final Coffee coffee;
```

---

### Mistake 4: Not Using the Base Decorator

Less clean:

```java
public class MilkDecorator implements Coffee
```

Preferred:

```java
public class MilkDecorator extends CoffeeDecorator
```

---

## 19. What This Module Tests

This module tests whether you understand:

```text
Component interface
Concrete component
Base decorator
Concrete decorators
Wrapping objects
Delegating to wrapped object
Adding extra behavior
Avoiding class explosion
Runtime composition
Open/Closed Principle
```

---

## 20. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `Coffee` interface | 1 |
| Correct `SimpleCoffee` concrete component | 1 |
| Correct `CoffeeDecorator` base decorator | 2 |
| Correct `MilkDecorator` | 1.5 |
| Correct `SugarDecorator` | 1.5 |
| Correct `CaramelDecorator` | 1.5 |
| Correct wrapping in `Main` | 1 |
| Code readability | 0.5 |

Total: **10 marks**

---

## 21. Key Learning

The key learning of Module 1 is:

```text
Decorator Pattern lets you add behavior to an object by wrapping it.
```

The base coffee is:

```java
new SimpleCoffee()
```

The decorators are:

```java
new MilkDecorator(coffee)
new SugarDecorator(coffee)
new CaramelDecorator(coffee)
```

Each decorator:

```text
1. Has the same interface as the original object
2. Stores a reference to the wrapped object
3. Calls the wrapped object
4. Adds its own behavior
```

The final object behaves like one coffee, but internally it is a chain of wrappers.

---

## 22. Final Mental Model

Remember this structure:

```text
Coffee coffee = new SimpleCoffee();

coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new CaramelDecorator(coffee);
```

Means:

```text
CaramelDecorator(
    SugarDecorator(
        MilkDecorator(
            SimpleCoffee
        )
    )
)
```

Decorator Pattern is basically:

```text
Same interface + wrapped object + extra behavior
```

In simple words:

> Decorator Pattern allows you to add features layer by layer without modifying the original class.
