package org.opennms.android.communication.alarms;

import android.util.Log;
import org.opennms.android.communication.Parser;
import org.opennms.android.dao.alarms.Alarm;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class AlarmsParser extends Parser {
    private static final String TAG = "AlarmsParser";

    public static ArrayList<Alarm> parse(String input) {
        ArrayList<Alarm> values = new ArrayList<Alarm>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeListForExpression("/alarms/alarm", input);
        } catch (XPathExpressionException e) {
            Log.i(TAG, e.getMessage());
        }
        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression("@id", currentNode).getNodeValue();
                    Alarm alarm = new Alarm(Integer.parseInt(id));

                    Node description = getXmlNodeForExpression("description", currentNode);
                    if (description != null) alarm.setDescription(description.getTextContent());

                    Node severity = getXmlNodeForExpression("@severity", currentNode);
                    if (severity != null) alarm.setSeverity(severity.getNodeValue());

                    Node logMessage = getXmlNodeForExpression("logMessage", currentNode);
                    if (logMessage != null) alarm.setLogMessage(logMessage.getTextContent());

                    values.add(alarm);
                }
            }
        } catch (XPathExpressionException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (DOMException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return values;
    }

}
