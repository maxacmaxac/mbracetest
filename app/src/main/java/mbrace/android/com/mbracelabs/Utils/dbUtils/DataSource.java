package mbrace.android.com.mbracelabs.utils.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mbrace.android.com.mbracelabs.utils.FileUtils;
import mbrace.android.com.mbracelabs.model.FlickrImage;

public class DataSource {

    private SQLiteDatabase database;
    private FlickrSQLHelper dbHelper;
    private String[] allColumns = {FlickrSQLHelper.ID, FlickrSQLHelper.SERVER_ID,
            FlickrSQLHelper.POSITION, FlickrSQLHelper.THUMB, FlickrSQLHelper.PHOTO,
            FlickrSQLHelper.LARGE_URL, FlickrSQLHelper.THUMB_URL, FlickrSQLHelper.OWNER,
            FlickrSQLHelper.SECRET, FlickrSQLHelper.SERVER, FlickrSQLHelper.FARM,
            FlickrSQLHelper.TAG};

    public DataSource(Context context) {
        dbHelper = new FlickrSQLHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(FlickrImage flickrImage) {
        ContentValues values = new ContentValues();
        values.put(FlickrSQLHelper.SERVER_ID, flickrImage.getServerId());
        values.put(FlickrSQLHelper.POSITION, flickrImage.getPosition());
        values.put(FlickrSQLHelper.THUMB, flickrImage.getSavedThumbURL());
        values.put(FlickrSQLHelper.PHOTO, flickrImage.getSavedPhotoURL());
        values.put(FlickrSQLHelper.LARGE_URL, flickrImage.getLargeURL());
        values.put(FlickrSQLHelper.THUMB_URL, flickrImage.getThumbURL());
        values.put(FlickrSQLHelper.OWNER, flickrImage.getOwner());
        values.put(FlickrSQLHelper.SECRET, flickrImage.getSecret());
        values.put(FlickrSQLHelper.SERVER, flickrImage.getServer());
        values.put(FlickrSQLHelper.FARM, flickrImage.getFarm());
        values.put(FlickrSQLHelper.TAG, flickrImage.getTAG());

        long insertId = database.insert(FlickrSQLHelper.TABLE_NAME, null,
                values);
        return insertId;
    }

    public void delete(FlickrImage flickrImage) {
        int id = flickrImage.getId();
        database.delete(FlickrSQLHelper.TABLE_NAME, FlickrSQLHelper.ID
                + " = " + id, null);
    }

    public List<FlickrImage> getAllDataByTag(String tag) {
        List<FlickrImage> resultDataArrayList = new ArrayList<FlickrImage>();
        Cursor cursor = database.query(FlickrSQLHelper.TABLE_NAME,
                allColumns, FlickrSQLHelper.TAG + "=" + tag , null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FlickrImage image = cursorToResluts(cursor);
            resultDataArrayList.add(image);
            cursor.moveToNext();
        }
        cursor.close();
        return resultDataArrayList;
    }

    private FlickrImage cursorToResluts(Cursor cursor) {
        FlickrImage flickrImage = new FlickrImage();
        flickrImage.setId(cursor.getInt(0));
        flickrImage.setServer(cursor.getString(1));
        flickrImage.setPosition(cursor.getInt(2));
        flickrImage.setThumb(FileUtils.loadImageFromStorage(cursor.getString(3)));
        flickrImage.setPhoto(FileUtils.loadImageFromStorage(cursor.getString(4)));
        flickrImage.setLargeURL(cursor.getString(5));
        flickrImage.setThumbURL(cursor.getString(6));
        flickrImage.setOwner(cursor.getString(7));
        flickrImage.setSecret(cursor.getString(8));
        flickrImage.setServer(cursor.getString(9));
        flickrImage.setTAG(cursor.getString(10));
        flickrImage.setFarm(cursor.getString(11));
        return flickrImage;
    }
}
