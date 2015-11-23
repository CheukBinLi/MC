package com.ben.mc.AnthingTest.code_test.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ben.mc.AnthingTest.code_test.xml.DefaultConfigInfo.Bean;
import com.ben.mc.AnthingTest.code_test.xml.DefaultConfigInfo.CachePool;
import com.ben.mc.AnthingTest.code_test.xml.DefaultConfigInfo.Intercept;

public class XmlHandler extends DefaultHandler {

	LinkedList<String> queue = null;
	DefaultConfigInfo defaultConfigInfo = null;
	Bean bean = null;
	Intercept intercept = null;;
	CachePool cachePool = null;;

	public Object read() throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		queue = new LinkedList<String>();
		defaultConfigInfo = new DefaultConfigInfo();
		// parser.parse(new InputSource(new FileInputStream(new File(""))),
		// this);
		parser.parse(new InputSource(this.getClass().getClassLoader().getResourceAsStream("bean.xml")), this);
		return defaultConfigInfo;
	}

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		DefaultConfigInfo defaultConfigInfo = (DefaultConfigInfo) new XmlHandler().read();
		for (Entry<String, Bean> en : defaultConfigInfo.getBeans().entrySet())
			System.err.println(en.getKey() + " : " + en.getValue().getClassName());
		for (Entry<String, Intercept> en : defaultConfigInfo.getIntercepts().entrySet())
			System.err.println(en.getKey() + " : " + en.getValue().getClassName() + " m:" + en.getValue().getMethods());
		System.out.println(defaultConfigInfo.getCachePool().getClassName());
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (defaultConfigInfo.isBean(qName)) {
			bean = new Bean().fill(attributes);
			defaultConfigInfo.getBeans().put(bean.getName(), bean);
		}
		else if (defaultConfigInfo.isIntercepts(qName)) {
			intercept = new Intercept().fill(attributes);
			defaultConfigInfo.getIntercepts().put(intercept.getName(), intercept);
		}
		else if (defaultConfigInfo.isCachePool(qName)) {
			defaultConfigInfo.setCachePool(new CachePool().fill(attributes));
		}
		super.startElement(uri, localName, qName, attributes);
	}

}
