package com.android.lovelymovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.lovelymovies.LovelyMoviesApplication;
import com.android.lovelymovies.provider.LovelyMoviesContract.MoviesColumns;

public class LovelyMoviesDatabase extends SQLiteOpenHelper {
	
	public static final Boolean DEBUG_MODE = LovelyMoviesApplication.DEBUG_MODE;
	protected static final String TAG = LovelyMoviesDatabase.class.getSimpleName();
	
	private static final String DATABASE_NAME = "lovelyxml.db";
	private static final int DATABASE_VERSION = 1;

	interface Tables {
		String MOVIES = "movies";
	}

	public LovelyMoviesDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.MOVIES + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MoviesColumns.MOVIE_ID + " LONG,"
				+ MoviesColumns.MOVIE_TITLE + " TEXT,"
				+ MoviesColumns.MOVIE_DESCRIPTION + " TEXT,"
				+ MoviesColumns.MOVIE_DIRECTOR + " TEXT,"
				+ MoviesColumns.MOVIE_IMGURL + " TEXT,"
				+ "UNIQUE (" + MoviesColumns.MOVIE_ID + ") ON CONFLICT REPLACE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG_MODE)
			Log.d(TAG,
					"Upgrading database from version " + oldVersion
							+ " to " + newVersion
							+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Tables.MOVIES);
		onCreate(db);
	}
}