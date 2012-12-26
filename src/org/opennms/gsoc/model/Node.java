package org.opennms.gsoc.model;

import java.io.Serializable;
import java.util.Date;

public class Node implements Serializable, Comparable<Node> {

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
        return this.id;
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

    @Override
    public int compareTo(Node o) {
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