package org.pattonvillecs.pattonvilleapp.fragments.news;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.fragments.news.articles.NewsArticle;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class NewsParser extends DefaultHandler {

    NewsArticle item = null;
    String currentValue = "";
    ArrayList<NewsArticle> items = new ArrayList<>();
    String responseData;
    DataSource dataSource;

    public NewsParser(String responseData, DataSource dataSource) {
        this.responseData = responseData;
        this.dataSource = dataSource;
    }

    public ArrayList<NewsArticle> getItems() {
        return items;
    }

    // this method is called every time the parser gets an open tag '<'
    // identifies which tag is being open at time by assigning an open flag
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        currentValue = "";
        switch (qName.toLowerCase()) {
            case "item":
                item = new NewsArticle();
                break;
        }
    }

    // prints data stored in between '<' and '>' tags
    public void characters(char ch[], int start, int length)
            throws SAXException {
        currentValue = currentValue + new String(ch, start, length);
    }

    // calls by the parser whenever '>' end tag is found in xml
    // makes tags flag to 'close'
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (item != null) {
            switch (qName.toLowerCase()) {
                case "item":
                    item.setSourceColor(dataSource.calendarColor);
                    items.add(item);
                    break;
                case "title":
                    item.setTitle(currentValue);
                    break;
                case "link":
                    item.setUrl(dataSource.websiteURL + "/?" + currentValue);
                    break;
                case "author":
                    //item.setAuthor(currentValue);
                    break;
                case "pubdate":

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss Z", Locale.US);
                    try {
                        item.setPublishDate(dateFormat.parse(currentValue));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        item.setPublishDate(new Date(1484300000));
                    }
                    break;
            }
        }
    }

    // parse the XML specified in the given path and uses supplied
    // handler to parse the document
    // this calls startElement(), endElement() and character() methods
    // accordingly
    public void getXml() {
        try {
            // obtain and configure a SAX based parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            // obtain object for SAX parser
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader reader = saxParser.getXMLReader();
            reader.setContentHandler(this);
            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(responseData));
            reader.parse(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}