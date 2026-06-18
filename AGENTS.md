# AGENTS.md - LLD / Design Patterns Workspace

> **Single source of truth** for how Codex and any agentic LLM should behave in this codebase.
> This file mirrors the intent and structure of `CLAUDE.md`, adapted for agent workflows and with Superpowers skills integrated.
> Rules are listed in **strict priority order**. When two rules conflict, the lower-numbered section wins.
> User instructions in the current conversation still take highest priority.
> The tradeoff is intentional: these guidelines bias toward caution, correctness, and verifiable work over speed.
> For genuinely trivial tasks such as fixing a typo or renaming a local variable, use judgment, but still preserve the repository's style and avoid unrelated edits.

---

## 0. Use Superpowers First - Always

**Before responding, planning, debugging, editing, reviewing, or verifying, check whether a Superpowers skill applies.**

If a Superpowers skill might apply, use it before taking action. Do not treat the skills as optional process notes. They are the default workflow for this repository.

### Superpowers skill priority

Use process skills before implementation skills:

| Situation | Required Superpowers skill |
|---|---|
| Any conversation or task where a skill might apply | `superpowers:using-superpowers` |
| Creative work, feature design, new components, or behavior changes | `superpowers:brainstorming` |
| Bug investigation or unexpected behavior | `superpowers:systematic-debugging` |
| Implementing a feature or bugfix | `superpowers:test-driven-development` when practical |
| Multi-step implementation work | `superpowers:writing-plans` before touching code |
| Executing an existing written plan | `superpowers:executing-plans` |
| Independent tasks that can run in parallel | `superpowers:dispatching-parallel-agents` or `superpowers:subagent-driven-development` |
| Receiving review feedback | `superpowers:receiving-code-review` |
| Requesting or performing final review | `superpowers:requesting-code-review` when the change is non-trivial |
| Before claiming work is complete, fixed, or passing | `superpowers:verification-before-completion` |
| Starting isolated feature work when the current tree may be dirty | `superpowers:using-git-worktrees` when appropriate |
| Wrapping up completed branch work | `superpowers:finishing-a-development-branch` |

### Skill usage rules

- Announce the skill being used and why in one short sentence.
- Read and follow the selected skill's instructions before acting.
- If multiple skills apply, use the minimum set that covers the task.
- User instructions, this `AGENTS.md`, and `CLAUDE.md` override Superpowers skills when they conflict.
- Superpowers skills override generic agent habits when they conflict.
- Do not skip a relevant skill because the task appears simple.

---

## 1. Orient First - Always

**Never read, edit, or create a project file without first understanding where it fits.**

This step is non-negotiable. Skipping it is the root cause of most LLM coding mistakes in this project.

### When `graphify-out/graph.json` EXISTS - use the graph, in this order:

| Situation | Command to run first |
|---|---|
| General codebase question | `graphify query "<question>"` - BFS traversal across all nodes |
| Tracing a relationship | `graphify path "<ClassA>" "<ClassB>"` - finds the dependency chain |
| Understanding a pattern or class | `graphify explain "<PatternName or ClassName>"` - plain-language summary |
| Architecture-level review | Read `graphify-out/GRAPH_REPORT.md` only when the above return insufficient context |
| Broad navigation across patterns | Use `graphify-out/wiki/index.md` instead of raw source browsing |

**Example workflow before touching any Observer code:**

```bash
graphify explain "Observer"
graphify query "which modules use push notification"
graphify path "Subject" "ConcreteObserver"
```

**After any file modification**, always run:

```bash
graphify update .
```

This is AST-only and has no API cost. Do not skip it.

### When `graphify-out/graph.json` does NOT exist - fallback procedure:

1. `ls "<PatternFolder>/"` - identify existing module numbers and folder names.
2. Open the **most recently numbered module's** main class and read it fully before touching anything.
3. Never grep or glob blindly across the whole repo. Read only what you need.
4. If you still cannot determine structure from step 1-2, ask the user before proceeding.

### Search guidance

- Use `rg` or `rg --files` for searches when available.
- Prefer targeted searches over whole-repo browsing.
- Do not guess file paths. Confirm them with `graphify`, `rg --files`, or `ls`.

---

## 2. Think Before Coding - Surface Everything

**Do not assume. Do not hide confusion. Do not pick silently.**

Before writing implementation:

### Step 1 - Use Superpowers and graphify

Use the relevant Superpowers skill first, then run the relevant `graphify` command.

Even if you think you know the answer, run:

```bash
graphify explain "<relevant pattern>"
```

Confirm what already exists. Do not implement something that already exists.

### Step 2 - State your interpretation

Write your interpretation in one explicit sentence before coding:

> "Interpreting this as: add a ConcreteObserver to Module 3 (push variant) that logs the updated stock price to stdout."

If multiple interpretations exist, list them and ask which is intended. Do not pick silently.

### Step 3 - State tradeoffs if they exist

If a simpler approach exists than what was requested, say so:

> "You asked for X. A simpler alternative that achieves the same goal is Y. Should I proceed with X or Y?"

Push back when warranted. A clarifying question costs less than a wrong implementation.

### One question at a time

Never ask more than one clarifying question per response. Ask the most blocking one. Wait for the answer. Then proceed.

### Ambiguous requests that require clarification

- "Add an observer" - which module, which Subject, push or pull variant?
- "Fix the strategy" - bug fix, naming fix, or design change?
- "Refactor the pattern" - refactor toward what goal and which modules?
- "Add a new module" - what concept should it demonstrate and should it build on a prior module?
- "Use Superpowers" - which workflow, unless the task itself clearly maps to a skill?

---

## 3. Simplicity First - Minimum Viable Implementation

**Write the smallest code that correctly solves the stated problem. Nothing speculative.**

### What NOT to add

- Participants not explicitly requested, such as extra `ConcreteObserver`, `ConcreteStrategy`, or service classes.
- Abstractions or interfaces for single-use code.
- Flexibility or configurability that was not asked for.
- Error handling for scenarios that are impossible given this codebase's constraints.
- Comments, Javadoc, or annotations on code you did not change.
- Build tools, frameworks, or dependencies.

### The senior engineer test

Before finalizing, ask:

> "Would a senior engineer call this overcomplicated?"

If yes, rewrite it. If the implementation is 200 lines and could be 50 without losing correctness, the 50-line version is the correct answer.

### The optional refactor rule

If your implementation works but you see a meaningful simplification, surface it. Do not silently apply it:

> "This works as written. I could also halve its length by [X]. Want me to do that?"

Never silently refactor code that was not part of the stated task.

---

## 4. Surgical Changes - Touch Only What You Must

**Every changed line must trace directly to the user's request. If you cannot justify a line, remove it.**

### When editing existing modules

- Do not rename, reformat, or reorganize files outside the scope of the task.
- Do not improve adjacent comments, spacing, or formatting, even if they are wrong.
- Do not refactor patterns you were not asked to touch.
- Match the existing style of the file exactly: naming, indentation, brace placement, structure, and demonstration style.
- Preserve user changes. Do not overwrite, revert, or discard work you did not make unless the user explicitly asks.

### Handling dead code

- If your changes orphan an import, variable, method, or class, remove it.
- If you notice pre-existing dead code, mention it but do not delete it:

> "I noticed `NotificationHelper` appears unused (not related to this task). Want me to remove it separately?"

### Adding a new module - required procedure

Before creating any file:

1. Use the relevant Superpowers skill, usually `brainstorming` and, for implementation, `test-driven-development` when practical.
2. Run `graphify explain "<PatternName>"` if the graph exists.
3. `ls "<PatternFolder>/"` - find the highest-numbered module.
4. Open that module's main class and read it fully.
5. Mirror its structure exactly:
   - Same package naming pattern.
   - Same class organization.
   - Same `main()` demonstration style.
   - Same level of complexity.

**Concrete naming example:**

| Existing (Module 4) | New module (Module 5) |
|---|---|
| `com.patterns.observer.module4.Subject` | `com.patterns.observer.module5.Subject` |
| `com.patterns.observer.module4.ConcreteObserver` | `com.patterns.observer.module5.ConcreteObserver` |

Never deviate into names like `SubjectImpl`, `ObservableSubject`, or `ObserverModule5` unless that exact style is already established in the module being extended.

---

## 5. Goal-Driven Execution - Define Success, Then Loop

**Transform every vague request into a verifiable goal before writing code. Loop until that goal is met.**

### Convert requests into verifiable goals

| Vague request | Verifiable goal |
|---|---|
| "Add an observer" | `ConcreteObserver` added to Module X, registered with Subject, `main()` prints its output without errors |
| "Fix the bug" | Reproduce the bug first, then make it pass with no unrelated `main()` output changes |
| "Refactor X" | Existing `main()` outputs are identical before and after the refactor |
| "Create Module 5" | Module 5 compiles, `main()` runs, output logically extends what Module 4 demonstrated |
| "Add validation" | Invalid inputs are defined and handled, while valid inputs still behave identically |

State the success criterion before writing code. If you cannot define one, stop and ask.

### For multi-step tasks, state a plan upfront

Use `superpowers:writing-plans` for multi-step work, and write:

```text
Plan:
1. [What will be done] -> verify: [how it will be confirmed]
2. [What will be done] -> verify: [how it will be confirmed]
3. [What will be done] -> verify: [how it will be confirmed]
```

Strong success criteria let the agent loop and self-correct independently. Weak criteria such as "make it work" cause unnecessary back-and-forth.

### Verify independently

Never ask the user to check trivial things:

- Does this compile? Verify it.
- Does `main()` call the right method? Trace it.
- Does the output make sense? Run it when possible.
- Did `graphify update .` succeed? Run it after file changes.

### When verification is impossible

If a task has no `main()`, no test, and no observable output, say so explicitly:

> "There is no runnable entry point to verify this against. I can add a minimal `main()` that demonstrates the behavior, or you can confirm it manually. Which do you prefer?"

Then wait for direction. Do not proceed with unverifiable code silently.

---

## 6. Codebase Conventions

### Language and structure

- **Language:** Java only.
- **No scripting:** Do not add scripts or use another language for project code.
- **Top-level folders:** One per design pattern, such as `Observer Design Pattern/` and `Strategy Design Pattern/`.
- **Module folders:** `Module 1/`, `Module 2/`, etc. inside each pattern folder, though some existing folders may use legacy naming such as `Module-5/`.
- **Module purpose:** Each module demonstrates exactly one incremental concept within the pattern. Never combine two new concepts into a single module.

### Module internal structure

Each module typically contains:

```text
Module N/
├── <Interface>.java
├── <ConcreteClass>.java
├── <ContextOrSubject>.java
└── Main.java
```

Follow the actual local module structure over this generic outline when they differ.

### Naming rules

- Class names, package names, and file names must match the pattern already established in the module being extended.
- When uncertain about a name, run `graphify explain "<ClassName>"` before guessing.
- Never introduce framework-style suffixes such as `Impl`, `Helper`, `Manager`, or `Handler` unless they already appear in the target module.

### What each module's `main()` must do

- Demonstrate the concept introduced in that module and nothing more.
- Print enough to stdout to confirm the pattern is working.
- Avoid external input, config files, environment variables, network calls, and dependencies.

---

## 7. Testing, Checks, and Verification

Use `superpowers:verification-before-completion` before claiming work is complete, fixed, or passing.

### Expected checks

- Run the most relevant compile, test, or `main()` command after code changes when practical.
- If no test framework exists, compile the changed Java files or run the relevant `Main`.
- After modifying files, run:

```bash
graphify update .
```

- If `graphify update .` fails, report the failure and do not pretend the graph is updated.

### Command safety

- If a command requires network access, elevated permissions, destructive changes, or writes outside the workspace, ask first.
- Do not add Maven, Gradle, Ant, wrapper scripts, or dependency managers.
- Do not run destructive git commands such as `git reset --hard` or checkout-based file reverts unless explicitly requested.

### Final response requirements

Every final response after code changes must include:

- What changed.
- Which files were changed.
- Which checks were run.
- Any checks that could not be run and why.
- Any important follow-up or risk.

Keep the summary concise and factual.

---

## 8. Self-Check Before Submitting

Run this checklist before presenting any implementation:

```text
[ ] Did I use the relevant Superpowers skill before acting?
[ ] Did I run graphify or the ls fallback before touching project files?
[ ] Did I state my interpretation explicitly before writing code?
[ ] Did I ask about ambiguity before picking silently?
[ ] Did I define success criteria before implementation?
[ ] Is every changed line traceable to the user's request?
[ ] Did I avoid touching files outside the task scope?
[ ] Did I preserve user changes?
[ ] Is this the simplest correct implementation?
[ ] Did I remove only the orphans my changes created?
[ ] Did I mirror the module structure exactly if adding a module?
[ ] Did I verify against the stated success criteria?
[ ] Did I run `graphify update .` after modifying files?
[ ] Did I use `verification-before-completion` before claiming success?
```

If any box is unchecked and the reason is not obvious, fix it or explain it before responding.

---

## 9. Hard Constraints - No Exceptions

These are not subject to casual exceptions:

- **No new pattern folders** unless explicitly requested by name.
- **No build tooling** - no Maven, Gradle, Ant, or wrapper scripts.
- **No new dependencies** - no logging frameworks, test frameworks, or libraries not already present.
- **No documentation files** such as README or CONTRIBUTING unless explicitly asked.
- **No guessing file paths** - use `graphify`, `rg --files`, or `ls`.
- **No cross-pattern refactoring** - do not touch Observer code when asked to fix Strategy code.
- **No silent decisions** - every non-trivial choice must be stated before implementation.
- **No overwriting user work** - do not revert or discard changes unless explicitly asked.
- **No broad formatting changes** - preserve existing formatting outside changed lines.

---

## 10. Quick Reference - Graphify Commands

```bash
# Before any task - understand what exists
graphify explain "Observer"
graphify query "push notification pattern"
graphify path "Subject" "ConcreteObserver"

# After any file change - keep graph current
graphify update .

# Architecture review only when query/explain are insufficient
# Read graphify-out/GRAPH_REPORT.md manually
```

---

## 11. Quick Reference - Superpowers Skills

```text
superpowers:using-superpowers              - check and load relevant skills first
superpowers:brainstorming                  - clarify and shape creative feature work
superpowers:systematic-debugging           - investigate bugs before fixing
superpowers:test-driven-development        - write failing tests/checks before implementation when practical
superpowers:writing-plans                  - create implementation plans for multi-step work
superpowers:executing-plans                - execute an existing implementation plan
superpowers:dispatching-parallel-agents    - split independent investigations/tasks
superpowers:subagent-driven-development    - execute independent implementation tasks with subagents
superpowers:receiving-code-review          - process review feedback rigorously
superpowers:requesting-code-review         - review completed non-trivial work
superpowers:verification-before-completion - verify before claiming completion
superpowers:using-git-worktrees            - isolate larger feature work when appropriate
superpowers:finishing-a-development-branch - wrap up completed branch work
```

---

> **These guidelines are working if:**
> - Relevant Superpowers skills are used automatically.
> - Diffs contain only lines traceable to the request.
> - Clarifying questions arrive before implementation, not after mistakes.
> - No rewrites are needed because of overcomplication or wrong interpretation.
> - `graphify update .` is run after every session that modified files.
