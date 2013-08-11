package org.opennms.android.parsing;

import android.content.ContentValues;
import android.util.Log;

import org.opennms.android.provider.Contract;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

public class NodesParser extends Parser {

    private static final String TAG = "NodesParser";

    public static ArrayList<ContentValues> parseMultiple(String xml) {
        ArrayList<ContentValues> valuesArray = new ArrayList<ContentValues>();
        try {
            NodeList nodes = getXmlNodeListForExpression("/nodes/node", xml);
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    valuesArray.add(getContentValues(nodes.item(i)));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return valuesArray;
    }

    public static ContentValues parseSingle(String xml) {
        try {
            Node node = getXmlNodeForExpression("/node", xml);
            if (node != null) {
                return getContentValues(node);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static ContentValues getContentValues(Node node)
            throws XPathExpressionException {
        ContentValues values = new ContentValues();

        Node id = getXmlNodeForExpression("@id", node);
        values.put(Contract.Nodes._ID, id.getNodeValue());

        Node name = getXmlNodeForExpression("@label", node);
        if (name != null) {
            values.put(Contract.Nodes.NAME, name.getNodeValue());
        }

        Node type = getXmlNodeForExpression("@type", node);
        if (type != null) {
            values.put(Contract.Nodes.TYPE, type.getNodeValue());
        }

        Node createTime = getXmlNodeForExpression("createTime", node);
        if (createTime != null) {
            values.put(Contract.Nodes.CREATED_TIME, createTime.getTextContent());
        }

        Node sysContact = getXmlNodeForExpression("sysContact", node);
        if (sysContact != null) {
            values.put(Contract.Nodes.CONTACT, sysContact.getTextContent());
        }

        Node labelSource = getXmlNodeForExpression("labelSource", node);
        if (labelSource != null) {
            values.put(Contract.Nodes.LABEL_SOURCE, labelSource.getTextContent());
        }

        Node sysDescription = getXmlNodeForExpression("sysDescription", node);
        if (sysDescription != null) {
            values.put(Contract.Nodes.DESCRIPTION, sysDescription.getTextContent());
        }

        Node sysLocation = getXmlNodeForExpression("sysLocation", node);
        if (sysLocation != null) {
            values.put(Contract.Nodes.LOCATION, sysLocation.getTextContent());
        }

        Node sysObjectId = getXmlNodeForExpression("sysObjectId", node);
        if (sysObjectId != null) {
            values.put(Contract.Nodes.SYS_OBJECT_ID, sysObjectId.getTextContent());
        }

        return values;
    }

}
