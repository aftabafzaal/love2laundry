package com.love2laundry.project.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "love2laundry.db";
    public static final String CART_TABLE_NAME = "cart";
    public static final String CART_COLUMN_ID = "id";
    public static final String CART_COLUMN_ANDROID_ID = "androidId";
    public static final String CART_COLUMN_SERVICE_ID = "service_id";
    public static final String CART_COLUMN_SERVICE_NAME = "serviceName";
    public static final String CART_COLUMN_CONTENT = "content";

    public static final String CART_COLUMN_UNIT_PRICE = "unitPrice";
    public static final String CART_COLUMN_PRICE = "price";
    public static final String CART_COLUMN_DISCOUNT = "discount";
    public static final String CART_COLUMN_QUANTITY = "quantity";
    public static final String CART_COLUMN_CATEGORY_ID = "category_id";
    public static final String CART_COLUMN_CATEGORY_NAME = "categoryName";
    public static final String CART_COLUMN_DESKTOP_IMAGE = "desktopImage";
    public static final String CART_COLUMN_PREFERENCES_SHOW = "preferencesShow";
    public static final String CART_COLUMN_IS_PACKAGE = "isPackage";
    public static final String CART_COLUMN_IMAGE = "image";
    public static final String CART_COLUMN_COUNTRY = "country";
    public static final String CART_COLUMN_CREATED_AT = "createdAt";
    public static final String CART_COLUMN_UPDATED_AT = "updatedAt";


    public Cart(Context context) {

        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
        //onUpgrade(db,1,2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + CART_TABLE_NAME);
        String sql = "CREATE TABLE " + CART_TABLE_NAME + "( " +
                CART_COLUMN_ID + " integer PRIMARY KEY, " +
                CART_COLUMN_ANDROID_ID + " TEXT NOT NULL, " +
                CART_COLUMN_SERVICE_ID + " integer NOT NULL, " +
                CART_COLUMN_CONTENT + " TEXT NOT NULL, " +
                CART_COLUMN_SERVICE_NAME + " TEXT NOT NULL, " +
                CART_COLUMN_UNIT_PRICE + " double NOT NULL, " +
                CART_COLUMN_PRICE + " double NOT NULL, " +
                CART_COLUMN_DISCOUNT + " double NOT NULL, " +
                CART_COLUMN_IMAGE + " TEXT , " +
                CART_COLUMN_QUANTITY + " integer NOT NULL, " +
                CART_COLUMN_CATEGORY_ID + " integer NOT NULL, " +
                CART_COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                CART_COLUMN_DESKTOP_IMAGE + " TEXT NOT NULL, " +
                CART_COLUMN_PREFERENCES_SHOW + " TEXT NOT NULL, " +
                CART_COLUMN_IS_PACKAGE + " TEXT NOT NULL, " +
                CART_COLUMN_COUNTRY + " TEXT NOT NULL, " +
                CART_COLUMN_CREATED_AT + " DATETIME, " +
                CART_COLUMN_UPDATED_AT + " DATETIME);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS " + CART_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertCart(String androidId, Integer service_id, String content, String serviceName, Double unitPrice, Double price, Integer quantity, Integer category_id,
                              String categoryName, String country, String image, String desktopImage, String isPackage, String preferencesShow, Double discount) {

        Log.e("Aftab insert", "insertCart " + androidId);
        SQLiteDatabase db = this.getWritableDatabase();

        Log.e("Images -> ", desktopImage + " <--> " + image);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CART_COLUMN_ANDROID_ID, androidId);
        contentValues.put(CART_COLUMN_SERVICE_ID, service_id);
        contentValues.put(CART_COLUMN_CONTENT, content);
        contentValues.put(CART_COLUMN_SERVICE_NAME, serviceName);
        contentValues.put(CART_COLUMN_UNIT_PRICE, unitPrice);
        contentValues.put(CART_COLUMN_DISCOUNT, discount);
        contentValues.put(CART_COLUMN_PRICE, price);
        contentValues.put(CART_COLUMN_QUANTITY, quantity);
        contentValues.put(CART_COLUMN_CATEGORY_ID, category_id);
        contentValues.put(CART_COLUMN_CATEGORY_NAME, categoryName);
        contentValues.put(CART_COLUMN_COUNTRY, country);
        contentValues.put(CART_COLUMN_IMAGE, image);
        contentValues.put(CART_COLUMN_DESKTOP_IMAGE, desktopImage);
        contentValues.put(CART_COLUMN_IS_PACKAGE, isPackage);
        contentValues.put(CART_COLUMN_PREFERENCES_SHOW, preferencesShow);
        contentValues.put(CART_COLUMN_CREATED_AT, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        db.insert(CART_TABLE_NAME, null, contentValues);
        insertId();
        return true;
    }

    public Double getServicesTotal(String androidId,String country){


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(unitPrice * quantity) as total  from cart where androidId=? and country=?;", new String[]{androidId, country});
        Double total = 0.00;

        if (res.getCount() == 0) {

        } else {

            res.moveToFirst();
            total = res.getDouble(0);
            res.close();

            Log.e("count ",""+total);
            return total;
        }
        return 0.00;

    }

    public int insertId() {
        String sql = "SELECT last_insert_rowid();";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, null);
        return 1;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CART_TABLE_NAME + " where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CART_TABLE_NAME);
        return numRows;
    }

    public boolean updateCart(String androidId, Integer service_id, Integer quantity, Double unitPrice, Double discountAmount, String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", quantity);
        contentValues.put("price", unitPrice * quantity);
        contentValues.put("discount", discountAmount * quantity);
        db.update(CART_TABLE_NAME, contentValues, "androidId = ? and service_id=? and country=? ",
                new String[]{androidId, service_id.toString(), country});
        return true;
    }

    public Integer deleteAll(String androidId, String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart",
                "",
                null);
    }


    public Integer deleteCartItem(String androidId, Integer service_id, String country) {
        SQLiteDatabase db = this.getWritableDatabase();


        return db.delete("cart",
                "androidId=? and service_id=? and country=?",
                new String[]{androidId, service_id.toString(), country});
    }


    public Double getTotalForDiscount(String androidId, String country) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sum(unitPrice * quantity) as total  from cart where isPackage='No' and discount=0.0 and androidId=? and country=?;", new String[]{androidId, country});
        Double count = 0.00;
        if (res.getCount() == 0) {
            Log.e("res ","-- ");
            return 0.00;
        } else {

            res.moveToFirst();
            count = res.getDouble(0);
            res.close();

            Log.e("count ",""+count);
            return count;
        }


    }

    public boolean showPreferences(String androidId, String country) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id from cart where categoryName='LAUNDRY' and androidId=? and country=?;", new String[]{androidId, country});
        if (res.getCount() == 0) {
            return false;
        } else {

            return true;
        }
    }

    public HashMap<String, String> getService(String androidId, Integer service_id, String country) {

        HashMap<String, String> service = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor res = db.rawQuery("select * from cart where service_id=? and androidId=? and country=? limit 1", new String[]{service_id.toString(), androidId, country});
        res.moveToFirst();
        int i = 0;
        while (res.isAfterLast() == false) {
            service.put(CART_COLUMN_ID, res.getString(res.getColumnIndex(CART_COLUMN_ID)));
            service.put(CART_COLUMN_SERVICE_ID, res.getString(res.getColumnIndex(CART_COLUMN_SERVICE_ID)));
            service.put(CART_COLUMN_CONTENT, res.getString(res.getColumnIndex(CART_COLUMN_CONTENT)));
            service.put(CART_COLUMN_SERVICE_NAME, res.getString(res.getColumnIndex(CART_COLUMN_SERVICE_NAME)));
            service.put(CART_COLUMN_QUANTITY, res.getString(res.getColumnIndex(CART_COLUMN_QUANTITY)));
            service.put(CART_COLUMN_UNIT_PRICE, res.getString(res.getColumnIndex(CART_COLUMN_UNIT_PRICE)));
            service.put(CART_COLUMN_PRICE, res.getString(res.getColumnIndex(CART_COLUMN_PRICE)));
            service.put(CART_COLUMN_DISCOUNT, res.getString(res.getColumnIndex(CART_COLUMN_DISCOUNT)));
            service.put(CART_COLUMN_IMAGE, res.getString(res.getColumnIndex(CART_COLUMN_IMAGE)));
            service.put(CART_COLUMN_CATEGORY_ID, res.getString(res.getColumnIndex(CART_COLUMN_CATEGORY_ID)));
            service.put(CART_COLUMN_CATEGORY_NAME, res.getString(res.getColumnIndex(CART_COLUMN_CATEGORY_NAME)));
            service.put(CART_COLUMN_DESKTOP_IMAGE, res.getString(res.getColumnIndex(CART_COLUMN_DESKTOP_IMAGE)));
            service.put(CART_COLUMN_PREFERENCES_SHOW, res.getString(res.getColumnIndex(CART_COLUMN_PREFERENCES_SHOW)));
            res.moveToNext();
            i++;
        }
        return service;
    }

    public long getCount(String androidId, String country) {

        long count = 0;
        try {
            String sql = "select COUNT(*) as c from cart where androidId=? and country=?";
            //String res =  db.rawQuery( "select * from cart where androidId=? and country=?", new String[] { androidId,country } );
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, new String[]{androidId, country});
            cursor.moveToFirst();
            count = cursor.getLong(0);
            cursor.close();
            return count;

        } catch (Exception e) {
            // TODO: handle exception
            //Log.e("GroupDBErro", "GetGroup Count "+e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    public ArrayList<HashMap<String, String>> getServices(String androidId, String country) {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> services = new ArrayList<>();

        String sql = "select * from cart where androidId='" + androidId + "' and country='" + country + "'";
        Cursor res = db.rawQuery(sql, new String[]{});
        res.moveToFirst();
        int i = 0;
        while (res.isAfterLast() == false) {

            HashMap<String, String> service = new HashMap<>();

            service.put(CART_COLUMN_ID, res.getString(res.getColumnIndex(CART_COLUMN_ID)));
            service.put(CART_COLUMN_SERVICE_ID, res.getString(res.getColumnIndex(CART_COLUMN_SERVICE_ID)));
            service.put(CART_COLUMN_SERVICE_NAME, res.getString(res.getColumnIndex(CART_COLUMN_SERVICE_NAME)));
            service.put(CART_COLUMN_CONTENT, res.getString(res.getColumnIndex(CART_COLUMN_CONTENT)));
            service.put(CART_COLUMN_QUANTITY, res.getString(res.getColumnIndex(CART_COLUMN_QUANTITY)));
            service.put(CART_COLUMN_UNIT_PRICE, res.getString(res.getColumnIndex(CART_COLUMN_UNIT_PRICE)));
            service.put(CART_COLUMN_PRICE, res.getString(res.getColumnIndex(CART_COLUMN_PRICE)));
            service.put(CART_COLUMN_DISCOUNT, res.getString(res.getColumnIndex(CART_COLUMN_DISCOUNT)));
            service.put(CART_COLUMN_IMAGE, res.getString(res.getColumnIndex(CART_COLUMN_IMAGE)));

            service.put(CART_COLUMN_CATEGORY_ID, res.getString(res.getColumnIndex(CART_COLUMN_CATEGORY_ID)));
            service.put(CART_COLUMN_CATEGORY_NAME, res.getString(res.getColumnIndex(CART_COLUMN_CATEGORY_NAME)));
            service.put(CART_COLUMN_DESKTOP_IMAGE, res.getString(res.getColumnIndex(CART_COLUMN_DESKTOP_IMAGE)));
            service.put(CART_COLUMN_IS_PACKAGE, res.getString(res.getColumnIndex(CART_COLUMN_IS_PACKAGE)));
            service.put(CART_COLUMN_PREFERENCES_SHOW, res.getString(res.getColumnIndex(CART_COLUMN_PREFERENCES_SHOW)));
            services.add(i, service);
            res.moveToNext();
            i++;
        }

        return services;
    }
}