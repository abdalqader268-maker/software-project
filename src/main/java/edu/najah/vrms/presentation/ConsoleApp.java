package edu.najah.vrms.presentation;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import edu.najah.vrms.billing.StandardPricingStrategy;
import edu.najah.vrms.domain.Car;
import edu.najah.vrms.domain.ElectricVehicle;
import edu.najah.vrms.domain.Manager;
import edu.najah.vrms.domain.Motorcycle;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.RentalReceipt;
import edu.najah.vrms.domain.Truck;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.notification.ConsoleEmailService;
import edu.najah.vrms.notification.EmailReminderObserver;
import edu.najah.vrms.notification.RentalExpiryPublisher;
import edu.najah.vrms.persistence.InMemoryManagerRepository;
import edu.najah.vrms.persistence.InMemoryRentalRepository;
import edu.najah.vrms.persistence.InMemoryVehicleRepository;
import edu.najah.vrms.persistence.RentalRepository;
import edu.najah.vrms.persistence.VehicleRepository;
import edu.najah.vrms.service.AuthService;
import edu.najah.vrms.service.ExpiryReminderService;
import edu.najah.vrms.service.RentalService;
import edu.najah.vrms.service.ReturnService;
import edu.najah.vrms.service.VehicleService;
import edu.najah.vrms.validation.DurationLimitRule;
import edu.najah.vrms.validation.NoOverlapRule;
import edu.najah.vrms.validation.RentalValidator;
import edu.najah.vrms.validation.TypeSpecificRule;

/**
 * Console entry point of the Vehicle Rental Management System.
 * <p>
 * Wires the layers together (presentation, service, domain, persistence),
 * seeds demo data and exposes a small interactive menu covering the Phase 1
 * and Phase 2 user stories.
 */
public final class ConsoleApp {

    /** Smallest allowed rental length in days. */
    private static final int MIN_RENTAL_DAYS = 1;

    /** Largest allowed rental length in days. */
    private static final int MAX_RENTAL_DAYS = 30;

    /** Look-ahead window used when generating expiry reminders. */
    private static final int REMINDER_DAYS_AHEAD = 3;

    /** Reads the manager's menu choices. */
    private final Scanner scanner;

    /** Authentication service (US1.1, US1.2). */
    private final AuthService authService;

    /** Catalog service (US1.3). */
    private final VehicleService vehicleService;

    /** Rental workflow service (US2.x, US5.x). */
    private final RentalService rentalService;

    /** Returns and billing service (US4.x). */
    private final ReturnService returnService;

    /** Reminder service (US3.1). */
    private final ExpiryReminderService reminderService;

    /**
     * Builds the application object graph.
     *
     * @param scanner source of the interactive input
     */
    private ConsoleApp(Scanner scanner) {
        this.scanner = scanner;

        InMemoryManagerRepository managerRepository = new InMemoryManagerRepository();
        VehicleRepository vehicleRepository = new InMemoryVehicleRepository();
        RentalRepository rentalRepository = new InMemoryRentalRepository();
        Clock clock = Clock.systemDefaultZone();

        this.authService = new AuthService(managerRepository);
        this.vehicleService = new VehicleService(vehicleRepository);
        this.rentalService = new RentalService(
                vehicleRepository,
                rentalRepository,
                new RentalValidator(Arrays.asList(
                        new DurationLimitRule(clock, MIN_RENTAL_DAYS, MAX_RENTAL_DAYS),
                        new NoOverlapRule(rentalRepository),
                        new TypeSpecificRule())),
                authService);
        this.returnService = new ReturnService(
                vehicleRepository, rentalRepository, authService,
                new StandardPricingStrategy());

        RentalExpiryPublisher publisher = new RentalExpiryPublisher();
        publisher.subscribe(new EmailReminderObserver(new ConsoleEmailService()));
        this.reminderService = new ExpiryReminderService(rentalRepository, publisher, clock);

        seedData(managerRepository, vehicleRepository);
    }

    /**
     * Application entry point.
     *
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleApp(scanner).run();
        }
    }

    /**
     * Stores the demo manager account and a small mixed demo fleet covering
     * every vehicle type (US5.1).
     *
     * @param managerRepository target for the demo manager
     * @param vehicleRepository target for the demo fleet
     */
    private void seedData(InMemoryManagerRepository managerRepository,
                          VehicleRepository vehicleRepository) {
        managerRepository.save(new Manager("admin", "admin123", "Fleet Manager"));

        vehicleRepository.save(new Car("V-1", "NAB-1234", "Toyota", "Corolla",
                new BigDecimal("35.00"), VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Car("V-2", "NAB-5678", "Hyundai", "Tucson",
                new BigDecimal("55.00"), VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Motorcycle("V-3", "NAB-9012", "Honda", "CB500",
                new BigDecimal("40.00"), VehicleStatus.AVAILABLE));
        vehicleRepository.save(new Truck("V-4", "NAB-3456", "Volvo", "FH16",
                new BigDecimal("120.00"), VehicleStatus.AVAILABLE));
        vehicleRepository.save(new ElectricVehicle("V-5", "NAB-7890", "Tesla", "Model 3",
                new BigDecimal("90.00"), VehicleStatus.AVAILABLE, 85));
    }

    /**
     * Runs the interactive menu loop until the manager chooses to exit.
     */
    private void run() {
        System.out.println("=== Vehicle Rental Management System ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                running = handleChoice(choice);
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    /**
     * Prints the main menu together with the session state.
     */
    private void printMenu() {
        String session = authService.isAuthenticated()
                ? "logged in as " + authService.getCurrentManager().getUsername()
                : "not logged in";
        System.out.println();
        System.out.println("[" + session + "]");
        System.out.println("1) Login");
        System.out.println("2) Logout");
        System.out.println("3) View available vehicles");
        System.out.println("4) Rent a vehicle");
        System.out.println("5) Return a vehicle");
        System.out.println("6) Generate expiry reminders");
        System.out.println("0) Exit");
        System.out.print("> ");
    }

    /**
     * Dispatches one menu choice.
     *
     * @param choice the entered menu number
     * @return {@code false} when the application should exit
     */
    private boolean handleChoice(String choice) {
        switch (choice) {
            case "1":
                login();
                return true;
            case "2":
                authService.logout();
                System.out.println("Logged out.");
                return true;
            case "3":
                listAvailableVehicles();
                return true;
            case "4":
                rentVehicle();
                return true;
            case "5":
                returnVehicle();
                return true;
            case "6":
                generateReminders();
                return true;
            case "0":
                return false;
            default:
                System.out.println("Unknown option: " + choice);
                return true;
        }
    }

    /**
     * Interactive login flow (US1.1).
     */
    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        Manager manager = authService.login(username, password);
        System.out.println("Welcome, " + manager.getFullName() + "!");
    }

    /**
     * Prints the available vehicles (US1.3).
     */
    private void listAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles are available right now.");
            return;
        }
        System.out.println("Available vehicles:");
        for (Vehicle vehicle : vehicles) {
            System.out.printf("  %-5s %-12s %-8s %-10s %-10s %8s USD/day%n",
                    vehicle.getId(), vehicle.getPlateNumber(), vehicle.getBrand(),
                    vehicle.getModel(), vehicle.getCategory(), vehicle.getDailyRate());
        }
    }

    /**
     * Interactive rent flow (US2.1 - US2.3, US5.2).
     */
    private void rentVehicle() {
        System.out.print("Vehicle id: ");
        String vehicleId = scanner.nextLine().trim();
        System.out.print("Customer name: ");
        String customerName = scanner.nextLine().trim();
        System.out.print("Customer e-mail: ");
        String customerEmail = scanner.nextLine().trim();
        int customerAge = readInt("Customer age: ");
        boolean specialLicense = readYesNo("Holds special (truck) license? (y/n): ");
        LocalDate start = readDate("Start date (YYYY-MM-DD): ");
        LocalDate end = readDate("End date   (YYYY-MM-DD): ");

        Rental rental = rentalService.rentVehicle(
                vehicleId, customerName, customerEmail, start, end,
                customerAge, specialLicense);
        System.out.println("Rental " + rental.getId() + " created for "
                + rental.getCustomerName() + " until " + rental.getEndDate() + ".");
    }

    /**
     * Interactive return flow (US4.1 - US4.3).
     */
    private void returnVehicle() {
        System.out.print("Rental id: ");
        String rentalId = scanner.nextLine().trim();
        LocalDate returnDate = readDate("Actual return date (YYYY-MM-DD): ");

        RentalReceipt receipt = returnService.returnVehicle(rentalId, returnDate);
        System.out.println("Vehicle returned. Bill for " + receipt.getRentalId() + ":");
        printMoneyLine("Base cost", receipt.getBaseCost());
        printMoneyLine("Late fee", receipt.getLateFee());
        printMoneyLine("Total", receipt.getTotal());
    }

    /**
     * Prints one aligned money line of a bill.
     *
     * @param label  the line label, e.g. {@code "Base cost"}
     * @param amount the amount to show
     */
    private void printMoneyLine(String label, BigDecimal amount) {
        System.out.printf("  %-10s %8s USD%n", label, amount);
    }

    /**
     * Reads a date from the console, re-prompting on invalid input.
     *
     * @param prompt text shown before reading
     * @return the parsed date
     */
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, expected YYYY-MM-DD.");
            }
        }
    }

    /**
     * Reads a non-negative integer from the console, re-prompting on invalid
     * input.
     *
     * @param prompt text shown before reading
     * @return the parsed integer
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a yes/no answer from the console.
     *
     * @param prompt text shown before reading
     * @return {@code true} when the manager answers yes
     */
    private boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String raw = scanner.nextLine().trim().toLowerCase();
        return raw.startsWith("y");
    }

    /**
     * Generates expiry reminders for the next few days (US3.1).
     */
    private void generateReminders() {
        List<Rental> reminded = reminderService.generateReminders(REMINDER_DAYS_AHEAD);
        System.out.println(reminded.size() + " reminder(s) generated.");
    }
}
