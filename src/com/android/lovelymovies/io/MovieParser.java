package com.android.lovelymovies.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.lovelymovies.provider.LovelyMoviesContract.Movies;

import android.content.ContentValues;
import android.util.Xml;

public class MovieParser {

	static final String NAMESPACE = null;
	static final String TAG_ROOT = "movies";
	static final String TAG_ITEM = "movie";
	static final String TAG_ID = "id";
	static final String TAG_NAME = "name";
	static final String TAG_DIRECTOR = "director";
	static final String TAG_DESCRIPTION = "description";
	static final String TAG_IMAGE_URL = "image";

	public List<ContentValues> parse(InputStream in) throws XmlPullParserException,
			IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		return readMovies(parser);
	}

	private List<ContentValues> readMovies(XmlPullParser parser)
			throws XmlPullParserException, IOException {

		final ArrayList<ContentValues> listMovies = new ArrayList<ContentValues>();
		
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_ROOT);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(TAG_ITEM)) {
				listMovies.add(readMovie(parser));

			} else {
				skip(parser);
			}
		}
		return listMovies;
	}

	private ContentValues readMovie(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_ITEM);

		final ContentValues movieCV = new ContentValues();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tagName = parser.getName();
			if (tagName.equals(TAG_ID)) {
				movieCV.put(Movies.MOVIE_ID, readId(parser));
			} else if (tagName.equals(TAG_NAME)) {
				movieCV.put(Movies.MOVIE_TITLE, readName(parser));
			} else if (tagName.equals(TAG_DIRECTOR)) {
				movieCV.put(Movies.MOVIE_DIRECTOR, readDirector(parser));
			} else if (tagName.equals(TAG_DESCRIPTION)) {
				movieCV.put(Movies.MOVIE_DESCRIPTION, readDescription(parser));
			} else if (tagName.equals(TAG_IMAGE_URL)) {
				movieCV.put(Movies.MOVIE_IMGURL, readImage(parser));
			} else {
				skip(parser);
			}
		}

		return movieCV;
	}

	private String readImage(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_IMAGE_URL);
		final String url = readText(parser);
		parser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_IMAGE_URL);
		return url;
	}

	private String readDescription(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_DESCRIPTION);
		final String description = readText(parser);
		parser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_DESCRIPTION);
		return description;
	}

	private String readDirector(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_DIRECTOR);
		final String director = readText(parser);
		parser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_DIRECTOR);
		return director;
	}

	private String readName(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_NAME);
		final String name = readText(parser);
		parser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_NAME);
		return name;
	}

	private int readId(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, NAMESPACE, TAG_ID);
		final int id = Integer.parseInt(readText(parser));
		parser.require(XmlPullParser.END_TAG, NAMESPACE, TAG_ID);
		return id;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

}
