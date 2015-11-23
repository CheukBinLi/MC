package com.ben.mc.AnthingTest.code_test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class xmlX {

	public static void main(String[] args) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(this.getClass().getClassLoader().getSystemResourceAsStream("Mc.dtd"));
				}
			});
			builder.setErrorHandler(new ErrorHandler() {

				public void warning(SAXParseException exception) throws SAXException {
					System.err.println("warning");
				}

				public void fatalError(SAXParseException exception) throws SAXException {
					System.err.println("fatalError");
				}

				public void error(SAXParseException exception) throws SAXException {
					// throw new SAXException("不会法");
					throw new SAXException(exception);
				}
			});
			builder.parse(new File("E:/javaProject/Eclipse/MC/mc-util/src/main/java/bean.xml"));
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setValidating(true);
			SAXParser parser = spf.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			String dtd = xmlX.class.getClassLoader().getResource("Mc.dtd").toString();
			// reader.setFeature("E:/javaProject/Eclipse/MC/mc-util/target/classes/Mc.dtd",
			// true);
			reader.setContentHandler(new ContentHandler() {

				public void startPrefixMapping(String prefix, String uri) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void startElement(String uri, String localName, String qName, Attributes atts)
						throws SAXException {
					// TODO Auto-generated method stub

				}

				public void startDocument() throws SAXException {
					// TODO Auto-generated method stub

				}

				public void skippedEntity(String name) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void setDocumentLocator(Locator locator) {
					// TODO Auto-generated method stub

				}

				public void processingInstruction(String target, String data) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void endPrefixMapping(String prefix) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void endDocument() throws SAXException {
					// TODO Auto-generated method stub

				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					// TODO Auto-generated method stub

				}
			});
			reader.setEntityResolver(new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(this.getClass().getClassLoader().getSystemResourceAsStream("Mc.dtd"));
				}
			});
			reader.setErrorHandler(new ErrorHandler() {

				public void warning(SAXParseException exception) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void fatalError(SAXParseException exception) throws SAXException {
					// TODO Auto-generated method stub

				}

				public void error(SAXParseException exception) throws SAXException {
					// TODO Auto-generated method stub

				}
			});
			reader.parse(new InputSource(xmlX.class.getClassLoader().getSystemResourceAsStream("bean.xml")));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
