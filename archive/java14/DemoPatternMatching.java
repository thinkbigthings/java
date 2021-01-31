package org.thinkbigthings.demo.java14;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;


public class DemoPatternMatching {

    public static void main(String[] args) throws Exception {

        File xmlFile = new File("/Users/jason-dev/dev/java/java-14/books.xml");
        String content = Files.lines(xmlFile.toPath()).collect(Collectors.joining());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        Document doc = dBuilder.parse(stream);

        Element root = doc.getDocumentElement();
        System.out.println(root.getTagName());

        // try for XML parser where you DO use instanceof...
        recursivePrint(root);

//        // Also useful for equals() implementations...


        // can do specific checks on typed object too
//        Object object = "this is a string";
//        if (object instanceof String s) {
//            System.out.println(s + " ... has length " + s.length());
//        }
//        else {
//            System.out.println("object is not a string");
//        }
//
//        Object t2 = "";
//        if (t2 instanceof String s && ! s.isEmpty()) {
//            System.out.println(s + " ... has length " + s.length());
//        }
//        else {
//            System.out.println("object is not a non-empty string");
//        }
    }

    // https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/org/w3c/dom/Node.html
    public static void recursivePrint(Node node) {

        if(node instanceof Attr) {
            // attr has getName() and getValue()
            Attr attribute = (Attr)node;
            System.out.println("Attr: " + attribute.getName() + ": " + attribute.getValue());
        }
        if(node instanceof Element) {
            Element element = ((Element)node);
            System.out.println("Element: " + element.getTagName());
            for(int a = 0; a < element.getAttributes().getLength(); a++) {
                recursivePrint(element.getAttributes().item(a));
            }
        }
        if(node instanceof Text) {
            String text = ((Text)node).getWholeText();
            System.out.println("Text: " + text);
        }
        if(node instanceof Comment) {
            String comment = ((Comment)node).getTextContent();
            System.out.println("Comment: " + comment);
        }

        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            recursivePrint(child);
        }

    }

    public static void recursivePatternMatchingPrint(Node node) {

        if(node instanceof Attr attribute) {
            System.out.println("Attr: " + attribute.getName() + ": " + attribute.getValue());
        }
        if(node instanceof Element element) {
            System.out.println("Element: " + element.getTagName());
            for(int a = 0; a < element.getAttributes().getLength(); a++) {
                recursivePrint(element.getAttributes().item(a));
            }
        }
        if(node instanceof Text text) {
            System.out.println("Text: " + text.getWholeText());
        }
        if(node instanceof Comment comment) {
            System.out.println("Comment: " + comment.getTextContent());
        }

        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            recursivePrint(child);
        }
    }
}