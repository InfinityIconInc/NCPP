package com.infinityicon.newcellphoneprices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CellPhoneData {
	static final String TAG = "CellPhoneData";

	public static final String DB_NAME = "ncpp";
	public static final int DB_VERSION = 2;
	public static final String TABLE   = "cellphones";

	public static final String C_ID    = "_id";
	public static final String C_NAME  = "cellphone_name";
	public static final String C_PRICE = "cellphone_price";
	public static final String C_IMAGE = "cellphone_image";
	public static final String C_BRAND = "cellphone_brand";
	public static final String C_CORE  = "cellphone_core";
	public static final String C_OS    = "cellphone_os";

	Context context;
	DBHelper dbHelper;
	SQLiteDatabase db;

	public CellPhoneData(Context context) {
		this.context = context;
		dbHelper = new DBHelper();
		////Log.d(TAG, "Constructor");
	}

	public void insert(CellPhone_OS_Core cellPhone) {
		db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_ID, cellPhone.getCellID());
		values.put(C_NAME, cellPhone.getCellName());
		values.put(C_PRICE, cellPhone.getCellDesc());
		values.put(C_IMAGE, cellPhone.getCellImage());
		values.put(C_BRAND, cellPhone.getCellBrand());
		values.put(C_CORE, cellPhone.getCellCore());
		values.put(C_OS, cellPhone.getCellOS());
		
		db.insert(TABLE, null, values);
	}

	public void zap() {
		db = dbHelper.getWritableDatabase();
		db.execSQL(String.format("DELETE FROM %s", TABLE));
	}

	public Cursor SelectByBrand(String strCat) {
		db = dbHelper.getReadableDatabase();

		return db.query(TABLE, null, C_BRAND + " LIKE '" + strCat + "'", null,
				null, null, C_ID + " DESC");
	}
	
	public void CloseDB ( ) {
		dbHelper.close();
	}

	class DBHelper extends SQLiteOpenHelper {

		public DBHelper() {
			super(context, DB_NAME, null, DB_VERSION);
			////Log.d(TAG, "DBHelper Constructor");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			////Log.d(TAG, "onCreate");
			String strSql;
			strSql = String.format(
					"CREATE TABLE %s ( %s int PRIMARY KEY, %s text"
							+ ", %s text, %s text, %s text, %s text, %s text )", TABLE, C_ID,
					C_NAME, C_PRICE, C_IMAGE, C_BRAND, C_CORE, C_OS);
			
			Log.d(TAG, "db onCreate " + strSql);
			db.execSQL(strSql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE);
			onCreate(db);
		}
	}
}
