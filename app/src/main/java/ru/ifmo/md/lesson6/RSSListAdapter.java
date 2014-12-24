package ru.ifmo.md.lesson6;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Snopi on 30.11.2014.
 */
public class RSSListAdapter extends CursorAdapter {
    public RSSListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.channel_view_chunk, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.channel_title_list);
        name.setText(cursor.getString(1));
        TextView num = (TextView) view.findViewById(R.id.hidden_view);
        num.setText(cursor.getString(2));
    }
}
