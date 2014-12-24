package ru.ifmo.md.lesson6;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RSSListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int CONSTANT_LOADER = 1;
    private static final String[] PROJECTION = new String[]{
            DataBaseHelper.COLUMNS.ID,
            DataBaseHelper.COLUMNS.CHANNEL_NAME, DataBaseHelper.COLUMNS.URL
    };
    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_list_activity);
        setListAdapter(new RSSListAdapter(this, null, 0));
        getLoaderManager().initLoader(CONSTANT_LOADER, null, this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.hidden_view)).getText().toString();
                String chName = ((TextView) view.findViewById(R.id.channel_title_list)).getText().toString();
                Intent intent = new Intent(RSSListActivity.this, RSSFeedActivity.class);
                intent.putExtra(DataBaseHelper.COLUMNS.CHANNEL_NAME, chName);
                intent.putExtra(DataBaseHelper.COLUMNS.CHANNEL_URL, url);
                startActivity(intent);
            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.hidden_view)).getText().toString();
                getContentResolver().delete(Provider.Constants.CONTENT_URI, DataBaseHelper.COLUMNS.CHANNEL_URL +
                "=?", new String[]{url});
                return true;
            }
        });
        addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RSSListActivity.this);
                builder.setCancelable(true);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                final EditText ed = new EditText(RSSListActivity.this);
                builder.setView(ed);
                final AlertDialog dial = builder.create();
                dial.show();
                dial.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = ed.getText().toString();
                        ContentValues cv = new ContentValues();
                        cv.put(DataBaseHelper.COLUMNS.SPECIAL_FLAG, DataBaseHelper.COLUMNS.CHANNEL_FLAG);
                        cv.put(DataBaseHelper.COLUMNS.CHANNEL_NAME, url);
                        cv.put(DataBaseHelper.COLUMNS.URL, url);
                        cv.put(DataBaseHelper.COLUMNS.CHANNEL_URL, url);
                        getContentResolver().insert(Provider.Constants.CONTENT_URI, cv);
                        dial.dismiss();
                    }
                });
            }

        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONSTANT_LOADER:
                return new CursorLoader(getApplicationContext(),
                        Provider.Constants.CONTENT_URI,
                        PROJECTION, DataBaseHelper.COLUMNS.SPECIAL_FLAG + "=" + DataBaseHelper.COLUMNS.CHANNEL_FLAG
                        , null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((RSSListAdapter) getListAdapter()).changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((RSSListAdapter) getListAdapter()).changeCursor(null);
    }
}
