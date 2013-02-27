package com.ztspeech.unisay.trans.dc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.manager.ZtspeechWeiboManager;
import com.ztspeech.unisay.utils.LogInfo;

public class ZtspeechWeiboDC extends BaseDC {
	private static String APP_KEY = "801273041";
	private static String APP_SECRET = "104c26eb32e7346c95571065bcf5e4c2";

	private Handler handler = null;
	public View layout = null;
	private boolean dialog_flag = true;
	private Button back;
	private Button syn;
	private TextView title;
	private WebView web;
	private WebSettings webSet;
	public final static int RESULT_CODE = 1;
	private static final String TAG = "OAuthV1AuthorizeWebView";
	private OAuthV1 oAuth;

	public ZtspeechWeiboDC(Context context, Handler h, OAuthV1 oAuth) {
		super(context);
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		handler = h;
		this.oAuth = oAuth;
		layout = inflater.inflate(R.layout.weibo_syn, null);

		back = (Button) layout.findViewById(R.id.bbs_back);
		syn = (Button) layout.findViewById(R.id.bbs_syn);
		title = (TextView) layout.findViewById(R.id.bbs_title);
		web = (WebView) layout.findViewById(R.id.bbs_webView);
		back.setOnClickListener(this);
		syn.setOnClickListener(this);

		setWebView();
		addView(layout);
	}

	private void setWebView() {
		webSet = web.getSettings();
		webSet.setSavePassword(false);
		webSet.setAllowFileAccess(false);
		webSet.setJavaScriptEnabled(true);
		webSet.setSaveFormData(false);
		webSet.setSupportZoom(true);
		webSet.setBuiltInZoomControls(true);
		webSet.setCacheMode(WebSettings.LOAD_NO_CACHE);
		layout.setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);

		web.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocus();
				return false;
			}
		});
		web.setWebChromeClient(new WebChromeClient() { // 设置网页加载的进度条
			public void onProgressChanged(WebView view, int newProgress) {
				if (dialog_flag == true) {
					_showLoading();
					dialog_flag = false;
				}
				if (newProgress == 100) {
					dismissLoading();
					dialog_flag = true;
				}
			}

			// 设置应用程序的标题
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});
		web.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocus();
				return false;
			}
		});
		/*
		 * 
		 * URL = ztspeechandroid://OAuthActivity?oauth_token=
		 * f11203c98d813b227204254e3c148884&oauth_verifier=871525
		 */
		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				LogInfo.LogOut(TAG, "WebView onPageStarted...");
				LogInfo.LogOut(TAG, "URL = " + url);
				if (url != null) {
					if (url.indexOf("checkType=verifycode") != -1) {
						int start = url.indexOf("checkType=verifycode&v=") + 23;
						String verifyCode = url.substring(start, start + 6);
						oAuth.setOauthVerifier(verifyCode);
						view.destroyDrawingCache();
						handler.sendMessage(Message.obtain(handler, ZtspeechWeiboManager.CONSTENT_TENCENT_WEIBO_REGIST,
								oAuth));
					} else if (url.contains("ztspeechandroid")) {
						// handler.sendMessage(Message.obtain(handler,
						// ZtspeechWeiboManager.CONSTENT_SINA_WEIBO_REGIST,
						// url));
					}
				}
				super.onPageStarted(view, url, favicon);
			}
		});
	}

	public void clearSelf() {
		web.stopLoading();
		web.clearHistory();
		web.clearMatches();
		web.clearFormData();
		web.destroyDrawingCache();
		web = null;
		CookieManager cm = CookieManager.getInstance();
		cm.removeAllCookie();
	}

	@Override
	public void init(Handler h, int SWidth, int SHeight) {
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);

	}

	@Override
	public void onClicked(View v) {
		switch (v.getId()) {
		case R.id.bbs_back:
			handler.sendEmptyMessage(0);
			break;
		case R.id.bbs_syn:
			break;
		default:
			break;
		}

	}

	public OAuthV1 getOAuthV1() throws Exception {
		OAuthV1 oAuth = new OAuthV1("null");
		oAuth.setOauthConsumerKey(APP_KEY);
		oAuth.setOauthConsumerSecret(APP_SECRET);
		oAuth = OAuthV1Client.requestToken(oAuth);
		return oAuth;
	}

	public void changeTitle(String title) {
		this.title.setText(title);
	}

	public void loadURL() {
		_showLoading();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					oAuth = getOAuthV1();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String url = OAuthConstants.OAUTH_V1_AUTHORIZE_URL + "?oauth_token=" + oAuth.getOauthToken();
				if (URLUtil.isNetworkUrl(url)) {
					web.loadUrl(url);
				} else {
					dismissLoading();
					showToast(context.getString(R.string.net_address_error));
				}
			}
		}).start();
	}

	public void loadURL(String url) {
		_showLoading();
		if (URLUtil.isNetworkUrl(url)) {
			web.loadUrl(url);
		} else {
			dismissLoading();
			showToast(context.getString(R.string.net_address_error));
		}
	}
}
