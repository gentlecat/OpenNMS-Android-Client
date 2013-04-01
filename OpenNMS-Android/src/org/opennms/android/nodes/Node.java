package org.opennms.android.nodes;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import org.opennms.android.dao.DatabaseHelper;
import org.opennms.android.nodes.dao.NodesListProvider;

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

    public Node(ContentResolver contentResolver, long listItemId) {
        String projection[] = {
                DatabaseHelper.COL_NODE_ID,
                DatabaseHelper.COL_TYPE,
                DatabaseHelper.COL_LABEL,
                DatabaseHelper.COL_CREATED_TIME,
                DatabaseHelper.COL_SYS_CONTACT,
                DatabaseHelper.COL_LABEL_SOURCE
        };
        Cursor nodesCursor = contentResolver.query(
                Uri.withAppendedPath(NodesListProvider.CONTENT_URI, String.valueOf(listItemId)),
                projection, null, null, null);
        if (nodesCursor.moveToFirst()) {
            id = nodesCursor.getInt(0);
            type = nodesCursor.getString(1);
            label = nodesCursor.getString(2);
            createTime = nodesCursor.getString(3);
            sysContact = nodesCursor.getString(4);
            labelSource = nodesCursor.getString(5);
        }
        nodesCursor.close();
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