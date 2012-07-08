package com.android.lovelymovies.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.lovelymovies.LovelyMoviesApplication;
import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;
import com.android.lovelymovies.ui.MoviesDetailsActivity;
import com.android.lovelymovies.ui.MoviesEditActivity;
import com.android.lovelymovies.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MoviesDetailsFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {
	
	public static final Boolean DEBUG_MODE = LovelyMoviesApplication.DEBUG_MODE;
	public static final String TAG = MoviesDetailsFragment.class.getSimpleName();

	private long idOfCurrentMovie;
	
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mOptions;
	
	public static MoviesDetailsFragment newInstance(Bundle extras) {
		MoviesDetailsFragment f = new MoviesDetailsFragment();
		f.setArguments(extras);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View viewer = inflater.inflate(R.layout.fragment_movies_details, container, false);
		
		idOfCurrentMovie = getArguments().getLong(
				MoviesDetailsActivity.MOVIE_ID, 0);
		
		setHasOptionsMenu(true);
		mOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUrl(R.drawable.ic_launcher)
		.showStubImage(R.drawable.ic_launcher)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
		
		getLoaderManager().restartLoader(0, null, this);
		
		return viewer;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInf) {
		menuInf.inflate(R.menu.menu_alone, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.alone_action_share:
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie I Want to Share");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "Dat Movie");
			shareIntent.setType("text/plain");
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getString(R.id.alone_action_share)));
			break;
		case R.id.alone_action_delete:
			if (DEBUG_MODE)
				Log.d(TAG,
						"Deleting item with id : " + idOfCurrentMovie);
			getSherlockActivity().getContentResolver()
			.delete(Movies.buildMovieUri(Long.toString(idOfCurrentMovie)),
					null, null);
			getSherlockActivity().finish();
			break;
		case R.id.alone_action_edit:
			Intent newActivity = new Intent(getSherlockActivity(),
					MoviesEditActivity.class);
			newActivity
					.putExtra(MoviesEditActivity.MOVIE_ID, idOfCurrentMovie);
			newActivity.putExtra(MoviesEditActivity.CALL_TYPE,
					MoviesEditActivity.EDIT);
			startActivity(newActivity);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
		
		final String[] columns = {Movies._ID, Movies.MOVIE_TITLE, Movies.MOVIE_DIRECTOR, Movies.MOVIE_DESCRIPTION, Movies.MOVIE_IMGURL};
		
		if(DEBUG_MODE)
			Log.d(TAG, "Detail Current Movie is : " + idOfCurrentMovie);
		
		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(),
				Movies.buildMovieUri(Long.toString(idOfCurrentMovie)), columns, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if(DEBUG_MODE)
			Log.d(TAG, "Detail Load is finished...");

		if(cursor.moveToFirst()) {
			
			if(DEBUG_MODE)
				Log.d(TAG, "Detail Load is filling the fields...");
			
			String title = cursor.getString(cursor.getColumnIndex(Movies.MOVIE_TITLE));
			String director = cursor.getString(cursor.getColumnIndex(Movies.MOVIE_DIRECTOR));
			String description = cursor.getString(cursor.getColumnIndex(Movies.MOVIE_DESCRIPTION));
			String url = cursor.getString(cursor.getColumnIndex(Movies.MOVIE_IMGURL));
			
			((TextView) (getView().findViewById(R.id.fragment_movies_details_title))).setText(title);
			((TextView) (getView().findViewById(R.id.fragment_movies_details_description))).setText(description);
			((TextView) (getView().findViewById(R.id.fragment_movies_details_director))).setText(director);
			mImageLoader.displayImage(url, (ImageView) getView().findViewById(R.id.fragment_movies_details_img), mOptions);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// Nothing to do?
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mImageLoader.stop();
	}
}
