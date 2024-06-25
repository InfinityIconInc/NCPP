package com.infinityicon.newcellphoneprices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
	private final int FLASH_DISPLAY_LENGTH = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent StartAppIntent = new Intent (SplashScreen.this, Categories.class );
				SplashScreen.this.startActivity ( StartAppIntent );
				SplashScreen.this.finish();
			}
		}, FLASH_DISPLAY_LENGTH);
	}
}