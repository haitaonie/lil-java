package com.lil.jdk8;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class MigrationDemo {
	public static void main(String[] args) throws Exception {
		
		String message="<root>123</root>";
		System.out.println(message);
		message = xmlFormat(message);
		System.out.println(message);
		
boolean value =	 DatatypeConverter.parseBoolean("false");
System.out.println(value);
	}
	public static String xmlFormat(String xml) throws Exception {
	    if (null == xml || xml.trim().isEmpty()) {
	        return "Empty/Null xml content";
	    }
	    String message;
	    Source xmlInput = new StreamSource(new StringReader(xml));
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, xmlOutput);
        message = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
	    return message;
	}
}
