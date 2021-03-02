package models.ldap;

public class Organization {
	
	private String organizationName;
	private String organizationSupportEmail;
	private String organizationSupportPhone;
	private String organizationId_MM;
	private String organizationHisUrl;

	public Organization() { }

	public String getOrganizationHisUrl() {
		return this.organizationHisUrl;
	}
	public void setOrganizationHisUrl(String organizationHisUrl) {
		this.organizationHisUrl = organizationHisUrl;
	}
	
	public String getOrganizationName() {
		return this.organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationSupportEmail() {
		return this.organizationSupportEmail;
	}
	public void setOrganizationSupportEmail(String organizationSupportEmail) {
		this.organizationSupportEmail = organizationSupportEmail;
	}

	public String getOrganizationSupportPhone() {
		return this.organizationSupportPhone;
	}
	public void setOrganizationSupportPhone(String organizationSupportPhone) {
		this.organizationSupportPhone = organizationSupportPhone;
	}

	public String getOrganizationId_MM() {
		return this.organizationId_MM;
	}
	public void setOrganizationId_MM(String organizationId_MM) {
		this.organizationId_MM = organizationId_MM;
	}
}