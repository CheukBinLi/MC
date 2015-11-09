package com.ben.mc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlReader extends DefaultHandler {
	private List<String> list = null;
	private Map<String, List<String>> result;
	private boolean isStart;
	private String startNode;
	private String tag;

	public Map<String, List<String>> read(InputStream in, String startNode) throws ParserConfigurationException, SAXException, IOException {
		this.startNode = startNode;
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		result = new HashMap<String, List<String>>();
		saxParser.parse(in, this);
		return result;
	}

	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (isStart) {
			tag = qName;
			System.err.println("attr:"+attributes.getValue("value"));
			System.err.println("attr:"+attributes.getLength());
			list = new ArrayList<String>();
			result.put(tag, list);
		}
		if (qName.equals(startNode))
			isStart = true;

		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (tag == qName)
			tag = null;
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (null != tag) {
			list.add(new String(ch, start, length));
			//			System.out.println(new String(ch, start, length));
		}
		//super.characters(ch, start, length);
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("ScanClassContext.xml");
		Map<String, List<String>> result = new XmlReader().read(in, "bean");
		for (Entry<String, List<String>> en : result.entrySet()) {
			System.out.println(en.getKey());
			for (String s : (List<String>) en.getValue()) {
				System.out.println("          -------------:" + s);
			}
		}
	}
}
