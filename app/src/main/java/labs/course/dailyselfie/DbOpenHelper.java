package labs.course.dailyselfie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuan on 8/22/15.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    final static String TABLE_NAME = "selfies";
    final static String _ID = "_id";
    final static String PHOTO_PATH = "photo_path";
    final static String PHOTO_DATE = "photo_date";

    final static String[] columns = { _ID, PHOTO_PATH,PHOTO_DATE };

    final private static String CREATE_QUERY =
            "CREATE TABLE selfies (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PHOTO_PATH + " TEXT NOT NULL," + PHOTO_DATE + " DATE NOT NULL)";

    final private static String NAME = "SelfieDb";
    final private static Integer VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
