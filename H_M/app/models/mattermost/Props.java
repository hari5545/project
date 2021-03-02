package models.mattermost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Props {

    @SerializedName("addedUserId")
    @Expose
    private String addedUserId;
    @SerializedName("addedUsername")
    @Expose
    private String addedUsername;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;

    public String getAddedUserId() {
        return addedUserId;
    }

    public void setAddedUserId(String addedUserId) {
        this.addedUserId = addedUserId;
    }

    public Props withAddedUserId(String addedUserId) {
        this.addedUserId = addedUserId;
        return this;
    }

    public String getAddedUsername() {
        return addedUsername;
    }

    public void setAddedUsername(String addedUsername) {
        this.addedUsername = addedUsername;
    }

    public Props withAddedUsername(String addedUsername) {
        this.addedUsername = addedUsername;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Props withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Props withUsername(String username) {
        this.username = username;
        return this;
    }

}
