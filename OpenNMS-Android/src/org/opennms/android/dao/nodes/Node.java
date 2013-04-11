package org.opennms.android.dao.nodes;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import org.opennms.android.dao.Columns.NodeColumns;

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
                NodeColumns.COL_NODE_ID,
                NodeColumns.COL_TYPE,
                NodeColumns.COL_LABEL,
                NodeColumns.COL_CREATED_TIME,
                NodeColumns.COL_SYS_CONTACT,
                NodeColumns.COL_LABEL_SOURCE
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

}