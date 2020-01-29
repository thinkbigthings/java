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
        // yield becomes the value of the evaulated expression, don't use return because you'd return from the outer function
        // use yield if you need to evaluate a block of code in the switch case.

        // TODO use text blocks for XML test content

        // TODO use short values from Node.getNodeType() and switch on short to demonstrate parsing
        File xmlFile = new File("/Users/jason-dev/dev/java/java-14/books.xml");
        String content = Files.lines(xmlFile.toPath()).collect(Collectors.joining());
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
}