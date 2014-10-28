package mbrace.android.com.mbracelabs.Utils.dbUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FlickrSQLHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "flickr";
    public static final String ID = "_id";
    public static final String SERVER_ID = "server_id";
    public static final String POSITION = "position";
    public static final String THUMB = "thumb";
    public static final String PHOTO = "PHOTO";
    public static final String LARGEURL = "largeURL";
    public static final String THUMBURL = "thumbURL";
    public static final String OWNER = "owner";
    public static final String SECRET = "secret";
    public static final String SERVER = "server";
    public static final String FARM = "farm";
    public static final String TAG = "tag";

    private static final String DATABASE_NAME = "gson.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + ID
            + " integer primary key autoincrement, "
            + SERVER_ID + " string, "
            + POSITION + " integer, "
            + THUMB + " text, "
            + PHOTO + " text, "
            + LARGEURL + " text, "
            + THUMBURL + " text, "
            + OWNER + " text, "
            + SECRET + " text, "
            + SERVER + " text, "
            + TAG + " text, "
            + FARM + " text); "
            ;

    public FlickrSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FlickrSQLHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
