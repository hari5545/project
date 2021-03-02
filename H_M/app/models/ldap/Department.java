package models.ldap;

public class Department {
	
	private String departmentName;
	private String organizationUnitName;
	private String organizationName;

	public Department() { }
	
	public String getOrganizationName() {
		return this.organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationUnitName() {
		return this.organizationUnitName;
	}
	public void setOrganizationUnitName(String organizationUnitName) {
		this.organizationUnitName = organizationUnitName;
	}

	public String getDepartmentName() {
		return this.departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
}