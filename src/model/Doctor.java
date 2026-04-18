package model;

/**
 * Doctor model class representing a doctor in the hospital system
 */
public class Doctor {
    private int doctorId;
    private String doctorName;
    private String specialization;
    private String phoneNumber;
    private String email;
    private String gender;
    private int experienceYears;
    private String availability;
    private String status;
    private String registeredOn;
    
    /**
     * Default constructor
     */
    public Doctor() {
    }
    
    /**
     * Constructor with all fields
     */
    public Doctor(int doctorId, String doctorName, String specialization, String phoneNumber,
                  String email, String gender, int experienceYears, String availability,
                  String status, String registeredOn) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.experienceYears = experienceYears;
        this.availability = availability;
        this.status = status;
        this.registeredOn = registeredOn;
    }
    
    /**
     * Constructor without doctorId (for new registrations)
     */
    public Doctor(String doctorName, String specialization, String phoneNumber,
                  String email, String gender, int experienceYears, String availability,
                  String status) {
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.experienceYears = experienceYears;
        this.availability = availability;
        this.status = status;
    }
    
    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public int getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRegisteredOn() {
        return registeredOn;
    }
    
    public void setRegisteredOn(String registeredOn) {
        this.registeredOn = registeredOn;
    }
    
    @Override
    public String toString() {
        return doctorName + " (" + specialization + ")";
    }
}
