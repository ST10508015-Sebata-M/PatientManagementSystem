package patientmanagementsystem;
//I have an enum  method representing the three categories a patient can fall under
// used across Patient, Inpatient, and HospitalSystem to enforce valid categories only
public enum PatientCategory {
    INPATIENT,
    OUTPATIENT,
    EMERGENCY
}