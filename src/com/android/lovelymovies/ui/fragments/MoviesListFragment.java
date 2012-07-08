package com.android.lovelymovies.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.lovelymovies.LovelyMoviesApplication;
import com.android.lovelymovies.adapters.MovieCursorAdapter;
import com.android.lovelymovies.io.MovieDownloader;
import com.android.lovelymovies.io.MovieDownloader.OnMoviesDownloadedListener;
import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;
import com.android.lovelymovies.ui.MoviesDetailsActivity;
import com.android.lovelymovies.ui.MoviesEditActivity;
import com.android.lovelymovies.R;

public class MoviesListFragment extends SherlockListFragment implements
		OnItemClickListener, OnItemLongClickListener, LoaderCallbacks<Cursor>,
		OnMoviesDownloadedListener, ActionMode.Callback {

	public static final Boolean DEBUG_MODE = LovelyMoviesApplication.DEBUG_MODE;
	public static final String TAG = MoviesListFragment.class.getSimpleName();

	private final int MULTIPLE_MODE = 0;
	private final int SINGLE_MODE = 1;

	private ActionMode mMode;

	private MovieCursorAdapter mAdapter;

	private MenuInflater inflater;

	private int type;

	public static MoviesListFragment newInstance(Bundle extras) {
		MoviesListFragment f = new MoviesListFragment();
		f.setArguments(extras);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (DEBUG_MODE)
			Log.d(TAG, "Getting Movies From DataBase...");
		onDataChanged();
	}

	public void onDataChanged() {

		getLoaderManager().restartLoader(0, null, this);
		if (mAdapter == null) {

			mAdapter = new MovieCursorAdapter(getSherlockActivity());
			setListAdapter(mAdapter);

			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			getListView().setOnItemClickListener(this);
			getListView().setOnItemLongClickListener(this);
		}
	}

	private void getMoviesFromNet() {
		ConnectivityManager connMgr = (ConnectivityManager) (getSherlockActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (DEBUG_MODE)
				Log.d(TAG, "Connected...");

			final MovieDownloader downloader = new MovieDownloader(
					getSherlockActivity().getContentResolver());
			downloader.setOnMoviesDownloadedListener(this);
			downloader.execute();

		} else {
			if (DEBUG_MODE)
				Log.d(TAG, "No Internet Connection...");
			Toast.makeText(getSherlockActivity(), R.string.nointernet,
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		// Create the menu from the xml file
		type = SINGLE_MODE;
		inflater = getSherlockActivity().getSupportMenuInflater();
		inflater.inflate(R.menu.contextual_actions_single, menu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

		if (getListView().getCheckedItemPositions().size() == 1
				&& type != SINGLE_MODE) {
			menu.clear();
			type = SINGLE_MODE;
			inflater.inflate(R.menu.contextual_actions_single, menu);

			return true;
		} else if (getListView().getCheckedItemPositions().size() > 1
				&& type != MULTIPLE_MODE) {
			menu.clear();
			type = MULTIPLE_MODE;
			inflater.inflate(R.menu.contextual_actions_multiple, menu);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// Destroying action mode, let's unselect all items
		for (int i = 0; i < mAdapter.getCount(); i++)
			getListView().setItemChecked(i, false);

		if (mode == mMode) {
			mMode = null;
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

		SparseBooleanArray selected = getListView().getCheckedItemPositions();

		switch (item.getItemId()) {
		case R.id.cab_action_add:
			startAddOrEditActivity(0, MoviesEditActivity.ADD);
			break;
		case R.id.cab_action_delete:
			for (int i = 0; i < mAdapter.getCount(); i++) {
				if (selected.get(i)) {
					long id = mAdapter.getItemId(i);

					// Delete Movies in DB
					getListView().setItemChecked((int) i, false);
					getSherlockActivity().getContentResolver()
							.delete(Movies.buildMovieUri(Long.toString(id)),
									null, null);
				}
				onDataChanged();
			}
			Toast.makeText(
					getSherlockActivity(),
					getResources().getQuantityString(R.plurals.moviez,
							selected.size(), selected.size()),
					Toast.LENGTH_LONG).show();

			break;
		case R.id.cab_action_edit:
			for (int i = 0; i < mAdapter.getCount(); i++) {
				if (selected.get(i)) {
					long id = mAdapter.getItemId(i);
					if (LovelyMoviesApplication.DEBUG_MODE)
						Log.d(TAG, "Id of Clicked Item: " + id);
					startAddOrEditActivity(id, MoviesEditActivity.EDIT);
					break;
				}
			}
			break;
		default:

			break;
		}
		mode.finish();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		getListView().setItemChecked(position, false);

		if (mMode != null) {
			mMode.finish();
		}

		Intent newActivity = new Intent(getSherlockActivity(),
				MoviesDetailsActivity.class);
		newActivity.putExtra(MoviesDetailsActivity.MOVIE_ID, id);
		startActivity(newActivity);

	};

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		getListView().setItemChecked(position,
				!(getListView().getChildAt(position).isFocused()));
		SparseBooleanArray checked = getListView().getCheckedItemPositions();

		boolean hasCheckedElement = false;
		for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
			hasCheckedElement = checked.valueAt(i);
		}

		if (hasCheckedElement) {
			if (mMode == null) {
				mMode = getSherlockActivity().startActionMode(this);
			} else {
				mMode.invalidate();
			}

		} else {
			if (mMode != null) {
				mMode.finish();
			}
		}
		return true;
	}

	public void startAddOrEditActivity(long id, int callType) {
		Intent newActivity = new Intent(getSherlockActivity(),
				MoviesEditActivity.class);
		newActivity.putExtra(MoviesEditActivity.CALL_TYPE, callType);
		if (callType == MoviesEditActivity.EDIT)
			newActivity.putExtra(MoviesEditActivity.MOVIE_ID, id);
		startActivity(newActivity);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { Movies._ID, Movies.MOVIE_ID,
				Movies.MOVIE_IMGURL, Movies.MOVIE_TITLE };

		CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(),
				Movies.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);
		if (mAdapter.isEmpty())
			getMoviesFromNet();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInf) {
		menuInf.inflate(R.menu.add_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cab_action_add:
			startAddOrEditActivity(0, MoviesEditActivity.ADD);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onMoviesDownloaded() {
		// TODO Auto-generated method stub

	}

}
