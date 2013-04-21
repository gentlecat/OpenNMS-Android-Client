package org.opennms.android.communication.events;

import android.util.Log;
import org.opennms.android.dao.events.Event;
import org.opennms.android.util.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class EventsParser extends Parser {

    private static final String EVENTS_EXPRESSION = "/events/event";
    private static final String EVENT_ID = "@id";
    private static final String EVENT_SEVERITY = "@severity";
    private static final String EVENT_DESCRIPTION = "description";

    public static ArrayList<org.opennms.android.dao.events.Event> parse(String is) {
        ArrayList<org.opennms.android.dao.events.Event> values = new ArrayList<org.opennms.android.dao.events.Event>();

        NodeList nodes = null;
        try {
            nodes = getXmlNodeSetForExpression(EVENTS_EXPRESSION, is);
        } catch (XPathExpressionException e) {
            Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
        }

        try {
            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node currentNode = nodes.item(i);
                    Node id = getXmlNodeForExpression(EVENT_ID, currentNode);
                    Node description = getXmlNodeForExpression(EVENT_DESCRIPTION, currentNode);
                    Node severity = getXmlNodeForExpression(EVENT_SEVERITY, currentNode);
                    Event event = new Event(
                            Integer.parseInt(id.getNodeValue()),
                            severity.getNodeValue(),
                            description.getTextContent()
                    );
                    values.add(event);
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
