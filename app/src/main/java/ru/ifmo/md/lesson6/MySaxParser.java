package ru.ifmo.md.lesson6;

import android.text.Html;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Snopi on 10.11.2014.
 */
public class MySaxParser extends DefaultHandler {
    private ArrayList<FeedItem> storage;
    private FeedItem currentFeed;
    private boolean title = false;
    private boolean time = false;
    private boolean link = false;
    private boolean description = false;
    private boolean channelInfo = false;
    private boolean item = false;
    private String channelName = "";
    private StringBuilder tmpStr;
    public String getChannelName() {
        return channelName;
    }

    public ArrayList<FeedItem> getStorage() {
        return storage;
    }

    @Override
    public void startDocument() throws SAXException {
        storage = new ArrayList<FeedItem>();
    }
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        Log.i("I'm from start element", qName);
        if (qName.equalsIgnoreCase("title")) {
            title = true;
            tmpStr = new StringBuilder();
        } else if (item && qName.equalsIgnoreCase("pubdate")) {
            time = true;
            tmpStr = new StringBuilder();
        } else if (qName.equalsIgnoreCase("item")) {
            currentFeed = new FeedItem();
            item = true;
        } else if (item && qName.equalsIgnoreCase("link")) {
            link = true;
            tmpStr = new StringBuilder();
        } else if (item && (qName.equalsIgnoreCase("summary")
        || qName.equalsIgnoreCase("description"))) {
            description = true;
            tmpStr = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("item") && item) {
            storage.add(currentFeed);
            item = false;
        } if (item && title && qName.equalsIgnoreCase("title")) {
            title = false;
            currentFeed.setTitle(tmpStr.toString());
        } else if (time && qName.equalsIgnoreCase("pubdate")) {
            time = false;
            currentFeed.setDate(tmpStr.toString());
        }  else if (link && qName.equalsIgnoreCase("link")) {
            link = false;
            currentFeed.setUrl(tmpStr.toString());
        } else if (description && (qName.equalsIgnoreCase("summary")
                || qName.equalsIgnoreCase("description"))) {
            description = false;
            currentFeed.setDescription(Html.fromHtml(tmpStr.toString()).toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (title) {
            if (!channelInfo) {
                channelInfo = true;
                title = false;
                channelName = new String(ch, start, length);
            } else if (item) {
                tmpStr.append(ch, start, length);
            }
        } else if (link || description || time) {
            tmpStr.append(ch, start, length);
        }
    }
}
