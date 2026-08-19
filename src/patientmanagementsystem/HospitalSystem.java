
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
    
    //creating a method that  searches the list for a patient matching the given ID
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
}
