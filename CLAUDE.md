# CLAUDE.md — LLD / Design Patterns Workspace

> **Single source of truth** for how any LLM should behave in this codebase.
> Rules are listed in **strict priority order** — when two rules conflict, the lower-numbered section wins.
> The tradeoff is intentional: these guidelines bias toward caution and correctness over speed.
> For genuinely trivial tasks (renaming a variable, fixing a typo), use judgment.

---

## Instruction Priority — Who Wins When Rules Conflict

When instructions from different sources conflict, follow this strict hierarchy:

| Priority | Source | Examples |
|---|---|---|
| **1 (Highest)** | User's explicit instructions | This CLAUDE.md, direct requests in chat, AGENTS.md |
| **2** | Superpowers skills | Skill tool output (brainstorming, TDD, debugging, etc.) |
| **3 (Lowest)** | Claude's default system behavior | Built-in tendencies, default response style |

If this CLAUDE.md says "don't use TDD" and a Superpowers skill says "always use TDD" — **follow this file**. The user is always in control.

---

## SP. Superpowers Skill System — The Non-Negotiable Skill Rule

**Before any response, any action, or any clarifying question — check if a skill applies. If there is even a 1% chance a skill is relevant, invoke it using the `Skill` tool before doing anything else.**

This is not optional. This is not subject to judgment calls. If a skill exists for the situation, it must be used.

### What Is the Superpowers System?

Superpowers is a plugin-based skill framework installed at `~/.claude/plugins/`. Skills are specialized instruction sets that override Claude's default behavior for specific workflows — debugging, planning, testing, code review, brainstorming, and more. They are loaded on-demand via the `Skill` tool and must be followed exactly once loaded.

Skills are **not suggestions**. They are **loaded workflows** that define the correct process for a given class of task. Skipping them is equivalent to ignoring this CLAUDE.md entirely.

### How to Invoke a Skill

Always use the `Skill` tool — never try to read skill files directly:

```
Skill tool → skill: "superpowers:brainstorming"
Skill tool → skill: "superpowers:systematic-debugging"
Skill tool → skill: "superpowers:test-driven-development"
```

After invoking, announce what you are doing:
> "Using `superpowers:brainstorming` to explore requirements before implementation."

Then follow the skill exactly.

### The Decision Flow — Every Single Message

```
User message received
        ↓
Might any skill apply? (even 1% chance?)
        ↓ YES                    ↓ DEFINITELY NOT
  Invoke Skill tool          Respond / act directly
        ↓
  Announce: "Using [skill] to [purpose]"
        ↓
  Does the skill have a checklist?
        ↓ YES                    ↓ NO
  Create a task per item     Follow the skill directly
        ↓
  Follow the skill exactly
        ↓
  Respond
```

**This flow applies even before asking clarifying questions.** Skill check comes first — always.

### Skill Priority — Which Skill Wins When Multiple Apply

When more than one skill could apply, use this order:

1. **Process skills first** — these define *how* to approach the task
   - Brainstorming before building anything
   - Debugging before proposing any fix
   - Writing plans before writing code for multi-step tasks

2. **Implementation skills second** — these guide *execution*
   - TDD while writing the actual code
   - Verification before claiming completion
   - Code review before merging

**Example:** "Let's build a new module" → invoke `brainstorming` first, then `test-driven-development` when implementing.
**Example:** "Fix this bug" → invoke `systematic-debugging` first, then `verification-before-completion` before closing it.

### Skill Types — Rigid vs Flexible

| Type | Description | How to treat it |
|---|---|---|
| **Rigid** | Defines a strict discipline (TDD, debugging steps) | Follow exactly. Do not adapt, skip steps, or abbreviate. |
| **Flexible** | Defines principles and patterns | Adapt to context. Apply the spirit, not the letter blindly. |

The skill itself will indicate which type it is. When in doubt, treat it as rigid.

### Complete List of Available Skills

These are the skills currently installed in this environment. Trigger conditions are listed so you know when to invoke each one without being asked:

| Skill | Invoke When... |
|---|---|
| `superpowers:using-superpowers` | Starting any new conversation — establishes the skill framework |
| `superpowers:brainstorming` | **Any** creative work: new features, new modules, new components, modifying behavior. MUST run before implementation. |
| `superpowers:writing-plans` | You have a spec or multi-step requirements — before touching any code |
| `superpowers:executing-plans` | You have a written plan and are moving to implementation in a separate review checkpoint session |
| `superpowers:subagent-driven-development` | Executing implementation plans with independent parallel tasks in the current session |
| `superpowers:dispatching-parallel-agents` | 2 or more independent tasks can be worked on without shared state or sequential dependencies |
| `superpowers:test-driven-development` | Implementing any feature or bugfix — before writing implementation code |
| `superpowers:systematic-debugging` | Any bug, test failure, or unexpected behavior — before proposing any fix |
| `superpowers:requesting-code-review` | Completing a task, implementing a major feature, or before merging — verify work meets requirements |
| `superpowers:receiving-code-review` | Receiving code review feedback — before implementing suggestions, especially if feedback is unclear |
| `superpowers:verification-before-completion` | About to claim work is complete, fixed, or passing — requires running verification commands and confirming output |
| `superpowers:finishing-a-development-branch` | Implementation is complete, all tests pass, and you need to decide how to integrate (merge, PR, cleanup) |
| `superpowers:using-git-worktrees` | Starting feature work that needs isolation, or before executing implementation plans |
| `superpowers:writing-skills` | Creating new skills, editing existing skills, or verifying skills work before deployment |
| `graphify` | User types `/graphify` or wants to build/query a knowledge graph from code or docs |

**Non-superpowers skills also available:**
| Skill | Invoke When... |
|---|---|
| `update-config` | Configuring Claude Code settings, hooks, permissions, env vars, or `settings.json` changes |
| `keybindings-help` | Customizing keyboard shortcuts or modifying `~/.claude/keybindings.json` |
| `simplify` | Reviewing changed code for reuse, quality, and efficiency after implementation |
| `loop` | Setting up a recurring task or polling on an interval |
| `schedule` | Creating, updating, or listing scheduled remote agents on a cron schedule |
| `claude-api` | Building apps with the Claude API or Anthropic SDK |
| `last30days` | Researching what people say about any topic in the last 30 days |

### Red Flags — Stop If You Think Any of These

These thoughts are rationalizations. They mean you are about to skip a skill you should be using:

| Thought | Why it's wrong |
|---|---|
| "This is just a simple question" | Questions are tasks. Check for skills first. |
| "I need more context before invoking a skill" | Skill check comes BEFORE gathering context. |
| "Let me explore the codebase first" | Skills tell you HOW to explore. Check first. |
| "This doesn't need a formal skill" | If a skill exists for it, use it. Period. |
| "I remember this skill from before" | Skills evolve. Always read the current version via the Skill tool. |
| "The skill feels like overkill here" | Simple things become complex. The skill prevents that. |
| "I'll just do this one small thing first" | Check BEFORE doing anything. No exceptions. |
| "I know what that skill says" | Knowing the concept ≠ invoking the skill. Invoke it. |
| "Let me ask a clarifying question first" | Skill check comes before clarifying questions. |
| "This doesn't count as creative/implementation work" | If you are producing or modifying anything, brainstorm first. |

### Superpowers Self-Check (Add to Pre-Submission Checklist)

Before responding to any non-trivial request, verify:

```
[ ] Did I check whether any skill applies before responding?
[ ] Did I invoke the skill via the Skill tool (not from memory)?
[ ] Did I announce which skill I am using and why?
[ ] Did I follow the skill exactly (rigid) or adapt its principles (flexible)?
[ ] Did I use brainstorming before writing any new code or module?
[ ] Did I use systematic-debugging before proposing any fix?
[ ] Did I use verification-before-completion before claiming anything is done?
```

---

## 0. Orient First — Always (Highest Priority)

**Never read, edit, or create a file without first understanding where it fits.**

This step is non-negotiable. Skipping it is the root cause of most LLM coding mistakes in this project.

### When `graphify-out/graph.json` EXISTS — use the graph, in this order:

| Situation | Command to run first |
|---|---|
| General codebase question | `graphify query "<question>"` — BFS traversal across all nodes |
| Tracing a relationship | `graphify path "<ClassA>" "<ClassB>"` — finds the dependency chain |
| Understanding a pattern or class | `graphify explain "<PatternName or ClassName>"` — plain-language summary |
| Architecture-level review | Read `graphify-out/GRAPH_REPORT.md` — only when the above return insufficient context |
| Broad navigation across patterns | Use `graphify-out/wiki/index.md` instead of raw source browsing |

**Example workflow before touching any Observer code:**
```
graphify explain "Observer"         → see what modules/classes exist
graphify query "which modules use push notification"  → narrow to the right module
graphify path "Subject" "ConcreteObserver"  → confirm the dependency direction
```

**After any file modification**, always run:
```
graphify update .
```
This is AST-only — no API cost. Do not skip it.

### When `graphify-out/graph.json` does NOT exist — fallback procedure:

1. `ls "<PatternFolder>/"` — identify existing module numbers and folder names.
2. Open the **most recently numbered module's** main class and read it fully before touching anything.
3. Never grep or glob blindly across the whole repo. Read only what you need.
4. If you still can't determine structure from step 1–2, ask the user before proceeding.

---

## 1. Think Before Coding — Surface Everything

**Don't assume. Don't hide confusion. Don't pick silently.**

Before writing a single line of implementation:

### Step 1 — Run graphify
Even if you think you know the answer, run `graphify explain "<relevant pattern>"` first. Confirm what's already there. Do not implement something that already exists.

### Step 2 — State your interpretation
Write your interpretation in one explicit sentence before coding:
> "Interpreting this as: add a ConcreteObserver to Module 3 (push variant) that logs the updated stock price to stdout."

If multiple interpretations exist, list them all and ask which is intended. Do not pick silently.

**Ambiguous requests that always require clarification:**
- "Add an observer" → which module? which Subject? push or pull variant?
- "Fix the strategy" → is this a bug fix, naming fix, or design change?
- "Refactor the pattern" → refactor toward what goal? which module(s)?
- "Add a new module" → what concept should it demonstrate? should it build on a prior module?

### Step 3 — State tradeoffs if they exist
If a simpler approach exists than what was requested, say so:
> "You asked for X. A simpler alternative that achieves the same goal is Y. Should I proceed with X or Y?"

Push back when warranted. A clarifying question costs less than a wrong implementation.

### One question at a time
Never ask more than one clarifying question per response. Ask the most blocking one. Wait for the answer. Then proceed.

---

## 2. Simplicity First — Minimum Viable Implementation

**Write the smallest code that correctly solves the stated problem. Nothing speculative.**

### What NOT to add:
- Participants not explicitly requested (no extra ConcreteObserver, ConcreteStrategy, etc.)
- Abstractions or interfaces for single-use code
- "Flexibility" or "configurability" that wasn't asked for
- Error handling for scenarios that are impossible given this codebase's constraints
- Comments, Javadoc, or annotations on code you didn't change

### The senior engineer test:
Before finalizing, ask yourself: *"Would a senior engineer call this overcomplicated?"*
If yes — rewrite it. If the implementation is 200 lines and could be 50 without losing correctness, the 50-line version is the correct answer.

### The optional refactor rule:
If your implementation technically works but you see a meaningful simplification, surface it — don't silently apply it:
> "This works as written. I could also halve its length by [X] — want me to do that?"

Never silently refactor code that wasn't part of the stated task.

---

## 3. Surgical Changes — Touch Only What You Must

**Every changed line must trace directly to the user's request. If you can't justify a line, remove it.**

### When editing existing modules:
- Do not rename, reformat, or reorganize any file outside the scope of the task.
- Do not "improve" adjacent comments, spacing, or formatting — even if they're wrong.
- Do not refactor patterns you weren't asked to touch, even if you see a clear improvement.
- Match the existing style of the file you're editing exactly — naming, indentation, brace placement — even if you'd do it differently.

### Handling dead code:
- If YOUR changes orphan an import, variable, or function → remove it.
- If you notice pre-existing dead code → mention it, do not delete it:
  > "I noticed `NotificationHelper` appears unused (not related to this task). Want me to remove it separately?"

### Adding a new module — required procedure:
Before creating any file:

1. `ls "<PatternFolder>/"` — find the highest-numbered module (e.g., `Module 4/`)
2. Open that module's main class and read it fully
3. Mirror its structure exactly:
   - Same package naming pattern
   - Same class organization (interface → concrete → main)
   - Same `main()` demonstration style

**Concrete naming example:**

| Existing (Module 4) | Your new module (Module 5) |
|---|---|
| `com.patterns.observer.module4.Subject` | `com.patterns.observer.module5.Subject` |
| `com.patterns.observer.module4.ConcreteObserver` | `com.patterns.observer.module5.ConcreteObserver` |

Never deviate: not `SubjectImpl`, not `ObservableSubject`, not `ObserverModule5`.

---

## 4. Goal-Driven Execution — Define Success, Then Loop

**Transform every vague request into a verifiable goal before writing code. Loop until that goal is met.**

### How to convert a request into a goal:

| Vague request | Verifiable goal |
|---|---|
| "Add an observer" | ConcreteObserver added to Module X, registered with Subject, `main()` prints its output without errors |
| "Fix the bug" | Write a test or trace that reproduces it first, then make it pass — no other `main()` output changes |
| "Refactor X" | All existing `main()` outputs are identical before and after the refactor |
| "Create Module 5" | Module 5 compiles, `main()` runs, output logically extends what Module 4 demonstrated |
| "Add validation" | Define the invalid inputs, handle them, confirm valid inputs still behave identically |

**State the success criterion before writing code.** If you can't define one, stop and ask.

### For multi-step tasks, state a plan upfront:
```
Plan:
1. [What you'll do] → verify: [how you'll confirm it worked]
2. [What you'll do] → verify: [how you'll confirm it worked]
3. [What you'll do] → verify: [how you'll confirm it worked]
```

Strong success criteria let you loop and self-correct independently. Weak criteria ("make it work") guarantee unnecessary back-and-forth.

### Verify independently — never ask the user to check trivial things:
- Does this compile? → verify it yourself.
- Does `main()` call the right method? → trace it yourself.
- Does the output make sense? → run it yourself if possible.

### When verification is impossible:
If a task has no `main()`, no test, and no observable output, say so explicitly:
> "There's no runnable entry point to verify this against. I can add a minimal `main()` that demonstrates the behavior, or you can confirm it manually. Which do you prefer?"

Then wait for direction. Do not proceed with unverifiable code silently.

---

## 5. Codebase Conventions

### Language and structure
- **Language:** Java (no other languages, no scripting)
- **Top-level folders:** One per design pattern — `Observer Design Pattern/`, `Strategy Design Pattern/`, etc.
- **Module folders:** `Module 1/`, `Module 2/`, etc. inside each pattern folder
- **Module purpose:** Each module demonstrates exactly **one** incremental concept within the pattern. Never combine two new concepts into a single module.

### Module internal structure (standard)
Each module typically contains:
```
Module N/
├── <Interface>.java          # The pattern's core abstraction
├── <ConcreteClass>.java      # One or more implementations
├── <ContextOrSubject>.java   # The coordinating class
└── Main.java                 # Entry point that demonstrates the concept
```

### Naming rules
- Class names, package names, and file names must match the pattern already established in the module you're extending.
- When uncertain about a name, run `graphify explain "<ClassName>"` before guessing.
- Never introduce framework-style suffixes (`Impl`, `Helper`, `Manager`, `Handler`) unless they already appear in the module.

### What each module's `main()` must do
- Demonstrate the concept introduced in that module — nothing more.
- Print enough to stdout to confirm the pattern is working.
- Not depend on external input, config files, or environment variables.

---

## 6. Self-Check Before Submitting

Run this checklist mentally before presenting any implementation:

```
[ ] Did I check for applicable Superpowers skills before doing anything?
[ ] Did I invoke the relevant skill(s) via the Skill tool before responding?
[ ] Did I use brainstorming (superpowers:brainstorming) before writing any new code?
[ ] Did I use systematic-debugging before proposing any fix?
[ ] Did I use verification-before-completion before claiming the task is done?
[ ] Did I run graphify (or ls fallback) before touching any file?
[ ] Did I state my interpretation explicitly before writing code?
[ ] Did I ask about ambiguity before picking silently?
[ ] Is every changed line traceable to the user's request?
[ ] Did I avoid touching files outside the task scope?
[ ] Is this the simplest correct implementation (senior engineer test)?
[ ] Did I remove only the orphans MY changes created?
[ ] Did I mirror the module structure exactly (if adding a module)?
[ ] Did I state success criteria and verify against them?
[ ] Did I run `graphify update .` after modifying files?
```

If any box is unchecked and the reason isn't obvious, fix it or explain it before responding.

---

## Hard Constraints — No Exceptions

These are not subject to "good reason" exceptions or user override within a session:

- **No new pattern folders** unless explicitly requested by name.
- **No build tooling** — no Maven, Gradle, Ant, or wrapper scripts.
- **No new dependencies** — no logging frameworks, test frameworks, or libraries not already present.
- **No documentation files** (README, CONTRIBUTING, etc.) unless explicitly asked.
- **No guessing file paths** — use `graphify` or `ls` to confirm before referencing a path.
- **No cross-pattern refactoring** — do not touch `Observer` when asked to fix `Strategy`.
- **No silent decisions** — every non-trivial choice must be stated before implementation.

---

## Quick Reference — Graphify Commands

```bash
# Before any task — understand what exists
graphify explain "Observer"                        # What's already implemented
graphify query "push notification pattern"         # Find relevant nodes
graphify path "Subject" "ConcreteObserver"         # Trace a relationship

# After any file change — keep graph current
graphify update .

# Architecture review (only when query/explain aren't enough)
# → Read graphify-out/GRAPH_REPORT.md manually
```

---

> **These guidelines are working if:**
> - Diffs contain only lines traceable to the request
> - Clarifying questions arrive before implementation, not after mistakes
> - No rewrites caused by overcomplication or wrong interpretation
> - `graphify update .` is run after every session that modified files
