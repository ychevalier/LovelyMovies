package com.android.lovelymovies.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.lovelymovies.R;
import com.android.lovelymovies.ui.fragments.MoviesListFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class MoviesListActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movies_list);
		
		FragmentManager fm = getSupportFragmentManager();
		MoviesListFragment moviesListFrag = (MoviesListFragment) fm.findFragmentByTag(MoviesListFragment.TAG);
		if (moviesListFrag == null) {
			moviesListFrag = MoviesListFragment.newInstance(getIntent().getExtras());
			fm.beginTransaction().add(R.id.content_movies_list, moviesListFrag, MoviesListFragment.TAG).commit();
		}
	}

}