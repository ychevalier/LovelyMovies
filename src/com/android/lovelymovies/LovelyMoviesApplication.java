package com.android.lovelymovies;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LovelyMoviesApplication extends Application {
	
	public static final Boolean DEBUG_MODE = true;

	public static final String XML_URL = "http://devwidgets.feedget.eu/backend/iphone/xmlExample.xml";
	public static final String LOCAL_XML_URL = "file:///android_asset/xmlExample.xml";
	
	@Override
	public void onCreate() {
		super.onCreate();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.threadPoolSize(3)
		.threadPriority(Thread.NORM_PRIORITY)
		.memoryCacheSize(1572864) // 1.5 Mb
		.discCacheSize(104865760) // 10 Mb
		.httpReadTimeout(10000) // 10 s
		.denyCacheImageMultipleSizesInMemory()
		.build();
		
		ImageLoader.getInstance().init(config);
	}

}
