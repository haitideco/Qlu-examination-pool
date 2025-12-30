package com.example.yidongexperiment06.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yidongexperiment06.enity.CartInfo;
import com.example.yidongexperiment06.enity.GoodsInfo;

import java.util.ArrayList;
import java.util.List;

public class ShoppingDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shopping.db";
    private static final String TABLE_GOODS_INFO = "goods_info";
    private static final String TABLE_CART_INFO = "cart_info";
    private static final int DB_VERSION = 1; // (修复) 如果您之前修改过版本号，请确保它是1，或者卸载应用
    private static ShoppingDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private ShoppingDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static ShoppingDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new ShoppingDBHelper(context.getApplicationContext());
        }
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }
        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_GOODS_INFO +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " description VARCHAR NOT NULL," +
                " price FLOAT NOT NULL," +
                " pic INTEGER NOT NULL);"; // (修复) pic_path VARCHAR -> pic INTEGER
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CART_INFO +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " goods_id INTEGER NOT NULL," +
                " count INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertGoodsInfos(List<GoodsInfo> list) {
        openWriteLink();
        try {
            mWDB.beginTransaction();
            for (GoodsInfo info : list) {
                ContentValues values = new ContentValues();
                values.put("name", info.name);
                values.put("description", info.description);
                values.put("price", info.price);
                values.put("pic", info.pic);
                mWDB.insert(TABLE_GOODS_INFO, null, values);
            }
            mWDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWDB.endTransaction();
        }
    }

    public List<GoodsInfo> queryAllGoodsInfo() {
        openReadLink();
        String sql = "select * from " + TABLE_GOODS_INFO;
        List<GoodsInfo> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mRDB.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                GoodsInfo info = new GoodsInfo();
                info.id = cursor.getInt(0);
                info.name = cursor.getString(1);
                info.description = cursor.getString(2);
                info.price = cursor.getFloat(3);
                info.pic = cursor.getInt(4); // (修复) picPath = getString(4) -> pic = getInt(4)
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public void insertCartInfo(int goodsId) {
        openWriteLink();
        CartInfo cartInfo = queryCartInfoByGoodsId(goodsId);
        ContentValues values = new ContentValues();
        values.put("goods_id", goodsId);
        if (cartInfo == null) {
            values.put("count", 1);
            mWDB.insert(TABLE_CART_INFO, null, values);
        } else {
            values.put("_id", cartInfo.id);
            values.put("count", ++cartInfo.count);
            mWDB.update(TABLE_CART_INFO, values, "_id=?", new String[]{String.valueOf(cartInfo.id)});
        }
    }

    private CartInfo queryCartInfoByGoodsId(int goodsId) {
        openReadLink();
        Cursor cursor = null;
        CartInfo info = null;
        try {
            cursor = mRDB.query(TABLE_CART_INFO, null, "goods_id=?", new String[]{String.valueOf(goodsId)}, null, null, null);
            if (cursor.moveToNext()) {
                info = new CartInfo();
                info.id = cursor.getInt(0);
                info.goodsId = cursor.getInt(1);
                info.count = cursor.getInt(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return info;
    }

    public int countCartInfo() {
        openReadLink();
        int count = 0;
        String sql = "select sum(count) from " + TABLE_CART_INFO;
        Cursor cursor = null;
        try {
            cursor = mRDB.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public List<CartInfo> queryAllCartInfo() {
        openReadLink();
        List<CartInfo> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mRDB.query(TABLE_CART_INFO, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                CartInfo info = new CartInfo();
                info.id = cursor.getInt(0);
                info.goodsId = cursor.getInt(1);
                info.count = cursor.getInt(2);
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public GoodsInfo queryGoodsInfoById(int goodsId) {
        openReadLink();
        GoodsInfo info = null;
        Cursor cursor = null;
        try {
            cursor = mRDB.query(TABLE_GOODS_INFO, null, "_id=?", new String[]{String.valueOf(goodsId)}, null, null, null);
            if (cursor.moveToNext()) {
                info = new GoodsInfo();
                info.id = cursor.getInt(0);
                info.name = cursor.getString(1);
                info.description = cursor.getString(2);
                info.price = cursor.getFloat(3);
                info.pic = cursor.getInt(4); // (修复) picPath = getString(4) -> pic = getInt(4)
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return info;
    }

    public void deleteCartInfoByGoodsId(int goodsId) {
        openWriteLink();
        mWDB.delete(TABLE_CART_INFO, "goods_id=?", new String[]{String.valueOf(goodsId)});
    }

    public void deleteAllCartInfo() {
        openWriteLink();
        mWDB.delete(TABLE_CART_INFO, "1=1", null);
    }
}