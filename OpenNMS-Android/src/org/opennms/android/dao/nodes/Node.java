package org.opennms.android.dao.nodes;

import java.io.Serializable;

public class Node implements Serializable {

    private Integer id;
    private String type;
    private String label;
    private String createTime;
    private String labelSource;
    private String sysContact;

    public Node(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSysContact() {
        return sysContact;
    }

    public void setSysContact(String sysContact) {
        this.sysContact = sysContact;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelSource() {
        return labelSource;
    }

    public void setLabelSource(String labelSource) {
        this.labelSource = labelSource;
    }

}