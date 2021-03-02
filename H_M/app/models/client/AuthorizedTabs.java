package models.client;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizedTabs implements Serializable
{

@SerializedName("organization")
@Expose
private String organization;
@SerializedName("organizationId")
@Expose
private String organizationId;
@SerializedName("channels")
@Expose
private List<Channel> channels = null;
@SerializedName("organizationUnit")
@Expose
private List<OrganizationUnit> organizationUnit = null;
private final static long serialVersionUID = -8955769944131053863L;

/**
* No args constructor for use in serialization
*
*/
public AuthorizedTabs() {
}

/**
*
* @param organizationId
* @param channels
* @param organization
* @param organizationUnit
*/
public AuthorizedTabs(String organization, String organizationId, List<Channel> channels, List<OrganizationUnit> organizationUnit) {
super();
this.organization = organization;
this.organizationId = organizationId;
this.channels = channels;
this.organizationUnit = organizationUnit;
}

public String getOrganization() {
return organization;
}

public void setOrganization(String organization) {
this.organization = organization;
}

public AuthorizedTabs withOrganization(String organization) {
this.organization = organization;
return this;
}

public String getOrganizationId() {
return organizationId;
}

public void setOrganizationId(String organizationId) {
this.organizationId = organizationId;
}

public AuthorizedTabs withOrganizationId(String organizationId) {
this.organizationId = organizationId;
return this;
}

public List<Channel> getChannels() {
return channels;
}

public void setChannels(List<Channel> channels) {
this.channels = channels;
}

public AuthorizedTabs withChannels(List<Channel> channels) {
this.channels = channels;
return this;
}

public List<OrganizationUnit> getOrganizationUnit() {
return organizationUnit;
}

public void setOrganizationUnit(List<OrganizationUnit> organizationUnit) {
this.organizationUnit = organizationUnit;
}

public AuthorizedTabs withOrganizationUnit(List<OrganizationUnit> organizationUnit) {
this.organizationUnit = organizationUnit;
return this;
}

}