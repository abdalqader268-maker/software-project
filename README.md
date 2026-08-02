# Vehicle Rental Management System

Course project — a Vehicle Rental Management System built incrementally with
Java 17, Maven, JUnit 5, Mockito, JaCoCo and GitHub Actions CI, applying a
layered architecture and classic design patterns.

The system lets a manager log in, browse the available fleet, rent vehicles
to customers, enforce rental policies, return vehicles with billing and
generate rental-expiry reminders.

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

## Phase 2 Scope (Sprints 4–5)

| User Story | Description | Main Classes | Tests |
|-----------|-------------|--------------|-------|
| US4.1 | Return a vehicle (status → AVAILABLE, rental closed) | `ReturnService` | `ReturnServiceTest` |
| US4.2 | Calculate rental cost from duration | `StandardPricingStrategy`, `RentalReceipt` | `StandardPricingStrategyTest`, `ReturnServiceTest` |
| US4.3 | Apply late-return penalty | `StandardPricingStrategy` | `StandardPricingStrategyTest`, `ReturnServiceTest` |
| US5.1 | Support multiple vehicle types | `Car`, `Van`, `Motorcycle`, `Truck`, `ElectricVehicle` | `VehicleTypeRulesTest` |
| US5.2 | Apply type-specific rules (truck license, EV battery, motorcycle age) | `Vehicle.checkRentalEligibility`, `TypeSpecificRule` | `VehicleTypeRulesTest`, `TypeSpecificRuleTest`, `RentalServiceTypeRulesTest` |

## Architecture

The code follows the 4-layer architecture suggested in the assignment:

```
src/main/java/edu/najah/vrms/
├── presentation/   ConsoleApp (interactive menu, wiring)
├── service/        AuthService, VehicleService, RentalService, ReturnService, ExpiryReminderService
├── domain/         Manager, Vehicle hierarchy, Rental, RentalReceipt, enums, exceptions
├── validation/     Strategy pattern: RentalValidationRule + concrete rules
├── billing/        Strategy pattern: PricingStrategy + StandardPricingStrategy
├── notification/   Observer pattern: publisher, observers, EmailService
└── persistence/    Repository interfaces + in-memory implementations
```

### Design Patterns

- **Strategy** — rental business rules are `RentalValidationRule` strategies
  (`DurationLimitRule`, `NoOverlapRule`, `TypeSpecificRule`) and rental pricing
  is a `PricingStrategy` (`StandardPricingStrategy`). New rules or pricing
  schemes plug in without modifying the workflow.
- **Observer** — `RentalExpiryPublisher` notifies subscribed
  `RentalExpiryObserver`s about expiring rentals; `EmailReminderObserver`
  turns the events into e-mails. New channels (SMS, push) subscribe without
  changing the reminder service.
- **Polymorphism** — `Vehicle` is abstract; `Car`, `Van`, `Motorcycle`,
  `Truck` and `ElectricVehicle` override `checkRentalEligibility` to enforce
  their own rules (US5.2). `TypeSpecificRule` delegates to that method, so the
  validator chain treats every vehicle type uniformly.

### Testing

- **JUnit 5** unit tests for every service, validation rule, pricing strategy
  and vehicle type (57 tests).
- **Mockito** mocks the notification channel (`EmailService`,
  `RentalExpiryObserver`) and the date/time source (`Clock`), as required.
- **JaCoCo** measures coverage (~95% line coverage, presentation layer
  excluded as it is exercised manually).
- **SonarCloud** runs static analysis on every push/PR through GitHub Actions.

## Getting Started

Requirements: JDK 17+ (no local Maven needed — the wrapper downloads it).

```bash
# run all tests + coverage report
./mvnw verify          # report: target/site/jacoco/index.html

# generate the Java API documentation
./mvnw javadoc:javadoc # docs: target/site/apidocs/index.html

# run the console application
./mvnw compile exec:java
```

Demo manager account: `admin` / `admin123`.

## Continuous Integration & Static Analysis

Every push and pull request runs `mvnw verify` on GitHub Actions
(`.github/workflows/ci.yml`) and uploads the JaCoCo report as a build
artifact. Once the `SONAR_TOKEN` secret is configured, the same workflow runs
a **SonarCloud** static-analysis scan (see
[`docs/Phase2-Report.md`](docs/Phase2-Report.md) for the setup checklist).

## Documentation

- All classes, methods and fields carry Javadoc comments; generate the HTML
  API docs with `./mvnw javadoc:javadoc`.
- UML class diagram source: [`docs/class-diagram.puml`](docs/class-diagram.puml)
  (render with any PlantUML tool, e.g. <https://www.plantuml.com/plantuml>).
- Phase 2 report (SonarCloud, code smells, coverage, UML):
  [`docs/Phase2-Report.md`](docs/Phase2-Report.md).

## Team

- Abdalqader Awad
- Ali Abu-hijleh
