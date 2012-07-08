package com.android.lovelymovies.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.lovelymovies.LovelyMoviesApplication;
import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;
import com.android.lovelymovies.ui.MoviesEditActivity;
import com.android.lovelymovies.R;

public class MoviesEditFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor>, OnClickListener {

	public static final Boolean DEBUG_MODE = LovelyMoviesApplication.DEBUG_MODE;
	public static final String TAG = MoviesEditFragment.class
			.getSimpleName();

	private int typeOfCall;
	private long idOfEditedMovie;

	private EditText mTitleEd;
	private EditText mDescriptionEd;
	private EditText mUrlEd;
	private EditText mDirectorEd;

	private Button mOkBt;
	private Button mCancelBt;
	
	public static MoviesEditFragment newInstance(Bundle extras) {
		MoviesEditFragment f = new MoviesEditFragment();
		f.setArguments(extras);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View viewer = inflater.inflate(R.layout.fragment_movies_edit,
				container, false);
		
		typeOfCall = getArguments().getInt(MoviesEditActivity.CALL_TYPE,
				MoviesEditActivity.ADD);

		if (typeOfCall == MoviesEditActivity.EDIT) {
			Log.d(TAG,
					" ID:  "
							+ getArguments().getLong(
									MoviesEditActivity.MOVIE_ID, 0));
			idOfEditedMovie = getArguments().getLong(
					MoviesEditActivity.MOVIE_ID, 0);
		} else {
			idOfEditedMovie = -1;
		}

		mOkBt = (Button) viewer.findViewById(R.id.edit_ok);
		mCancelBt = (Button) viewer
				.findViewById(R.id.edit_cancel);

		mTitleEd = (EditText) viewer
				.findViewById(R.id.edit_title_edit);
		mDescriptionEd = (EditText) viewer
				.findViewById(R.id.edit_description_edit);
		mUrlEd = (EditText) viewer
				.findViewById(R.id.edit_url_edit);
		mDirectorEd = (EditText) viewer
				.findViewById(R.id.edit_director_edit);
		
		mOkBt.setOnClickListener(this);
		mCancelBt.setOnClickListener(this);
		return viewer;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (typeOfCall == MoviesEditActivity.EDIT)
			getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		String[] columns = { Movies.MOVIE_IMGURL, Movies.MOVIE_DESCRIPTION,
				Movies.MOVIE_TITLE, Movies.MOVIE_DIRECTOR };

		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(),
				Movies.buildMovieUri("" + idOfEditedMovie), columns, null,
				null, null);

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (cursor != null && cursor.moveToFirst()) {

			mTitleEd.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_TITLE)));
			mDirectorEd.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_DIRECTOR)));
			mDescriptionEd.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_DESCRIPTION)));
			mUrlEd.setText(cursor.getString(cursor
					.getColumnIndex(Movies.MOVIE_IMGURL)));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		if (v == mOkBt) {
			Boolean isValid = true;

			if (mTitleEd.getText().toString() == "") {
				isValid = false;
				mTitleEd.setError("Insert title");
			}
			if (mDescriptionEd.getText().toString() == "") {
				isValid = false;
				mDescriptionEd.setError("Insert description");
			}
			if (!URLUtil.isValidUrl(mUrlEd.getText().toString())) {
				isValid = false;
				mUrlEd.setError("Invalid URL");
			}
			if (mDirectorEd.getText().toString() == "") {
				mDirectorEd.setError("Insert director");
			}

			if (!isValid) {
				Toast.makeText(getSherlockActivity(), "Please correct infos",
						Toast.LENGTH_LONG).show();
			} else {

				// TODO: Update Datas in DB

			}
		} else if(v == mCancelBt) {
			getSherlockActivity().finish();
		}
	}

}
