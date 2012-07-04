package org.opennms.gsoc.nodes.model;

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
    
    public OnmsNode(Integer id, String label, String type, String createTime, String sysContact, String labelSource) {
    	this.id = id;
    	this.label = label;
    	this.type = type;
    	this.createTime = createTime;
    	this.labelSource = labelSource;
    	this.sysContact = sysContact;
    }
    
    public Integer getId() {
        return id;
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
        return createTime;
    }

    public String getType() {
        return type;
    }

    public String getSysContact() {
        return sysContact;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelSource() {
        return labelSource;
    }

    public Date getLastCapsdPoll() {
        return lastCapsdPoll;
    }

    public OnmsAssetRecord getAssetRecord() {
        return assetRecord;
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
		return "OnmsNode [id=" + id + ", type=" + type + ", label=" + label
				+ ", createTime=" + createTime + ", labelSource=" + labelSource
				+ ", sysContact=" + sysContact + "]";
	}
	
	
}