package dk.cphbusiness.droirc.datasource;

import dk.cphbusiness.droirc.entity.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
	// DB Info
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "droircdb";

	// Tables
	private static final String TABLE_USERS = "users";
	
	// Columns
	private static final String KEY_ID = "id";
	private static final String KEY_NICKNAME = "nickname";

	public DBHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION); // Context, DB Name, CursorFactory, DB Version
	}

	/**
	 * Create database tables
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_NICKNAME + " TEXT)";
		db.execSQL(CREATE_USERS_TABLE);
	}

	/**
	 * Run when modifying DB
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		// Create tables again
		onCreate(db);
	}

	/*
	 * DB operations below
	 */
	
	// Add new user
	public void addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cValues = new ContentValues();
		cValues.put(KEY_ID, user.getId());
		cValues.put(KEY_NICKNAME, user.getNickname());
		db.insert(TABLE_USERS, null, cValues); // inserting Row
		db.close();
	}

	// Getting user through ID
	public User getUser(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		User user = null;
		Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID, KEY_NICKNAME }, KEY_ID + "=?",
				new String[] { Integer.toString(id) }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			System.err.println("Received cursor....");
			System.err.println("Number of coloumns: " + cursor.getColumnCount());
			System.err.println("Number of rows: " + cursor.getCount());
		}
		if (cursor.getCount() > 0)
			user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
		return user;
	}
	
	
	// Getting user through nickname
	public User getUser(String nickname) {
		SQLiteDatabase db = this.getReadableDatabase();
		User user = null;
		Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID, KEY_NICKNAME }, KEY_NICKNAME + "=?",
				new String[] { nickname }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			System.err.println("Received cursor....");
			System.err.println("Number of users: " + cursor.getCount());
		}
		if (cursor.getCount() != 0) // If DB not empty
			user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
		
		return user;
	}

	// Update user
	public int updateUser(String nickname) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cValues = new ContentValues();
		cValues.put(KEY_NICKNAME, nickname);

		return db.update(TABLE_USERS, cValues, KEY_ID + " = ?",
				new String[] { String.valueOf(0) }); // ID of user
	}

	// Delete user
	public void deleteUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_USERS, KEY_ID + " = ?",
				new String[] { String.valueOf(user.getId()) });
		db.close();
	}
	
	// Get number of users
    public int getUsersCount() {
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

}
