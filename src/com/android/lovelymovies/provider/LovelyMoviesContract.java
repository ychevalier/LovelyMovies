package com.android.lovelymovies.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class LovelyMoviesContract {
	
	public static final String CONTENT_AUTHORITY = "com.android.lovelymovies";
	
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	public static final String PATH_MOVIES = "movies";
	
	interface MoviesColumns {		
		String MOVIE_ID = "movie_id";
		String MOVIE_TITLE = "title";
		String MOVIE_DESCRIPTION = "description";
		String MOVIE_DIRECTOR = "director";
		String MOVIE_IMGURL = "url";
	}
	
	public static class Movies implements MoviesColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
		
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.android.lovelyxml.movie";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.android.lovelyxml.movie";
		
		public static Uri buildMovieUri(String movieId) {
			return CONTENT_URI.buildUpon().appendPath(movieId).build();
		}
	}

}
