package com.android.lovelymovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lovelymovies.R;
import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MovieCursorAdapter extends CursorAdapter {

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mOptions;

	class MovieHolder {
		TextView title;
		ImageView image;
	}

	public MovieCursorAdapter(Context context) {
		super(context, null, 0);
		mOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUrl(R.drawable.ic_launcher)
				.showStubImage(R.drawable.ic_launcher).cacheInMemory()
				.cacheOnDisc().build();
	}

	@Override
	public long getItemId(int position) {
		return ((Cursor) getItem(position)).getLong(mCursor
				.getColumnIndex(Movies.MOVIE_ID));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.row_movies, parent, false);

		MovieHolder holder = new MovieHolder();
		holder.title = (TextView) v.findViewById(R.id.row_movies_title);
		holder.image = (ImageView) v.findViewById(R.id.row_movies_img);

		v.setTag(holder);

		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		MovieHolder holder = (MovieHolder) view.getTag();

		holder.title.setText(cursor.getString(cursor
				.getColumnIndex(Movies.MOVIE_TITLE)));

		mImageLoader.displayImage(
				cursor.getString(cursor.getColumnIndex(Movies.MOVIE_IMGURL)),
				holder.image, mOptions);
	}

}
