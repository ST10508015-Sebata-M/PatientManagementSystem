
package patientmanagementsystem;
//importing the array list from the java utils
import java.util.ArrayList;

// central engine of the system - owns all patient records and the ward's bed layout
// Main only calls into this class, it never manipulates patients or beds directly
public class HospitalSystem {

    // stores every registered patient regardless of category
    private ArrayList<Patient> patients;

    // 4x5 grid representing the physical ward layout, holds 20 Bed objects
    private Bed[][] wardLayout;

    // fixed dimensions of the ward per the assignment's assumptions
    private static final int ROWS = 4;
    private static final int COLUMNS = 5;

    // constructor initialises the empty patient list and builds the 20-bed layout
    public HospitalSystem() {
        patients = new ArrayList<>();  // start with zero patients registered
        wardLayout = new Bed[ROWS][COLUMNS]; // create the empty 4x5 grid
        buildWardLayout(); // fill the grid with labelled Bed objects B01-B20
    }

    // ===================== WARD SETUP =====================

    // fills the 2D array with Bed objects labelled B01 through B20 in row-major order
    private void buildWardLayout() {
        int bedCounter = 1; // tracks the running bed number across rows and columns
        for (int row = 0; row < ROWS; row++) {           // walk down each row
            for (int col = 0; col < COLUMNS; col++) {     // walk across each column in that row
                // pad single-digit numbers with a leading zero so labels read B01, not B1
                String label = String.format("B%02d", bedCounter);
                wardLayout[row][col] = new Bed(label); // place a new empty bed at this position
                bedCounter++; // move on to the next bed number
            }
        }
    }

    // ===================== FEATURE 1: PATIENT MANAGEMENT =====================

    // creating a method that registers a new patient, rejects duplicate patient IDs
    // returns true if registration succeeded, false if the ID already exists
    public boolean registerPatient(Patient patient) {
        if (searchPatientById(patient.getPatientID()) != null) { // check for an existing match first
            return false; // duplicate ID found, refuse registration
        }
        patients.add(patient); // no duplicate found, safe to add
        return true; // registration succeeded
    }

    // creating a method that  searches the list for a patient matching the given ID, case-insensitive
    // returns null if no match found, callers must check for that
    public Patient searchPatientById(String patientID) {
        for (Patient p : patients) { // walk through every registered patient
            if (p.getPatientID().equalsIgnoreCase(patientID)) { // compare ignoring case
                return p; // match found, return it immediately
            }
        }
        return null; // walked the whole list, nothing matched
    }

    // creating a method that updates an existing patient's mutable fields
    // ID and category are intentionally excluded - those should not change after registration
    public boolean updatePatient(String patientID, String firstName, String lastName,
                                  int age, String gender, String medicalCondition) {
        Patient patient = searchPatientById(patientID); // locate the patient first
        if (patient == null) {
            return false; // nothing to update if the patient doesn't exist
        }
        patient.setFirstName(firstName);           // overwrite first name
        patient.setLastName(lastName);              // overwrite last name
        patient.setAge(age);                        // overwrite age
        patient.setGender(gender);                  // overwrite gender
        patient.setMedicalCondition(medicalCondition); // overwrite medical condition
        return true; // update succeeded
    }

    // creating a method that removes a patient from the system entirely
    // returns true if a patient was found and removed, false if the ID didn't exist
    public boolean deletePatient(String patientID) {
        Patient patient = searchPatientById(patientID); // locate the patient first
        if (patient == null) {
            return false; // nothing to delete
        }
        patients.remove(patient); // remove the matched object from the list
        return true; // deletion succeeded
    }

    // creating a method that prints every registered patient in a numbered, readable list
    public void displayAllPatients() {
        if (patients.isEmpty()) { // handle the empty case explicitly rather than printing nothing
            System.out.println("No patients registered yet.");
            return; // nothing further to print
        }
        System.out.println("---- Registered Patients (" + patients.size() + ") ----");
        int count = 1; // used purely for numbering the printed list
        for (Patient p : patients) { // walks through every patient
            System.out.println(count + ". " + p.toString()); // print index + summary line
            count++; // increment the display counter
        }
    }

    //this method  gives Main and the JUnit tests direct read access to the patient list
    public ArrayList<Patient> getPatients() {
        return patients; // return the live list, used for reports and sorting
    }

    // ===================== FEATURE 2: BED MANAGEMENT =====================

    // created a private method searches the 2D ward layout for a bed matching the given label, e.g. "B05"
    // returns null if no bed with that label exists
    private Bed findBedByNumber(String bedNumber) {
        for (int row = 0; row < ROWS; row++) {              // outer loop walks each row
            for (int col = 0; col < COLUMNS; col++) {        // inner loop walks each column in the row
                if (wardLayout[row][col].getBedNumber().equalsIgnoreCase(bedNumber)) {
                    return wardLayout[row][col]; // found the matching bed, return it
                }
            }
        }
        return null; // no bed in the whole grid matched that label
    }

    // created a private method that checks whether a given patient ID currently occupies any bed in the ward
    // used to stop the same inpatient being allocated two beds at once
    private boolean patientAlreadyHasBed(String patientID) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Bed bed = wardLayout[row][col]; // grab the current bed being checked
                if (bed.isOccupied() && bed.getOccupyingPatientID().equalsIgnoreCase(patientID)) {
                    return true; // this patient is already linked to a bed somewhere
                }
            }
        }
        return false; // no bed in the ward is linked to this patient
    }

    // creating a method that allocates a specific bed to a specific inpatient
    // returns a result message string so Main can display exactly why it succeeded or failed
    public String allocateBed(String patientID, String bedNumber) {
        Patient patient = searchPatientById(patientID); // step 1: does this patient exist at all

        if (patient == null) {
            return "Allocation failed: No patient found with ID " + patientID;
        }
        if (!(patient instanceof Inpatient)) { // step 2: only inpatients are allowed beds
            return "Allocation failed: Only inpatients can be allocated a bed.";
        }
        if (patientAlreadyHasBed(patientID)) { // step 3: stop duplicate allocations for one patient
            return "Allocation failed: Patient already occupies a bed.";
        }

        Bed bed = findBedByNumber(bedNumber); // step 4: does the requested bed even exist
        if (bed == null) {
            return "Allocation failed: No bed found with number " + bedNumber;
        }
        if (bed.isOccupied()) { // step 5: is the requested bed already taken
            return "Allocation failed: Bed " + bedNumber + " is already occupied.";
        }

        // all checks passed - safe to allocate
        bed.setOccupied(true);                 // mark the bed as taken
        bed.setOccupyingPatientID(patientID);  // link the bed to this patient's ID

        InPatient inpatient = (InPatient) patient; // safe casting  cause we already confirmed instanceof above
        inpatient.setBedNumber(bedNumber); // keep the patient's own record of their bed in sync

        return "Bed " + bedNumber + " successfully allocated to patient " + patientID;
    }

    // releases a bed when a patient is discharged
    // returns a result message so Main can show exactly what happened
    public String releaseBed(String bedNumber) {
        Bed bed = findBedByNumber(bedNumber); // locate the bed first

        if (bed == null) {
            return "Release failed: No bed found with number " + bedNumber;
        }
        if (!bed.isOccupied()) { // can't release a bed that was never occupied
            return "Release failed: Bed " + bedNumber + " is already available.";
        }

        String freedPatientID = bed.getOccupyingPatientID(); // remember who was in the bed
        Patient patient = searchPatientById(freedPatientID); // find that patient's record

        // sync the patient's own bedNumber field back to unassigned, if they still exist in the system
        if (patient instanceof Inpatient) {
            ((Inpatient) patient).setBedNumber("Not Assigned");
        }

        bed.setOccupied(false);          // free up the bed
        bed.setOccupyingPatientID(null); // clear the link to the old patient

        return "Bed " + bedNumber + " successfully released.";
    }

    // prints the full 4x5 ward grid exactly as it's laid out physically
    public void displayWardLayout() {
        System.out.println("---- Ward Layout ----");
        for (int row = 0; row < ROWS; row++) {          // outer loop moves down each row
            StringBuilder rowLine = new StringBuilder(); // builds one printable row at a time
            for (int col = 0; col < COLUMNS; col++) {    // inner loop moves across each column
                Bed bed = wardLayout[row][col]; // current bed being added to this row's line
                String status = bed.isOccupied() ? "OCC" : "AVL"; // short status tag per bed
                rowLine.append(bed.getBedNumber()).append("(").append(status).append(")  ");
            }
            System.out.println(rowLine.toString()); // print the completed row
        }
    }

    // prints only the beds that are currently free
    public void displayAvailableBeds() {
        System.out.println("---- Available Beds ----");
        boolean anyAvailable = false; // tracks whether we found at least one free bed
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Bed bed = wardLayout[row][col]; // current bed being checked
                if (!bed.isOccupied()) { // only print it if it's free
                    System.out.println(bed.toString());
                    anyAvailable = true; // flag that we found at least one
                }
            }
        }
        if (!anyAvailable) { // handle the fully-occupied ward case explicitly
            System.out.println("No beds currently available.");
        }
    }

    // prints only the beds that are currently occupied
    public void displayOccupiedBeds() {
        System.out.println("---- Occupied Beds ----");
        boolean anyOccupied = false; // tracks whether we found at least one occupied bed
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Bed bed = wardLayout[row][col]; // current bed being checked
                if (bed.isOccupied()) { // only print it if it's taken
                    System.out.println(bed.toString());
                    anyOccupied = true; // flag that we found at least one
                }
            }
        }
        if (!anyOccupied) { // handle the fully-empty ward case explicitly
            System.out.println("No beds currently occupied.");
        }
    }

    // counts how many beds in the ward are currently occupied
    // used by both the bed report and the occupancy percentage calculation
    public int countOccupiedBeds() {
        int count = 0; // running total of occupied beds
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (wardLayout[row][col].isOccupied()) { // check each bed's status
                    count++; // increment when we find an occupied one
                }
            }
        }
        return count; // final tally after checking all 20 beds
    }

    // returns the total number of beds in the ward, used for percentage math and reports
    public int getTotalBeds() {
        return ROWS * COLUMNS; // 4 x 5 = 20, kept as a calculation rather than a hardcoded 20
    }

    // ===================== FEATURE 3: REPORTS =====================

    // prints the full patient report - reuses displayAllPatients plus a summary line
    public void generatePatientReport() {
        System.out.println("\n===== Patient Report =====");
        displayAllPatients(); // reuse the existing formatted patient list
        System.out.println("Total Registered Patients: " + patients.size());
    }

    // prints occupancy numbers and the calculated occupancy percentage
    public void generateBedOccupancyReport() {
        int occupied = countOccupiedBeds();     // how many beds are taken right now
        int total = getTotalBeds();             // total beds in the ward, always 20
        int available = total - occupied;       // whatever isn't occupied is available
        double occupancyPercentage = (occupied / (double) total) * 100; // cast to double before dividing

        System.out.println("\n===== Bed Occupancy Report =====");
        System.out.println("Total Beds        : " + total);
        System.out.println("Occupied Beds      : " + occupied);
        System.out.println("Available Beds     : " + available);
        // format the percentage to 2 decimal places for a clean report line
        System.out.printf("Occupancy Percentage: %.2f%%%n", occupancyPercentage);
    }

    // ===================== SORTING (LU1: Bubble Sort) =====================

    // sorts the patient list by last name ascending using bubble sort with an early-exit flag
    // the swapped flag is the "modified for improved efficiency" version taught in LU1
    public void sortPatientsBySurname() {
        int n = patients.size(); // total number of patients to sort
        for (int pass = 0; pass < n - 1; pass++) { // outer loop controls number of passes
            boolean swapped = false; // reset the flag at the start of every pass

            for (int i = 0; i < n - pass - 1; i++) { // inner loop compares adjacent elements
                String currentSurname = patients.get(i).getLastName();
                String nextSurname = patients.get(i + 1).getLastName();

                // compareTo > 0 means the current surname comes after the next one alphabetically
                if (currentSurname.compareToIgnoreCase(nextSurname) > 0) {
                    // swap the two Patient objects in the ArrayList
                    Patient temp = patients.get(i);
                    patients.set(i, patients.get(i + 1));
                    patients.set(i + 1, temp);
                    swapped = true; // record that a swap happened this pass
                }
            }

            if (!swapped) { // if nothing swapped, the list is already fully sorted
                break; // exit early instead of running the remaining unnecessary passes
            }
        }
    }

    // sorts the patient list by patient ID ascending, same bubble sort pattern as above
    public void sortPatientsById() {
        int n = patients.size(); // total number of patients to sort
        for (int pass = 0; pass < n - 1; pass++) { // outer loop controls number of passes
            boolean swapped = false; // reset the flag at the start of every pass

            for (int i = 0; i < n - pass - 1; i++) { // inner loop compares adjacent elements
                String currentID = patients.get(i).getPatientID();
                String nextID = patients.get(i + 1).getPatientID();

                if (currentID.compareToIgnoreCase(nextID) > 0) { // out of order, needs a swap
                    Patient temp = patients.get(i);
                    patients.set(i, patients.get(i + 1));
                    patients.set(i + 1, temp);
                    swapped = true; // record that a swap happened this pass
                }
            }

            if (!swapped) { // already sorted, no point running further passes
                break;
            }
        }
    }
}