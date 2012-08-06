package org.opennms.gsoc.model;

import java.io.Serializable;
import java.util.Date;


/**
 * Contains information on nodes discovered and potentially managed by OpenNMS.
 * sys* properties map to SNMP MIB 2 system table information.
 *
 * @hibernate.class table="node"
 */

public class OnmsNode implements Serializable, Comparable<OnmsNode> {

	private static final long serialVersionUID = -5736397583719151493L;

	private Integer id;
	private String type;
	private String label;
	private String createTime;
	private String labelSource;
	private String sysContact;
	private Date lastCapsdPoll;
	private OnmsAssetRecord assetRecord;

	public OnmsNode(Integer id, String label, String type) {
		this.id = id;
		this.label = label;
		this.type = type;
	}

	public OnmsNode(Integer id, String label, String type, String createTime, String sysContact, String labelSource) {
		this.id = id;
		this.label = label;
		this.type = type;
		this.createTime = createTime;
		this.labelSource = labelSource;
		this.sysContact = sysContact;
	}

	public Integer getId() {
		return this.id;
	}

	/**
	 * <p>getNodeId</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */

	 public String getNodeId() {
		 if (getId() != null) {
			 return getId().toString();
		 }
		 return null;
	 }

	 public String getCreateTime() {
		 return this.createTime;
	 }

	 public String getType() {
		 return this.type;
	 }

	 public String getSysContact() {
		 return this.sysContact;
	 }

	 public String getLabel() {
		 return this.label;
	 }

	 public String getLabelSource() {
		 return this.labelSource;
	 }

	 public Date getLastCapsdPoll() {
		 return this.lastCapsdPoll;
	 }

	 public OnmsAssetRecord getAssetRecord() {
		 return this.assetRecord;
	 }

	 @Override
	 public int compareTo(OnmsNode o) {
		 String compareLabel = "";
		 Integer compareId = 0;

		 if (o != null) {
			 compareLabel = o.getLabel();
			 compareId = o.getId();
		 }

		 int returnval = this.getLabel().compareToIgnoreCase(compareLabel);
		 if (returnval == 0) {
			 return this.getId().compareTo(compareId);
		 } else {
			 return returnval;
		 }
	 }

	 @Override
	 public String toString() {
		 return "OnmsNode [id=" + this.id + ", type=" + this.type + ", label=" + this.label
				 + ", createTime=" + this.createTime + ", labelSource=" + this.labelSource
				 + ", sysContact=" + this.sysContact + "]";
	 }


}