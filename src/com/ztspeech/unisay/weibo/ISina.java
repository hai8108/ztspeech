package com.ztspeech.unisay.weibo;

import java.io.IOException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.weibo.sdk.android.Oauth2AccessToken;
import com.ztspeech.weibo.sdk.android.Weibo;
import com.ztspeech.weibo.sdk.android.WeiboAuthListener;
import com.ztspeech.weibo.sdk.android.WeiboDialogError;
import com.ztspeech.weibo.sdk.android.WeiboException;
import com.ztspeech.weibo.sdk.android.api.StatusesAPI;
import com.ztspeech.weibo.sdk.android.net.RequestListener;

public class ISina {

	private static ISina iSina = null;
	// "1882480683","dc7b26e0e95c3c3d1da69d8c8b2479ee"
	public static String APP_KEY = "117304354";// 第三方应用的appkey
	public static String APP_SECRET = "3d91c4a6f118afd1498de74b3bb54f53";
	public static String REDIRECT_URL = "http://www.sina.com";// "ztspeechAndroid://OAuthActivity";//
	// 重定向url
	public static final int BIND_SINA_CODE = 2001;
	public static final int SEND_SINA_CODE = 2002;
	private SharedPreferences sp;

	private Context context;
	private Weibo mWeibo = null;
	private Oauth2AccessToken o2at;
	private Handler handler;
	private String contentStr;

	public ISina(Context context) {
		this.context = context;
	}

	public synchronized static ISina getInstance(Context context) {
		if (iSina == null) {
			iSina = new ISina(context);
		}
		return iSina;
	}

	public void setO2at() {
		o2at = null;
	}

	public void init() {
		sp = context.getSharedPreferences("sina", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			try {
				o2at = new Oauth2AccessToken(sp.getString("ACCESS_TOKEN", ""), sp.getString("EXPIRES_IN", ""));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			o2at = null;
		}
	}

	public void bindSina(Handler handler) {
		this.handler = handler;
		mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL);
		mWeibo.authorize(context, new AuthDialogListener(), true);
	}

	class AuthDialogListener implements WeiboAuthListener {
		@Override
		public void onCancel() {

		}

		@Override
		public void onComplete(Bundle arg0) {
			String token = arg0.getString("access_token");
			String expires_in = arg0.getString("expires_in");
			o2at = new Oauth2AccessToken(token, expires_in);
			if (o2at.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(o2at
						.getExpiresTime()));
				LogInfo.LogOut("haitian", "认证成功！\raccess_token:" + token + "\rexpires_in:" + expires_in + "/\r有效期至："
						+ date);
				sp.edit().putString("ACCESS_TOKEN", token).putString("EXPIRES_IN", expires_in)
						.putString("isBind", "yes").commit();
				LogInfo.LogOut("haitian", "Toast.makeText(context, \"授权成功\", Toast.LENGTH_SHORT).show()");
			}

		}

		@Override
		public void onError(WeiboDialogError arg0) {
			handler.sendMessage(Message.obtain(handler, BIND_SINA_CODE, "授权失败"));
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}
	}

	public void sendWeibo() {

		StatusesAPI api = new StatusesAPI(o2at);
		api.update(contentStr, "", "", new RequestListener() {

			@Override
			public void onIOException(IOException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(WeiboException arg0) {
				handler.sendMessage(Message.obtain(handler, SEND_SINA_CODE, "发表失败"));
				LogInfo.LogOut("haitian",
						"Toast.makeText(context, \"发表失败\", Toast.LENGTH_SHORT).show()    " + arg0.getMessage());
			}

			@Override
			public void onComplete(String arg0) {
				handler.sendMessage(Message.obtain(handler, SEND_SINA_CODE, "发表成功"));
				LogInfo.LogOut("haitian", "Toast.makeText(context, \"发表成功\", Toast.LENGTH_SHORT).show()    " + arg0);

			}
		});

	}

	public void sendWeibo(String mcontentStr, Handler mhandler) {
		handler = mhandler;
		contentStr = mcontentStr;
		if (o2at == null) {
			mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL);
			mWeibo.authorize(context, new AuthDialogListener() {
				@Override
				public void onComplete(Bundle arg0) {
					String token = arg0.getString("access_token");
					String expires_in = arg0.getString("expires_in");
					o2at = new Oauth2AccessToken(token, expires_in);
					if (o2at.isSessionValid()) {
						String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(o2at
								.getExpiresTime()));
						LogInfo.LogOut("haitian", "认证成功！\raccess_token:" + token + "\rexpires_in:" + expires_in
								+ "/\r有效期至：" + date);
						sp.edit().putString("ACCESS_TOKEN", token).putString("EXPIRES_IN", expires_in)
								.putString("isBind", "yes").commit();
						LogInfo.LogOut("haitian", "授权成功");
					}

				}
			}, false);
		} else {
			StatusesAPI api = new StatusesAPI(o2at);

			handler.sendMessage(Message.obtain(handler, BIND_SINA_CODE, "微博已发送"));
			api.update(contentStr, "", "", new RequestListener() {

				@Override
				public void onIOException(IOException arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(WeiboException arg0) {
					handler.sendMessage(Message.obtain(handler, BIND_SINA_CODE, "发表失败"));
					LogInfo.LogOut("haitian",
							"Toast.makeText(context, \"发表失败\", Toast.LENGTH_SHORT).show()    " + arg0.getMessage());
				}

				@Override
				public void onComplete(String arg0) {
					handler.sendMessage(Message.obtain(handler, BIND_SINA_CODE, "发表成功"));
					LogInfo.LogOut("haitian", "Toast.makeText(context, \"发表成功\", Toast.LENGTH_SHORT).show()    " + arg0);

				}
			});
		}
	}

}
