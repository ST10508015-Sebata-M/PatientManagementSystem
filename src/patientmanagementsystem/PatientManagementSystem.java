package  patientmanagementsystem;
import java.util.Scanner;
import patientmanagementsystem.HospitalSystem;
import patientmanagementsystem.InPatient;
import patientmanagementsystem.Patient;
import patientmanagementsystem.PatientCategory;

// entry point for the hospital patient admission system
// this version wires up all five features: patient mgmt, bed mgmt, reports, categories, and runs on HospitalSystem
public class PatientManagementSystem {
 // creating an instance of the  the scanner class and the HospitalSystem
    static Scanner scanner = new Scanner(System.in);
    static HospitalSystem hospitalSystem = new HospitalSystem(); // the one engine instance for the whole app

    public static void main(String[] args) {
        int choice; // stores the user's top-level menu selection
        do {
            printMainMenu();                      // show the main menu every loop
            choice = readInt("Enter your choice: "); // capture and validate the user's pick
            switch (choice) {
                case 1 -> patientManagementMenu(); // hand off to the patient mgmt sub-menu
                case 2 -> bedManagementMenu();      // hand off to the bed mgmt sub-menu
                case 3 -> reportsMenu();            // hand off to the reports sub-menu
                case 0 -> System.out.println("Exiting system. Goodbye!"); // exit message only
                default -> System.out.println("Invalid option, try again."); // catch anything else typed
            }
        } while (choice != 0); // keep looping until the user explicitly chooses to exit
    }

    // ===================== MAIN MENU =====================

    // prints the top-level menu that routes into the three sub-menus
    private static void printMainMenu() {
        System.out.println("\n===== MediCare Hospital Patient Admission System =====");
        System.out.println("1. Patient Management");
        System.out.println("2. Bed Management");
        System.out.println("3. Reports");
        System.out.println("0. Exit");
        System.out.println("========================================================");
    }

    // ===================== FEATURE 1: PATIENT MANAGEMENT MENU =====================

    // sub-menu handling all patient CRUD operations, loops until the user backs out
    private static void patientManagementMenu() {
        int choice; // stores the sub-menu selection
        do {
            System.out.println("\n-- Patient Management --");
            System.out.println("1. Register New Patient");
            System.out.println("2. Search Patient by ID");
            System.out.println("3. Update Patient Details");
            System.out.println("4. Delete Patient");
            System.out.println("5. Display All Patients");
            System.out.println("0. Back to Main Menu");
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> registerPatient();     // capture and register a new patient
                case 2 -> searchPatient();        // look up one patient by ID
                case 3 -> updatePatient();        // edit an existing patient's details
                case 4 -> deletePatient();        // remove a patient entirely
                case 5 -> hospitalSystem.displayAllPatients(); // list every registered patient
                case 0 -> System.out.println("Returning to main menu..."); // exit this sub-menu
                default -> System.out.println("Invalid option, try again."); // anything unrecognised
            }
        } while (choice != 0); // keep showing this sub-menu until the user backs out
    }

    // captures new patient details and registers them, branching on category for Inpatients
    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        String id = readString("Patient ID: ");                 // unique identifier for this patient
        String firstName = readString("First Name: ");           // patient's first name
        String lastName = readString("Last Name: ");              // patient's last name
        int age = readInt("Age: ");                                // patient's age as a whole number
        String gender = readString("Gender: ");                   // patient's gender
        String condition = readString("Medical Condition: ");      // reason for admission/visit
        PatientCategory category = readCategory();                 // menu-driven category selection

        Patient patient; // will hold either a plain Patient or an Inpatient depending on category

        if (category == PatientCategory.INPATIENT) {
            // inpatients need the extra ward/bed fields, so build an Inpatient object instead
            patient = new InPatient(id, firstName, lastName, age, gender, condition);
        } else {
            // outpatients and emergency patients only need the base Patient fields
            patient = new Patient(id, firstName, lastName, age, gender, condition, category);
        }

        boolean success = hospitalSystem.registerPatient(patient); // attempt registration, checks for duplicate ID

        if (success) {
            System.out.println("Patient registered successfully.");
        } else {
            System.out.println("Registration failed: Patient ID already exists.");
        }
    }

    // looks up and displays a single patient's full details by ID
    private static void searchPatient() {
        String id = readString("\nEnter Patient ID to search: "); // ID to look for
        Patient patient = hospitalSystem.searchPatientById(id);    // delegate the actual search
        if (patient == null) {
            System.out.println("No patient found with ID: " + id); // nothing matched
        } else {
            System.out.println("-- Patient Found --");
            patient.displayDetails(); // polymorphic call - prints extra ward/bed info if it's an Inpatient
        }
    }

    // updates an existing patient's editable details after confirming they exist
    private static void updatePatient() {
        String id = readString("\nEnter Patient ID to update: "); // ID of the patient to edit
        Patient existing = hospitalSystem.searchPatientById(id);   // confirm the patient exists first
        if (existing == null) {
            System.out.println("No patient found with ID: " + id);
            return; // nothing to update, exit early
        }

        System.out.println("Enter new details below:");
        String firstName = readString("First Name: ");            // replacement first name
        String lastName = readString("Last Name: ");                // replacement last name
        int age = readInt("Age: ");                                  // replacement age
        String gender = readString("Gender: ");                     // replacement gender
        String condition = readString("Medical Condition: ");        // replacement medical condition

        boolean success = hospitalSystem.updatePatient(id, firstName, lastName, age, gender, condition);
        System.out.println(success ? "Patient updated successfully." : "Update failed.");
    }

    // deletes a patient after the system confirms the ID exists
    private static void deletePatient() {
        String id = readString("\nEnter Patient ID to delete: "); // ID of the patient to remove
        boolean success = hospitalSystem.deletePatient(id);        // delegate the actual removal
        System.out.println(success ? "Patient deleted successfully." : "No patient found with ID: " + id);
    }

    // ===================== FEATURE 2: BED MANAGEMENT MENU =====================

    // sub-menu handling all bed operations, loops until the user backs out
    private static void bedManagementMenu() {
        int choice; // stores the sub-menu selection
        do {
            System.out.println("\n-- Bed Management --");
            System.out.println("1. Allocate Bed to Inpatient");
            System.out.println("2. Release Bed");
            System.out.println("3. Display Ward Layout");
            System.out.println("4. Display Available Beds");
            System.out.println("5. Display Occupied Beds");
            System.out.println("0. Back to Main Menu");
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> allocateBed();                              // assign a bed to an inpatient
                case 2 -> releaseBed();                                // free up a bed on discharge
                case 3 -> hospitalSystem.displayWardLayout();          // show the full 4x5 grid
                case 4 -> hospitalSystem.displayAvailableBeds();       // show only free beds
                case 5 -> hospitalSystem.displayOccupiedBeds();        // show only taken beds
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option, try again.");
            }
        } while (choice != 0); // keep showing this sub-menu until the user backs out
    }

    // captures the patient ID and bed number, then delegates the allocation logic to HospitalSystem
    private static void allocateBed() {
        System.out.println("\n-- Allocate Bed --");
        String patientID = readString("Patient ID: ");  // which patient is being allocated a bed
        String bedNumber = readString("Bed Number (e.g. B01): "); // which bed they're being placed in
        String result = hospitalSystem.allocateBed(patientID, bedNumber); // all validation happens here
        System.out.println(result); // print whatever outcome message HospitalSystem returned
    }

    // captures the bed number to free, then delegates the release logic to HospitalSystem
    private static void releaseBed() {
        System.out.println("\n-- Release Bed --");
        String bedNumber = readString("Bed Number to release (e.g. B01): "); // which bed is being freed
        String result = hospitalSystem.releaseBed(bedNumber); // all validation happens here
        System.out.println(result); // print whatever outcome message HospitalSystem returned
    }

    // ===================== FEATURE 3: REPORTS MENU =====================

    // sub-menu handling all reporting options, loops until the user backs out
    private static void reportsMenu() {
        int choice; // stores the sub-menu selection
        do {
            System.out.println("\n-- Reports --");
            System.out.println("1. Patient Report");
            System.out.println("2. Bed Occupancy Report");
            System.out.println("3. Sort Patients by Surname");
            System.out.println("4. Sort Patients by Patient ID");
            System.out.println("0. Back to Main Menu");
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> hospitalSystem.generatePatientReport();       // full patient list + count
                case 2 -> hospitalSystem.generateBedOccupancyReport();  // occupancy numbers + percentage
                case 3 -> {
                    hospitalSystem.sortPatientsBySurname(); // sort the underlying list in place
                    System.out.println("Patients sorted by surname:");
                    hospitalSystem.displayAllPatients();     // show the now-sorted list
                }
                case 4 -> {
                    hospitalSystem.sortPatientsById(); // sort the underlying list in place
                    System.out.println("Patients sorted by Patient ID:");
                    hospitalSystem.displayAllPatients();  // show the now-sorted list
                }
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid option, try again.");
            }
        } while (choice != 0); // keep showing this sub-menu until the user backs out
    }

    // ===================== SHARED INPUT HELPERS =====================

    // prompts the user to pick a category from a numbered list, avoids free-text typos
    private static PatientCategory readCategory() {
        System.out.println("Select Category: 1. Inpatient  2. Outpatient  3. Emergency");
        int choice = readInt("Choice: "); // capture the numbered selection
        return switch (choice) {
            case 1 -> PatientCategory.INPATIENT;   // maps option 1 to the INPATIENT enum value
            case 2 -> PatientCategory.OUTPATIENT;  // maps option 2 to the OUTPATIENT enum value
            case 3 -> PatientCategory.EMERGENCY;   // maps option 3 to the EMERGENCY enum value
            default -> {
                System.out.println("Invalid choice, defaulting to OUTPATIENT.");
                yield PatientCategory.OUTPATIENT; // fallback so the program never crashes on bad input
            }
        };
    }

    // reads a non-empty string from the user, re-prompts until something is actually typed
    private static String readString(String prompt) {
        System.out.print(prompt);                    // show the field label to the user
        String input = scanner.nextLine().trim();      // read the line and strip whitespace
        while (input.isEmpty()) {                       // keep asking if they submitted nothing
            System.out.print("This field cannot be empty. " + prompt);
            input = scanner.nextLine().trim();
        }
        return input; // return the first valid, non-empty response
    }

    // reads an integer safely, catching non-numeric input instead of letting the program crash
    private static int readInt(String prompt) {
        while (true) { // loop forever until a valid number is entered
            System.out.print(prompt);                 // show the prompt each attempt
            String input = scanner.nextLine().trim();   // read the raw input as text first
            try {
                return Integer.parseInt(input); // attempt to convert text to an integer
            } catch (NumberFormatException e) {
                // parseInt throws this if the text wasn't a valid whole number
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}