package com.soulstrewn.zonepos.Utility;

import com.soulstrewn.zonepos.Objects.Item;
import com.soulstrewn.zonepos.Objects.Modifiers;
import com.soulstrewn.zonepos.Objects.Option;
import com.soulstrewn.zonepos.Objects.Order;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: jeff
 * Date: Mar 2, 2009
 * Time: 6:29:15 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class Database {
    public static Item root;

    public static void SaveDatabase (OutputStream os, Item root)
    {
     		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom = null;
		try {
		DocumentBuilder db = dbf.newDocumentBuilder();
		dom = db.newDocument();
	    }catch(Exception E) {
            E.printStackTrace();
        }

        Element topEle = dom.createElement("items");
        Element rootEle = dom.createElement("categories");
        topEle.appendChild(rootEle);
        dom.appendChild(topEle);
        for(Item i : root.SubItems)
        {
            rootEle.appendChild(createTLElement(i, dom));
        }

        try
		{
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(os, format);

			serializer.serialize(dom);

		} catch(IOException ie) {
		    ie.printStackTrace();
		}

        
    }
    static Element createElement(Item i, Document d) {
        // middle level elements are items
        // they contain one description, one price, and possibly many modifiers


        Element element = d.createElement("item");
        element.setAttribute("name", i.Name);


        Element dElement = d.createElement("description");
        Text dText = d.createTextNode(i.Description);
        dElement.appendChild(dText);

        Element pElement = d.createElement("price");
        Text pText = d.createTextNode(String.valueOf(i.Price));
        pElement.appendChild(pText);

        Element dcElement = d.createElement("discount");
        Text dcText = d.createTextNode(i.isDiscount?"true":"false");
        dcElement.setAttribute("percent",i.isPercent?"true":"false");
        dcElement.appendChild(dcText);

        element.appendChild(dElement);
        element.appendChild(pElement);
        element.appendChild(dcElement);

        for(Modifiers m : i.Mods)
        {
            element.appendChild(createMods(m, d));
        }

        return element;
    }
    static Element createMods(Modifiers m, Document d) {
        Element Melement = d.createElement("modifier");
        Melement.setAttribute("name", m.Name);
        Melement.setAttribute("type", m.Type.toString());

        for(Option o : m.Options)
        {
            Element oElement = d.createElement("option");
            oElement.setAttribute("name", o.Name);
            oElement.setAttribute("price", String.valueOf(o.Price));
            Melement.appendChild(oElement);
        }

        return Melement;
    }
    static Element createTLElement(Item i, Document d)
    {
        // top level elements are categories
        // they contain one description, many items, and possibly many modifiers

        Element TLElement = d.createElement("category");
        TLElement.setAttribute("name", i.Name);
        TLElement.setAttribute("type", i.type.toString());


        Element dElement = d.createElement("description");
        Text dText = d.createTextNode(i.Description);
        dElement.appendChild(dText);
        TLElement.appendChild(dElement);

        for(Item it : i.SubItems)
        {
            TLElement.appendChild(createElement(it, d));
        }
        for(Modifiers m : i.Mods)
        {
            TLElement.appendChild(createMods(m, d));
        }

        return TLElement;
    }

    public Database(String fromFile)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(fromFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("category");

        root = new Item();
        root.Name = "";
        root.Description = "Menu Items";


        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                Item newItem = new Item();
                newItem.Name = el.getAttribute("name");
                newItem.type =Item.ItemTypes.valueOf(el.getAttribute("type"));
                 Node el2 = el.getFirstChild();
                    while(el2 != null) {
                        if (el2.getNodeName().equals("description")) {
                            newItem.Description = el2.getTextContent();
                        } else if (el2.getNodeName().equals("item")) {
                            Item ChildItem = ItemFromElement(el2);
                            newItem.SubItems.add(ChildItem);
                        } else if (el2.getNodeName().equals("modifier")) {
                            Modifiers Modifier = ModFromElement(el2);
                            newItem.Mods.add(Modifier);
                        }
                        el2 = el2.getNextSibling();

                    }

                root.SubItems.add(newItem);
            }
        }
    }

    static Item ItemFromElement(Node n)
    {
       Item i = new Item();
        i.Name = ((Element)n).getAttribute("name");

        Node el2 = n.getFirstChild();
                    while(el2 != null) {
                        if (el2.getNodeName().equals("description")) {
                            i.Description = el2.getTextContent();
                        } else if (el2.getNodeName().equals("price")) {
                            i.Price = Integer.parseInt(el2.getTextContent());
                        } else if (el2.getNodeName().equals("modifier")) {
                            Modifiers Modifier = ModFromElement(el2);
                            i.Mods.add(Modifier);
                        } else if (el2.getNodeName().equals("discount")) {
                            i.isPercent = ((Element) el2).getAttribute("percent").equals("true");
                            i.isDiscount = el2.getTextContent().equals("true");
                        }
                        el2 = el2.getNextSibling();

                    }
        return i;
    }

    static Modifiers ModFromElement(Node n)
    {
       Modifiers i = new Modifiers();
        i.Name = ((Element)n).getAttribute("name");
        i.Type = Modifiers.types.valueOf(((Element)n).getAttribute("type"));
        Node el2 = n.getFirstChild();
                    while(el2 != null) {
                        if (el2.getNodeName().equals("option")) {
                            Option o = new Option();
                            o.Name = ((Element)el2).getAttribute("name");
                            o.Price = Integer.parseInt(((Element)el2).getAttribute("price"));
                            i.Options.add(o);
                        }
                        el2 = el2.getNextSibling();

                    }
        return i;    }
    

}
