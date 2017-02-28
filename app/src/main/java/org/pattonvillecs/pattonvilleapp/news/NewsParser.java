package org.pattonvillecs.pattonvilleapp.news;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;
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

/**
 * Class to handle parsing the news listings from fccms.psdr3.org
 * If you wish to add more parsed information, add to NewsArticle and set properties as necessary
 * within endElement()
 *
 * @author Nathan Skelton
 * @author Jeremiah Simmons
 */
public class NewsParser extends DefaultHandler {

    private NewsArticle item = null;
    private String currentValue = "", responseData;
    private ArrayList<NewsArticle> items = new ArrayList<>();
    private DataSource dataSource;

    public NewsParser(String responseData, DataSource dataSource) {
        this.responseData = responseData;
        this.dataSource = dataSource;
    }

    public ArrayList<NewsArticle> getItems() {
        return items;
    }

    /**
     * Method which handles start tags, creating a new NewsArticle as necessary
     * Called when '<' is found
     *
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        currentValue = "";
        switch (qName.toLowerCase()) {
            case "item":
                item = new NewsArticle();
                break;
        }
    }

    /**
     * Method which saves the content found within '<' and '>'
     *
     * @throws SAXException
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        currentValue = currentValue + new String(ch, start, length);
    }

    /**
     * Method which handles end tags, modifying the current NewsArticle if necessary, or adding the
     * current one to the internal ArrayList<NewsArticle>
     *
     * @throws SAXException
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (item != null) {
            switch (qName.toLowerCase()) {
                case "item":
                    // Add finished item to items
                    item.setDataSource(dataSource);
                    items.add(item);
                    break;
                case "title":
                    item.setTitle(currentValue);
                    break;
                case "link":
                    item.setPublicUrl(dataSource.websiteURL + "?" + currentValue);
                    item.setPrivateUrl("http://fccms.psdr3.org" + currentValue);
                    break;
                case "pubdate":

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss Z", Locale.US);
                    try {
                        item.setPublishDate(dateFormat.parse(currentValue));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Setting default date to news article if unable to parse date
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

    /**
     * Method which handles using the combination of methods in parsing the news xml given
     */
    void getXml() {
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