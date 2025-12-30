package com.example.yidongexperiment05.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "login.db";
    private static final String TABLE_NAME = "login_info";
    private static final int DB_VERSION = 1;
    private static LoginDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private LoginDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static LoginDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new LoginDBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    // 打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    // 关闭数据库连接
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

    // 创建数据库，执行建表语句
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " phone VARCHAR NOT NULL UNIQUE," + // (建议) 手机号应该是唯一的
                " password VARCHAR NOT NULL," +
                " remember INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // (本次实验暂不处理数据库升级)
    }

    /**
     * (教学点: "增" - Create / Update)
     * 这是一个 "Upsert" (更新或插入) 操作。
     * 流程: 先删除，再插入。
     */
    public void save(LoginInfo info) {
        // 确保写连接是打开的
        if (mWDB == null) {
            openWriteLink();
        }

        try {
            mWDB.beginTransaction(); // 开始事务
            delete(info); // "删"
            insert(info); // "增"
            mWDB.setTransactionSuccessful(); // 标记事务成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWDB.endTransaction(); // 结束事务
        }
    }

    /**
     * (教学点: "删" - Delete)
     * 根据手机号删除一条记录
     */
    public long delete(LoginInfo info) {
        // 确保写连接是打开的
        if (mWDB == null) {
            openWriteLink();
        }
        return mWDB.delete(TABLE_NAME, "phone=?", new String[]{info.phone});
    }

    /**
     * (教学点: "增" - Create)
     * 插入一条新记录
     */
    public long insert(LoginInfo info) {
        // 确保写连接是打开的
        if (mWDB == null) {
            openWriteLink();
        }

        ContentValues values = new ContentValues();
        values.put("phone", info.phone);
        values.put("password", info.password);
        values.put("remember", info.remember);
        return mWDB.insert(TABLE_NAME, null, values);
    }

    /**
     * (教学点: "改" - Update)
     * (新增) 专门用于修改密码的方法
     */
    public long updatePassword(String phone, String newPassword) {
        // 确保写连接是打开的
        if (mWDB == null) {
            openWriteLink();
        }

        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        // "修改" phone 字段为 phone 的那一行的 password
        return mWDB.update(TABLE_NAME, values, "phone=?", new String[]{phone});
    }


    /**
     * (教学点: "查" - Read)
     * 查询最近一个被"记住"的账号
     */
    public LoginInfo queryTop() {
        // 确保读连接是打开的
        if (mRDB == null) {
            openReadLink();
        }

        LoginInfo info = null;
        String sql = "select * from " + TABLE_NAME + " where remember = 1 ORDER BY _id DESC limit 1";
        Cursor cursor = null; // (修正) 声明在 try 外部
        try {
            cursor = mRDB.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                info = new LoginInfo();
                info.id = cursor.getInt(0);
                info.phone = cursor.getString(1);
                info.password = cursor.getString(2);
                info.remember = (cursor.getInt(3) == 0) ? false : true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // (重要) 必须关闭 Cursor
            }
        }
        return info;
    }

    /**
     * (教学点: "查" - Read)
     * 根据手机号查询一个被"记住"的账号
     */
    public LoginInfo queryByPhone(String phone) {
        // 确保读连接是打开的
        if (mRDB == null) {
            openReadLink();
        }

        LoginInfo info = null;
        Cursor cursor = null; // (修正) 声明在 try 外部
        try {
            cursor = mRDB.query(TABLE_NAME, null, "phone=? and remember=1", new String[]{phone}, null, null, null);
            if (cursor.moveToNext()) {
                info = new LoginInfo();
                info.id = cursor.getInt(0);
                info.phone = cursor.getString(1);
                info.password = cursor.getString(2);
                info.remember = (cursor.getInt(3) == 0) ? false : true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // (重要) 必须关闭 Cursor
            }
        }
        return info;
    }
}