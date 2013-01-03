package org.opennms.gsoc.alarms;

import android.util.Log;
import org.opennms.gsoc.util.Parser;
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
            nodes = getXmlNodeSetForExpression(AlarmsParser.ALARMS_EXPRESSION, is);
        } catch (XPathExpressionException e) {
            Log.i("AlarmParser.getXmlNodeSetForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);
                    Node id = getXmlNodeForExpression(AlarmsParser.ALARM_ID, currentNode);
                    Node description = getXmlNodeForExpression(AlarmsParser.ALARM_DESCRIPTION, currentNode);
                    Node severity = getXmlNodeForExpression(AlarmsParser.ALARM_SEVERITY, currentNode);
                    Node logMessage = getXmlNodeForExpression(AlarmsParser.ALARM_LOG_MESSAGE, currentNode);
                    Alarm alarm = new Alarm(
                            Integer.parseInt(id.getNodeValue()),
                            severity.getNodeValue(),
                            description.getTextContent(),
                            logMessage.getTextContent()
                    );
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
