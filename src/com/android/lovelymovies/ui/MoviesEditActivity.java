package com.android.lovelymovies.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.lovelymovies.R;
import com.android.lovelymovies.ui.fragments.MoviesEditFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class MoviesEditActivity extends SherlockFragmentActivity {

	public static final String MOVIE_ID = "MovieId";
	public static final String CALL_TYPE = "CallType";

	public static final int EDIT = 1;
	public static final int ADD = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movies_edit);	
		
		FragmentManager fm = getSupportFragmentManager();
		MoviesEditFragment moviesEditFrag = (MoviesEditFragment) fm.findFragmentByTag(MoviesEditFragment.TAG);
		if (moviesEditFrag == null) {
			moviesEditFrag = MoviesEditFragment.newInstance(getIntent().getExtras());
			fm.beginTransaction().add(R.id.content_movies_edit, moviesEditFrag, MoviesEditFragment.TAG).commit();
		}
	}
}
