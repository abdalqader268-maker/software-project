# Vehicle Rental Management System — Phase 2 Report

<!--
  This Markdown is the source of the PDF you submit to Moodle.
  Export it to PDF (VS Code "Markdown PDF" extension, or print-to-PDF from a
  browser) after pasting the SonarCloud screenshots at the marked spots.
-->

## Cover Page

| | |
|---|---|
| **Course** | Software Engineering |
| **Project** | Vehicle Rental Management System (Phase 2) |
| **University** | An-Najah National University |
| **Repository** | https://github.com/abdalqader268-maker/software-project |
| **Semester** | Summer 2025/2026 |

**Team**

| # | Name | Student ID |
|---|------|-----------|
| 1 | Abdalqader Awad | _____________ |
| 2 | Ali Abu-hijleh | _____________ |
| 3 | _____________ | _____________ |

---

## 1. Scope Delivered in Phase 2

| User Story | Description | Status |
|-----------|-------------|--------|
| US4.1 | Return a vehicle (status → AVAILABLE, rental closed) | Done |
| US4.2 | Calculate rental cost from duration | Done |
| US4.3 | Apply late-return penalty | Done |
| US5.1 | Support multiple vehicle types (Car, Van, Motorcycle, Truck, Electric) | Done |
| US5.2 | Type-specific rules (truck license, EV battery, motorcycle age) | Done |

**Design patterns applied**

- **Strategy** — rental validation (`RentalValidationRule`) and pricing
  (`PricingStrategy`) are both strategy families.
- **Observer** — rental-expiry reminders (`RentalExpiryPublisher` /
  `RentalExpiryObserver`).
- **Polymorphism** — abstract `Vehicle` with per-type `checkRentalEligibility`
  overrides, driven uniformly through `TypeSpecificRule`.

---

## 2. Static Analysis — SonarCloud

The GitHub Actions workflow (`.github/workflows/ci.yml`) runs a SonarCloud
scan on every push and pull request once the `SONAR_TOKEN` secret is present.

### 2.1 Before refactoring

> _Paste the SonarCloud dashboard screenshot taken **before** the refactoring
> commit here (Bugs / Vulnerabilities / Code Smells / Coverage tiles)._

![SonarCloud dashboard — before](screenshots/sonar-before.png)

### 2.2 After refactoring

> _Paste the SonarCloud dashboard screenshot taken **after** the refactoring
> commit here._

![SonarCloud dashboard — after](screenshots/sonar-after.png)

---

## 3. Code Smells & Refactoring

SonarCloud's first analysis reported **42 code smells** (0 bugs, 0
vulnerabilities), all of severity *Major*, spread over just two rules:

| Rule | Description | Count |
|------|-------------|------:|
| `java:S6204` | `collect(Collectors.toList())` should be `Stream.toList()` | 3 |
| `java:S106` | Standard output should not be used to log | 39 |

Every change below was made against the green test suite (57 tests), so it is
behavior-preserving.

### Smell 1 — `Collectors.toList()` instead of `Stream.toList()` (`java:S6204`)

**Where:** `VehicleService`, `InMemoryRentalRepository`, `VehicleServiceTest`.

**Before:**

```java
return vehicleRepository.findAll().stream()
        .filter(Vehicle::isAvailable)
        .collect(Collectors.toList());
```

**After** — Java 16+ `Stream.toList()`; the now-unused `Collectors` import was
removed too:

```java
return vehicleRepository.findAll().stream()
        .filter(Vehicle::isAvailable)
        .toList();
```

**Decision:** Accepted — shorter, returns an unmodifiable list, and removes a
dependency on `Collectors`. Fixed all 3 occurrences → these smells disappear on
the next analysis.

### Smell 2 — Duplicated string literal / format (`java:S1192`, DRY)

**Where:** `presentation/ConsoleApp.java`, bill printing.

**Before:**

```java
System.out.printf("  Base cost : %8s USD%n", receipt.getBaseCost());
System.out.printf("  Late fee  : %8s USD%n", receipt.getLateFee());
System.out.printf("  Total     : %8s USD%n", receipt.getTotal());
```

**After** — Extract Method removes the repeated format and the `" USD"` literal:

```java
printMoneyLine("Base cost", receipt.getBaseCost());
printMoneyLine("Late fee", receipt.getLateFee());
printMoneyLine("Total", receipt.getTotal());

private void printMoneyLine(String label, BigDecimal amount) {
    System.out.printf("  %-10s %8s USD%n", label, amount);
}
```

**Decision:** Accepted — the currency format lives in one place; a change to it
now touches a single method.

### Smell 3 — "Standard output should not be used to log" (`java:S106`)

**Where:** every `System.out.println` / `printf` in `ConsoleApp` (39 issues).

**Analysis:** SonarCloud flags direct use of `System.out`. Here the class *is*
the interactive console UI — printing to standard output is its purpose, not
incidental logging. Introducing a logging framework for a teaching console app
would be over-engineering (YAGNI).

**Decision:** Reviewed and **Accepted / Won't Fix** in SonarCloud (bulk action
on the 39 issues) with the justification above. The presentation layer is also
excluded from coverage (`sonar.coverage.exclusions`) because it is exercised
manually, not by unit tests.

> **Result:** open code smells go from **42 → 39** after fixing `S6204`, then to
> **0 open** after accepting the `S106` issues — the "after" dashboard.

---

## 4. Test Coverage (JaCoCo)

Requirement: **> 80 %**. Achieved (presentation layer excluded as it is
exercised manually):

| Metric | Result |
|--------|--------|
| Tests | **57 passing** |
| Line coverage | **~95.7 %** |
| Branch coverage | **100 %** |

Regenerate locally with:

```bash
./mvnw verify        # HTML report: target/site/jacoco/index.html
```

> _Paste the JaCoCo `index.html` summary screenshot here._

![JaCoCo coverage summary](screenshots/jacoco-coverage.png)

---

## 5. UML Class Diagram

Source: [`class-diagram.puml`](class-diagram.puml). Render it with any PlantUML
tool (e.g. <https://www.plantuml.com/plantuml>) or generate it from IntelliJ by
reverse engineering, then paste the image here.

![UML class diagram](screenshots/uml-class-diagram.png)

---

## Appendix A — SonarCloud Setup Checklist

1. Make the repository **public** (SonarCloud's free tier analyses public
   repos): _Repo → Settings → Danger Zone → Change visibility → Public_.
2. Sign in to <https://sonarcloud.io/> with GitHub and **Analyze new project**;
   pick `software-project`.
3. Choose analysis method **GitHub Actions**.
4. Generate a token: _SonarCloud → My Account → Security → Generate token_.
5. Add it to GitHub: _Repo → Settings → Secrets and variables → Actions → New
   repository secret_, named **`SONAR_TOKEN`**.
6. Confirm the `sonar.organization` and `sonar.projectKey` in
   [`pom.xml`](../pom.xml) match the values SonarCloud shows for your project
   (defaults assume org `abdalqader268-maker`, key
   `abdalqader268-maker_software-project`).
7. Push (or open a PR) to `main`; the **Build and Analyze** workflow runs the
   SonarCloud scan and results appear on the SonarCloud dashboard.

## Appendix B — Generating Javadoc

```bash
./mvnw javadoc:javadoc      # output: target/site/apidocs/index.html
```
