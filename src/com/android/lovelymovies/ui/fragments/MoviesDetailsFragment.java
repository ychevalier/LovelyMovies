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
	public static final String TAG = MoviesDetailsFragment.class
			.getSimpleName();

	private long mCurrentMovieId;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mOptions;

	// UI
	private TextView mName;
	private ImageView mImage;
	private TextView mDirector;
	private TextView mDescription;

	public static MoviesDetailsFragment newInstance(Bundle extras) {
		MoviesDetailsFragment f = new MoviesDetailsFragment();
		f.setArguments(extras);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_movies_details,
				container, false);

		mCurrentMovieId = getArguments().getLong(
				MoviesDetailsActivity.MOVIE_ID, 0);

		mName = (TextView) view
				.findViewById(R.id.fragment_movies_details_title);
		mImage = (ImageView) view.findViewById(R.id.fragment_movies_details_img);
		mDirector = (TextView) view
				.findViewById(R.id.fragment_movies_details_director);
		mDescription = (TextView) view
				.findViewById(R.id.fragment_movies_details_description);
		
		setHasOptionsMenu(true);
		mOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUrl(R.drawable.ic_launcher)
				.showStubImage(R.drawable.ic_launcher).cacheInMemory()
				.cacheOnDisc().build();

		getLoaderManager().restartLoader(0, null, this);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.movies_details, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.movies_details_action_share:
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, mName.getText());
			shareIntent.putExtra(Intent.EXTRA_TEXT, mDescription.getText());
			shareIntent.setType("text/plain");
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getString(R.string.action_share)));
			break;
		case R.id.movies_details_action_delete:
			if (DEBUG_MODE)
				Log.d(TAG, "Deleting item with id : " + mCurrentMovieId);
			getSherlockActivity().getContentResolver().delete(
					Movies.buildMovieUri(Long.toString(mCurrentMovieId)), null,
					null);
			getSherlockActivity().finish();
			break;
		case R.id.movies_details_action_edit:
			Intent newActivity = new Intent(getSherlockActivity(),
					MoviesEditActivity.class);
			newActivity.putExtra(MoviesEditActivity.MOVIE_ID, mCurrentMovieId);
			newActivity.putExtra(MoviesEditActivity.CALL_TYPE,
					MoviesEditActivity.EDIT);
			startActivity(newActivity);
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		final String[] columns = { Movies._ID, Movies.MOVIE_TITLE,
				Movies.MOVIE_DIRECTOR, Movies.MOVIE_DESCRIPTION,
				Movies.MOVIE_IMGURL };

		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(),
				Movies.buildMovieUri(Long.toString(mCurrentMovieId)), columns,
				null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (cursor.moveToFirst()) {
			mName.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_TITLE)));
			mDirector.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_DIRECTOR)));
			mDescription.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_DESCRIPTION)));
			mImageLoader.displayImage(
					cursor.getString(cursor
							.getColumnIndex(Movies.MOVIE_IMGURL)),
					mImage, mOptions);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Nothing to do?
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageLoader.stop();
	}
}
