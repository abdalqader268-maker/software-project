# Vehicle Rental Management System

Course project — a Vehicle Rental Management System built incrementally with
Java 17, Maven, JUnit 5, Mockito, JaCoCo and GitHub Actions CI, applying a
layered architecture and classic design patterns.

The system lets a manager log in, browse the available fleet, rent vehicles
to customers, enforce rental policies and generate rental-expiry reminders.

## Phase 1 Scope (Sprints 1–3)

| User Story | Description | Main Classes | Tests |
|-----------|-------------|--------------|-------|
| US1.1 | Manager login | `AuthService` | `AuthServiceTest` |
| US1.2 | Manager logout, protected actions require re-login | `AuthService` | `AuthServiceTest`, `RentalServiceTest` |
| US1.3 | View available vehicles (unavailable hidden) | `VehicleService` | `VehicleServiceTest` |
| US2.1 | Rent a vehicle (record created, status → RENTED) | `RentalService` | `RentalServiceTest` |
| US2.2 | Prevent double booking | `NoOverlapRule` | `NoOverlapRuleTest`, `RentalServiceTest` |
| US2.3 | Enforce rental duration limits | `DurationLimitRule` | `DurationLimitRuleTest`, `RentalServiceTest` |
| US3.1 | Rental expiry reminder via e-mail service | `ExpiryReminderService`, `EmailReminderObserver` | `ExpiryReminderServiceTest`, `EmailReminderObserverTest` |

## Architecture

The code follows the 4-layer architecture suggested in the assignment:

```
src/main/java/edu/najah/vrms/
├── presentation/   ConsoleApp (interactive menu, wiring)
├── service/        AuthService, VehicleService, RentalService, ExpiryReminderService
├── domain/         Manager, Vehicle, Rental, enums, exceptions
├── validation/     Strategy pattern: RentalValidationRule + concrete rules
├── notification/   Observer pattern: publisher, observers, EmailService
└── persistence/    Repository interfaces + in-memory implementations
```

### Design Patterns

- **Strategy** — every rental business rule is a `RentalValidationRule`
  strategy (`DurationLimitRule`, `NoOverlapRule`). `RentalValidator` runs the
  configured chain, so new rules (e.g. pricing rules in Phase 2) plug in
  without modifying the rental workflow.
- **Observer** — `RentalExpiryPublisher` notifies subscribed
  `RentalExpiryObserver`s about expiring rentals; `EmailReminderObserver`
  turns the events into e-mails. New channels (SMS, push) subscribe without
  changing the reminder service.

### Testing

- **JUnit 5** unit tests for every service and validation rule (33 tests).
- **Mockito** mocks the notification channel (`EmailService`,
  `RentalExpiryObserver`) and the date/time source (`Clock`), as required.
- **JaCoCo** measures coverage (~93% line coverage, presentation layer
  excluded as it is exercised manually).

## Getting Started

Requirements: JDK 17+ (no local Maven needed — the wrapper downloads it).

```bash
# run all tests + coverage report
./mvnw verify          # report: target/site/jacoco/index.html

# run the console application
./mvnw compile exec:java
```

Demo manager account: `admin` / `admin123`.

## Continuous Integration

Every push and pull request runs `mvnw verify` on GitHub Actions
(`.github/workflows/ci.yml`) and uploads the JaCoCo report as a build
artifact.

## Documentation

- All classes, methods and fields carry Javadoc comments.
- UML class diagram source: [`docs/class-diagram.puml`](docs/class-diagram.puml)
  (render with any PlantUML tool, e.g. <https://www.plantuml.com/plantuml>).

## Team

- Abdalqader Awad
- Ali Abu-hijleh
