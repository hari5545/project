package models.client;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//import org.apache.commons.lang.builder.ToStringBuilder;
import models.mattermost.NotifyProps;

public class ChannelMember implements Serializable {

    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("roles")
    @Expose
    private String roles;
    @SerializedName("last_viewed_at")
    @Expose
    private long lastViewedAt;
    @SerializedName("msg_count")
    @Expose
    private long msgCount;
    @SerializedName("mention_count")
    @Expose
    private long mentionCount;
    @SerializedName("notify_props")
    @Expose
    private NotifyProps notifyProps;
    @SerializedName("last_update_at")
    @Expose
    private long lastUpdateAt;
    @SerializedName("scheme_guest")
    @Expose
    private boolean schemeGuest;
    @SerializedName("scheme_user")
    @Expose
    private boolean schemeUser;
    @SerializedName("scheme_admin")
    @Expose
    private boolean schemeAdmin;
    @SerializedName("explicit_roles")
    @Expose
    private String explicitRoles;

    @SerializedName("userRoles")
    @Expose
    private String userRoles;
    @SerializedName("userRoleTypes")
    @Expose
    private String userRoleTypes;

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
    
    private final static long serialVersionUID = -3584122009358178981L;



    /**
     * No args constructor for use in serialization
     */
    public ChannelMember() {
    }

    /**
     * @param schemeAdmin
     * @param mentionCount
     * @param schemeGuest
     * @param roles
     * @param explicitRoles
     * @param msgCount
     * @param notifyProps
     * @param lastUpdateAt
     * @param schemeUser
     * @param userId
     * @param channelId
     * @param lastViewedAt
     * @param userRoles
     * @param userRoleTypes
     * @param userFirstname
     * @param userLastname
     * @param userEmail
     * @param userGender
     * @param userDepartment
     */
    public ChannelMember(String channelId, String userId, String roles, long lastViewedAt, long msgCount, long mentionCount, NotifyProps notifyProps, long lastUpdateAt, boolean schemeGuest, boolean schemeUser, boolean schemeAdmin, String explicitRoles, String userRoles, String userRoleTypes,
        String userFirstname, String userLastname, String userEmail, String userGender, String userDepartment) {
        super();
        this.channelId = channelId;
        this.userId = userId;
        this.roles = roles;
        this.lastViewedAt = lastViewedAt;
        this.msgCount = msgCount;
        this.mentionCount = mentionCount;
        this.notifyProps = notifyProps;
        this.lastUpdateAt = lastUpdateAt;
        this.schemeGuest = schemeGuest;
        this.schemeUser = schemeUser;
        this.schemeAdmin = schemeAdmin;
        this.explicitRoles = explicitRoles;
        this.userRoles = userRoles;
        this.userRoleTypes = userRoleTypes;        
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userDepartment = userDepartment;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public ChannelMember withChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ChannelMember withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public ChannelMember withRoles(String roles) {
        this.roles = roles;
        return this;
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(long lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public ChannelMember withLastViewedAt(long lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
        return this;
    }

    public long getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public ChannelMember withMsgCount(long msgCount) {
        this.msgCount = msgCount;
        return this;
    }

    public long getMentionCount() {
        return mentionCount;
    }

    public void setMentionCount(long mentionCount) {
        this.mentionCount = mentionCount;
    }

    public ChannelMember withMentionCount(long mentionCount) {
        this.mentionCount = mentionCount;
        return this;
    }

    public NotifyProps getNotifyProps() {
        return notifyProps;
    }

    public void setNotifyProps(NotifyProps notifyProps) {
        this.notifyProps = notifyProps;
    }

    public ChannelMember withNotifyProps(NotifyProps notifyProps) {
        this.notifyProps = notifyProps;
        return this;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public ChannelMember withLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
        return this;
    }

    public boolean isSchemeGuest() {
        return schemeGuest;
    }

    public void setSchemeGuest(boolean schemeGuest) {
        this.schemeGuest = schemeGuest;
    }

    public ChannelMember withSchemeGuest(boolean schemeGuest) {
        this.schemeGuest = schemeGuest;
        return this;
    }

    public boolean isSchemeUser() {
        return schemeUser;
    }

    public void setSchemeUser(boolean schemeUser) {
        this.schemeUser = schemeUser;
    }

    public ChannelMember withSchemeUser(boolean schemeUser) {
        this.schemeUser = schemeUser;
        return this;
    }

    public boolean isSchemeAdmin() {
        return schemeAdmin;
    }

    public void setSchemeAdmin(boolean schemeAdmin) {
        this.schemeAdmin = schemeAdmin;
    }

    public ChannelMember withSchemeAdmin(boolean schemeAdmin) {
        this.schemeAdmin = schemeAdmin;
        return this;
    }

    public String getExplicitRoles() {
        return explicitRoles;
    }

    public void setExplicitRoles(String explicitRoles) {
        this.explicitRoles = explicitRoles;
    }

    public ChannelMember withExplicitRoles(String explicitRoles) {
        this.explicitRoles = explicitRoles;
        return this;
    }


    public int describeContents() {
        return 0;
    }


}