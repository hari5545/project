package models.mattermost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FileInfoResponse {
    @SerializedName("file_infos")
    @Expose
    private ArrayList<FileInfo> fileInfos = null;
    @SerializedName("client_ids")
    @Expose
    private ArrayList<Object> clientIds = null;

    public ArrayList<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(ArrayList<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public FileInfoResponse withFileInfos(ArrayList<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
        return this;
    }

    public ArrayList<Object> getClientIds() {
        return clientIds;
    }

    public void setClientIds(ArrayList<Object> clientIds) {
        this.clientIds = clientIds;
    }

    public FileInfoResponse withClientIds(ArrayList<Object> clientIds) {
        this.clientIds = clientIds;
        return this;
    }
}
