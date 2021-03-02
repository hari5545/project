package models.ldap;

public class OrganizationUnit {
	
	private String organizationUnitName;
	private String organizationName;
	private String organizationUnitLocation;
	private String organizationUnitLatitude;
	private String organizationUnitLongitude;
	private String organizationUnitEmergencyNumber;

	public OrganizationUnit() { }

	public String getOrganizationUnitName() {
		return organizationUnitName;
	}

	public void setOrganizationUnitName(String organizationUnitName) {
		this.organizationUnitName = organizationUnitName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationUnitLocation() {
		return organizationUnitLocation;
	}

	public void setOrganizationUnitLocation(String organizationUnitLocation) {
		this.organizationUnitLocation = organizationUnitLocation;
	}

	public String getOrganizationUnitLatitude() {
		return organizationUnitLatitude;
	}

	public void setOrganizationUnitLatitude(String organizationUnitLatitude) {
		this.organizationUnitLatitude = organizationUnitLatitude;
	}

	public String getOrganizationUnitLongitude() {
		return organizationUnitLongitude;
	}

	public void setOrganizationUnitLongitude(String organizationUnitLongitude) {
		this.organizationUnitLongitude = organizationUnitLongitude;
	}

	public String getOrganizationUnitEmergencyNumber() {
		return organizationUnitEmergencyNumber;
	}

	public void setOrganizationUnitEmergencyNumber(String organizationUnitEmergencyNumber) {
		this.organizationUnitEmergencyNumber = organizationUnitEmergencyNumber;
	}

}