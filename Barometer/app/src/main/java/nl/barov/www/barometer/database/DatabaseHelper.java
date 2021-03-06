package nl.barov.www.barometer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Tuncay on 3/05/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static SQLiteDatabase mSQLDB;
    private static DatabaseHelper mInstance;            // SINGLETON TRUC
    private static final String dbName = "barometer.db";    // Naam van je DB
    private static final int dbVersion = 1;                // Versie nr van je db.

    private DatabaseHelper(Context ctx) {                // De constructor doet niet veel meer dan ...
        super(ctx, dbName, null, dbVersion);            // … de super constructor aan te roepen.
    }

    public static synchronized DatabaseHelper getHelper(Context ctx) {  // SYNCRONIZED TRUC
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx);
            mSQLDB = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    @Override
    // CREATE TABLE course (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ects TEXT, code TEXT grade TEXT);
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseInfo.CourseTables.COURSE + " (" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseInfo.CourseColumn.NAME + " TEXT," + DatabaseInfo.CourseColumn.ECTS + " TEXT," +
                        DatabaseInfo.CourseColumn.GRADE + " TEXT," + DatabaseInfo.CourseColumn.PERIOD + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {     // BIJ EEN UPDATE VAN DE DB (ID verhoogd)
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.CourseTables.COURSE);     // GOOI ALLES WEG
        onCreate(db);                                     // EN CREER HET OPNIEUW
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void insert(String table, String nullColumnHack, ContentValues values) {
        mSQLDB.insert(table, nullColumnHack, values);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectArgs, String groupBy, String having, String orderBy) {
        return mSQLDB.query(table, columns, selection, selectArgs, groupBy, having, orderBy);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        mSQLDB.update(table, values, whereClause, whereArgs);
    }
}
