package models.ldap;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class User {

	private String userId;
	private String firstName;
	private String lastName;
	private String username;
	private String passwordHash;
	private String email;
	private String qualification;
	private String specialization;
	private String employeeId;
	private String aadhaarNumber;
	private String panNumber;
	private String passportNumber;
	private String phoneNumber;
	private String organization;
	private String organizationUnit;
	private String department;
	private ArrayList<String> roleList;
	private String gender;
	private boolean ouAccess;
	private boolean universalAccess;
	private String userId_MM;
	private String userAccessToken;
	private String dateOfDeactivation;
	private boolean activeStatus;

	public User() {
		this.userAccessToken = "";
		this.dateOfDeactivation = "";
		this.activeStatus = true;
	}

	public String getUserAccessToken() {
		return this.userAccessToken;
	}

	public void setUserAccessToken(String userAccessToken) {
		this.userAccessToken = userAccessToken;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public ArrayList<String> getRoleList() {
		return this.roleList;
	}

	public void setRoleList(ArrayList<String> roleList) {
		if(roleList != null) {
			this.roleList = roleList;
		} else {
			roleList = new ArrayList<String>();
			this.roleList = roleList;
		}		
	}

	public String getRoles() {
		String roles = "";
		if(roleList != null) {
			roles = StringUtils.join(roleList, ",");
		}
		return roles;		
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean hasOuAccess() {
		return ouAccess;
	}

	public void setOuAccess(boolean ouAccess) {
		this.ouAccess = ouAccess;
	}

	public boolean hasUniversalAccess() {
		return universalAccess;
	}

	public void setUniversalAccess(boolean universalAccess) {
		this.universalAccess = universalAccess;
	}

	public String getDisplayName() {
		return this.firstName + " " + this.lastName;
	}

	public String getUserId_MM() {
		return this.userId_MM;
	}

	public void setUserId_MM(String userId_MM) {
		this.userId_MM = userId_MM;
	}

	public String getDateOfDeactivation() {
		return dateOfDeactivation;
	}

	public void setDateOfDeactivation(String dateOfDeactivation) {
		this.dateOfDeactivation = dateOfDeactivation;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

}