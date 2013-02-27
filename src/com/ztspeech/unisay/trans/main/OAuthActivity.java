package com.ztspeech.unisay.trans.main;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.weibo.sdk.android.Weibo;

public class OAuthActivity extends Activity {
	public static Weibo weibo;
	String toke = null;
	String secret = null;
	public static final int SAVAINFO = 1;
	ProgressDialog pDialog;

	public void onCreate(Bundle savedInstanceState) throws RuntimeException {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		Uri uri = this.getIntent().getData();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		Button btgologin = (Button) this.findViewById(R.id.btnback);
		btgologin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
						} catch (Exception e) {
							e.printStackTrace();
							LogInfo.LogOut("haitian", "error--R.string.neterror");
							return;
						}
					}
				}).start();

			}
		});

	}

	public static URL getString(URL str) {
		String ssString = str.toString();
		String[] aaStrings = ssString.split("/");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < aaStrings.length; i++) {
			if (i == 4) {
				aaStrings[i] = "180";
			}
			sb.append(aaStrings[i] + "/");
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = null;
		try {
			url = new URL(sb.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

}
