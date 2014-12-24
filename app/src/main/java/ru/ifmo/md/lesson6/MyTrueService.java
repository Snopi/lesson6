package ru.ifmo.md.lesson6;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static java.lang.Thread.sleep;

/**
 * Created by Snopi on 01.12.2014.
 */
public class MyTrueService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyTrueService(String name) {
        super(name);
    }

    public MyTrueService() {
        super("tral");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String channel = "";
        String url = intent.getStringExtra(DataBaseHelper.COLUMNS.CHANNEL_URL);
        ArrayList<FeedItem> ans = new ArrayList<FeedItem>();

        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            MySaxParser handler = new MySaxParser();
            URL smth = new URL(url);
            InputStream in = (InputStream) smth.getContent();
            char tmp;
            StringBuilder x = new StringBuilder();
            while ((tmp = (char) in.read()) != '>') {
                x.append(tmp);
            }
            String str = x.toString();
            String[] t = str.split("encoding=");
            String enc = t[t.length - 1];
            String charset = enc.substring(1, enc.lastIndexOf(enc.charAt(0)));
            //Log.i("allhttp", scanner.next());
            InputSource encIn = new InputSource(new InputStreamReader(in, charset));
            parser.parse(encIn, handler);
            ans = handler.getStorage();
            channel = handler.getChannelName();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        ContentValues cv = new ContentValues();
        cv.put(DataBaseHelper.COLUMNS.CHANNEL_NAME, channel);
        getContentResolver().update(Provider.Constants.CONTENT_URI, cv,
                DataBaseHelper.COLUMNS.SPECIAL_FLAG + "=? AND " + DataBaseHelper.COLUMNS.URL + " =? ",
                new String[] {String.valueOf(DataBaseHelper.COLUMNS.CHANNEL_FLAG), url});
        for (int i = ans.size() - 1; i >= 0; i--) {
            FeedItem x = ans.get(i);
            cv.clear();
            cv.put(DataBaseHelper.COLUMNS.URL, x.getUrl());
            cv.put(DataBaseHelper.COLUMNS.CHANNEL_NAME, channel);
            cv.put(DataBaseHelper.COLUMNS.DESCRIPTION, x.getDescription());
            cv.put(DataBaseHelper.COLUMNS.TITLE, x.getTitle());
            cv.put(DataBaseHelper.COLUMNS.SPECIAL_FLAG, DataBaseHelper.COLUMNS.FEED_FLAG);
            cv.put(DataBaseHelper.COLUMNS.TIME, x.getDate());
            cv.put(DataBaseHelper.COLUMNS.CHANNEL_URL, url);
            getContentResolver().insert(Provider.Constants.CONTENT_URI, cv);
        }
    }
}
