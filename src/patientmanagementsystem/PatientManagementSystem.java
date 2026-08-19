package  patientmanagementsystem;
import java.util.Scanner;
import patientmanagementsystem.HospitalSystem;
import patientmanagementsystem.InPatient;
import patientmanagementsystem.Patient;
import patientmanagementsystem.PatientCategory;

// entry point for the hospital patient admission system
// this version wires up all five features: patient managment, bed managment, reports, categories, and runs on HospitalSystem
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
    case 1:
        patientManagementMenu();
        break;
    case 2:
        bedManagementMenu();
        break;
    case 3:
        reportsMenu();
        break;
    case 0:
        System.out.println("Exiting system. Goodbye!");
        break;
    default:
        System.out.println("Invalid option, try again.");
        break;
}
        } while (choice != 0); // keep looping until the user explicitly chooses to exit
    }

    // ===================== MAIN MENU =====================

    //  creating a method that prints the top-level menu that routes into the three sub-menus
    private static void printMainMenu() {
        System.out.println("\n===== MediCare Hospital Patient Admission System =====");
        System.out.println("1. Patient Management");
        System.out.println("2. Bed Management");
        System.out.println("3. Reports");
        System.out.println("0. Exit");
        System.out.println("========================================================");
    }

    // ===================== FEATURE 1: PATIENT MANAGEMENT MENU =====================

    // creating a method responsible  for sub-menu which handles all patient CRUD operations, loops until the user backs out
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
    case 1:
        registerPatient();     // capture and register a new patient
        break;
    case 2:
        searchPatient();       // look up one patient by ID
        break;
    case 3:
        updatePatient();       // edit an existing patient's details
        break;
    case 4:
        deletePatient();       // remove a patient entirely
        break;
    case 5:
        hospitalSystem.displayAllPatients(); // list every registered patient
        break;
    case 0:
        System.out.println("Returning to main menu...");
        break;
    default:
        System.out.println("Invalid option, try again.");
        break;
}
        } while (choice != 0); // keep showing this sub-menu until the user backs out
    }

    //creating a method that  captures new patient details and registers them, branching on category for Inpatients
    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        String id = readString("Patient ID: ");                 // unique identifier for this patient
        String firstName = readString("First Name: ");           // patient's first name
        String lastName = readString("Last Name: ");              // patient's last name
        int age = readAge();                               // patient's age as a whole number
        String gender = readGender();                   // patient's gender
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

    // creating a method that looks up and displays a single patient's full details by ID
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

    // creating a method that updates an existing patient's editable details after confirming they exist
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
       int age = readAge();                                 // replacement age
        String gender = readGender();                     // replacement gender
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

    // creating a method responsible for sub-menu which handles all bed operations and loops until the user backs out
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
    case 1:
        allocateBed();                              // assign a bed to an inpatient
        break;
    case 2:
        releaseBed();                               // free up a bed on discharge
        break;
    case 3:
        hospitalSystem.displayWardLayout();         // show the full 4x5 grid
        break;
    case 4:
        hospitalSystem.displayAvailableBeds();      // show only free beds
        break;
    case 5:
        hospitalSystem.displayOccupiedBeds();       // show only taken beds
        break;
    case 0:
        System.out.println("Returning to main menu...");
        break;
    default:
        System.out.println("Invalid option, try again.");
        break;
}
        } while (choice != 0); // keeps showing this sub-menu until the user backs out
    }

    // creating a method that captures the patient ID and bed number, then delegates the allocation logic to HospitalSystem
    private static void allocateBed() {
        System.out.println("\n-- Allocate Bed --");
        String patientID = readString("Patient ID: ");  // which patient is being allocated a bed
        String bedNumber = readString("Bed Number (e.g. B01): "); // which bed they're being placed in
        String result = hospitalSystem.allocateBed(patientID, bedNumber); // all validation happens here
        System.out.println(result); // print whatever outcome message HospitalSystem returned
    }

    // creating a method that captures the bed number to free, then delegates the release logic to HospitalSystem
    private static void releaseBed() {
        System.out.println("\n-- Release Bed --");
        String bedNumber = readString("Bed Number to release (e.g. B01): "); // which bed is being freed
        String result = hospitalSystem.releaseBed(bedNumber); // all validation happens here
        System.out.println(result); // print whatever outcome message HospitalSystem returned
    }

    // ===================== FEATURE 3: REPORTS MENU =====================

    //creating a method responsible for holding the sub-menu that handles all reporting options and loops until the user backs out
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
    case 1:
        hospitalSystem.generatePatientReport();       // full patient list + count
        break;
    case 2:
        hospitalSystem.generateBedOccupancyReport();  // occupancy numbers + percentage
        break;
    case 3:
        hospitalSystem.sortPatientsBySurname();       // sort the underlying list in place
        System.out.println("Patients sorted by surname:");
        hospitalSystem.displayAllPatients();          // show the now-sorted list
        break;
    case 4:
        hospitalSystem.sortPatientsById();            // sort the underlying list in place
        System.out.println("Patients sorted by Patient ID:");
        hospitalSystem.displayAllPatients();          // show the now-sorted list
        break;
    case 0:
        System.out.println("Returning to main menu...");
        break;
    default:
        System.out.println("Invalid option, try again.");
        break;
}

        } while (choice != 0); // keep showing this sub-menu until the user backs out
    }

    // ===================== SHARED INPUT HELPERS =====================

    //creating a method that  prompts the user to pick a category from a numbered list, avoids free-text typos
private static PatientCategory readCategory() {
    System.out.println("Select Category: 1. Inpatient  2. Outpatient  3. Emergency");
    int choice = readInt("Choice: "); // capture the numbered selection

    PatientCategory category;

    switch (choice) {
        case 1:
            category = PatientCategory.INPATIENT;   // maps option 1 to the INPATIENT enum value
            break;
        case 2:
            category = PatientCategory.OUTPATIENT;  // maps option 2 to the OUTPATIENT enum value
            break;
        case 3:
            category = PatientCategory.EMERGENCY;   // maps option 3 to the EMERGENCY enum value
            break;
        default:
            System.out.println("Invalid choice, defaulting to OUTPATIENT.");
            category = PatientCategory.OUTPATIENT;  // fallback so the program never crashes on bad input
            break;
    }

    return category;
}


    // creating a method that reads a non-empty string from the user, re-prompts until something is actually typed
    private static String readString(String prompt) {
        System.out.print(prompt);                    // show the field label to the user
        String input = scanner.nextLine().trim();      // read the line and strip whitespace
        while (input.isEmpty()) {                       // keep asking if they submitted nothing
            System.out.print("This field cannot be empty. " + prompt);
            input = scanner.nextLine().trim();
        }
        return input; // return the first valid, non-empty response
    }

    // creating a private method that reads an integer safely, catching non-numeric input instead of letting the program crash
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
    // creating a method that reads age with range validation, re-prompts until a realistic value is entered
private static int readAge() {
    int age;
    do {
        age = readInt("Age: ");
        if (age < 0 || age > 120) {
            System.out.println("Please enter a realistic age (0-120).");
        }
    } while (age < 0 || age > 120);
    return age;
}
// creating a method with menu-driven gender selection, avoids free-text typos.
private static String readGender() {
    System.out.println("Select Gender: 1. Male  2. Female  3. Other");
    int choice = readInt("Choice: ");

    String gender; // declare variable first

    switch (choice) {
        case 1:
            gender = "Male";
            break;
        case 2:
            gender = "Female";
            break;
        case 3:
            gender = "Other";
            break;
        default:
            System.out.println("Invalid choice, defaulting to Other.");
            gender = "Other";
            break;
    }

    return gender; // return after switch
}

}