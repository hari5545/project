 package models.client;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Role implements Serializable
{

@SerializedName("roleName")
@Expose
private String roleName;
@SerializedName("roleType")
@Expose
private String roleType;
private final static long serialVersionUID = 5095904339250617033L;

/**
* No args constructor for use in serialization
*
*/
public Role() {
}

/**
*
* @param roleName
* @param roleType
*/
public Role(String roleName, String roleType) {
super();
this.roleName = roleName;
this.roleType = roleType;
}

public String getRoleName() {
return roleName;
}

public void setRoleName(String roleName) {
this.roleName = roleName;
}

public Role withRoleName(String roleName) {
this.roleName = roleName;
return this;
}

public String getRoleType() {
return roleType;
}

public void setRoleType(String roleType) {
this.roleType = roleType;
}

public Role withRoleType(String roleType) {
this.roleType = roleType;
return this;
}

}