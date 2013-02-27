package com.ztspeech.unisay.trans.dc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.weibo.ISina;
import com.ztspeech.weibo.sdk.android.Weibo;
import com.ztspeech.weibo.sdk.android.WeiboAuthListener;
import com.ztspeech.weibo.sdk.android.WeiboDialogError;
import com.ztspeech.weibo.sdk.android.WeiboException;
import com.ztspeech.weibo.sdk.android.util.Utility;

public class ZtspeechSinaWeiboDC extends BaseDC {
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
	private WeiboAuthListener mListener;

	public ZtspeechSinaWeiboDC(Context context, Handler h) {
		super(context);
		super.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		handler = h;
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

	public void setWeiboAuthListener(WeiboAuthListener mListener) {
		this.mListener = mListener;
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
		web.setWebViewClient(new WeiboWebViewClient());
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, ">>>>>>>>>>>>>>>  Redirect URL: " + url);
			if (url.startsWith("sms:")) { // 针对webview里的短信注册流程，需要在此单独处理sms协议
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("address", url.replace("sms:", ""));
				sendIntent.setType("vnd.android-dir/mms-sms");
				context.startActivity(sendIntent);
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new WeiboDialogError(description, errorCode, failingUrl));
			handler.sendEmptyMessage(0);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, ">>>>>>>>>>>>>>>  onPageStarted URL: " + url);
			if (url.startsWith(Weibo.redirecturl)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}
			showLoading();
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, ">>>>>>>>>>>>>>>  onPageFinished URL: " + url);
			dismissLoading();
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	};

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			mListener.onComplete(values);
			handler.sendMessage(Message.obtain(handler, ISina.BIND_SINA_CODE));
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			mListener.onCancel();
		} else {
			if (error_code == null) {
				mListener.onWeiboException(new WeiboException(error, 0));
			} else {
				mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
			}

		}
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

	public void changeTitle(String title) {
		this.title.setText(title);
	}

	public void loadURL(final String url) {
		_showLoading();
		if (URLUtil.isNetworkUrl(url)) {
			web.loadUrl(url);
		} else {
			dismissLoading();
			showToast(context.getString(R.string.net_address_error));
		}
	}
}
