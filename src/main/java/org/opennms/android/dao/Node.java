package org.opennms.android.dao;

import java.io.Serializable;

public class Node implements Serializable {

    private Integer id;
    private String name;
    private String type;
    private String createTime;
    private String labelSource;
    private String sysContact;
    private String description;
    private String location;

    public Node(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getLabelSource() {
        return labelSource;
    }

    public void setLabelSource(String labelSource) {
        this.labelSource = labelSource;
    }

}