package models.mattermost;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Team implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("create_at")
    @Expose
    private long createAt;
    @SerializedName("update_at")
    @Expose
    private long updateAt;
    @SerializedName("delete_at")
    @Expose
    private long deleteAt;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("allowed_domains")
    @Expose
    private String allowedDomains;
    @SerializedName("invite_id")
    @Expose
    private String inviteId;
    @SerializedName("allow_open_invite")
    @Expose
    private boolean allowOpenInvite;
    @SerializedName("scheme_id")
    @Expose
    private Object schemeId;
    @SerializedName("group_constrained")
    @Expose
    private Object groupConstrained;
    
    private final static long serialVersionUID = 6577566608273528524L;


    public Team() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Team withId(String id) {
        this.id = id;
        return this;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public Team withCreateAt(long createAt) {
        this.createAt = createAt;
        return this;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public Team withUpdateAt(long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public long getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(long deleteAt) {
        this.deleteAt = deleteAt;
    }

    public Team withDeleteAt(long deleteAt) {
        this.deleteAt = deleteAt;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Team withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Team withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Team withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Team withType(String type) {
        this.type = type;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Team withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getAllowedDomains() {
        return allowedDomains;
    }

    public void setAllowedDomains(String allowedDomains) {
        this.allowedDomains = allowedDomains;
    }

    public Team withAllowedDomains(String allowedDomains) {
        this.allowedDomains = allowedDomains;
        return this;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    public Team withInviteId(String inviteId) {
        this.inviteId = inviteId;
        return this;
    }

    public boolean isAllowOpenInvite() {
        return allowOpenInvite;
    }

    public void setAllowOpenInvite(boolean allowOpenInvite) {
        this.allowOpenInvite = allowOpenInvite;
    }

    public Team withAllowOpenInvite(boolean allowOpenInvite) {
        this.allowOpenInvite = allowOpenInvite;
        return this;
    }

    public Object getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Object schemeId) {
        this.schemeId = schemeId;
    }

    public Team withSchemeId(Object schemeId) {
        this.schemeId = schemeId;
        return this;
    }

    public Object getGroupConstrained() {
        return groupConstrained;
    }

    public void setGroupConstrained(Object groupConstrained) {
        this.groupConstrained = groupConstrained;
    }

    public Team withGroupConstrained(Object groupConstrained) {
        this.groupConstrained = groupConstrained;
        return this;
    }
    
    public int describeContents() {
        return 0;
    }

}