package org.opennms.gsoc.outages;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.opennms.gsoc.model.OnmsOutage;
import org.opennms.gsoc.util.OnmsParserUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class OutagesParser {
	private static final String OUTAGES_EXPRESSION = "/outages/outage";
	private static final String OUTAGES_IF_LOST_SERVICE = "ifLostService";
	private static final String OUTAGES_ID = "@id";
	private static final String OUTAGES_IF_REGAINED_SERVICE = "ifRegainedService";
	private static final String OUTAGES_IP_ADDRESS = "ipAddress";
	
	public static ArrayList<OnmsOutage> parse(String is) {
		ArrayList<OnmsOutage> values = new ArrayList<OnmsOutage>();

		NodeList nodes = null;
		try {
			nodes = OnmsParserUtil.getXmlNodeSetForExpression(OutagesParser.OUTAGES_EXPRESSION, is);
		} catch (XPathExpressionException e) {
			Log.i("NodeParser.getXmlNodeSetForExpression", e.getMessage());
		}
		
		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				

				Node id = OnmsParserUtil.getXmlNodeForExpression(OutagesParser.OUTAGES_ID, node);
				Node ipAddress = OnmsParserUtil.getXmlNodeForExpression(OutagesParser.OUTAGES_IP_ADDRESS, node);
				Node ifLostService = OnmsParserUtil.getXmlNodeForExpression(OutagesParser.OUTAGES_IF_LOST_SERVICE, node);
				
				Node ifRegainedService = OnmsParserUtil.getXmlNodeForExpression(OutagesParser.OUTAGES_IF_REGAINED_SERVICE, node);
				
				OnmsOutage onmsOutage = new OnmsOutage(Integer.parseInt(id.getNodeValue()), ipAddress.getNodeValue(), ifLostService.getNodeValue(), ifRegainedService.getTextContent());
				values.add(onmsOutage);
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
