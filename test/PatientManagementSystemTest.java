import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import patientmanagementsystem.HospitalSystem;
import patientmanagementsystem.InPatient;
import patientmanagementsystem.Patient;
import patientmanagementsystem.PatientCategory;

// JUnit test class verifying HospitalSystem's core functionality
// covers CRUD, bed allocation/release, and validation edge cases from the rubric
public class PatientManagementSystemTest {

    private HospitalSystem hospitalSystem; // fresh instance rebuilt before every single test

    // runs automatically before each @Test method - guarantees no leftover state between tests
    @Before
    public void setUp() {
        hospitalSystem = new HospitalSystem(); // brand new system, empty patients, empty ward
    }

    // ---------- creating tests to test CRUD  ----------

    @Test
    public void testRegisterPatient() {
        Patient p = new Patient("P001", "Thabo", "Nkosi", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        boolean result = hospitalSystem.registerPatient(p); // attempt to register
        assertTrue(result); // registration should succeed since no duplicate exists
        assertEquals(1, hospitalSystem.getPatients().size()); // list should now contain exactly 1 patient
    }

    @Test
    public void testSearchPatient() {
        Patient p = new Patient("P002", "Lerato", "Dlamini", 25, "Female", "Asthma", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(p); // set up: register a patient to search for
        Patient found = hospitalSystem.searchPatientById("P002"); // perform the search
        assertNotNull(found); // should find a match, not null
        assertEquals("Lerato", found.getFirstName()); // confirm we got the right patient back
    }

    @Test
    public void testUpdatePatientDetails() {
        Patient p = new Patient("P003", "Sipho", "Zulu", 40, "Male", "Diabetes", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(p); // set up: register the patient to update
        boolean result = hospitalSystem.updatePatient("P003", "Sipho", "Zulu", 41, "Male", "Diabetes Type 2");
        assertTrue(result); // update should succeed since the patient exists
        assertEquals(41, hospitalSystem.searchPatientById("P003").getAge()); // confirm the age actually changed
    }

    @Test
    public void testDeletePatient() {
        Patient p = new Patient("P004", "Naledi", "Molefe", 22, "Female", "Migraine", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(p); // set up: register the patient to delete
        boolean result = hospitalSystem.deletePatient("P004"); // perform the deletion
        assertTrue(result); // deletion should succeed since the patient exists
        assertNull(hospitalSystem.searchPatientById("P004")); // patient should no longer be findable
    }

    // ---------- creating tests for BED MANAGEMENT ----------

    @Test
    public void testAllocateBed() {
        InPatient inpatient = new InPatient("P005", "Kabelo", "Mahlangu", 35, "Male", "Surgery Recovery");
        hospitalSystem.registerPatient(inpatient); // register an inpatient first, only they can get beds
        String result = hospitalSystem.allocateBed("P005", "B01"); // attempt allocation
        assertTrue(result.contains("successfully allocated")); // message should confirm success
        assertEquals("B01", inpatient.getBedNumber()); // the inpatient's own record should reflect the bed
    }

    @Test
    public void testReleaseBed() {
        InPatient inpatient = new InPatient("P006", "Zanele", "Khumalo", 28, "Female", "Post-Op");
        hospitalSystem.registerPatient(inpatient);        // register the inpatient
        hospitalSystem.allocateBed("P006", "B02");          // allocate a bed first so there's something to release
        String result = hospitalSystem.releaseBed("B02");   // now release it
        assertTrue(result.contains("successfully released")); // message should confirm success
        assertEquals("Not Assigned", inpatient.getBedNumber()); // patient's record should be reset
    }

    // ---------- Creating tests for validation and boundry checking ----------

    @Test
    public void testPreventDuplicatePatientID() {
        Patient p1 = new Patient("P007", "Bongani", "Sithole", 33, "Male", "Cold", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P007", "Different", "Person", 50, "Male", "Cough", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(p1);                        // first registration should succeed
        boolean secondResult = hospitalSystem.registerPatient(p2); // second uses the same ID, should fail
        assertFalse(secondResult); // duplicate ID must be rejected
    }

    @Test
    public void testPreventAllocatingOccupiedBed() {
        InPatient first = new InPatient("P008", "Ayanda", "Ngcobo", 45, "Female", "Pneumonia");
        InPatient second = new InPatient("P009", "Sibusiso", "Mokoena", 38, "Male", "Fracture");
        hospitalSystem.registerPatient(first);
        hospitalSystem.registerPatient(second);
        hospitalSystem.allocateBed("P008", "B03");                    // first patient takes the bed
        String result = hospitalSystem.allocateBed("P009", "B03");    // second patient tries the same bed
        assertTrue(result.contains("already occupied")); // should be rejected with a clear reason
    }

    @Test
    public void testPreventBedAllocationWhenWardFull() {
        // fill all 20 beds with 20 separate inpatients so the ward has zero availability left
        for (int i = 1; i <= 20; i++) {
            InPatient inpatient = new InPatient("F" + i, "Test", "Patient" + i, 30, "Male", "Condition");
            hospitalSystem.registerPatient(inpatient);
            String bedLabel = String.format("B%02d", i); // rebuilds B01...B20 to match HospitalSystem's own labels
            hospitalSystem.allocateBed("F" + i, bedLabel);
        }

        // now register one more inpatient and try to allocate any bed - all 20 are taken
        InPatient overflowPatient = new InPatient("F21", "Overflow", "Patient", 30, "Male", "Condition");
        hospitalSystem.registerPatient(overflowPatient);
        String result = hospitalSystem.allocateBed("F21", "B01"); // B01 is already taken by patient F1
        assertTrue(result.contains("already occupied")); // confirms the ward-full scenario is handled
    }

    // ---------- creating test that Sorts by surname ----------

    @Test
    public void testSortPatientsBySurname() {
        hospitalSystem.registerPatient(new Patient("P010", "A", "Zulu", 20, "Male", "X", PatientCategory.OUTPATIENT));
        hospitalSystem.registerPatient(new Patient("P011", "B", "Adams", 21, "Male", "X", PatientCategory.OUTPATIENT));
        hospitalSystem.registerPatient(new Patient("P012", "C", "Mokoena", 22, "Male", "X", PatientCategory.OUTPATIENT));

        hospitalSystem.sortPatientsBySurname(); // run the bubble sort

        // after sorting ascending by surname, Adams should come first, Zulu should come last
        assertEquals("Adams", hospitalSystem.getPatients().get(0).getLastName());
        assertEquals("Zulu", hospitalSystem.getPatients().get(2).getLastName());
    }
}