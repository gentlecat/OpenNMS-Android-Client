package org.opennms.gsoc.alarms;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.opennms.gsoc.model.OnmsAlarm;
import org.opennms.gsoc.util.OnmsParserUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * Class performers parsing actions on the alarms retrieved from the demo.opennms.org server.
 * 
 * @author melania galea
 *
 */
public class AlarmsParser {
	private static final String ALARMS_EXPRESSION = "/alarms/alarm";
	private static final String ALARM_ID = "@id";
	private static final String ALARM_SEVERITY = "@severity";
	private static final String ALARM_DESCRIPTION = "description";
	private static final String ALARM_LOG_MESSAGE = "lastEvent/logMessage";

	public static ArrayList<OnmsAlarm> parse(String is) {
		ArrayList<OnmsAlarm> values = new ArrayList<OnmsAlarm>();

		NodeList nodes = null;
		try {
			nodes = OnmsParserUtil.getXmlNodeSetForExpression(AlarmsParser.ALARMS_EXPRESSION, is);
		} catch (XPathExpressionException e) {
			Log.i("AlarmParser.getXmlNodeSetForExpression", e.getMessage());
		}

		try {
			if(nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);


					Node id = OnmsParserUtil.getXmlNodeForExpression(AlarmsParser.ALARM_ID, node);
					Node description = OnmsParserUtil.getXmlNodeForExpression(AlarmsParser.ALARM_DESCRIPTION, node);
					Node severity = OnmsParserUtil.getXmlNodeForExpression(AlarmsParser.ALARM_SEVERITY, node);
					Node logMessage = OnmsParserUtil.getXmlNodeForExpression(AlarmsParser.ALARM_LOG_MESSAGE, node);
					OnmsAlarm onmsAlarm = new OnmsAlarm(Integer.parseInt(id.getNodeValue()), severity.getNodeValue(), description.getTextContent(), logMessage.getTextContent());
					values.add(onmsAlarm);
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
