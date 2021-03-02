package models.ldap;


public class UsersDtos {

    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String password;
    protected String email;
    protected String qualification;
    protected String specialization;
    protected String employeeId;
    protected String aadhaarNumber;
    protected String panNumber;
    protected String passportNumber;
    protected String phoneNumber;
    protected String organizationName;
    protected String organizationUnitName;
    protected String department;
    protected String role;
    protected String gender;
    protected String channelName;
    protected String channelPurpose;
    protected String hisURL;
    protected String channelDisplayName;

    public UsersDtos() {
        super();
    }

    @Override
    public String toString() {
        return "UsersDtos{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", qualification='" + qualification + '\'' +
                ", specialization='" + specialization + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", aadhaarNumber='" + aadhaarNumber + '\'' +
                ", panNumber='" + panNumber + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", organizationUnitName='" + organizationUnitName + '\'' +
                ", department='" + department + '\'' +
                ", role='" + role + '\'' +
                ", gender='" + gender + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelPurpose='" + channelPurpose + '\'' +
                ", hisURL='" + hisURL + '\'' +
                ", channelDisplayName='" + channelDisplayName + '\'' +
                '}';
    }

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    public String getHisURL() {
        return hisURL;
    }

    public void setHisURL(String hisURL) {
        this.hisURL = hisURL;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationUnitName() {
        return organizationUnitName;
    }

    public void setOrganizationUnitName(String organizationUnitName) {
        this.organizationUnitName = organizationUnitName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }


    public String getChannelPurpose() {
        return channelPurpose;
    }

    public void setChannelPurpose(String channelPurpose) {
        this.channelPurpose = channelPurpose;
    }

}

