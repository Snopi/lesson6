package ru.ifmo.md.lesson6;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Snopi on 24.12.2014.
 */
public class RSSFeedActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int CONSTANT_LOADER = 1;
    private static final String[] PROJECTION = new String[]{
            DataBaseHelper.COLUMNS.ID,
            DataBaseHelper.COLUMNS.TITLE,
            DataBaseHelper.COLUMNS.DESCRIPTION,
            DataBaseHelper.COLUMNS.TIME,
            DataBaseHelper.COLUMNS.URL
    };
    String match_url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        TextView name = (TextView) findViewById(R.id.channel);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    Toast.makeText(RSSFeedActivity.this, "No connection, connect and tap to the top", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), MyTrueService.class);
                intent.putExtra(DataBaseHelper.COLUMNS.CHANNEL_URL, match_url);
                getApplicationContext().startService(intent);
            }
        });

        name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getContentResolver().delete(Provider.Constants.CONTENT_URI,
                        DataBaseHelper.COLUMNS.SPECIAL_FLAG + "=" + DataBaseHelper.COLUMNS.FEED_FLAG + " AND " +
                                DataBaseHelper.COLUMNS.CHANNEL_URL + "=?", new String[]{match_url});
                return true;
            }
        });
        name.setText(getIntent().getStringExtra(DataBaseHelper.COLUMNS.CHANNEL_NAME));
        match_url = getIntent().getStringExtra(DataBaseHelper.COLUMNS.CHANNEL_URL);
        setListAdapter(new RSSFeedAdapter(this, null, 0));
        getLoaderManager().initLoader(CONSTANT_LOADER, null, this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.hidden_url_to_webview)).getText().toString();
                Intent intent1 = new Intent(RSSFeedActivity.this, WebActivity.class);
                intent1.putExtra("URL", url);
                startActivity(intent1);
            }
        });
        name.callOnClick();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONSTANT_LOADER:
                return new CursorLoader(getApplicationContext(),
                        Provider.Constants.CONTENT_URI,
                        PROJECTION, DataBaseHelper.COLUMNS.SPECIAL_FLAG + "=" + DataBaseHelper.COLUMNS.FEED_FLAG +
                        " AND " + DataBaseHelper.COLUMNS.CHANNEL_URL + "=?"
                        , new String[]{match_url}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((RSSFeedAdapter) getListAdapter()).changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((RSSFeedAdapter) getListAdapter()).changeCursor(null);
    }
}
