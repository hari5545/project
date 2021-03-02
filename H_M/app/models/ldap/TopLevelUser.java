package models;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class TopLevelUser {

	private String userId;
	private String username;
	private String passwordHash;
	private String email;
	private String organization;
	private ArrayList<String> roleList;

	public TopLevelUser() { }

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public ArrayList<String> getRoleList() {
		return this.roleList;
	}

	public void addRole(String roleName) {
		if(this.roleList != null) {
			this.roleList.add(roleName);
		} else {
			roleList = new ArrayList<String>();
			this.roleList.add(roleName);
		}
	}

	public void setRoleList(ArrayList<String> roleList) {
		if(this.roleList != null) {
			this.roleList = roleList;
		} else {
			roleList = new ArrayList<String>();
			this.roleList = roleList;
		}		
	}

	public String getRoles() {
		String roles = "";
		if(this.roleList != null) {
			roles = StringUtils.join(this.roleList, ",");
		}
		return roles;		
	}
}