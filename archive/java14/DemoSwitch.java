package org.thinkbigthings.demo.java14;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import static org.w3c.dom.Node.*;

public class DemoSwitch {

    public static void main(String[] args) throws Exception {

        DAY_OF_WEEK day = DAY_OF_WEEK.MONDAY;

        switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> System.out.println(6);
            case TUESDAY                -> System.out.println(7);
            case THURSDAY, SATURDAY     -> System.out.println(8);
            case WEDNESDAY              -> System.out.println(9);
        }

        // switch expression
        int numLetters = switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY                -> 7;
            case THURSDAY, SATURDAY     -> 8;
            case WEDNESDAY              -> 9;
        };

        System.out.println(numLetters);


        // TODO yield statement, difference from return?
        // see switch expression vs statement: statement can NOT use yield, expression CAN.
        // flipped for break
        // yield becomes the value of the evaulated expression, don't use return because you'd return from the function
        // use yield if you need to evaluate a block of code in the switch case.

        // TODO use short values from Node.getNodeType() and switch on short to demonstrate parsing
//        File xmlFile = new File("/Users/jason-dev/dev/java/java-14/books.xml");
//        String content = Files.lines(xmlFile.toPath()).collect(Collectors.joining());
        String content = booksXML;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        Document doc = dBuilder.parse(stream);

        Element root = doc.getDocumentElement();

        // TODO demonstrate what happens on missing case
        // (switch on short needs a default)

        recursiveSwitchPrint(root, "");
    }

    enum DAY_OF_WEEK {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // based on http://www.java2s.com/Code/JavaAPI/org.w3c.dom/NodegetNodeType.htm
    public static void recursiveSwitchPrint(Node node, String indent) {

        short nodeType = node.getNodeType();

        String type = switch(nodeType) {
            case ELEMENT_NODE                -> "Element";
            case DOCUMENT_TYPE_NODE          -> "Document type";
            case ENTITY_NODE                 -> "Entity";
            case ENTITY_REFERENCE_NODE       -> "Entity reference";
            case NOTATION_NODE               -> "Notation";
            case TEXT_NODE                   -> "Text";
            case COMMENT_NODE                -> "Comment";
            case CDATA_SECTION_NODE          -> "CDATA Section";
            case ATTRIBUTE_NODE              -> "Attribute";
            case PROCESSING_INSTRUCTION_NODE -> "Attribute";
            default -> throw new IllegalArgumentException("Node type not recognized: " + nodeType);
        };

        System.out.println(indent + type);

        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            recursiveSwitchPrint(child, indent + " ");
        }
    }


    // Can use text blocks for test content
    private final static String booksXML = """
    <?xml version="1.0"?>
    <catalog>
        <!-- I added a handy comment here -->
        <book id="bk101">
            <author>Gambardella, Matthew</author>
            <title>XML Developer's Guide</title>
            <genre>Computer</genre>
            <price>44.95</price>
            <publish_date>2000-10-01</publish_date>
            <description>An in-depth look at creating applications
             with XML.</description>
        </book>
        <book id="bk102">
            <author>Ralls, Kim</author>
            <title>Midnight Rain</title>
            <genre>Fantasy</genre>
            <price>5.95</price>
            <publish_date>2000-12-16</publish_date>
            <description>A former architect battles corporate zombies,
            an evil sorceress, and her own childhood to become queen
            of the world.</description>
        </book>
        <book id="bk103">
            <author>Corets, Eva</author>
            <title>Maeve Ascendant</title>
            <genre>Fantasy</genre>
            <price>5.95</price>
            <publish_date>2000-11-17</publish_date>
            <description>After the collapse of a nanotechnology
            society in England, the young survivors lay the
            foundation for a new society.</description>
        </book>
        <book id="bk104">
            <author>Corets, Eva</author>
            <title>Oberon's Legacy</title>
            <genre>Fantasy</genre>
            <price>5.95</price>
            <publish_date>2001-03-10</publish_date>
            <description>In post-apocalypse England, the mysterious
            agent known only as Oberon helps to create a new life
                    for the inhabitants of London. Sequel to Maeve
                Ascendant.</description>
        </book>
        <book id="bk105">
            <author>Corets, Eva</author>
            <title>The Sundered Grail</title>
            <genre>Fantasy</genre>
            <price>5.95</price>
            <publish_date>2001-09-10</publish_date>
            <description>The two daughters of Maeve, half-sisters,
            battle one another for control of England. Sequel to
            Oberon's Legacy.</description>
        </book>
        <book id="bk106">
            <author>Randall, Cynthia</author>
            <title>Lover Birds</title>
            <genre>Romance</genre>
            <price>4.95</price>
            <publish_date>2000-09-02</publish_date>
            <description>When Carla meets Paul at an ornithology
            conference, tempers fly as feathers get ruffled.</description>
        </book>
        <book id="bk107">
            <author>Thurman, Paula</author>
            <title>Splish Splash</title>
            <genre>Romance</genre>
            <price>4.95</price>
            <publish_date>2000-11-02</publish_date>
            <description>A deep sea diver finds true love twenty
            thousand leagues beneath the sea.</description>
        </book>
        <book id="bk108">
            <author>Knorr, Stefan</author>
            <title>Creepy Crawlies</title>
            <genre>Horror</genre>
            <price>4.95</price>
            <publish_date>2000-12-06</publish_date>
            <description>An anthology of horror stories about roaches,
            centipedes, scorpions  and other insects.</description>
        </book>
        <book id="bk109">
            <author>Kress, Peter</author>
            <title>Paradox Lost</title>
            <genre>Science Fiction</genre>
            <price>6.95</price>
            <publish_date>2000-11-02</publish_date>
            <description>After an inadvertant trip through a Heisenberg
            Uncertainty Device, James Salway discovers the problems
            of being quantum.</description>
            </book>
    </catalog>
    """;
}