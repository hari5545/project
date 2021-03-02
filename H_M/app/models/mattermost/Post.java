package models.mattermost;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("create_at")
    @Expose
    private Long createAt;
    @SerializedName("update_at")
    @Expose
    private Long updateAt;
    @SerializedName("edit_at")
    @Expose
    private Long editAt;
    @SerializedName("delete_at")
    @Expose
    private Long deleteAt;
    @SerializedName("is_pinned")
    @Expose
    private Boolean isPinned;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("root_id")
    @Expose
    private String rootId;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("original_id")
    @Expose
    private String originalId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("props")
    @Expose
    private Props props;
    @SerializedName("hashtags")
    @Expose
    private String hashtags;
    @SerializedName("file_ids")
    @Expose
    private ArrayList<String> fileIds = null;
    @SerializedName("pending_post_id")
    @Expose
    private String pendingPostId;
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Post withId(String id) {
        this.id = id;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Post withCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public Post withUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public Long getEditAt() {
        return editAt;
    }

    public void setEditAt(Long editAt) {
        this.editAt = editAt;
    }

    public Post withEditAt(Long editAt) {
        this.editAt = editAt;
        return this;
    }

    public Long getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Long deleteAt) {
        this.deleteAt = deleteAt;
    }

    public Post withDeleteAt(Long deleteAt) {
        this.deleteAt = deleteAt;
        return this;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Post withIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Post withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Post withChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public Post withRootId(String rootId) {
        this.rootId = rootId;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Post withParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public Post withOriginalId(String originalId) {
        this.originalId = originalId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Post withMessage(String message) {
        this.message = message;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Post withType(String type) {
        this.type = type;
        return this;
    }

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }

    public Post withProps(Props props) {
        this.props = props;
        return this;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public Post withHashtags(String hashtags) {
        this.hashtags = hashtags;
        return this;
    }

    public ArrayList<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(ArrayList<String> fileIds) {
        this.fileIds = fileIds;
    }

    public Post withFileIds(ArrayList<String> fileIds) {
        this.fileIds = fileIds;
        return this;
    }

    public String getPendingPostId() {
        return pendingPostId;
    }

    public void setPendingPostId(String pendingPostId) {
        this.pendingPostId = pendingPostId;
    }

    public Post withPendingPostId(String pendingPostId) {
        this.pendingPostId = pendingPostId;
        return this;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Post withMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

}