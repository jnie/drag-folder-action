# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [0.0.1-SNAPSHOT]

### Added
- Initial release of drag-folder-action v2 multi-module architecture

### Features
- Folder monitoring for new files using Java NIO WatchService
- Automatic ZIP file extraction
- Configurable via command-line properties

### Architecture
- Clean Architecture / Hexagonal Architecture patterns
- Multi-module Maven project structure
- ArchUnit architecture tests
- Domain-driven design with explicit ports and adapters

## [0.0.2-SNAPSHOT]

### Changed
- **Simplify handler lookup**: Remove dummy event creation in FileProcessingServiceImpl constructor
  - Add `getSupportedTypes()` to `FileHandler` interface for explicit type declaration
  - Change `processFile()` to use runtime lookup via `canHandle()` instead of pre-built map
  - Add helpful warning log with available handlers and supported types when no handler matches

### Security
- **Fix Zip Slip vulnerability**: Add path traversal validation in `ZipFileHandler.extractEntry()`
  - Validates resolved path stays within target directory
  - Skips malicious entries with warning log

### Bug Fixes
- **Fix output folder configuration**: `FileProcessingServiceImpl` now uses configured `outputFolder` from `Configuration` instead of hardcoded `System.getProperty("java.io.tmpdir")`

### Dependencies
- Spring Boot 3.2.5
- Java 17+
- ArchUnit 1.4.1
- SLF4J 2.0.16
