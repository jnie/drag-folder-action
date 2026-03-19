# drag-folder-action

[![Build Status](https://github.com/jnie/drag-folder-action/actions/workflows/maven.yml/badge.svg)](https://github.com/jnie/drag-folder-action/actions)
[![Maven Central](https://img.shields.io/badge/Maven-3.9+-blue)](https://maven.apache.org/)
[![Java](https://img.shields.io/badge/Java-17+-blue)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)

## Overview

DragFolderAction is a CLI application that monitors a folder for new files and automatically processes them. It demonstrates Clean Architecture and Hexagonal Architecture patterns in a Spring Boot command-line application.

## Quick Start

```bash
# Clone the repository
git clone https://github.com/jnie/drag-folder-action.git
cd drag-folder-action

# Build the project
./mvnw clean package

# Run the application
java -jar app/application/target/application-*.jar \
  -Dmonitor.folder=/path/to/monitor \
  -Doutput.folder=/path/to/output \
  -Dtimer.seconds=5
```

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Programming language |
| Spring Boot | 3.2.5 | Application framework |
| Maven | 3.9+ | Build tool |
| Lombok | Latest | Code generation |
| SLF4J | 2.0+ | Logging |
| ArchUnit | 1.4+ | Architecture testing |

## 🎯 Purpose

- Demonstrate **Clean Architecture** and **Hexagonal Architecture** in a CLI application
- Provide a **reference implementation** for file processing workflows
- Showcase **separation of concerns** with strict module boundaries

## 📦 Architecture

The project follows a **Clean Architecture** approach with clear separation of concerns:

```
app/
├── inbound/
│   └── cli/                 # CLI interface, user interaction
├── application/             # Spring Boot initializer, bean configuration
├── architecture-tests/      # ArchUnit architecture validation
├── domain/                  # Domain models, interfaces (business logic contracts)
├── service/                 # Business logic implementation
└── outbound/
    ├── file-system/         # File system monitoring (WatchService)
    └── zip-handler/         # ZIP file processing
```

### Module Responsibilities

| Module | Description |
|--------|-------------|
| **inbound/cli** | Command-line interface, user interaction |
| **application** | Entry point (Application.main), Spring configuration, dependency wiring |
| **architecture-tests** | ArchUnit tests validating architectural boundaries |
| **domain** | Business models, interfaces (contracts), no framework dependencies |
| **service** | Business logic, orchestrates file processing |
| **outbound/file-system** | File system monitoring using Java NIO WatchService |
| **outbound/zip-handler** | ZIP file compression handling |

## Constraints for Module Dependencies

- `inbound/cli` → depends only on `domain`
- `application` → depends on all modules to wire the full application
- `domain` → no dependencies (pure business logic)
- `service` → depends only on `domain`
- `outbound/*` → depends only on `domain`

## Usage

### Configuration

The application is configured via system properties:

| Property | Default | Description |
|----------|---------|-------------|
| `monitor.folder` | `/tmp/monitor` | Folder to monitor for new files |
| `output.folder` | `/tmp/output` | Folder for processed output |
| `timer.seconds` | `5` | Polling interval in seconds |
| `clear.folder` | `false` | Clear output folder on startup |

### Running from Command Line

```bash
# Build all modules
./mvnw clean package

# Run with custom configuration
java -jar app/application/target/application-*.jar \
  -Dmonitor.folder=/home/user/Downloads \
  -Doutput.folder=/home/user/Processed \
  -Dtimer.seconds=10 \
  -Dclear.folder=true
```

### Running in IDE

1. Import the project as a Maven project
2. Select the `app/application` module as the main module
3. Add VM options in run configuration:
   ```
   -Dmonitor.folder=/path/to/monitor
   -Doutput.folder=/path/to/output
   ```

## Development

### Build Commands

```bash
# Full build
./mvnw clean package

# Run tests
./mvnw test

# Build specific module
./mvnw install -pl app/application -am

# Check dependencies
./mvnw dependency:tree
```

### Architecture Tests

The project includes ArchUnit tests that verify:
- Domain layer has no external dependencies
- Module boundaries are respected
- No cyclic dependencies between modules

Run architecture tests:
```bash
./mvnw test -pl app/architecture-tests
```

## Why This Architecture

### Benefits

1. **Separation of Concerns**: Each module has a clear responsibility
2. **Testability**: Business logic can be tested without external dependencies
3. **Flexibility**: External integrations can be swapped without affecting core logic
4. **Maintainability**: Changes to one module have minimal impact on others
5. **Framework Agnostic**: Domain logic has no Spring dependencies

## License

This project is available for learning and demonstration purposes.
