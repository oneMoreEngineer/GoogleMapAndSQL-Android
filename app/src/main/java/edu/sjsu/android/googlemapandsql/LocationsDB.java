package edu.sjsu.android.googlemapandsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationsDB extends SQLiteOpenHelper {

    private static String DB_NAME = "LocationMarkers";
    private static final String TB_NAME = "locations";
    public static final String ID = "_id";
    public static final String LAT = "latitude";
    public static final String LNG = "longitude";
    public static final String ZOOM = "zoom";
    public static final int VERSION = 1;
    private SQLiteDatabase db;

    public LocationsDB(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = 	"create table " + TB_NAME + " ( " +
                ID + " integer primary key autoincrement , " +
                LNG + " double , " +
                LAT + " double , " +
                ZOOM + " text " +
                " ) ";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insert (ContentValues cv){
        long rowID = db.insert(TB_NAME, null, cv);
        return rowID;
    }

    public int delete(){
        int cnt = db.delete(TB_NAME, null , null);
        return cnt;
    }

    public Cursor getAllLocations(){
        return db.query(TB_NAME, new String[] { ID,  LAT , LNG, ZOOM } ,
                null, null, null, null, null);
    }
}
