package com.ztspeech.unisay.weibo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.manager.ZtsSpeechWriteWithVoiceManager;
import com.ztspeech.unisay.trans.manager.ZtspeechWeiboManager;
import com.ztspeech.unisay.utils.LogInfo;

public class ITencent {

	private static ITencent iTencent = null;

	private static String APP_KEY = "801273041";
	private static String APP_SECRET = "104c26eb32e7346c95571065bcf5e4c2";
	public final static int BIND_TENCENT_CODE = 1000;

	private SharedPreferences sp;
	private String token;
	private String token_secret;
	private Context context;
	private OAuthV1 oAuth = null;
	private String shareMsg = "";
	private Handler handler;
	private ZtspeechWeiboManager mZtspeechWeiboManager;

	public synchronized static ITencent getInstance(Context context) {
		if (iTencent == null) {
			iTencent = new ITencent(context);
		}
		return iTencent;

	}

	public ITencent(Context context) {
		this.context = context;
	}

	public void init() {
		sp = context.getSharedPreferences("tencent", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						oAuth = getOAuthV1();
						oAuth.setOauthToken(sp.getString("TOKEN_KEY", ""));
						oAuth.setOauthTokenSecret(sp.getString("TOKEN_SECRET_KEY", ""));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		} else {
			oAuth = null;
		}
	}

	public static OAuthV1 getOAuthV1() throws Exception {

		OAuthV1 oAuth = new OAuthV1("null");
		oAuth.setOauthConsumerKey(APP_KEY);
		oAuth.setOauthConsumerSecret(APP_SECRET);
		oAuth = OAuthV1Client.requestToken(oAuth);
		return oAuth;
	}

	public void bindTencent() {
		mZtspeechWeiboManager = null;
		mZtspeechWeiboManager = new ZtspeechWeiboManager();
		mZtspeechWeiboManager.isBinderOrNot(true);
		mZtspeechWeiboManager.initDC(context);
		mZtspeechWeiboManager.changeTitle(context.getString(R.string.tencent_sync));
		mZtspeechWeiboManager.showDC();
	}

	public void sendWeibo(String contentStr, Handler mHandler) {
		shareMsg = contentStr;
		handler = mHandler;
		if (oAuth == null) {
			mZtspeechWeiboManager = null;
			mZtspeechWeiboManager = new ZtspeechWeiboManager();
			mZtspeechWeiboManager.isBinderOrNot(false);
			mZtspeechWeiboManager.initDC(context);
			mZtspeechWeiboManager.setUrl(null);
			mZtspeechWeiboManager.changeTitle(context.getString(R.string.tencent_sync));
			mZtspeechWeiboManager.showDC();
		} else {
			handler.sendEmptyMessage(ZtsSpeechWriteWithVoiceManager.CONSTANT_SHOW_LOADING_DIALOG);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					TAPI tapi = new TAPI("1.0");
					String resultMsg = null;
					try {
						resultMsg = tapi.add(oAuth, "json", shareMsg, "127.0.0.1");
						LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>>>--resultMsg =" + resultMsg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						handler.sendMessage(Message.obtain(handler, BIND_TENCENT_CODE, resultMsg));

					}

				}
			}).start();

		}
	}

	public void sendWeibo(Handler mHandler) {
		handler = mHandler;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TAPI tapi = new TAPI("1.0");
				String resultMsg = null;
				try {
					resultMsg = tapi.add(oAuth, "json", shareMsg, "127.0.0.1");
					LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>>>--resultMsg =" + resultMsg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					handler.sendMessage(Message.obtain(handler, BIND_TENCENT_CODE, resultMsg));
				}

			}
		}).start();
	}

	public void setOAuth() {
		oAuth = null;
	}

	public boolean saveStatus(OAuthV1 oAuth2) {
		if (oAuth == oAuth2) {
			return false;
		} else {
			oAuth = oAuth2;
			try {
				oAuth = OAuthV1Client.accessToken(oAuth);

				token = oAuth.getOauthToken();
				token_secret = oAuth.getOauthTokenSecret();
				sp.edit().putString("TOKEN_KEY", token).commit();
				sp.edit().putString("TOKEN_SECRET_KEY", token_secret).commit();
				sp.edit().putString("isBind", "yes").commit();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return true;
		}
	}

	public void getUserApi() {
		// //调用API获取用户信息
		// UserAPI userAPI=new UserAPI(OAuthConstants.OAUTH_VERSION_1);
		// try {
		// String response=userAPI.info(oAuth, "json");//获取用户信息
		// tvResult.setText(response+"\n");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// userAPI.shutdownConnection();
	}
}
