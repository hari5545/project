package models;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import models.client.Channel;

public class LoginResponse implements Serializable {

    private final static long serialVersionUID = -8627413048004353562L;
    @SerializedName("statusCode")
    @Expose
    private String statusCode;
    @SerializedName("statusMessage")
    @Expose
    private String statusMessage;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userRoles")
    @Expose
    private String userRoles;
    @SerializedName("userRoleTypes")
    @Expose
    private String userRoleTypes;
    @SerializedName("userOrganization")
    @Expose
    private String userOrganization;
    @SerializedName("userFirstname")
    @Expose
    private String userFirstname;
    @SerializedName("userLastname")
    @Expose
    private String userLastname;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("userGender")
    @Expose
    private String userGender;
    @SerializedName("userDepartment")
    @Expose
    private String userDepartment;
    @SerializedName("userAuthToken")
    @Expose
    private String userAuthToken;
    @SerializedName("userURL")
    @Expose
    private String userURL;
    @SerializedName("hisURL")
    @Expose
    private String hisURL;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("employeeId")
    @Expose
    private String employeeId;
    @SerializedName("broadCastChannel")
    @Expose
    private ArrayList<Channel> broadCastChannel;
    

    /**
     * No args constructor for use in serialization
     */
    public LoginResponse() {
        broadCastChannel = new ArrayList();
    }

    public LoginResponse(String statusCode, String statusMessage, String userName, String userRoles, String userRoleTypes, String userOrganization, String userFirstname, String userLastname, String userEmail, String userGender, String userDepartment, String userAuthToken, String userURL, String hisURL, String userId, ArrayList<Channel> broadCastChannel, String employeeId) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.userName = userName;
        this.userRoles = userRoles;
        this.userRoleTypes = userRoleTypes;
        this.userOrganization = userOrganization;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userDepartment = userDepartment;
        this.userAuthToken = userAuthToken;
        this.userURL = userURL;
        this.hisURL = hisURL;
        this.userId = userId;
        this.broadCastChannel = broadCastChannel;
        this.employeeId = employeeId;
    }

    public void setBroadCastChannel(ArrayList<Channel> broadCastChannel) {
        this.broadCastChannel = broadCastChannel;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public LoginResponse withStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public LoginResponse withStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LoginResponse withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String userRoles) {
        this.userRoles = userRoles;
    }

    public String getUserRoleTypes() {
        return userRoleTypes;
    }

    public void setUserRoleTypes(String userRoleTypes) {
        this.userRoleTypes = userRoleTypes;
    }

    public LoginResponse withUserRoles(String userRoles) {
        this.userRoles = userRoles;
        return this;
    }

    public String getUserOrganization() {
        return userOrganization;
    }

    public void setUserOrganization(String userOrganization) {
        this.userOrganization = userOrganization;
    }

    public LoginResponse withUserOrganization(String userOrganization) {
        this.userOrganization = userOrganization;
        return this;
    }

    public String getUserAuthToken() {
        return userAuthToken;
    }

    public void setUserAuthToken(String userAuthToken) {
        this.userAuthToken = userAuthToken;
    }

    public LoginResponse withUserAuthToken(String userAuthToken) {
        this.userAuthToken = userAuthToken;
        return this;
    }

    public String getUserURL() {
        return userURL;
    }

    public void setUserURL(String userURL) {
        this.userURL = userURL;
    }

    public String getHisURL() {
        return hisURL;
    }

    public void setHisURL(String hisURL) {
        this.hisURL = hisURL;
    }

    public LoginResponse withUserURL(String userURL) {
        this.userURL = userURL;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LoginResponse withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LoginResponse withEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        return this;
    }


    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserLastname(String userLastname) {
        this.userLastname = userLastname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

}