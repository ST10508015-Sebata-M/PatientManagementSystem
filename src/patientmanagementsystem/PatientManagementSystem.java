
package patientmanagementsystem;

import java.util.Scanner;

// entry point for the hospital patient admission system
// this version only wires up feature 1 (patient management) - bed/report menus get added next
public class PatientManagementSystem {

    static Scanner scanner = new Scanner(System.in);
    static HospitalSystem hospitalSystem = new HospitalSystem();

    public static void main(String[] args) {
        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");
switch (choice) {
    case 1:
        registerPatient();
        break;
    case 2:
        searchPatient();
        break;
    case 3:
        updatePatient();
        break;
    case 4:
        deletePatient();
        break;
    case 5:
        hospitalSystem.displayAllPatients();
        break;
    case 0:
        System.out.println("Exiting system. Goodbye!");
        break;
    default:
        System.out.println("Invalid option, try again.");
        break;
}
        } while (choice != 0);
    }

    // prints the main patient management menu
    private static void printMenu() {
        System.out.println("\n===== MediCare Hospital - Patient Management =====");
        System.out.println("1. Register New Patient");
        System.out.println("2. Search Patient by ID");
        System.out.println("3. Update Patient Details");
        System.out.println("4. Delete Patient");
        System.out.println("5. Display All Patients");
        System.out.println("0. Exit");
        System.out.println("====================================================");
    }

    // captures new patient details and registers them, rejecting duplicate IDs
    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        String id = readString("Patient ID: ");
        String firstName = readString("First Name: ");
        String lastName = readString("Last Name: ");
        int age = readInt("Age: ");
        String gender = readString("Gender: ");
        String condition = readString("Medical Condition: ");
        PatientCategory category = readCategory();

        Patient patient = new Patient(id, firstName, lastName, age, gender, condition, category);
        boolean success = hospitalSystem.registerPatient(patient);

        if (success) {
            System.out.println("Patient registered successfully.");
        } else {
            System.out.println("Registration failed: Patient ID already exists.");
        }
    }

    // looks up and displays a single patient by ID
    private static void searchPatient() {
        String id = readString("\nEnter Patient ID to search: ");
        Patient patient = hospitalSystem.searchPatientById(id);
        if (patient == null) {
            System.out.println("No patient found with ID: " + id);
        } else {
            System.out.println("-- Patient Found --");
            patient.displayDetails();
        }
    }

    // updates an existing patient's details after confirming they exist
    private static void updatePatient() {
        String id = readString("\nEnter Patient ID to update: ");
        Patient existing = hospitalSystem.searchPatientById(id);
        if (existing == null) {
            System.out.println("No patient found with ID: " + id);
            return;
        }

        System.out.println("Enter new details below:");
        String firstName = readString("First Name: ");
        String lastName = readString("Last Name: ");
        int age = readInt("Age: ");
        String gender = readString("Gender: ");
        String condition = readString("Medical Condition: ");

        boolean success = hospitalSystem.updatePatient(id, firstName, lastName, age, gender, condition);
        System.out.println(success ? "Patient updated successfully." : "Update failed.");
    }

    // deletes a patient after confirming they exist
    private static void deletePatient() {
        String id = readString("\nEnter Patient ID to delete: ");
        boolean success = hospitalSystem.deletePatient(id);
        System.out.println(success ? "Patient deleted successfully." : "No patient found with ID: " + id);
    }

    // prompts the user to pick a category from a numbered list, avoids typos from free text
    private static PatientCategory readCategory() {
        System.out.println("Select Category: 1. Inpatient  2. Outpatient  3. Emergency");
        int choice = readInt("Choice: ");
        return switch (choice) {
            case 1 -> PatientCategory.INPATIENT;
            case 2 -> PatientCategory.OUTPATIENT;
            case 3 -> PatientCategory.EMERGENCY;
            default -> {
                System.out.println("Invalid choice, defaulting to OUTPATIENT.");
                yield PatientCategory.OUTPATIENT;
            }
        };
    }

    // reads a non-empty string from the user, re-prompts on blank input
    private static String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print("This field cannot be empty. " + prompt);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    // reads an integer safely, catches non-numeric input instead of crashing
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}