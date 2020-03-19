package com.nonexistentware.cloudnotev1.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.nonexistentware.cloudnotev1.Model.NoteItem;

import java.util.ArrayList;
import java.util.List;


public class NoteDataBase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NoteDB.db";
    public static final String TABLE_NAME = "note_db";

    private static String DATABASE_PATH;
    private Context myContext;

    public NoteDataBase(Context context) { //change context to my myContext
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "note_title";
    public static final String KEY_BODY = "note_body";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDb = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TITLE + " TEXT," +
                KEY_BODY + " TEXT," +
                KEY_DATE + " TEXT," +
                KEY_TIME + " TEXT"
                + " )";
        db.execSQL(createDb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addNote(NoteItem note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE, note.getNoteTitle());
        v.put(KEY_BODY, note.getNoteBody());
        v.put(KEY_DATE, note.getDate());
        v.put(KEY_TIME, note.getTime());

        long ID = db.insert(TABLE_NAME, null, v);
        return ID;
    }

    public NoteItem getNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] query = new String[]{KEY_ID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_TIME};
        Cursor cursor = db.query(TABLE_NAME, query, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

            return new NoteItem(
                    Long.parseLong(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));

        }

    public List<NoteItem> getAllNotes(){
        List<NoteItem> allNotes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                NoteItem note = new NoteItem();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setNoteTitle(cursor.getString(1));
                note.setNoteBody(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setTime(cursor.getString(4));
                allNotes.add(note);
            }while (cursor.moveToNext());
        }

        db.close();
        return allNotes;

    }

    //get all notes title
    public List<String> getNote(){
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[]  sqlSelect = {"note_title"};
        String tableName = TABLE_NAME;

        qb.setTables(tableName);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);
        List<String> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add(cursor.getString(cursor.getColumnIndex("note_title")));
            }while (cursor.moveToNext());
        }
        return result;
    }

    public List<NoteItem> getNoteByTitle() {
        List<NoteItem> allNotes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                NoteItem note = new NoteItem();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setNoteTitle(cursor.getString(1));
                note.setNoteBody(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setTime(cursor.getString(4));
                allNotes.add(note);
            }while (cursor.moveToNext());
        }

        db.close();
        return allNotes;
    }

    public int getMemoCount() {
        String countQuery = "SELECT * FROM " + NoteDataBase.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int editNote(NoteItem note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        Log.d("Edited", "Edited Title: -> "+ note.getNoteTitle() + "\n ID -> "+note.getId());
        c.put(KEY_TITLE,note.getNoteTitle());
        c.put(KEY_BODY,note.getNoteBody());
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());
        return db.update(TABLE_NAME,c,KEY_ID+"=?",new String[]{String.valueOf(note.getId())});
    }



    public void deleteNote(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+"=?",
                new String[]{String.valueOf(id)});

        db.close();

        }
    }

