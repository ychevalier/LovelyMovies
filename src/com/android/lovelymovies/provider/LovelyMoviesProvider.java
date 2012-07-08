package com.android.lovelymovies.provider;

import com.android.lovelymovies.LovelyMoviesApplication;
import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;
import com.android.lovelymovies.provider.LovelyMoviesDatabase.Tables;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class LovelyMoviesProvider extends ContentProvider {
	
	public static final Boolean DEBUG_MODE = LovelyMoviesApplication.DEBUG_MODE;
	protected static final String TAG = LovelyMoviesProvider.class.getSimpleName();

	private static final int MOVIES = 0;
	private static final int MOVIES_ID = 1;
	
	static final String UNDERSCORE = "_";
	static final String SLASH = "/";
	static final String STAR = "*";
	static final String ALL = "all";

	private LovelyMoviesDatabase dbHelper;
	
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = LovelyMoviesContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, LovelyMoviesContract.PATH_MOVIES, MOVIES);
		matcher.addURI(authority, LovelyMoviesContract.PATH_MOVIES + SLASH + STAR, MOVIES_ID);
		
		return matcher;
	}
	
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case MOVIES:
			return LovelyMoviesContract.Movies.CONTENT_TYPE;
		case MOVIES_ID:
			return LovelyMoviesContract.Movies.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		dbHelper = new LovelyMoviesDatabase(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);

		switch (match) {
		case MOVIES:
		case MOVIES_ID:
			long id = db.insertOrThrow(Tables.MOVIES, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(uri, id);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String seg = uri.getLastPathSegment();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			Long id = new Long(seg);
			return db.update(Tables.MOVIES, values,
					Movies.MOVIE_ID + "=" + id, null);
		} catch(Exception e) {
			return db.update(Tables.MOVIES, values, selection, selectionArgs);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if(DEBUG_MODE)
			Log.d(TAG, "Deleting an Item!");
		String seg = uri.getLastPathSegment();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			Long id = new Long(seg);
			return db.delete(Tables.MOVIES,
					Movies.MOVIE_ID + "=" + id, null);
		} catch(Exception e) {
			return db.delete(Tables.MOVIES, selection, selectionArgs);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(Tables.MOVIES);

		int uriType = sUriMatcher.match(uri);
		switch (uriType) {
		case MOVIES_ID:
			queryBuilder.appendWhere(Movies.MOVIE_ID + "="
					+ uri.getLastPathSegment());
			break;
		case MOVIES:
			// no filter
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}

		Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
}
