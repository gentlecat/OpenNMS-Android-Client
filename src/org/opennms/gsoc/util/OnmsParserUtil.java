package org.opennms.gsoc.util;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class OnmsParserUtil {

	public static  NodeList getXmlNodeSetForExpression(String expression,
			String is) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(is));
		return (NodeList) xpath.evaluate(expression, inputSource,
				XPathConstants.NODESET);
	}

	public static Node getXmlNodeForExpression(String expression, Node widgetNode)
			throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return (Node) xpath.evaluate(expression, widgetNode,
				XPathConstants.NODE);

	}
}
