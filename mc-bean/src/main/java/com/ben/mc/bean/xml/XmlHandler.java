package com.ben.mc.bean.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ben.mc.bean.classprocessing.ConfigInfo;
import com.ben.mc.bean.xml.DefaultConfigInfo.Bean;
import com.ben.mc.bean.xml.DefaultConfigInfo.CachePool;
import com.ben.mc.bean.xml.DefaultConfigInfo.Intercept;

public class XmlHandler extends DefaultHandler {

	private static XmlHandler newInstance = new XmlHandler();

	public static XmlHandler newInstance() {
		return newInstance;
	}

	LinkedList<String> queue = null;
	DefaultConfigInfo defaultConfigInfo = null;
	Bean bean = null;
	Intercept intercept = null;
	CachePool cachePool = null;

	public ConfigInfo read(InputStream in) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		SAXParser parser = factory.newSAXParser();
		queue = new LinkedList<String>();
		defaultConfigInfo = new DefaultConfigInfo();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				return new InputSource(this.getClass().getClassLoader().getResourceAsStream("Mc.dtd"));
			}
		});
		xmlReader.setContentHandler(this);
		xmlReader.parse(new InputSource(in));
		//		parser.parse(new InputSource(this.getClass().getClassLoader().getResourceAsStream("bean.xml")), this);
		return defaultConfigInfo;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (defaultConfigInfo.isBean(qName)) {
			bean = new Bean().fill(attributes);
			defaultConfigInfo.getBeans().put(bean.getName(), bean);
		}
		else if (defaultConfigInfo.isIntercepts(qName)) {
			intercept = new Intercept().fill(attributes);
			defaultConfigInfo.getIntercepts().put(intercept.getClassName(), intercept);
		}
		else if (defaultConfigInfo.isCachePool(qName)) {
			defaultConfigInfo.setCachePool(new CachePool().fill(attributes));
		}
		else if (defaultConfigInfo.isScanToPack(qName)) {
			defaultConfigInfo.setScanToPack(attributes);
		}
		else if (defaultConfigInfo.isInitSystemClassLoader(qName)) {
			defaultConfigInfo.setInitSystemClassLoader(attributes);
		}
		super.startElement(uri, localName, qName, attributes);
	}

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		DefaultConfigInfo defaultConfigInfo = (DefaultConfigInfo) new XmlHandler().read(XmlHandler.class.getClassLoader().getResourceAsStream("bean.xml"));
		for (Entry<String, Bean> en : defaultConfigInfo.getBeans().entrySet())
			System.err.println(en.getKey() + " : " + en.getValue().getClassName());
		for (Entry<String, Intercept> en : defaultConfigInfo.getIntercepts().entrySet())
			System.err.println(en.getKey() + " : " + en.getValue().getClassName() + " m:" + en.getValue().getMethods());
		System.out.println(defaultConfigInfo.getCachePool().getClassName());
	}

}
