package edu.sjsu.android.googlemapandsql;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;



public class LocationsContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "edu.sjsu.android.googlemapandsql.locations";
    static final String URL = "content://" + PROVIDER_NAME + "/locations";
    static final Uri CONTENT_URI = Uri.parse(URL);
    private static final int LOCATIONS = 1;
    private static final UriMatcher uriMatcher ;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "locations", LOCATIONS);
    }

    LocationsDB myDB;


    @Override
    public boolean onCreate() {
        myDB = new LocationsDB(getContext());
        return (myDB == null) ? false : true;
    }


    @Override
    public Cursor query( Uri uri, String[] strings, String s, String[] strings1, String s1) {
        if(uriMatcher.match(uri) == LOCATIONS){
            return myDB.getAllLocations();
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = myDB.insert(contentValues);
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add into " + uri);
    }

    @Override
    public int delete( Uri uri, String s, String[] strings) {
        int count = 0;
        count = myDB.delete();
        return count;
    }

    @Override
    public int update( Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
