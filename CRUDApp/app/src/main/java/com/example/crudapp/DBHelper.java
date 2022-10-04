package com.example.crudapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "reto8.db";
    public static final String COMPANIES_TABLE_NAME = "companies";
    public static final String COMPANIES_COLUMN_ID = "id";
    public static final String COMPANIES_COLUMN_NAME = "name";
    public static final String COMPANIES_COLUMN_URL = "url";
    public static final String COMPANIES_COLUMN_PHONE = "phone";
    public static final String COMPANIES_COLUMN_EMAIL = "email";
    public static final String COMPANIES_COLUMN_PRODUCTS = "phone";
    public static final String COMPANIES_COLUMN_CATEGORY = "category";
    private HashMap hp;

    class DataObject {
        Integer id;
        String name;

        public DataObject(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table companies " +
                        "(id integer primary key, name text, url text, phone text, email text, products text, category text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS companies");
        onCreate(db);
    }

    public boolean insertCompany (String name, String url, String phone, String email, String products, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", url);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("products", products);
        contentValues.put("category", category);
        db.insert("companies", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, COMPANIES_TABLE_NAME);
        return numRows;
    }

    public boolean updateCompany (Integer id, String name, String url, String phone, String email, String products, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", url);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("products", products);
        contentValues.put("category", category);
        db.update("companies", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteCompany (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("companies",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    @SuppressLint("Range")
    public ArrayList<DataObject> getAllCompanies() {
        ArrayList<DataObject> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            // res.getString(res.getColumnIndex(COMPANIES_COLUMN_NAME))
            array_list.add(new DataObject(res.getInt(res.getColumnIndex(COMPANIES_COLUMN_ID)),
                    res.getString(res.getColumnIndex(COMPANIES_COLUMN_NAME))));
            res.moveToNext();
        }
        return array_list;
    }

    @SuppressLint({"Range", "Recycle"})
    public ArrayList<DataObject> filter(String name, String category) {
        ArrayList<DataObject> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from companies", null );
        if (!name.equals("") && !category.equals("")) {
            res =  db.rawQuery( "select * from companies where name = \"" + name + "\" " +
                    "and category = \"" + category + "\"", null );
        } else if (!name.equals("")) {
            res =  db.rawQuery( "select * from companies where name = \"" + name + "\" ", null);
        } else if (!category.equals("")) {
            res =  db.rawQuery( "select * from companies where category = \"" + category + "\" ", null);
        }
        res.moveToFirst();

        while(!res.isAfterLast()){
            // res.getString(res.getColumnIndex(COMPANIES_COLUMN_NAME))
            array_list.add(new DataObject(res.getInt(res.getColumnIndex(COMPANIES_COLUMN_ID)),
                    res.getString(res.getColumnIndex(COMPANIES_COLUMN_NAME))));
            res.moveToNext();
        }
        return array_list;
    }
}
