package com.android.lovelymovies.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.lovelymovies.R;
import com.android.lovelymovies.ui.fragments.MoviesDetailsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class MoviesDetailsActivity extends SherlockFragmentActivity {
	
	public static final String MOVIE_ID = "MovieId";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movies_details);
		
		FragmentManager fm = getSupportFragmentManager();
		MoviesDetailsFragment moviesDetailsFrag = (MoviesDetailsFragment) fm.findFragmentByTag(MoviesDetailsFragment.TAG);
		if (moviesDetailsFrag == null) {
			moviesDetailsFrag = MoviesDetailsFragment.newInstance(getIntent().getExtras());
			fm.beginTransaction().add(R.id.content_movies_details, moviesDetailsFrag, MoviesDetailsFragment.TAG).commit();
		}
	}

}
