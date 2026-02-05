# CIM Project Guide

CIM (Cross-platform Instant Messaging) is a Java-based instant messaging framework.

## Requirements

- **Minimum JDK Version**: JDK 17
- **Build Tool**: Maven
- **Spring Boot Version**: 3.3.0

## Environment Setup

Before running compile or test commands, set the correct JDK version:

```bash
export JAVA_HOME=$JAVA_17_HOME
```

`JAVA_17_HOME` is defined in `~/.zshrc`.

## Common Commands

### Compile the project
```bash
mvn clean compile
```

### Run tests
```bash
mvn test
```

### Package the project
```bash
mvn clean package -DskipTests
```

### Full build (with tests)
```bash
mvn clean install
```

## Notes

- Checkstyle code style checks run automatically during Maven's `validate` phase
- Ensure code passes Checkstyle checks before committing
