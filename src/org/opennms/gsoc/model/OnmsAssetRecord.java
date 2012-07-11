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
		return category;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public int getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		return "OnmsAssetRecord [category=" + category + ", lastModifiedDate="
				+ lastModifiedDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", nodeId=" + nodeId + "]";
	}
}
