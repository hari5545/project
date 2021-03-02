package models.mattermost;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClientPosted {

	@SerializedName("channel_id")
	@Expose
	private String channelId;
	@SerializedName("root_id")
	@Expose
	private String rootId;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("file_ids")
	@Expose
	private ArrayList<String> fileIds = null;
	@SerializedName("props")
	@Expose
	private Props props;


	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<String> getFileIds() {
		return fileIds;
	}

	public void setFileIds(ArrayList<String> fileIds) {
		this.fileIds = fileIds;
	}

	public Props getProps() {
		return props;
	}

	public void setProps(Props props) {
		this.props = props;
	}

}
