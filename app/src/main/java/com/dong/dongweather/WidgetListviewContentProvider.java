package com.dong.dongweather;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dong.dongweather.db.CountyChanged;

import org.litepal.crud.DataSupport;

import java.util.List;

public class WidgetListviewContentProvider extends ContentProvider {

    private static final String authority = "com.dong.dongweather.WidgetListviewContentProvider";

    public static final int COUNTYCHANGED_DIR = 0;
    public static final int COUNTYCHANGED_ITEM = 1;
    public static final int SELECTEDCOUNTY_DIR = 2;
    public static final int SELECTEDCOUNTY_ITEM = 3;

    private static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, "CountyChanged", COUNTYCHANGED_DIR );
        uriMatcher.addURI(authority, "CountyChanged/#", COUNTYCHANGED_ITEM);
        uriMatcher.addURI(authority, "SelectedCounty", SELECTEDCOUNTY_DIR);
        uriMatcher.addURI(authority, "SelectedCounty/#", SELECTEDCOUNTY_ITEM);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.dong.dongweather/databases/dong_weather.db", null);
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case COUNTYCHANGED_DIR:
                cursor =  db.query("CountyChanged", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case COUNTYCHANGED_ITEM:
                String countyChangedID = uri.getPathSegments().get(1);
                cursor = db.query("CountyChanged", projection, "id = ?", new String[] {countyChangedID}, null, null, sortOrder);
                break;
            case SELECTEDCOUNTY_DIR:
                cursor =  db.query("SelectedCounty", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SELECTEDCOUNTY_ITEM:
                String SelectedCountyId = uri.getPathSegments().get(1);
                cursor = db.query("CountyChanged", projection, "id = ?", new String[] {SelectedCountyId}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        DataSupport.deleteAll(CountyChanged.class);
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case COUNTYCHANGED_DIR:
                return "vnd.android.cursor.dir/vnd.com.dong.dongweather.WidgetListviewContentProvider.CountyChanged";
            case COUNTYCHANGED_ITEM:
                return "vnd.android.cursor.item/vnd.com.dong.dongweather.WidgetListviewContentProvider.CountyChanged";
            default:
                break;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
