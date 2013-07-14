package org.opennms.android.communication;

import android.content.ContentValues;
import android.util.Log;
import org.opennms.android.dao.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class NodesParser extends Parser {

    private static final String TAG = "NodesParser";

    public static ArrayList<ContentValues> parse(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/nodes/node", xml);
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    ContentValues values = new ContentValues();
                    Node currentNode = nodes.item(i);

                    Node id = getXmlNodeForExpression("@id", currentNode);
                    values.put(Contract.Nodes.COLUMN_NODE_ID, id.getNodeValue());

                    Node name = getXmlNodeForExpression("@label", currentNode);
                    if (name != null) values.put(Contract.Nodes.COLUMN_NAME, name.getNodeValue());

                    Node type = getXmlNodeForExpression("@type", currentNode);
                    if (type != null) values.put(Contract.Nodes.COLUMN_TYPE, type.getNodeValue());

                    Node createTime = getXmlNodeForExpression("createTime", currentNode);
                    if (createTime != null) values.put(Contract.Nodes.COLUMN_CREATED_TIME, createTime.getTextContent());

                    Node sysContact = getXmlNodeForExpression("sysContact", currentNode);
                    if (sysContact != null) values.put(Contract.Nodes.COLUMN_SYS_CONTACT, sysContact.getTextContent());

                    Node labelSource = getXmlNodeForExpression("labelSource", currentNode);
                    if (labelSource != null)
                        values.put(Contract.Nodes.COLUMN_LABEL_SOURCE, labelSource.getTextContent());

                    Node sysDescription = getXmlNodeForExpression("sysDescription", currentNode);
                    if (sysDescription != null)
                        values.put(Contract.Nodes.COLUMN_DESCRIPTION, sysDescription.getTextContent());

                    Node sysLocation = getXmlNodeForExpression("sysLocation", currentNode);
                    if (sysLocation != null) values.put(Contract.Nodes.COLUMN_LOCATION, sysLocation.getTextContent());

                    valuesArray.add(values);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

}