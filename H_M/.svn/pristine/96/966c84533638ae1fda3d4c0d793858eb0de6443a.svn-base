package models.client;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrganizationUnit implements Serializable
{

@SerializedName("organizationUnitName")
@Expose
private String organizationUnitName;
@SerializedName("channels")
@Expose
private List<Channel> channels = null;
@SerializedName("department")
@Expose
private List<Department> department = null;
private final static long serialVersionUID = 7900292224918963706L;

/**
* No args constructor for use in serialization
*
*/
public OrganizationUnit() {
}

/**
*
* @param channels
* @param department
* @param organizationUnitName
*/
public OrganizationUnit(String organizationUnitName, List<Channel> channels, List<Department> department) {
super();
this.organizationUnitName = organizationUnitName;
this.channels = channels;
this.department = department;
}

public String getOrganizationUnitName() {
return organizationUnitName;
}

public void setOrganizationUnitName(String organizationUnitName) {
this.organizationUnitName = organizationUnitName;
}

public OrganizationUnit withOrganizationUnitName(String organizationUnitName) {
this.organizationUnitName = organizationUnitName;
return this;
}

public List<Channel> getChannels() {
return channels;
}

public void setChannels(List<Channel> channels) {
this.channels = channels;
}

public OrganizationUnit withChannels(List<Channel> channels) {
this.channels = channels;
return this;
}

public List<Department> getDepartment() {
return department;
}

public void setDepartment(List<Department> department) {
this.department = department;
}

public OrganizationUnit withDepartment(List<Department> department) {
this.department = department;
return this;
}

}