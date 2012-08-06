package org.opennms.gsoc.model;

import java.util.Date;

public class OnmsAssetRecord {
	private String category;
	private Date lastModifiedDate;
	private String lastModifiedBy;
	private int nodeId;

	public OnmsAssetRecord(String category, String lastModifiedBy, Date lastModifiedDate, int nodeId) {
		this.category = category;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
		this.nodeId = nodeId;
	}

	public String getCategory() {
		return this.category;
	}
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}
	public String getLastModifiedBy() {
		return this.lastModifiedBy;
	}
	public int getNodeId() {
		return this.nodeId;
	}

	@Override
	public String toString() {
		return "OnmsAssetRecord [category=" + this.category + ", lastModifiedDate="
				+ this.lastModifiedDate + ", lastModifiedBy=" + this.lastModifiedBy
				+ ", nodeId=" + this.nodeId + "]";
	}
}
