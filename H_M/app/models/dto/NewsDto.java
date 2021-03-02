package models.dto;

public class NewsDto {
	protected String content;
	protected String orgName;
	protected String orgUnitName;
	protected  String tabName;
	
	
	public NewsDto() {
		super();
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgUnitName() {
		return orgUnitName;
	}
	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	@Override
	public String toString() {
		return "NewsDto [content=" + content + ", orgName=" + orgName + ", orgUnitName=" + orgUnitName + ", tabName="
				+ tabName + "]";
	}
	
}
