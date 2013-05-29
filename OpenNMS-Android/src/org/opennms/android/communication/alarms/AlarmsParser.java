package org.opennms.android.communication.alarms;

import android.util.Log;
import org.opennms.android.dao.alarms.Alarm;
import org.opennms.android.communication.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

/**
 * Class performers parsing actions on the alarms retrieved from the demo.opennms.org server.
 *
 * @author melania galea
 */
public class AlarmsParser extends Parser {

    private static final String ALARMS_EXPRESSION = "/alarms/alarm";
    private static final String ALARM_ID = "@id";
    private static final String ALARM_SEVERITY = "@severity";
    private static final String ALARM_DESCRIPTION = "description";
    private static final String ALARM_LOG_MESSAGE = "lastEvent/logMessage";

    public static ArrayList<Alarm> parse(String is) {
        ArrayList<Alarm> values = new ArrayList<Alarm>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeSetForExpression(ALARMS_EXPRESSION, is);
        } catch (XPathExpressionException e) {
            Log.i("AlarmParser.getXmlNodeSetForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);

                    String id = getXmlNodeForExpression(ALARM_ID, currentNode).getNodeValue();
                    Alarm alarm = new Alarm(Integer.parseInt(id));

                    Node description = getXmlNodeForExpression(ALARM_DESCRIPTION, currentNode);
                    if (description != null) alarm.setDescription(description.getTextContent());

                    Node severity = getXmlNodeForExpression(ALARM_SEVERITY, currentNode);
                    if (severity != null) alarm.setSeverity(severity.getNodeValue());

                    Node logMessage = getXmlNodeForExpression(ALARM_LOG_MESSAGE, currentNode);
                    if (logMessage != null) alarm.setLogMessage(logMessage.getTextContent());

                    values.add(alarm);
                }
            }
        } catch (XPathExpressionException e) {
            Log.e("node attributes", e.getMessage(), e);
        } catch (NumberFormatException e) {
            Log.e("node attributes", e.getMessage(), e);
        } catch (DOMException e) {
            Log.e("node attributes", e.getMessage(), e);
        }
        return values;
    }

}
