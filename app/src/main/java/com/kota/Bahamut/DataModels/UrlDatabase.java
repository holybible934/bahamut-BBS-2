package com.kota.Bahamut.DataModels;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kota.Telnet.TelnetClient;
import java.util.Vector;

public class UrlDatabase extends SQLiteOpenHelper {
    public UrlDatabase(Context context) {
        super(context, TelnetClient.getClient().getUsername().toLowerCase().trim() + "_database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase aDatabase) {
        try {
            String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS urls (" +
                    "url TEXT PRIMARY KEY, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "imageUrl TEXT, " +
                    "isPic TEXT" +
                    ")";

            aDatabase.execSQL(CREATE_TABLE_QUERY);
            CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS shorten_urls (" +
                    "shorten_url TEXT PRIMARY KEY, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "url TEXT)";

            aDatabase.execSQL(CREATE_TABLE_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }

    public void addUrl(String url, String title, String description, String imageUrl, boolean isPic) {
        if (title.isEmpty() && description.isEmpty())
            return;
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("title", title);
        values.put("description", description);
        values.put("imageUrl", imageUrl);
        values.put("isPic", isPic);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("urls", null, values);
        db.close();
    }

    @SuppressLint("Range")
    public Vector<String> getUrl(String url) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"url", "title", "description", "imageUrl", "isPic"};
        String selection = "url = ?";
        String[] selectionArgs = {url};

        Cursor cursor = db.query("urls", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            Vector<String> data = new Vector<>();
            data.add(cursor.getString(cursor.getColumnIndex("url")));
            data.add(cursor.getString(cursor.getColumnIndex("title")));
            data.add(cursor.getString(cursor.getColumnIndex("description")));
            data.add(cursor.getString(cursor.getColumnIndex("imageUrl")));
            data.add(cursor.getString(cursor.getColumnIndex("isPic")));

            cursor.close();
            db.close();

            return data;
        } else {
            cursor.close();
            db.close();

            return null;
        }
    }

    public void addShortenUrl(String url, String title, String description, String shortenUrl) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("shorten_url", shortenUrl);
        values.put("title", title);
        values.put("description", description);
        values.put("url", url);
        int deletedRows = db.delete("shorten_urls", "shorten_url=?",new String[]{shortenUrl});
        long insertId = db.insert("shorten_urls", null, values);
        db.close();
    }

    @SuppressLint("Range")
    public Vector<ShortenUrl> getShortenUrls() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"shorten_url", "title", "description", "url"};
        String selection = "";
        String[] selectionArgs = {""};

        Cursor cursor = db.query("shorten_urls", columns, selection, null, null, null, "rowid DESC");

        if (cursor.moveToFirst()) {
            Vector<ShortenUrl> returnList = new Vector<>();
            do {
                ShortenUrl data = new ShortenUrl();
                data.setShorten_url(cursor.getString(cursor.getColumnIndex("shorten_url")));
                data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                data.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                returnList.add(data);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return returnList;
        } else {
            cursor.close();
            db.close();

            return new Vector<>();
        }
    }

    @SuppressLint("Range")
    public Vector<ShortenUrl> getShortenUrl(String url) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"shorten_url", "title", "description", "url"};
        String selection = "url = ?";
        String[] selectionArgs = {url};

        Cursor cursor = db.query("shorten_urls", columns, selection, selectionArgs, null, null, "rowid DESC");

        if (cursor.moveToFirst()) {
            Vector<ShortenUrl> returnList = new Vector<>();
            do {
                ShortenUrl data = new ShortenUrl();
                data.setShorten_url(cursor.getString(cursor.getColumnIndex("shorten_url")));
                data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                data.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                returnList.add(data);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return returnList;
        } else {
            cursor.close();
            db.close();

            return new Vector<>();
        }
    }

    public void clearDb() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM urls WHERE ROWID IN (SELECT ROWID FROM urls ORDER BY ROWID DESC LIMIT -1 OFFSET 20)");
        db.execSQL("DELETE FROM shorten_urls WHERE ROWID IN (SELECT ROWID FROM shorten_urls ORDER BY ROWID DESC LIMIT -1 OFFSET 20)");
        onCreate(db);
    }
}
