# AGENTS.md guide

## 1. Repository Overview

- The drag-folder-action project is a multi-module Maven command-line application demonstrating Clean Architecture and Hexagonal Architecture patterns.
- This CLI application monitors a folder for new files and processes them (e.g., zipping).
- Separation of concerns is kept strict in separate modules for low cognitive load in the IDE.

**Technology Stack**
- Java 17+
- Spring Boot 3.2.5
- Maven 3.9+
- SLF4J (logging)
- ArchUnit (architecture testing)

---

## 2. Project Structure, Code style & patterns

```
drag-folder-action/
├── app/
│   ├── inbound/
│   │   └── cli/              # CLI interface, user input handling
│   ├── outbound/
│   │   ├── file-system/      # File system monitoring (WatchService)
│   │   └── zip-handler/      # ZIP file processing
│   ├── application/          # Spring Boot initializer, bean wiring
│   ├── architecture-tests/   # ArchUnit architecture tests
│   ├── domain/               # Domain models & interfaces
│   └── service/              # Business logic implementation
├── AGENTS.md                 # Agents guidelines
├── README.md                 # Project documentation
├── .github/                  # GitHub workflows
└── pom.xml                   # Parent POM (flat structure)
```

### General principles

1. **Always check existing modules first** - Understand the architecture before making changes
2. **Keep the module boundaries clean** - Don't mix concerns across modules
3. **Domain layer should be vendor-agnostic** - No Spring annotations in domain/
4. **main branch** - is off limits, when changing code always create a branch
5. **Run tests before committing** - Use `./mvnw test`
6. **Port/adapter pattern** - External systems go in `app/outbound/`, interfaces in `app/domain/`
7. **Port/adapter pattern** - CLI interface in `app/inbound/`, depends only on domain
8. **Dependency versions** - keep versions in properties in root pom.xml

### Type safety & Code quality

- Language: Java 17+; use strict typing; avoid raw types and unchecked casts.
- Formatting: Use Google Java Style (2 spaces indentation)
- Verification: `./mvnw verify` runs all checks; fix violations, don't suppress.
- Never use `@SuppressWarnings` without justification; fix root causes instead.
- Never use reflection or runtime bytecode manipulation to share class behavior.
- In tests, use Mockito `@Mock` per-instance stubs; avoid static state mutation.
- Add brief comments for tricky logic (explain WHY, not WHAT).
- Keep files under ~500 LOC; extract helpers instead of "V2" copies.
- Lombok: prefer `@Value`, `@Builder`, `@RequiredArgsConstructor`; avoid `@Data` on mutable classes.
- Naming: follow Spring Boot conventions (`@Service`, `@Component`, `@Bean`).

### Formatting
- Use the generic Google Java Style formatting
- Use 2 spaces for indentation.
- Always include curly braces, even for single-line `if` statements.

---

## 3. Agentic workflow (The "How-To")

When you are tasked with a feature or bug fix, follow this exact sequence:

### Phase 1: Exploration & Plan
1. **Search:** Locate relevant logic using `grep` or symbol search.
2. **Propose:** Briefly summarize your plan in the chat before writing code.

### Phase 2: Implementation & Testing
1. **Branch from a clean main** - Make sure you have all the latest from main branch
2. **Branch naming** - make /feature/{name} for new feature sets, bugfix/{name} for fixing bugs
3. **Execute:** Modify files. Do not delete comments unless they are obsolete.
4. **Local Validation:**
   - Build command: `./mvnw clean compile`
   - Test command: `./mvnw test`
5. **Self-Correction:** If tests fail, analyze the logs, fix the code, and re-run tests until green. **Do not ask for help until you have attempted 2 logical fixes.**

---
### ⚡ Quick Reference

| Action | Command |
|--------|---------|
| Full build | `./mvnw clean package` |
| Run app | `java -jar app/application/target/application-*.jar -Dmonitor.folder=/tmp/monitor -Doutput.folder=/tmp/output` |
| Run tests | `./mvnw test` |
| Build specific module | `./mvnw install -pl <module> -am` |
| Check dependencies | `./mvnw dependency:tree` |
| Architecture tests | `./mvnw test -pl app/architecture-tests` |

### 🔧 Common Issues & Solutions

**Problem:** "Unable to find main class"
```bash
# Build the application module first
./mvnw clean package -pl app/application -am
```

**Problem:** Module not found
```bash
# Ensure all modules are built
./mvnw clean install
```

---

## Testing Guidelines

### Test Framework & Coverage
- Framework: JUnit 5 (Jupiter) with Mockito and AssertJ.
- Unit tests: `*Test.java` suffix.
- Run `./mvnw test` before pushing when you touch logic.

### Test Execution & Performance
- Do not set Surefire forkCount above 6.
- If local test runs cause memory pressure, use: `MAVEN_OPTS="-Xmx1g -XX:MaxMetaspaceSize=512m" ./mvnw test`.
- Test profiles: use `-Dspring.profiles.active=test` for test configuration.

### Test Patterns & Best Practices
- Mocking: Use Mockito `@Mock`, `@InjectMocks`, `@ExtendWith(MockitoExtension.class)`.
- Assertions: Prefer AssertJ fluent assertions (`assertThat(...)`).
- Method naming: Use `@DisplayName("descriptive test name")` for clarity.

### Architecture Tests
The project uses ArchUnit to enforce:
- Domain has no dependencies on other modules
- Inbound only depends on Domain
- Service only depends on Domain
- No cyclic dependencies

Run architecture tests:
```bash
./mvnw test -pl app/architecture-tests
```

---

## 4. Pull Request (PR) requirements

### Communication within the repo
- **Repo:** https://github.com/jnie/drag-folder-action
- **In chat replies** file references must be repo-root relative only (example: `app/inbound/cli/src/main/java/dk/jnie/dragfolder/inbound/cli/CliRunner.java`); never absolute paths or `~/...`.
- **GitHub PR Summary:** What was changed and why.
- **Breaking Changes:** Explicitly state if any APIs or behaviors were modified.
- **GitHub issues/comments/PR comments** use literal multiline strings; never embed "\\n".
- **GitHub comments** never use `gh issue/pr comment -b "..."` when body contains backticks or shell chars.
- **PR landing comments** always make commit SHAs clickable with full commit links.
- **PR review conversations** if a bot leaves review conversations on your PR, address them and resolve those conversations yourself once fixed.
- **Risk Assessment:** Label as [Low/Medium/High] risk.

---

## 5. Constraints & Boundaries
- **Dependencies:** Do not add new external libraries without explicit user approval.
- **Secrets:** Never commit `.env` files or sensitive configuration.
- **CLI Nature:** This is a command-line application, not a web service.
- **File Handling:** Always use try-with-resources for file operations.
- **NO Blocking Operations:** File monitoring is async; keep operations non-blocking where possible.
