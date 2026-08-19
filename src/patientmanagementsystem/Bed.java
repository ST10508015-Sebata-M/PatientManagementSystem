
package patientmanagementsystem;
// This class represents a single bed inside the hospital ward
// used to build the 4x5 ward layout as a 2D array of Bed objects instead of raw strings
// this gives us a cleaner OOP design and somewhere to store which patient occupies it

public class Bed {
    //creating private fields which can be accessed through getter/setters
    
     // the bed's display label, e.g. "B01" through "B20"
    private String bedNumber;

    // tracks whether this bed currently has a patient in it
    private boolean occupied;

    // stores the ID of the patient occupying this bed, null when the bed is free
    private String occupyingPatientID;
    
    // adding a constructor that runs when the ward layout is first built
    // every bed starts empty and unoccupied
    public Bed(String bedNumber) {
        this.bedNumber = bedNumber;   // assign the fixed label passed in from the ward builder
        this.occupied = false;        // beds start empty by default
        this.occupyingPatientID = null; // no patient assigned yet
    }
    //adding getter/setters
    
      // returns this bed's label, e.g. "B07"
    public String getBedNumber() {
        return bedNumber; // simple getter, no logic needed
    }

    // returns true if a patient currently occupies this bed
    public boolean isOccupied() {
        return occupied; // simple getter for the occupied flag
    }

    // used by HospitalSystem to flip the bed's status during allocate/release
    public void setOccupied(boolean occupied) {
        this.occupied = occupied; // updates the occupied flag directly
    }

    // returns the ID of whichever patient is in this bed, or null if empty
    public String getOccupyingPatientID() {
        return occupyingPatientID; // simple getter
    }
    
     // used by HospitalSystem to link/unlink a patient ID to this bed
    public void setOccupyingPatientID(String occupyingPatientID) {
        this.occupyingPatientID = occupyingPatientID; // updates the linked patient ID
    }

    // gives a readable one-line summary of the bed's current state
    // used when printing the ward layout and bed reports
    @Override
    public String toString() {
        // if occupied, show which patient is in it, otherwise show it's free
        if (occupied) {
            return bedNumber + " [Occupied - Patient: " + occupyingPatientID + "]";
        } else {
            return bedNumber + " [Available]";
        }
    }
}
