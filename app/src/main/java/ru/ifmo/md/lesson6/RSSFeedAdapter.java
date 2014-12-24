package ru.ifmo.md.lesson6;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by Snopi on 24.12.2014.
 */
public class RSSFeedAdapter extends CursorAdapter {
    public RSSFeedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.feeditem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.title)).setText(cursor.getString(1));
        ((TextView) view.findViewById(R.id.description)).setText(cursor.getString(2));
        ((TextView) view.findViewById(R.id.date)).setText(cursor.getString(3));
        ((TextView) view.findViewById(R.id.hidden_url_to_webview)).setText(cursor.getString(4));
    }
}
