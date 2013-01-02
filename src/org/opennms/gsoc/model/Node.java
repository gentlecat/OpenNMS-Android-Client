package org.opennms.gsoc.model;

import java.io.Serializable;

public class Node implements Serializable {

    private Integer id;
    private String type;
    private String label;
    private String createTime;
    private String labelSource;
    private String sysContact;

    public Node(Integer id, String label, String type, String createTime, String sysContact, String labelSource) {
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Node ID: " + id + "\n");
        builder.append("Label: " + label + "\n");
        builder.append("Type: " + type + "\n");
        builder.append("Creation time: " + createTime + "\n");
        builder.append("Sys. contact: " + sysContact + "\n");
        builder.append("Label source: " + labelSource + "\n");
        return builder.toString();
    }

}