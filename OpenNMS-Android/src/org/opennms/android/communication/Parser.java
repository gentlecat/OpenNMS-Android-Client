package org.opennms.android.communication;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class Parser {

    public static NodeList getXmlNodeSetForExpression(String expression, String xml) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        InputSource inputSource = new InputSource(new StringReader(xml));
        return (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
    }

    public static Node getXmlNodeForExpression(String expression, Node widgetNode) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (Node) xpath.evaluate(expression, widgetNode, XPathConstants.NODE);
    }

}
