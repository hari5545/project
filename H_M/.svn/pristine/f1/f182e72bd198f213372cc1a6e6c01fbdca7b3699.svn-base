package models.dto;

import java.util.Objects;

public class OrganizationDto {
	
	protected String orgName;
	protected String orgSupportEmail;
	protected String orgSupportContactNumber;



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrganizationDto that = (OrganizationDto) o;
		return orgName.equals(that.orgName) &&
				orgSupportEmail.equals(that.orgSupportEmail) &&
				orgSupportContactNumber.equals(that.orgSupportContactNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(orgName, orgSupportEmail, orgSupportContactNumber);
	}

	@Override
	public String toString() {
		return "OrganizationDto{" +
				"orgName='" + orgName + '\'' +
				", orgSupportEmail='" + orgSupportEmail + '\'' +
				", orgSupportContactNumber='" + orgSupportContactNumber + '\'' +
				'}';
	}

	public OrganizationDto() {
		super();
	}


	public OrganizationDto(String orgName, String orgSupportEmail, String orgSupportContactNumber) {
		super();
		this.orgName = orgName;
		this.orgSupportEmail = orgSupportEmail;
		this.orgSupportContactNumber = orgSupportContactNumber;
	}


	public String getOrgName() {
		return orgName;
	}


	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}


	public String getOrgSupportEmail() {
		return orgSupportEmail;
	}


	public void setOrgSupportEmail(String orgSupportEmail) {
		this.orgSupportEmail = orgSupportEmail;
	}


	public String getOrgSupportContactNumber() {
		return orgSupportContactNumber;
	}


	public void setOrgSupportContactNumber(String orgSupportContactNumber) {
		this.orgSupportContactNumber = orgSupportContactNumber;
	}


}
