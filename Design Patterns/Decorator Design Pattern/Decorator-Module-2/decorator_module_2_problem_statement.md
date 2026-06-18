# Decorator Design Pattern Practice — Module 2

## Pizza Toppings Decorator

### Difficulty Level

**Beginner+**

This is the second practice module for the **Decorator Design Pattern**.

In Module 1, you practiced the Decorator Pattern using a simple coffee example:

```text
Simple Coffee
+ Milk
+ Sugar
+ Caramel
```

Module 2 increases the difficulty slightly.

Instead of only one base item, you will now work with **multiple base pizzas** and **multiple topping decorators**.

The goal is to understand that decorators can wrap different base objects as long as all of them follow the same interface.

---

## 1. Problem Statement

You are building a simple pizza ordering system.

A customer can choose a base pizza:

```text
Margherita Pizza
Farmhouse Pizza
```

Then the customer can add toppings dynamically:

```text
Cheese
Olives
Mushroom
Extra Paneer
```

Each topping should do two things:

```text
1. Add its name to the pizza description
2. Add its price to the total pizza cost
```

You must solve this using the **Decorator Design Pattern**.

---

## 2. Example Requirement

A customer orders:

```text
Margherita Pizza
+ Cheese
+ Olives
+ Mushroom
```

Base and topping prices:

```text
Margherita Pizza = 150.0
Cheese           = 40.0
Olives           = 30.0
Mushroom         = 35.0
```

Final result:

```text
Description: Margherita Pizza, Cheese, Olives, Mushroom
Total Cost: 255.0
```

Calculation:

```text
150.0 + 40.0 + 30.0 + 35.0 = 255.0
```

---

## 3. Why This Problem Needs Decorator Pattern

Without Decorator Pattern, you might create many classes like:

```java
CheesePizza
OlivesPizza
MushroomPizza
CheeseOlivesPizza
CheeseMushroomPizza
OlivesMushroomPizza
CheeseOlivesMushroomPizza
FarmhouseCheesePizza
FarmhousePaneerMushroomPizza
```

This becomes messy very quickly.

If you add more toppings tomorrow, the number of possible combinations grows even more.

This problem is called **class explosion**.

Decorator Pattern helps by letting you compose the pizza dynamically at runtime.

Instead of creating one class for every possible combination, you create:

```text
Base pizzas
+
Reusable topping decorators
```

Then combine them like this:

```java
Pizza pizza = new MargheritaPizza();

pizza = new CheeseDecorator(pizza);
pizza = new OlivesDecorator(pizza);
pizza = new MushroomDecorator(pizza);
```

This creates:

```text
MushroomDecorator(
    OlivesDecorator(
        CheeseDecorator(
            MargheritaPizza
        )
    )
)
```

Each decorator adds its own topping and delegates the rest to the pizza it wraps.

---

## 4. Real-Life Analogy

Think of ordering pizza at a restaurant.

First, you choose the base pizza:

```text
Margherita Pizza
```

Then you add toppings:

```text
Cheese
Olives
Mushroom
Extra Paneer
```

The restaurant does not create a brand-new menu item class for every topping combination.

Instead, it starts with the base pizza and adds toppings layer by layer.

That is how the Decorator Pattern works.

---

## 5. Decorator Pattern Mapping

| Decorator Pattern Term | Meaning in This Module |
|---|---|
| Component | `Pizza` interface |
| Concrete Components | `MargheritaPizza`, `FarmhousePizza` |
| Base Decorator | `PizzaDecorator` |
| Concrete Decorators | `CheeseDecorator`, `OlivesDecorator`, `MushroomDecorator`, `ExtraPaneerDecorator` |
| Wrapped Object | The `Pizza` object inside each decorator |
| Operations | `getDescription()` and `getCost()` |

---

## 6. Key Difference from Module 1

### Module 1

Module 1 had only one base component:

```text
SimpleCoffee
```

### Module 2

Module 2 has multiple base components:

```text
MargheritaPizza
FarmhousePizza
```

This teaches you that decorators are not limited to wrapping only one specific class.

They can wrap any object that implements the same interface.

For this module, every base pizza and every decorator should implement or extend something connected to:

```java
Pizza
```

---

## 7. Required Classes

You need to create:

```text
Pizza
MargheritaPizza
FarmhousePizza
PizzaDecorator
CheeseDecorator
OlivesDecorator
MushroomDecorator
ExtraPaneerDecorator
Main
```

---

## 8. Class 1 — `Pizza` Interface

Create:

```java
public interface Pizza {
    String getDescription();
    double getCost();
}
```

### Purpose

This is the **Component interface**.

It defines the common behavior for both:

```text
1. Base pizzas
2. Topping decorators
```

Every pizza-like object must be able to provide:

```text
Description
Cost
```

This is what allows decorators to wrap both `MargheritaPizza` and `FarmhousePizza`.

---

## 9. Class 2 — `MargheritaPizza`

Create:

```java
public class MargheritaPizza implements Pizza {

    @Override
    public String getDescription() {
        return "Margherita Pizza";
    }

    @Override
    public double getCost() {
        return 150.0;
    }
}
```

### Purpose

This is one concrete base pizza.

Expected behavior:

```text
Description: Margherita Pizza
Cost: 150.0
```

This class should not know anything about toppings.

It should only represent the base Margherita pizza.

---

## 10. Class 3 — `FarmhousePizza`

Create:

```java
public class FarmhousePizza implements Pizza {

    @Override
    public String getDescription() {
        return "Farmhouse Pizza";
    }

    @Override
    public double getCost() {
        return 220.0;
    }
}
```

### Purpose

This is another concrete base pizza.

Expected behavior:

```text
Description: Farmhouse Pizza
Cost: 220.0
```

This class should also not know anything about toppings.

---

## 11. Class 4 — `PizzaDecorator`

Create:

```java
public abstract class PizzaDecorator implements Pizza {

    protected final Pizza pizza;

    protected PizzaDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public double getCost() {
        return pizza.getCost();
    }

    @Override
    public String getDescription() {
        return pizza.getDescription();
    }
}
```

### Purpose

This is the **Base Decorator**.

It wraps another `Pizza` object.

This line is the heart of the decorator:

```java
protected final Pizza pizza;
```

It means:

```text
Every topping decorator contains another pizza inside it.
```

Example:

```java
new CheeseDecorator(new MargheritaPizza())
```

Means:

```text
CheeseDecorator wraps MargheritaPizza
```

### Why is `PizzaDecorator` abstract?

Because `PizzaDecorator` is not a real topping by itself.

You should not create:

```java
new PizzaDecorator(...)
```

Instead, real topping decorators should extend it:

```java
CheeseDecorator
OlivesDecorator
MushroomDecorator
ExtraPaneerDecorator
```

The base decorator only contains common wrapping logic.

### Why is `pizza` protected?

Because child classes like `CheeseDecorator` and `OlivesDecorator` need access to the wrapped pizza.

Example:

```java
return pizza.getCost() + 40.0;
```

But outside classes should not access the wrapped object directly.

So `protected` is better than `public`.

---

## 12. Class 5 — `CheeseDecorator`

Create:

```java
public class CheeseDecorator extends PizzaDecorator {

    public CheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 40.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Cheese";
    }
}
```

### Purpose

This decorator adds cheese.

Expected addition:

```text
Description adds: Cheese
Cost adds: 40.0
```

Important:

Do not write:

```java
return this.getCost() + 40.0;
```

That causes infinite recursion.

You must call:

```java
pizza.getCost()
```

because `pizza` is the wrapped object.

---

## 13. Class 6 — `OlivesDecorator`

Create:

```java
public class OlivesDecorator extends PizzaDecorator {

    public OlivesDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 30.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Olives";
    }
}
```

### Purpose

This decorator adds olives.

Expected addition:

```text
Description adds: Olives
Cost adds: 30.0
```

---

## 14. Class 7 — `MushroomDecorator`

Create:

```java
public class MushroomDecorator extends PizzaDecorator {

    public MushroomDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 35.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Mushroom";
    }
}
```

### Purpose

This decorator adds mushroom.

Expected addition:

```text
Description adds: Mushroom
Cost adds: 35.0
```

---

## 15. Class 8 — `ExtraPaneerDecorator`

Create:

```java
public class ExtraPaneerDecorator extends PizzaDecorator {

    public ExtraPaneerDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 50.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Extra Paneer";
    }
}
```

### Purpose

This decorator adds extra paneer.

Expected addition:

```text
Description adds: Extra Paneer
Cost adds: 50.0
```

---

## 16. Class 9 — `Main`

Create:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Module 2: Pizza Toppings Decorator");

        Pizza pizza = new MargheritaPizza();

        pizza = new CheeseDecorator(pizza);
        pizza = new OlivesDecorator(pizza);
        pizza = new MushroomDecorator(pizza);

        System.out.println("Description: " + pizza.getDescription());
        System.out.println("Total Cost: " + pizza.getCost());
    }
}
```

Expected output:

```text
Module 2: Pizza Toppings Decorator
Description: Margherita Pizza, Cheese, Olives, Mushroom
Total Cost: 255.0
```

---

## 17. Additional Test Case

Also test with `FarmhousePizza`.

Example:

```java
Pizza pizza = new FarmhousePizza();

pizza = new ExtraPaneerDecorator(pizza);
pizza = new CheeseDecorator(pizza);
pizza = new MushroomDecorator(pizza);

System.out.println("Description: " + pizza.getDescription());
System.out.println("Total Cost: " + pizza.getCost());
```

Expected output:

```text
Description: Farmhouse Pizza, Extra Paneer, Cheese, Mushroom
Total Cost: 345.0
```

Calculation:

```text
Farmhouse Pizza = 220.0
Extra Paneer    = 50.0
Cheese          = 40.0
Mushroom        = 35.0

Total = 345.0
```

---

## 18. What Actually Happens Internally

This code:

```java
Pizza pizza = new MargheritaPizza();

pizza = new CheeseDecorator(pizza);
pizza = new OlivesDecorator(pizza);
pizza = new MushroomDecorator(pizza);
```

creates this structure:

```text
MushroomDecorator
    wraps OlivesDecorator
        wraps CheeseDecorator
            wraps MargheritaPizza
```

When you call:

```java
pizza.getDescription();
```

it executes like this:

```text
MushroomDecorator.getDescription()
    -> OlivesDecorator.getDescription()
        -> CheeseDecorator.getDescription()
            -> MargheritaPizza.getDescription()
```

Final result:

```text
Margherita Pizza, Cheese, Olives, Mushroom
```

When you call:

```java
pizza.getCost();
```

it executes like this:

```text
MushroomDecorator adds 35
    OlivesDecorator adds 30
        CheeseDecorator adds 40
            MargheritaPizza returns 150
```

Final result:

```text
150 + 40 + 30 + 35 = 255
```

---

## 19. Expected Price Table

| Item | Cost |
|---|---:|
| Margherita Pizza | 150.0 |
| Farmhouse Pizza | 220.0 |
| Cheese | 40.0 |
| Olives | 30.0 |
| Mushroom | 35.0 |
| Extra Paneer | 50.0 |

---

## 20. Important Rules

### Rule 1: Do Not Call `this.getCost()` Inside Decorator

Bad:

```java
@Override
public double getCost() {
    return this.getCost() + 40.0;
}
```

This causes infinite recursion.

Correct:

```java
@Override
public double getCost() {
    return pizza.getCost() + 40.0;
}
```

### Rule 2: Do Not Call `this.getDescription()` Inside Decorator

Bad:

```java
@Override
public String getDescription() {
    return this.getDescription() + ", Cheese";
}
```

This also causes infinite recursion.

Correct:

```java
@Override
public String getDescription() {
    return pizza.getDescription() + ", Cheese";
}
```

### Rule 3: Do Not Hardcode Final Total

Bad:

```java
@Override
public double getCost() {
    return 255.0;
}
```

Correct:

```java
@Override
public double getCost() {
    return pizza.getCost() + 35.0;
}
```

Each decorator should add only its own cost.

### Rule 4: Do Not Create Combination Classes

Avoid:

```java
CheeseOlivesPizza
PaneerMushroomPizza
CheeseOlivesMushroomPizza
```

The point of Decorator Pattern is to avoid these combination classes.

### Rule 5: Use Public Constructors for Concrete Decorators

Preferred:

```java
public CheeseDecorator(Pizza pizza) {
    super(pizza);
}
```

Avoid protected constructors in concrete decorators unless you have a specific reason.

---

## 21. Common Mistakes to Avoid

### Mistake 1: Infinite Recursion

Problem:

```java
return this.getCost() + 40.0;
```

Why it is wrong:

```text
CheeseDecorator.getCost()
    calls CheeseDecorator.getCost()
        calls CheeseDecorator.getCost()
            calls CheeseDecorator.getCost()
                ...
```

This ends in:

```text
StackOverflowError
```

### Mistake 2: Forgetting to Extend `PizzaDecorator`

Less clean:

```java
public class CheeseDecorator implements Pizza
```

Preferred:

```java
public class CheeseDecorator extends PizzaDecorator
```

### Mistake 3: Forgetting One Required Decorator

For Module 2, required decorators are:

```text
CheeseDecorator
OlivesDecorator
MushroomDecorator
ExtraPaneerDecorator
```

### Mistake 4: Description Formatting Issues

Bad:

```text
Margherita Pizza ,Cheese ,Olives
```

Better:

```text
Margherita Pizza, Cheese, Olives
```

### Mistake 5: Making Wrapped Pizza Public

Bad:

```java
public final Pizza pizza;
```

Better:

```java
protected final Pizza pizza;
```

---

## 22. What This Module Tests

This module checks whether you understand:

```text
Decorator Pattern with multiple base components
Component interface
Concrete components
Base decorator
Concrete decorators
Runtime wrapping
Delegation to wrapped object
Avoiding class explosion
Correct cost calculation
Correct description building
```

---

## 23. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `Pizza` interface | 1 |
| Correct base pizzas | 1.5 |
| Correct `PizzaDecorator` | 1.5 |
| Correct `CheeseDecorator` | 1 |
| Correct `OlivesDecorator` | 1 |
| Correct `MushroomDecorator` | 1 |
| Correct `ExtraPaneerDecorator` | 1 |
| Correct wrapping in `Main` | 1 |
| Output correctness | 1 |
| Code readability | 1 |

Total: **10 marks**

---

## 24. Key Learning

The key learning of Module 2 is:

```text
Decorators can wrap any object that implements the same interface.
```

In Module 1, decorators wrapped only:

```text
SimpleCoffee
```

In Module 2, decorators can wrap:

```text
MargheritaPizza
FarmhousePizza
CheeseDecorator
OlivesDecorator
MushroomDecorator
ExtraPaneerDecorator
```

This works because all of them follow the same `Pizza` interface.

---

## 25. Final Mental Model

Remember:

```java
Pizza pizza = new MargheritaPizza();

pizza = new CheeseDecorator(pizza);
pizza = new OlivesDecorator(pizza);
pizza = new MushroomDecorator(pizza);
```

This means:

```text
MushroomDecorator(
    OlivesDecorator(
        CheeseDecorator(
            MargheritaPizza
        )
    )
)
```

Each decorator:

```text
1. Is also a Pizza
2. Wraps another Pizza
3. Calls the wrapped Pizza
4. Adds its own description and cost
```

In simple words:

> Decorator Pattern lets you build flexible object combinations layer by layer without creating a separate class for every possible combination.
