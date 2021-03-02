package models.client;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channel implements Serializable {

    private final static long serialVersionUID = 7515659055038991209L;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("channelname")
    @Expose
    private String channelname;
    @SerializedName("channelHeader")
    @Expose
    private String channelHeader;
    @SerializedName("channelPurpose")
    @Expose
    private String channelPurpose;
    @SerializedName("accessAcrossUnits")
    @Expose
    private String accessAcrossUnits;
    @SerializedName("accessAcrossDepartments")
    @Expose
    private String accessAcrossDepartments;
    @SerializedName("channelType")
    @Expose
    private String channelType;
    @SerializedName("channelDisplayName")
    @Expose
    private String channelDisplayName;

    /**
     * No args constructor for use in serialization
     */
    public Channel() {
    }

    /**
     * @param accessAcrossDepartments
     * @param accessAcrossUnits
     * @param channelHeader
     * @param channelType
     * @param channelname
     * @param channeldisplayname
     * @param channelId
     * @param channelPurpose
     */
    public Channel(String channelId, String channelname, String channelHeader, String channelPurpose, String accessAcrossUnits, String accessAcrossDepartments, String channelType, String channeldisplayname) {
        super();
        this.channelId = channelId;
        this.channelname = channelname;
        this.channelHeader = channelHeader;
        this.channelPurpose = channelPurpose;
        this.accessAcrossUnits = accessAcrossUnits;
        this.accessAcrossDepartments = accessAcrossDepartments;
        this.channelType = channelType;
        this.channelDisplayName = channeldisplayname;
    }

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Channel withChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public Channel withChannelname(String channelname) {
        this.channelname = channelname;
        return this;
    }

    public String getChannelHeader() {
        return channelHeader;
    }

    public void setChannelHeader(String channelHeader) {
        this.channelHeader = channelHeader;
    }

    public Channel withChannelHeader(String channelHeader) {
        this.channelHeader = channelHeader;
        return this;
    }

    public String getChannelPurpose() {
        return channelPurpose;
    }

    public void setChannelPurpose(String channelPurpose) {
        this.channelPurpose = channelPurpose;
    }

    public Channel withChannelPurpose(String channelPurpose) {
        this.channelPurpose = channelPurpose;
        return this;
    }

    public String getAccessAcrossUnits() {
        return accessAcrossUnits;
    }

    public void setAccessAcrossUnits(String accessAcrossUnits) {
        this.accessAcrossUnits = accessAcrossUnits;
    }

    public Channel withAccessAcrossUnits(String accessAcrossUnits) {
        this.accessAcrossUnits = accessAcrossUnits;
        return this;
    }

    public String getAccessAcrossDepartments() {
        return accessAcrossDepartments;
    }

    public void setAccessAcrossDepartments(String accessAcrossDepartments) {
        this.accessAcrossDepartments = accessAcrossDepartments;
    }

    public Channel withAccessAcrossDepartments(String accessAcrossDepartments) {
        this.accessAcrossDepartments = accessAcrossDepartments;
        return this;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Channel withChannelType(String channelType) {
        this.channelType = channelType;
        return this;
    }

}