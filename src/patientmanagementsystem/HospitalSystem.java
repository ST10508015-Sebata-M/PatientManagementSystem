
package patientmanagementsystem;
//importing a arraylist class
import java.util.ArrayList;

// creating a method that manages all patient records and bed operations for the ward
// this section only handles patient management (feature 1) - bed logic gets added next

public class HospitalSystem {
    //creating a private instance of the ArrayList class
      private ArrayList<Patient> patients;
    
      // creating the in-memory patient list, which is  used for quick register/search/update/delete (Feature 1)
      public HospitalSystem() {
    patients = new ArrayList<>();
}
      
       //creating a method that  registers a new patient and rejects duplicate patient IDs
    // returns true if registration succeeded, false if ID already exists
    public boolean registerPatient(Patient patient) {
        if (searchPatientById(patient.getPatientID()) != null) {
            return false;
        }
        patients.add(patient);
        return true;
    }
    
    //creating a method that  searches the array list for a patient matching the given ID
    // returns null if no match found, caller must handle that case
    public Patient searchPatientById(String patientID) {
        for (Patient p : patients) {
            if (p.getPatientID().equalsIgnoreCase(patientID)) {
                return p;
            }
        }
        return null;
    }
    
     // creating a method that updates an existing patient's mutable fields (not ID, not category)
    // returns true if patient was found and updated, false otherwise
    public boolean updatePatient(String patientID, String firstName, String lastName,
                                  int age, String gender, String medicalCondition) {
        Patient patient = searchPatientById(patientID);
        if (patient == null) {
            return false;
        }
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setMedicalCondition(medicalCondition);
        return true;
    }
    
      // creating a method that removes a patient from the system by ID
    // returns true if a patient was removed, false if ID not found
    public boolean deletePatient(String patientID) {
        Patient patient = searchPatientById(patientID);
        if (patient == null) {
            return false;
        }
        patients.remove(patient);
        return true;
    }
    
      // creating a method that prints every registered patient in a numbered and readable list
    public void displayAllPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients registered yet.");
            return;
        }
        System.out.println("---- Registered Patients (" + patients.size() + ") ----");
        int count = 1;
        for (Patient p : patients) {
            System.out.println(count + ". " + p.toString());
            count++;
        }
    }
    // creating a method gives Main class (or JUnit tests) read access to the raw list when needed
    // e.g. for sorting and reports later
    public ArrayList<Patient> getPatients() {
        return patients;
    }
    
}
