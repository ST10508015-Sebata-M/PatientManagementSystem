
package patientmanagementsystem;

// base class representing a hospital patient
// holds core patient info shared by all the categories (inpatient, outpatient, emergency)
public class Patient {
    //Declaring fields that are private to enforce encapsulation - they can only be accessed  through getters/setters
    private String patientID;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;
   
   // creating constructor to  set all fields on patient creation
    public Patient(String patientID, String firstName, String lastName, int age,
                   String gender, String medicalCondition, PatientCategory category) {
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.medicalCondition = medicalCondition;
        this.category = category;
    }
   //Adding getters and setters for my private fields
    
     // patient ID has no setter - once assigned it should not change
    public String getPatientID() {
        return patientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
     public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }
    
      public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public PatientCategory getCategory() {
        return category;
    }

    public void setCategory(PatientCategory category) {
        this.category = category;
    }
    
    
    //  creating a method that prints a formatted block of this patient's details
    // Inpatient overrides this to add ward/bed info on top of what's printed here
    public void displayDetails() {
        System.out.println("Patient ID       : " + patientID);
        System.out.println("Name             : " + firstName + " " + lastName);
        System.out.println("Age              : " + age);
        System.out.println("Gender           : " + gender);
        System.out.println("Medical Condition: " + medicalCondition);
        System.out.println("Category         : " + category);
    }

    // Concise single-line summary used in list views and logs: "ID | First Last | Age | Gender | Category"
    @Override
    public String toString() {
        return patientID + " | " + firstName + " " + lastName + " | " + age +
               " | " + gender + " | " + category;
    }
}
