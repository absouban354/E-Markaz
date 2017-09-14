package in.tomtontech.markaz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static in.tomtontech.markaz.CustomFunction.APP_SDCARD_LOCATION;
import static in.tomtontech.markaz.CustomFunction.SQL_DATABASE;

/**
 * Created by Mushfeeeq on 8/3/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String QURAN_FILE_ZIP = "quran.zip";
  public static final String JSON_QURAN_ID = "db_id";
  public static final String JSON_QURAN_TEXT = "db_text";
  private static int DB_VERSION = 9;
  private static String DB_NAME = "markaz.sqlite";
  private static String QURAN_DB = "quran.sqlite";
  private static String MESSAGE_TABLE = "messages";
  private static String TEMP_TABLE = "temp";
  private static String PERSONAL_TABLE = "personal";
  private static String Quran_TABLE = "quran_table";
  public static String Quran_Ayah_id = "VerseID";
  public static String Quran_Surah_id = "SuraID  ";
  public static String Quran_Text = "AyahText";
  private static String QURAN_ID = "QuranID";
  private static String QURAN_DB_ID = "DatabaseID";
  public static String MESSAGE_ID = "message_id";
  public static String TEMP_ID = "temp_id";
  public static String LIVE_URL = "liveURL";
  public static String LIVE_DATE = "liveDate";
  public static String TEMP_TIME = "temp_time";
  public static String TEMP_TYPE = "temp_type";
  public static String MESSAGE_USER = "message_user";
  public static String MESSAGE_TEXT = "message_text";
  public static String MESSAGE_TIME = "message_time";
  public static String MESSAGE_TYPE = "message_type";
  private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE + "(" +
      MESSAGE_TEXT + " TEXT NOT NULL," +
      MESSAGE_ID + " INT PRIMARY KEY NOT NULL," +
      MESSAGE_USER + " VARCHAR(40) NOT NULL," +
      MESSAGE_TYPE + " VARCHAR(10) NOT NULL," +
      MESSAGE_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
  private static final String CREATE_QURAN_TABLE = "CREATE TABLE IF NOT EXISTS " + Quran_TABLE + "(" +
      Quran_Text + " TEXT NOT NULL," +
      QURAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
      QURAN_DB_ID + " INT NOT NULL," +
      Quran_Surah_id + " INT NOT NULL," +
      Quran_Ayah_id + " INT NOT NULL)";
  private static final String CREATE_TEMP_TABLE = "CREATE TABLE IF NOT EXISTS " + TEMP_TABLE + "(" +
      TEMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
      TEMP_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
      TEMP_TYPE + " VARCHAR(10) NOT NULL)";
  private static final String CREATE_PERSONAL_TABLE = "CREATE TABLE IF NOT EXISTS " + PERSONAL_TABLE + "(" +
      LIVE_URL + " TEXT," +
      LIVE_DATE + " DATETIME)";
  private static final String DELETE_MESSAGE_TABLE = "DROP TABLE IF EXISTS " + MESSAGE_TABLE;
  private static final String DELETE_QURAN_TABLE = "DROP TABLE IF EXISTS " + Quran_TABLE;
  private static final String DELETE_TEMP_TABLE = "DROP TABLE IF EXISTS " + TEMP_TABLE;
  private static final String DELETE_PERSONAL_TABLE = "DROP TABLE IF EXISTS " + PERSONAL_TABLE;
  private String LOG_TAG = "databasehelper";
  private Context myContext;
  private String Quran_RowId = "rowid";

  public DatabaseHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
    this.myContext = context;
  }

  public DatabaseHelper(Context context, int database) {
    super(context, QURAN_DB, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(CREATE_MESSAGE_TABLE);
    sqLiteDatabase.execSQL(CREATE_QURAN_TABLE);
    sqLiteDatabase.execSQL(CREATE_TEMP_TABLE);
    sqLiteDatabase.execSQL(CREATE_PERSONAL_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(DELETE_MESSAGE_TABLE);
    sqLiteDatabase.execSQL(DELETE_QURAN_TABLE);
    sqLiteDatabase.execSQL(DELETE_TEMP_TABLE);
    sqLiteDatabase.execSQL(DELETE_PERSONAL_TABLE);
    onCreate(sqLiteDatabase);
  }

  public Boolean addChat(String[] data) {
    String strId = data[0];
    String strMessage = data[1];
    String strUser = data[2];
    String strTime = data[3];
    String strType = data[4];
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(MESSAGE_ID, strId);
    cv.put(MESSAGE_TEXT, strMessage);
    cv.put(MESSAGE_USER, strUser);
    cv.put(MESSAGE_TIME, strTime);
    cv.put(MESSAGE_TYPE, strType);
    long i = 0;
    try {
      i = db.insert(MESSAGE_TABLE, null, cv);
    } catch (SQLiteConstraintException e) {
      Log.v(LOG_TAG, "exception occured");
    }
    Log.v(LOG_TAG, "inserted:" + i);
    db.close();
    return i > 0;
  }

  public int addTempFile(String[] data) {
    String strType = data[0];
    String strTime = data[1];
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(TEMP_TIME, strTime);
    cv.put(TEMP_TYPE, strType);
    long i = 0;
    try {
      i = db.insert(TEMP_TABLE, null, cv);
      if (i < 0) {
        String query = "SELECT " + TEMP_ID + " FROM " + TEMP_TABLE + " ORDER BY " + TEMP_ID + " DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
          c.moveToFirst();
          i = c.getInt(c.getColumnIndex(TEMP_ID));
        }
        c.close();
      }
    } catch (SQLiteConstraintException e) {
      Log.v(LOG_TAG, "exception occured");
    }
    Log.v(LOG_TAG, "inserted:" + i);
    db.close();
    return ((int) i);
  }

  public int getLastMessageId() {
    int messageId = 0;
    SQLiteDatabase db = this.getReadableDatabase();
    String getQuery = "SELECT " + MESSAGE_ID + " FROM " + MESSAGE_TABLE + " ORDER BY " + MESSAGE_ID + " DESC LIMIT 1";
    Cursor c = db.rawQuery(getQuery, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      messageId = c.getInt(c.getColumnIndex(MESSAGE_ID));
    }
    c.close();
    db.close();
    return messageId;
  }

  public Cursor getMessages(int id) {
    Cursor c;
    SQLiteDatabase db = this.getReadableDatabase();
    String getQuery = "SELECT * FROM ( SELECT * FROM " + MESSAGE_TABLE;
    if (id > 0)
      getQuery = getQuery.concat(" WHERE " + MESSAGE_ID + "<" + id);
    getQuery = getQuery
        .concat(" ORDER BY " + MESSAGE_ID + " DESC LIMIT 25 ) sub ORDER BY " + MESSAGE_ID + " ASC");
    c = db.rawQuery(getQuery, null);
    return c;
  }

  public void createDataBase() throws IOException {

    boolean dbExist = doesDatabaseExist();

    if (dbExist) {
      Log.v("databaseclass", "database already exist.");
      //do nothing - database already exist
    } else {

      //By calling this method and empty database will be created into the default system path
      //of your application so we are gonna be able to overwrite that database with our database.
      Log.v("databaseclass", "database doesn't exist.trying to create database.");
      this.getReadableDatabase();

      try {

        //copyDataBase();
        unpackZip("db/quran_ar.zip", false);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  /*
  * This Checks If The Quran Is loaded to the database.
  * */
  private boolean doesDatabaseExist() {
    SQLiteDatabase db = this.getReadableDatabase();
    String sqlQuery = "SELECT " + Quran_Text + " FROM " + Quran_TABLE + " LIMIT 10";
    Cursor c = db.rawQuery(sqlQuery, null);
    int len = c.getCount();
    c.close();
    db.close();
    return len > 0;
  }

  public boolean unpackZip(String fileName, Boolean isUrl) {
    InputStream is = null;
    ZipInputStream zis;
    try {
      if (!isUrl)
        is = myContext.getAssets().open(fileName);
      else {
        String url = SQL_DATABASE.concat(fileName).concat(".zip");
        is = (InputStream) new URL(url).getContent();
      }
      zis = new ZipInputStream(new BufferedInputStream(is));
      ZipEntry ze;
      byte[] buffer = new byte[1024];
      int count;
      String temp_path = "";
      while ((ze = zis.getNextEntry()) != null) {
        Log.v("databaseclass", "reached a while in unzip");
        // Need to create directories if not exists, or
        // it will generate an Exception...
        String path = APP_SDCARD_LOCATION.concat("temp/");
        File fmd = new File(path);
        if (!fmd.isDirectory()) {
          fmd.mkdirs();
        }
        path = path.concat("file.csv");
        fmd = new File(path);
        if (!fmd.exists())
          fmd.createNewFile();
        FileOutputStream fout = new FileOutputStream(path);

        // cteni zipu a zapis
        while ((count = zis.read(buffer)) != -1) {
          fout.write(buffer, 0, count);
        }
        temp_path = path;
        fout.close();
        zis.closeEntry();
      }
      zis.close();
      if (copyCsvToSqlite(temp_path)) {
        Log.v(LOG_TAG, "inserted file succesffulyy.");
      } else {
        Log.v(LOG_TAG, "error inserting");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  String[] getQuranVerse(int surahId)//get Whole Ayah Of The Surah
  {
    SQLiteDatabase db = this.getReadableDatabase();
    String getAyahSurah = "SELECT " + Quran_Text + " FROM " + Quran_TABLE + " WHERE " + Quran_Surah_id + " = " + surahId;
    Cursor c = db.rawQuery(getAyahSurah, null);
    int len = c.getCount();
    String[] ayah = null;
    if (len > 0) {
      ayah = new String[len];
      for (int i = 0; c.moveToNext(); i++) {
        ayah[i] = c.getString(c.getColumnIndex(Quran_Text));
        Log.v(LOG_TAG, "ayah:" + ayah[i]);
      }
    }
    c.close();
    db.close();
    return ayah;
  }

  public Cursor getQuranVerse(int suraID, int suraNextId, int ayahId, int nextAyahId) {
    String getQuran = "SELECT * FROM " + Quran_TABLE + " WHERE " + QURAN_DB_ID + "=" + "1 AND ";
    SQLiteDatabase db = this.getReadableDatabase();
    if (suraID == suraNextId)//both page starts with same sura
    {
      getQuran = getQuran.concat(
          Quran_Surah_id + " = " + suraID + " AND " + Quran_Ayah_id + " >= " + ayahId + " AND " + Quran_Ayah_id + " < " + nextAyahId);
    } else if (suraNextId == 999)//no next id
    {
      getQuran = getQuran.concat(Quran_Surah_id + " >= " + suraID);
    } else {
      getQuran = getQuran.concat(QURAN_ID + " between " +
          "(select " + QURAN_ID + " from " + Quran_TABLE
          + " where " + Quran_Surah_id + " = " + suraID + " and " + Quran_Ayah_id + "=" + ayahId + ") " +
          "AND " +
          "(select " + QURAN_ID + " from " + Quran_TABLE + " where "
          + Quran_Surah_id + "=" + suraNextId + " and " + Quran_Ayah_id + "=" + nextAyahId + ")-1");
    }
    Cursor c = db.rawQuery(getQuran, null);
    return c;
  }

  public Cursor getQuranTranslation(int dataID, int suraID, int suraNextId, int ayahId,
                                    int nextAyahId) {
    String getQuran = "SELECT * FROM " + Quran_TABLE + " WHERE " + QURAN_DB_ID + "=" + dataID + " AND ";
    SQLiteDatabase db = this.getReadableDatabase();
    if (suraID == suraNextId)//both page starts with same sura
    {
      getQuran = getQuran.concat(
          Quran_Surah_id + " = " + suraID + " AND " + Quran_Ayah_id + " >= " + ayahId + " AND " + Quran_Ayah_id + " < " + nextAyahId);
    } else if (suraNextId == 999)//no next id
    {
      getQuran = getQuran.concat(Quran_Surah_id + " >= " + suraID);
    } else {
      getQuran = getQuran.concat(QURAN_ID + " between " +
          "(select " + QURAN_ID + " from " + Quran_TABLE
          + " where " + Quran_Surah_id + " = " + suraID + " and " + Quran_Ayah_id + "=" + ayahId + ") " +
          "AND " +
          "(select " + QURAN_ID + " from " + Quran_TABLE + " where "
          + Quran_Surah_id + "=" + suraNextId + " and " + Quran_Ayah_id + "=" + nextAyahId + ")-1");
    }
    Cursor c = db.rawQuery(getQuran, null);
    db.close();
    return c;
  }

  public boolean copyCsvToSqlite(String fileName) {
    SQLiteDatabase db = this.getReadableDatabase();
    try {
      Log.v(LOG_TAG, "inside try in copycsv");
      Log.v(LOG_TAG, "fileName:" + fileName);
      FileReader file = new FileReader(fileName);
      BufferedReader buffer = new BufferedReader(file);
      String line;
      String tableName = Quran_TABLE;
      String columns = QURAN_DB_ID + "," + Quran_Surah_id + "," + Quran_Ayah_id + "," + Quran_Text;
      String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
      String str2 = ");";
      db.beginTransaction();
      while ((line = buffer.readLine()) != null) {
        StringBuilder sb = new StringBuilder(str1);
        String[] str = line.split(",");
        sb.append(str[0]).append(",");
        sb.append(str[1]).append(",");
        sb.append(str[2]).append(",'");
        sb.append(str[3]).append("'");
        sb.append(str2);
        db.execSQL(sb.toString());
      }
      db.setTransactionSuccessful();
      db.endTransaction();
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  public Boolean isLangugageExist(int DatabaseId) {
    SQLiteDatabase db = this.getReadableDatabase();
    String getQuery = "SELECT * FROM " + Quran_TABLE + " WHERE " + QURAN_DB_ID + "=" + DatabaseId + " LIMIT 1";
    Cursor c = db.rawQuery(getQuery, null);
    int len = c.getCount();
    c.close();
    db.close();
    return len > 0;
  }

  public JSONArray getTranslation() {
    SQLiteDatabase db = this.getReadableDatabase();
    String getQuery = "SELECT " + QURAN_DB_ID + " FROM " + Quran_TABLE + " GROUP BY " + QURAN_DB_ID;
    Cursor c = db.rawQuery(getQuery, null);
    int len = c.getCount();
    JSONArray ja = new JSONArray();
    if (len > 0) {
      while (c.moveToNext()) {
        JSONObject jo = new JSONObject();
        try {
          int db_id = c.getInt(c.getColumnIndex(QURAN_DB_ID));
          jo.put(JSON_QURAN_ID, db_id);
          String getVerse = "SELECT " + Quran_Text + " FROM " + Quran_TABLE + " WHERE " + QURAN_DB_ID + "=" + db_id + " ORDER BY " + Quran_Surah_id + " LIMIT 1";
          Cursor c1 = db.rawQuery(getVerse, null);
          if (c1.getCount() > 0) {
            c1.moveToFirst();
            jo.put(JSON_QURAN_TEXT, c1.getString(c1.getColumnIndex(Quran_Text)));
          }
          c1.close();
          ja.put(jo);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
    c.close();
    db.close();
    return ja;
  }

  public void closeDataBase() {
    if (this.getReadableDatabase() != null) {
      if (this.getReadableDatabase().isOpen())
        this.getReadableDatabase().close();
    }
  }

  public Boolean addLive(String[] data) {
    String strUrl = data[0];
    String strDate = data[1];
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(LIVE_URL, strUrl);
    cv.put(LIVE_DATE, strDate);
    String getSql="SELECT * FROM "+PERSONAL_TABLE+" LIMIT 1";
    Cursor c=db.rawQuery(getSql,null);
    long i=0;
    if(c.getCount()==1)
    {
      //TODO:update
      i = db.update(PERSONAL_TABLE,cv,null, null);
    }
    else
    {
      //TODO:add
      i = db.insert(PERSONAL_TABLE, null, cv);
    }
    c.close();
    db.close();
    return i>0;
  }
  public JSONObject getLive() {
    SQLiteDatabase db = this.getReadableDatabase();
    String getQuery = "SELECT * FROM " + PERSONAL_TABLE + " LIMIT 1 ";
    Cursor c = db.rawQuery(getQuery, null);
    int len = c.getCount();
    JSONObject jo = new JSONObject();
    if (len > 0 && c.moveToFirst()) {
      try {
        jo.put(LIVE_URL, c.getString(c.getColumnIndex(LIVE_URL)));
        jo.put(LIVE_DATE, c.getString(c.getColumnIndex(LIVE_DATE)));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    c.close();
    db.close();
    return jo;
  }
}
