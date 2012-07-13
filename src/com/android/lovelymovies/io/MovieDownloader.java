package com.android.lovelymovies.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import com.android.lovelymovies.LovelyMoviesApplication;
import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;

public class MovieDownloader extends
		AsyncTask<Object, Object, Object> {
	
	public static final Boolean DEBUG_MODE = LovelyMoviesApplication.DEBUG_MODE;
	protected static final String TAG = MovieDownloader.class.getSimpleName();

	public interface OnMoviesDownloadedListener {
		public void onMoviesDownloaded();
	}
	
	private OnMoviesDownloadedListener mListener;
	private ContentResolver mContentResolver;
	
	public MovieDownloader(ContentResolver contentResolver) {
		mContentResolver = contentResolver;
	}

	@Override
	protected Object doInBackground(Object... objects) {
		try {
			return downloadUrl(LovelyMoviesApplication.XML_URL);
		} catch (IOException e) {
			if(DEBUG_MODE)
				Log.d(TAG, "Unable to Download XML...");
			return null;
		}
	}

	private Object downloadUrl(String url) throws IOException {
		InputStream is = null;

		if(!URLUtil.isValidUrl(url)) {
			if(DEBUG_MODE)
				Log.d(TAG, "Url is not a valid URL");
			return null;
		}
		
		if(DEBUG_MODE)
			Log.d(TAG, "Trying to download XML...");
		HttpURLConnection conn = (HttpURLConnection) new URL(url) .openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		is = conn.getInputStream();
		
		MovieParser sp = new MovieParser();
		
		try {
			List<ContentValues> listMovies = sp.parse(is);
			for(ContentValues movie : listMovies) {
				Uri uri = Movies.buildMovieUri(movie.getAsString(Movies.MOVIE_ID));
				mContentResolver.insert(uri, movie);
			}
		} catch (Exception e) {
			if(DEBUG_MODE)
				Log.d(TAG, "Unable to Parse XML!");
		} 
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if(mListener != null)
			mListener.onMoviesDownloaded();
	}
	
	public void setOnMoviesDownloadedListener(OnMoviesDownloadedListener listener) {
		this.mListener = listener;
	}

}
