package xml;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.lang.String;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.CityMap;
import model.Intersection;
import model.Road;
import model.Distribution;
import model.Request;
import model.DepotAddress;

/**
 * @author 4IF-4114
 */
public class XMLDeserializer {

    /**
     * Default constructor
     */
    public XMLDeserializer() {
    }

    /**
     * Open an XML file and create plan from this file
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XMLException
     * @param cityMap 
     * @return
     */
    public static void loadCityMap(CityMap cityMap) throws ParserConfigurationException, SAXException, IOException, XMLException {
        File xml = XMLFileOpener.getInstance().open(true);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("citymap")) { //unsure
            buildCityMapFromDOMXML(root, cityMap);
        }
        else {
            throw new XMLException("Wrong format");
        }
    }

    /**
     * @param distribution 
     * @return
     */
    public static void loadDistribution(Distribution distribution) {
        // TODO implement here
    }

    /**
     * @param rootDOMNode 
     * @param cityMap 
     * @return
     */
    private static void buildCityMapFromDOMXML(Element rootDOMNode, CityMap cityMap) throws XMLException, NumberFormatException {
        Double height = Double.parseDouble(rootDOMNode.getAttribute("height"));
        if (height <= 0)
            throw new XMLException("Error when reading file: The plan height must be positive");
        Double width = Double.parseDouble(rootDOMNode.getAttribute("width"));
        if (width <= 0)
            throw new XMLException("Error when reading file: The plan width must be positive");
        cityMap.reset(width,height);

        NodeList intersectionList = rootDOMNode.getElementsByTagName("intersection");
        for (int i = 0; i < intersectionList.getLength(); i++) {
            cityMap.addIntersection(createIntersection((Element) intersectionList.item(i)));
        }

        NodeList roadList = rootDOMNode.getElementsByTagName("segment");
        for (int i = 0; i < roadList.getLength(); i++) {
            Element elt= (Element) roadList.item(i);
            int id1 = Integer.parseInt(elt.getAttribute("origin"));
            int id2 = Integer.parseInt(elt.getAttribute("destination"));
            cityMap.addRoad(createRoad(elt),id1,id2);
        }
    }



    /**
     * @param rootDOMNode 
     * @param distribution 
     * @return
     */
    public static final void buildDistributionFromDOMXML(Element rootDOMNode, Distribution distribution)  {
        // TODO implement here
    }

    /**
     * @param elt 
     * @return
     */
    private static Intersection createIntersection(Element elt) {
        int id = Integer.parseInt(elt.getAttribute("id"));
        double latitude = Double.parseDouble(elt.getAttribute("latitude"));
        double longitude = Double.parseDouble(elt.getAttribute("longitude"));

        return new Intersection(id,latitude,longitude);
    }

    /**
     * @param elt 
     * @return
     */
    private static Road createRoad(Element elt) {

        String name = elt.getAttribute("name");
        double length = Double.parseDouble(elt.getAttribute("length"));

        return new Road(name,length);
    }

    /**
     * @param elt 
     * @return
     */
    public static Request createRequest(Element elt) {
        // TODO implement here
        return null;
    }

    /**
     * @param elt 
     * @return
     */
    public static final DepotAddress createDepot(Element elt) {
        // TODO implement here
        return null;
    }

}