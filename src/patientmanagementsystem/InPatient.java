
package patientmanagementsystem;

// Inpatient extends Patient class which represents a patient who has been allocated a hospital bed
// only inpatients carry ward/bed info, which is why this data lives here and not in Patient

public class InPatient {
    //adding private fields
    
     // fixed at 1 since the assumptions state there is only ever one ward in this system
    private int wardNumber;

    // holds the bed label (e.g. "B05") once allocated, or a placeholder before allocation
    private String bedNumber;
    
     // constructor calls the Patient constructor first via super()
    // this initialises all the inherited fields before we set the inpatient-specific ones
    public InPatient(String patientID, String firstName, String lastName, int age,
                      String gender, String medicalCondition) {

        // super() must be the first line, it basically 3 hands the shared fields up to Patient
        // category is hardcoded to INPATIENT here since this class only ever represents inpatients
        super(patientID, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);

        this.wardNumber = 1;              // only one ward exists per the assignment assumptions
        this.bedNumber = "Not Assigned";  // no bed allocated yet at registration time
    }
    //adding getters/setters
       // returns the ward number this inpatient belongs to
    public int getWardNumber() {
        return wardNumber; // simple getter
    }

    // returns the currently allocated bed label, or "Not Assigned" if none yet
    public String getBedNumber() {
        return bedNumber; // simple getter
    }

    // called by HospitalSystem when a bed is allocated or released for this patient
    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber; // updates the bed label tracked on this inpatient
    }
     // creating a method that overrides the base displayDetails() to show ward/bed info on top of normal patient info
    // this is the "extends the behaviour" requirement from the rubric, not a full replacement
    @Override
    public void displayDetails() {
        super.displayDetails(); // first print everything the base Patient class already prints
        System.out.println("Ward Number      : " + wardNumber);   // then print the extra ward info
        System.out.println("Bed Number       : " + bedNumber);    // then print the extra bed info
    }

    // creating a method that overrides toString() so inpatients show their bed status in list views too
    @Override
    public String toString() {
        return super.toString() + " | Bed: " + bedNumber; // reuse Patient's line, append bed info
    }
    
}
